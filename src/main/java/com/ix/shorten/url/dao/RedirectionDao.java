package com.ix.shorten.url.dao;

import java.util.List;

import com.ix.shorten.url.model.Redirection;

public interface RedirectionDao {
    void save(Redirection redirect);

    Redirection findByUsernameAndUrl(String userName, String url);

    List<Redirection> findByUserName(String accountId);

    Redirection findRedirectionByShortUrl(String shortUrl);
}
