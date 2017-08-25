package com.ix.shorten.url.service;

import com.ix.shorten.url.dto.AccountResponse;
import com.ix.shorten.url.dto.RegisterRequest;
import com.ix.shorten.url.dto.RegisterResponseHolder;
import com.ix.shorten.url.dto.StatisticResponseHolder;
import com.ix.shorten.url.model.Redirection;

public interface ShortenedUrlService {
  AccountResponse openAccount(String accountId);

  RegisterResponseHolder registerUrl(String userName, RegisterRequest registerRequest);

  StatisticResponseHolder createStatisticForAccountId(String accountId);

  Redirection getRedirectionForShortUrl(String shortenedUrl);

}
