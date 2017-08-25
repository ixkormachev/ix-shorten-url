package com.ix.shorten.url.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.ix.shorten.url.exception.ShortenUrlError;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BEAppConfiguration.class, BEWebConfiguration.class})
@ActiveProfiles("dev")
@Transactional
@WebAppConfiguration
public class ShortenedUrlApiTest {

    private static final String ACCOUNT_URL = "/account";
    private static final String ACCOUNT_ID_JSON = "{ \"AccountId\" : \"myAccountId\"}";
    private static final String YOUR_ACCOUNT_IS_OPENED = "Your account is opened";
    private static final String ACCOUNT_WITH_THAT_ID_ALREADY_EXISTS = "Account with that ID already exists";
    private static final String REGISTER_URL = "/register";
    private static final String NON_EXIST_URL = "/statistic/xxxxx";
    private static final String STATISTIC_ACCOUNTID1_URL = "/statistic/accountid1";
    private static final String NOT_EXIST_SHORT_URL = "/xxxx";
//    private static final String NOT_EXIST_SHORT_URL = "/short/xxxx";
    private static final String EXISTING_SHORT_URL = "/xYswlE";
//    private static final String EXISTING_SHORT_URL = "/short/xYswlE";
    private static final String SHORT_ACCOUNTID1_URL = "/xYswlE?id=accountid1";
//    private static final String SHORT_ACCOUNTID1_URL = "/short/xYswlE?id=accountid1";

    private final static String GOOD_AUTH_HEADER_ID_1 = "Basic YWNjb3VudGlkMToxMjM0NTY3OA==";
    private final static String BAD_AUTH_HEADER = "Basic YWNjb3VudGlkMToxMjM0NTYBBB==";
    private final static String REQUEST_BODY_EQUALS_REDIRECTION_CODE_301 =
            "{\"url\": \"http://stackoverflow.com/questions/1567929/website-safe-data-access-architecture-question?rq\u003d1\",\"redirectType\":301}";
    private final static String REQUEST_BODY_URL_FOUND_REDIRECTION_CODE_301 =
            "{\"url\":\"http://ya.ru\",\"redirectType\":301}";
    private final static String REQUEST_BODY_URL_FOUND_REDIRECTION_CODE_302 =
    		"{\"url\":\"http://ya.ru\",\"redirectType\":302}";

    private static final String REQUEST_BODY_EQUALS_REDIRECTION_CODE_302 =
            "{\"url\": \"http://ya.ru\",\"redirectType\":302}";
    private static final String REQUEST_BODY_EQUALS_REDIRECTION_CODE_YA_RU_301 =
            "{\"url\": \"http://ya.ru\",\"redirectType\":301}";

    private static final String REQUEST_BODY_EQUALS_REDIRECTION_CODE_IXBLOG_301 =
    		"{\"url\": \"http://ixkormachev-multi-war-test.blogspot.ru\",\"redirectType\":301}";



    protected MockMvc backEndMockMvc;

    @Autowired
    private WebApplicationContext backEndwebApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public final void initMockMvc() throws Exception, ShortenUrlError {
        backEndMockMvc = webAppContextSetup(backEndwebApplicationContext)
                .addFilters(springSecurityFilterChain).build();
    }

    @Test
    public void test_url_account_post_Then_created() throws ShortenUrlError, Exception {
        createNewAccount(HttpStatus.CREATED.value());
    }

    private MvcResult createNewAccount(int expectedHttpStatusCode) {
        MvcResult response = doPostAndGetResponse(ACCOUNT_URL, ACCOUNT_ID_JSON);
        assertEquals(expectedHttpStatusCode, response.getResponse().getStatus());
        assertEquals(MediaType.APPLICATION_JSON.toString(), response.getResponse().getContentType());
        return response;
    }

