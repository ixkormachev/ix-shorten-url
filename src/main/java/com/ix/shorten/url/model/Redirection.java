package com.ix.shorten.url.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

@Table(name = "redirection")
public class Redirection implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String url;
    private String shortUrl;
    private int redirectionCode;
    private long redirectCount;
    private String username;

    public Redirection() {
    }
    
    public Redirection(Redirection r) {
    	this.id = r.id;
    	this.url = r.url;
    	this.shortUrl = r.shortUrl;
    	this.redirectionCode = r.redirectionCode;
    	this.redirectCount = r.redirectCount;
    	this.username = r.username;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "USERNAME")
    public String getUsername() {
        return username;
    }

    public void setUsername(String accountId) {
        this.username = accountId;
    }

    @Column(name = "URL")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "SHORT_URL")
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @Column(name = "REDIRECT_COUNT")
    public long getRedirectCount() {
        return redirectCount;
    }

    public void setRedirectCount(long count) {
        this.redirectCount = count;
    }

    @Column(name = "REDIRECT_CODE")
    public int getRedirectionCode() {
        return redirectionCode;
    }

    public void setRedirectionCode(int redirectionCode) {
        this.redirectionCode = redirectionCode;
    }

}
