package com.ix.shorten.url.dao;

import static org.junit.Assert.assertEquals;

import javax.persistence.NoResultException;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ix.shorten.url.api.BEAppConfiguration;
import com.ix.shorten.url.api.BEWebConfiguration;
import com.ix.shorten.url.exception.ShortenUrlError;
import com.ix.shorten.url.model.Redirection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BEAppConfiguration.class, BEWebConfiguration.class})
@ActiveProfiles("dev")
@Transactional
public class RedirectionDaoTest {
    private static final String SHORT_URL_FOR_ID1 = "xYswlE";
    public static final String HTTP_SHORT = "http://short.com/xYswlE";
    @Autowired
    private RedirectionDao redirectionDao;
    private static final String USER_NAME = "accountid1";
    private static final String URL = "http://ya222.ru";
   // private static final String BASE_URL = "http://short.com/";
    private static final String URL1 = "http://ya.ru";

    @Test
    public void add_when_enter_redirection_object_Then_save() throws ShortenUrlError {
        final String newShortUrl = RandomStringUtils.randomAlphabetic(8);
        
        createRedirectionObjectAndSaveToDb(USER_NAME, URL, newShortUrl);
        final Redirection newRedirection = readRedirectionObjectFromDb(URL);
        assertEquals(newShortUrl, newRedirection.getShortUrl());
    }

    @Test
    public void add_new_RedirectionObject_Then_read_and_check_shortUrl() throws ShortenUrlError {
    	final String newShortUrl = RandomStringUtils.randomAlphabetic(8);
    	createRedirectionObjectAndSaveToDb(USER_NAME, URL, newShortUrl);
    	Redirection newRedirectionObject = redirectionDao.findRedirectionByShortUrl(newShortUrl);
    	
    	assertEquals(newShortUrl, newRedirectionObject.getShortUrl());
    }
    
    private void createRedirectionObjectAndSaveToDb(String userName, String url, String newShortUrl) {
        final Redirection redirection = new Redirection();
        redirection.setShortUrl(newShortUrl);
        redirection.setUsername(userName);
        redirection.setUrl(url);
        redirectionDao.save(redirection);
    }

    private Redirection readRedirectionObjectFromDb(String url) {
        return redirectionDao.findByUsernameAndUrl(USER_NAME, url);
    }

    @Test
    public void test_save_when_enter_301_for_row_id_1_Then_update() throws ShortenUrlError {
        final String userName = "accountid1";
        Redirection redirection = readRedirectionObjectFromDb(URL1);
        assertEquals(302, redirection.getRedirectionCode());

        redirection.setRedirectionCode(301);
        redirectionDao.save(redirection);

        redirection = readRedirectionObjectFromDb(URL1);

        assertEquals(301, redirection.getRedirectionCode());
        assertEquals(1L, redirection.getId().longValue());
    }

    @Test
    public void test_findByUsernameAndUrl_When_enter_name_and_url_Then_return_redirection_object()
            throws ShortenUrlError {
        final Redirection redirection = readRedirectionObjectFromDb(URL1);
        assertEquals(SHORT_URL_FOR_ID1, redirection.getShortUrl());
    }

    @Test(expected = NoResultException.class)
    public void test_findByUsernameAndUrl_When_enter_new_url_Then_error() throws ShortenUrlError {
        readRedirectionObjectFromDb(URL);
    }
}