    private MvcResult doPostAndGetResponse(String url, String content) {
        try {
            return backEndMockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)).andReturn();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void test_url_account_2posts_Then_found() throws ShortenUrlError, Exception {
        MvcResult response = createNewAccount(HttpStatus.CREATED.value());
        assertTrue(response.getResponse().getContentAsString().contains(YOUR_ACCOUNT_IS_OPENED));

        response = createNewAccount(HttpStatus.FOUND.value());
        assertTrue(response.getResponse().getContentAsString().contains(ACCOUNT_WITH_THAT_ID_ALREADY_EXISTS));
    }

    @Test
    public void registerUrl_when_wrong_token_Then_403() throws ShortenUrlError, Exception {
        MvcResult response = doPostWithTokenAndGetResponse(REGISTER_URL, BAD_AUTH_HEADER,
                REQUEST_BODY_EQUALS_REDIRECTION_CODE_301);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getResponse().getStatus());
    }

    private MvcResult doPostWithTokenAndGetResponse(String url, String tokenHeader, String content) {
        try {
            return backEndMockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", tokenHeader)
                    .content(content)).andReturn();
        } catch (final Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    @Test
    public void registerUrl_when_new_url_Then_add()
            throws ShortenUrlError, Exception {
        MvcResult mockServiceResult = addNewRedirection(REQUEST_BODY_EQUALS_REDIRECTION_CODE_IXBLOG_301);
        String result = mockServiceResult.getResponse().getContentAsString();
        String[] newRedirection = result.split("/");
        String newShortUrl = newRedirection[newRedirection.length - 1].split("\"")[0];

        assertTrue(result.contains(newShortUrl));
        
        mockServiceResult = doGetWithTokenHeader("/" + newShortUrl, GOOD_AUTH_HEADER_ID_1);
        String locationHeader = mockServiceResult.getResponse().getHeader("Location");
        //result = mockServiceResult.getResponse().getContentAsString();

        assertEquals(302, mockServiceResult.getResponse().getStatus());
        assertTrue(locationHeader.equals("http://ixkormachev-multi-war-test.blogspot.ru"));
    }

    @Test
    public void registerUrl_when_url_found_and_redirectCode_not_equal_Then_update()
    		throws ShortenUrlError, Exception {
    	MvcResult mockServiceResult = changeRedirectionStatusTo301(REQUEST_BODY_URL_FOUND_REDIRECTION_CODE_301);
    	String result = mockServiceResult.getResponse().getContentAsString();
    	mockServiceResult = changeRedirectionStatusTo302();
    	result = mockServiceResult.getResponse().getContentAsString();
    	assertTrue(result.contains("xYswlE"));
    }

    private MvcResult addNewRedirection(String body) throws Exception {
    	MvcResult response = doPostWithTokenAndGetResponse(REGISTER_URL, GOOD_AUTH_HEADER_ID_1,
    			body);
    	
    	assertEquals(201, response.getResponse().getStatus());
    	return response;
    }
    
    private MvcResult changeRedirectionStatusTo301(String body) throws Exception {
    	MvcResult response = doPostWithTokenAndGetResponse(REGISTER_URL, GOOD_AUTH_HEADER_ID_1,
    			body);
    	
    	assertEquals(200, response.getResponse().getStatus());
    	return response;
    }
    private MvcResult changeRedirectionStatusTo302() throws Exception {
    	MvcResult response = doPostWithTokenAndGetResponse(REGISTER_URL, GOOD_AUTH_HEADER_ID_1,
    			REQUEST_BODY_URL_FOUND_REDIRECTION_CODE_302);
    	
    	assertEquals(200, response.getResponse().getStatus());
    	return response;
    }

    @Test
    public void registerUrl_when_url_found_and_redirectCodes_equal_Then_do_nothing()
            throws ShortenUrlError, Exception {
        MvcResult response = doPostWithTokenAndGetResponse(REGISTER_URL, GOOD_AUTH_HEADER_ID_1,
                REQUEST_BODY_EQUALS_REDIRECTION_CODE_302);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getResponse().getStatus());
    }

    @Test
    public void registerUrl_when_url_not_found_Then_add() throws ShortenUrlError, Exception {
        MvcResult response = doPostWithTokenAndGetResponse(REGISTER_URL, GOOD_AUTH_HEADER_ID_1,
                REQUEST_BODY_EQUALS_REDIRECTION_CODE_301);

        assertEquals(HttpStatus.CREATED.value(), response.getResponse().getStatus());
        assertTrue(response.getResponse().getContentAsString().contains("http://"));
    }

    @Test
    public void statistic_when_accountid_not_found_Then_noContent()
            throws ShortenUrlError, Exception {
        MvcResult response = doGetWithTokenHeader(NON_EXIST_URL, GOOD_AUTH_HEADER_ID_1);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getResponse().getStatus());
    }

    private MvcResult doGetWithTokenHeader(String url, String tokenHeader) throws Exception {
        return backEndMockMvc.perform(get(url)
                .header("Authorization", tokenHeader)
                .contentType("application/json"))
                .andReturn();
    }

    @Test
    public void statistic_when_accountid_found_Then_responseJson() throws ShortenUrlError, Exception {
        final String result = getStatisticForID1(STATISTIC_ACCOUNTID1_URL);
        assertTrue(result.contains("http://ya.ru"));
    }

    private String getStatisticForID1(String url) throws Exception {
        MvcResult response = doGetWithTokenHeader(STATISTIC_ACCOUNTID1_URL, GOOD_AUTH_HEADER_ID_1);

        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        return response.getResponse().getContentAsString();
    }

    @Test
    public void statistic_when_wrong_shortUrl_Then_help() throws ShortenUrlError, Exception {
        MvcResult response = doGetWithTokenHeader(NOT_EXIST_SHORT_URL, GOOD_AUTH_HEADER_ID_1);

        assertEquals(HttpStatus.MOVED_TEMPORARILY.value(), response.getResponse().getStatus());
        assertTrue(response.getResponse().getHeader("Location").contains("/help"));
    }

    @Test
    public void statistic_when_correct_shortUrl_Then_redirects302()
            throws ShortenUrlError, Exception {
        MvcResult response = doGetWithTokenHeader(EXISTING_SHORT_URL, GOOD_AUTH_HEADER_ID_1);

        assertEquals(HttpStatus.MOVED_TEMPORARILY.value(), response.getResponse().getStatus());
    }

    @Test
    public void statistic_when_correct_shortUrl_Then_redirects301()
            throws ShortenUrlError, Exception {
        changeRedirectStatusTo301ForId1();
        MvcResult response = doGetWithTokenHeader(SHORT_ACCOUNTID1_URL, GOOD_AUTH_HEADER_ID_1);

        assertEquals(HttpStatus.MOVED_PERMANENTLY.value(), response.getResponse().getStatus());
    }

    @Test
    public void statistic_when_after_1short_Then_count1()
            throws ShortenUrlError, Exception {
        changeRedirectStatusTo301ForId1();

        doGetWithTokenHeader(SHORT_ACCOUNTID1_URL, GOOD_AUTH_HEADER_ID_1);
        MvcResult response = doGetWithTokenHeader(SHORT_ACCOUNTID1_URL, GOOD_AUTH_HEADER_ID_1);

        assertEquals(HttpStatus.MOVED_PERMANENTLY.value(), response.getResponse().getStatus());
        String result = getStatisticForID1(STATISTIC_ACCOUNTID1_URL);

        assertTrue(result.contains("\"http://ya.ru\" : 2"));
    }

    private void changeRedirectStatusTo301ForId1() throws Exception {
        doPost(GOOD_AUTH_HEADER_ID_1, REQUEST_BODY_EQUALS_REDIRECTION_CODE_YA_RU_301);
    }

    private void doPost(String goodAuthHeaderId1, String requestBody) throws Exception {
        backEndMockMvc.perform(post("/register")
                .header("Authorization", goodAuthHeaderId1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType("application/json"));
    }
}