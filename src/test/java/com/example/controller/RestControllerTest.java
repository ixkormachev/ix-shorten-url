package com.example.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Transactional
public class RestControllerTest {

    private static final String ADD_URL_BODY = "{ \"url\": \"http://stackoverflow.com/questions/1567929/website-safe-dataaccess\", \"redirectType\" : 301}";
    private static final String HEADER_AUTH_OK = "Basic dGVzdDJAdGVzdC5jb206cGFzc3dvcmQy";
    private static final String PROCESS_BODY = "{\"result\":\"process me, please\"}";
    private static final String PROCESS_URL = "/process";
    private static final String ADD_USER_BODY = "{\"id\":null,\"email\":\"test2@test.com\",\"password\":\"password2\",\"name\":\"user2\",\"lastName\":\"lastName2\",\"active\":1,\"roles\":[\"ADMIN\"]}";
    private static final String ADD_USER_URL = "/register/user";
    private static final String ADD_URL = "/register/url";
    protected MockMvc backEndMockMvc;

    @Autowired
    private WebApplicationContext backEndwebApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public final void
            initMockMvc() throws Exception {
        backEndMockMvc = webAppContextSetup(backEndwebApplicationContext).addFilters(springSecurityFilterChain).build();
    }

    @Test
    public void
            testCreateNewUser() {
        MvcResult response = doPostAndGetResponse(ADD_USER_URL, ADD_USER_BODY);
        assertEquals("Condition error", HttpStatus.CONFLICT.value(), response.getResponse().getStatus());
    }

    private MvcResult
            doPostAndGetResponse(String url, String content) {
        try {
            return backEndMockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(content))
                    .andReturn();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void
            testProcess_unauthorized() {
        MvcResult response = doPostAndGetResponse(PROCESS_URL, PROCESS_BODY);
        assertEquals("Condition error", HttpStatus.UNAUTHORIZED.value(), response.getResponse().getStatus());
    }

    @Test
    public void
            testProcess_OK() {
        MvcResult response = doPostWithTokenAndGetResponse(PROCESS_URL, HEADER_AUTH_OK, PROCESS_BODY);
        assertEquals("Condition error", HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    private MvcResult
            doPostWithTokenAndGetResponse(String url, String tokenHeader, String body) {
        try {
            return backEndMockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", tokenHeader).content(body)).andReturn();
        } catch (final Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    @Test
    public void
            testGetUsers() throws Exception {
        MvcResult response = doGet("/user");
        assertEquals("Condition error", HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    private MvcResult
            doGet(String url) throws Exception {
        return backEndMockMvc.perform(get(url)).andReturn();
    }

    @Test
    public void
            testGetStatistic() throws Exception {
        MvcResult response = doGet("/statistic/test2@test.com");
        assertEquals("Condition error", HttpStatus.NO_CONTENT.value(), response.getResponse().getStatus());
    }

    @Test
    public void
            testAddUrl() throws Exception {
        MvcResult response = doPostWithTokenAndGetResponse(ADD_URL, HEADER_AUTH_OK, ADD_URL_BODY);
        assertEquals("Condition error", HttpStatus.CREATED.value(), response.getResponse().getStatus());
    }
}
