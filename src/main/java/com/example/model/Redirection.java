package com.example.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "redirection")
public class Redirection implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String url;
    private String shortUrl;
    private int redirectionCode;
    private long redirectCount;
    private String userEmail;

    public Redirection() {
    }
    
    public Redirection(Redirection r) {
    	this.id = r.id;
    	this.url = r.url;
    	this.shortUrl = r.shortUrl;
    	this.redirectionCode = r.redirectionCode;
    	this.redirectCount = r.redirectCount;
    	this.userEmail = r.userEmail;
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

    @Column(name = "USER_EMAIL")
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
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