package com.ix.shorten.url.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ix.shorten.url.dto.AccountRequest;
import com.ix.shorten.url.dto.AccountResponse;
import com.ix.shorten.url.dto.RegisterRequest;
import com.ix.shorten.url.dto.RegisterResponse;
import com.ix.shorten.url.dto.RegisterResponseHolder;
import com.ix.shorten.url.dto.StatisticResponse;
import com.ix.shorten.url.dto.StatisticResponseHolder;
import com.ix.shorten.url.model.Redirection;
import com.ix.shorten.url.service.ShortenedUrlService;

@BasePathAwareController
public class ShortenedUrlController {

    @Autowired
    private ShortenedUrlService shortenUrlService;

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    @ResponseBody
    public AccountResponse openAccount(@RequestBody AccountRequest accountRequest,
                                       HttpServletResponse response) {
        final AccountResponse accountResponse = doOpenAccount(accountRequest);

        setHttpResponseAtributes(response,
                accountResponse.isSuccess() ? HttpStatus.CREATED.value() : HttpStatus.FOUND.value());
        return accountResponse;
    }

    private AccountResponse doOpenAccount(@RequestBody AccountRequest accountRequest) {
        return shortenUrlService.openAccount(accountRequest.getAccountId());
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public RegisterResponse registerUrl(@RequestBody RegisterRequest registerRequest,
                                        HttpServletResponse response, Principal authentication) {
        final RegisterResponseHolder responseHolder =
                doRegisterUrl(registerRequest, authentication);

        setHttpResponseAtributes(response, responseHolder.getStatus().value());
        return responseHolder.getRegisterResponse();
    }

    private RegisterResponseHolder doRegisterUrl(RegisterRequest registerRequest,
        Principal authentication) {
        final String userName = authentication.getName();
        return shortenUrlService.registerUrl(userName, registerRequest);
    }

    private void setHttpResponseAtributes(HttpServletResponse response, int status) {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
    }

    @RequestMapping(value = "/statistic/{AccountId}", method = RequestMethod.GET)
    @ResponseBody
    public StatisticResponse statistic(@PathVariable("AccountId") String accountId, HttpServletResponse response) {
        final StatisticResponseHolder responseHolder = doStatistic(accountId);
        setHttpResponseAtributes(response, responseHolder.getStatus().value());
        return responseHolder.getStatisticResponse();
    }

    private StatisticResponseHolder doStatistic(@PathVariable("AccountId") String accountId) {
        return shortenUrlService.createStatisticForAccountId(accountId);
    }

    @Value("classpath:pages/help.html")
    private Resource helpPage;

    @RequestMapping({"/help"})
    public void showHomePage(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        try (InputStream is = helpPage.getInputStream(); PrintWriter out = response.getWriter()) {
            returnHelpPage(is, out);
        }
    }

    private void returnHelpPage(InputStream is, PrintWriter out) throws IOException {
        final String str = IOUtils.toString(is);
        out.println(str);
        out.flush();
    }

    @RequestMapping(value = "/{shortenedUrl}", method = RequestMethod.GET)
    public void redirect(@PathVariable("shortenedUrl") String shortenedUrl,
                         HttpServletResponse response) throws IOException {
        Redirection redirection = doRedirection(shortenedUrl);
        doRedirectionWith301CodeOr302Code(response, redirection);
    }

    private void doRedirectionWith301CodeOr302Code(HttpServletResponse response, Redirection redirection) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(redirection.getRedirectionCode());
        response.setHeader("Location", redirection.getUrl());
        response.setHeader("Connection", "close");
    }

    private Redirection doRedirection(String shortUrl) {
        Redirection redirection;
        redirection = shortenUrlService.getRedirectionForShortUrl(shortUrl);
        return redirection;
    }

}
