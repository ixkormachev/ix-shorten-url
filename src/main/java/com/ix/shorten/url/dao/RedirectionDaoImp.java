package com.ix.shorten.url.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.ix.shorten.url.model.Redirection;

@Repository
public class RedirectionDaoImp implements RedirectionDao {

    @PersistenceContext(unitName = "puShortenUrl")
    protected EntityManager entityManager;

    @Override
    public void save(Redirection redirection) {
        entityManager.persist(redirection);
        entityManager.flush();
    }

    @Override
    public Redirection findByUsernameAndUrl(String userName, String url) {
        return (Redirection) entityManager.createQuery(
                "select r from Redirection as r where r.username = :userName AND r.url = :url)")
                .setParameter("userName", userName)
                .setParameter("url", url)
                .getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Redirection> findByUserName(String accountId) {
        return entityManager.createQuery(
                "select r from Redirection as r where r.username = :userName")
                .setParameter("userName", accountId)
                .getResultList();
    }

    @Override
    public Redirection findRedirectionByShortUrl(String shortUrl) {
        return (Redirection) entityManager.createQuery(
                "select r from Redirection as r where r.shortUrl = :shortUrl")
                .setParameter("shortUrl", shortUrl)
                .getSingleResult();
    }

}
