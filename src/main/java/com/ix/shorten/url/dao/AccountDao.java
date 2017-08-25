package com.ix.shorten.url.dao;

import com.ix.shorten.url.model.Account;

public interface AccountDao {
    Account findAccountId(String accountId);

    void saveAccountId(Account account);
}
