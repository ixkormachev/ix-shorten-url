package com.ix.shorten.url.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.ix.shorten.url.model.Account;

@Repository
public class AccountDaoImp implements AccountDao {

    @PersistenceContext(unitName = "puShortenUrl")
    protected EntityManager entityManager;

    @Override
    public Account findAccountId(String accountId) {
        return (Account) entityManager.createQuery(
                "select ac from Account as ac where username = :accountId")
                .setParameter("accountId", accountId).getSingleResult();
    }

    @Override
    public void saveAccountId(Account account) {
        entityManager.persist(account);
        entityManager.flush();

    }

}
