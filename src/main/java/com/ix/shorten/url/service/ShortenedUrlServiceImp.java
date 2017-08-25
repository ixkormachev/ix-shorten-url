package com.ix.shorten.url.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ix.shorten.url.dao.AccountDao;
import com.ix.shorten.url.dao.RedirectionDao;
import com.ix.shorten.url.dto.AccountResponse;
import com.ix.shorten.url.dto.RegisterRequest;
import com.ix.shorten.url.dto.RegisterResponseHolder;
import com.ix.shorten.url.dto.StatisticResponseHolder;
import com.ix.shorten.url.model.Account;
import com.ix.shorten.url.model.Redirection;

@Service
@Transactional
public class ShortenedUrlServiceImp implements ShortenedUrlService {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private RedirectionDao redirectionDao;

    @Override
    public AccountResponse openAccount(String accountId) {
        AccountResponse result = new AccountResponse();
        try {
            doesAccountExist(accountId);
        } catch (final NoResultException ex) {
            result = noThenCreateNewAccount(accountId, result);
            return result;
        }
        result = yesAccountExistsThenItCannotBeOpened(result);
        return result;
    }

    private AccountResponse noThenCreateNewAccount(String accountId, AccountResponse result) {
        Account account = createNewAccountAndAddToDb(accountId);
        result = processResultForNewAccount(result, account);
        return result;
    }

    private void doesAccountExist(String accountId) {
        accountDao.findAccountId(accountId);
    }

    private AccountResponse yesAccountExistsThenItCannotBeOpened(AccountResponse result) {
        result.setSuccess(false);
        result.setDescription(
                "Account with that ID already exists");
        return result;
    }

    private AccountResponse processResultForNewAccount(AccountResponse result, Account account) {
        result.setSuccess(true);
        result.setDescription("Your account is opened");
        result.setPassword(account.getPassword());
        return result;
    }

    private Account createNewAccountAndAddToDb(String accountId) {
        final Account account = new Account();
        account.setUsername(accountId);
        account.setPassword(RandomStringUtils.randomAlphanumeric(8));
        accountDao.saveAccountId(account);
        return account;
    }

    private static final String baseUrl;
    private static final String helpUrl;

    static {
        String canonicalHostName = "127.0.0.1";
        InetAddress iAddress;
        try {
            iAddress = InetAddress.getLocalHost();
            canonicalHostName = iAddress.getCanonicalHostName();
        } catch (final UnknownHostException e) {
            e.printStackTrace();
        }
        baseUrl = "http://" + canonicalHostName + ":8080/";
        helpUrl = "http://" + canonicalHostName + ":8080/help/";
    }

    @Override
    public RegisterResponseHolder registerUrl(String userName, RegisterRequest registerRequest) {
        final RegisterResponseHolder registerResponseHolder = new RegisterResponseHolder();
        Redirection redirection;

        try {
            redirection = getIfExistTheRedirectionObject(userName, registerRequest);
        } catch (final NoResultException ex) {
            return processRedirectionObjectDoesNotExistCase(userName, registerRequest, registerResponseHolder);
        }

        if (isDoNothingCase(registerRequest, redirection))
            return registerResponseHolder;
        else
            return processUpdateRedirectionObjectCase(registerRequest, registerResponseHolder, redirection);
    }

    private Redirection getIfExistTheRedirectionObject(String userName, RegisterRequest registerRequest) {
        return redirectionDao.findByUsernameAndUrl(userName, registerRequest.getUrl());
    }

    private RegisterResponseHolder processRedirectionObjectDoesNotExistCase(
            String userName, RegisterRequest registerRequest,
            RegisterResponseHolder registerResponseHolder) {
        final Redirection redirection =
                createNewRedirectionObjectAndAddItToDb(userName, registerRequest);
        registerResponseHolder =
                processRegisterResponseHolderForNewObject(registerResponseHolder, redirection);
        return registerResponseHolder;
    }

    private RegisterResponseHolder processRegisterResponseHolderForNewObject(
            RegisterResponseHolder registerResponseHolder, Redirection redirection) {
        registerResponseHolder.getRegisterResponse().setShortUrl(redirection.getShortUrl());
        registerResponseHolder.setStatus(HttpStatus.CREATED);
        return registerResponseHolder;
    }

    private Redirection
    createNewRedirectionObjectAndAddItToDb(String userName, RegisterRequest registerRequest) {
        Redirection redirection;
        redirection = new Redirection();
        redirection.setUsername(userName);
        redirection.setUrl(registerRequest.getUrl());
        redirection.setShortUrl(RandomStringUtils.randomAlphabetic(8));
        redirection.setRedirectionCode(302);
        redirectionDao.save(redirection);
        
        Redirection result = new Redirection(redirection);
        
        result.setShortUrl(baseUrl + redirection.getShortUrl());
        return result;
    }

    private boolean isDoNothingCase(RegisterRequest registerRequest, Redirection redirection) {
        return registerRequest.getRedirectionType() == redirection.getRedirectionCode();
    }

    private RegisterResponseHolder processUpdateRedirectionObjectCase(
            RegisterRequest registerRequest, RegisterResponseHolder registerResponseHolder,
            Redirection redirection) {
        redirection.setRedirectionCode(registerRequest.getRedirectionType());
        redirectionDao.save(redirection);

      //  redirection.setShortUrl(baseUrl + redirection.getShortUrl());
        registerResponseHolder.getRegisterResponse().setShortUrl(baseUrl + redirection.getShortUrl());
        registerResponseHolder.setStatus(HttpStatus.OK);
        return registerResponseHolder;
    }

    @Override
    public StatisticResponseHolder createStatisticForAccountId(String accountId) {

        final List<Redirection> redirections = redirectionDao.findByUserName(accountId);

        final StatisticResponseHolder statisticResponseHolder = new StatisticResponseHolder();
        if (isNothingToDoCaseForStatistic(redirections)) {
            statisticResponseHolder.setStatus(HttpStatus.NO_CONTENT);
            return statisticResponseHolder;
        }

        createStatisticReport(statisticResponseHolder, redirections);
        statisticResponseHolder.setStatus(HttpStatus.OK);
        return statisticResponseHolder;
    }

    private boolean isNothingToDoCaseForStatistic(List<Redirection> redirections) {
        return redirections.isEmpty();
    }

    private void createStatisticReport(
            StatisticResponseHolder statisticResponseHolder, List<Redirection> redirections) {
        final Map<String, Long> response =
                statisticResponseHolder.getStatisticResponse().getRedirectStatistics();
        for (final Redirection r : redirections) {
            response.put(r.getUrl(), r.getRedirectCount());
        }
    }

    @Override
    public Redirection getRedirectionForShortUrl(String shortUrl) {
        Redirection result;
        try {
            result = redirectionDao.findRedirectionByShortUrl(shortUrl);
        } catch (final NoResultException ex) {
            return createNewRedirectionResult();
        }
        result.setRedirectCount(result.getRedirectCount() + 1);
        redirectionDao.save(result);
        return result;
    }

    private Redirection createNewRedirectionResult() {
        Redirection result;
        result = new Redirection();
        result.setUrl(helpUrl);
        result.setRedirectionCode(302);
        return result;
    }

}
