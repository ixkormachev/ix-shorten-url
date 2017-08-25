package com.ix.shorten.url.exception;

public class ShortenUrlError extends Throwable {
    private static final long serialVersionUID = 1L;

    public ShortenUrlError() {

    }

    public ShortenUrlError(String message) {
        super(message);
    }

    public ShortenUrlError(Throwable cause) {
        super(cause);
    }

    public ShortenUrlError(String message, Throwable cause) {
        super(message, cause);
    }

}
