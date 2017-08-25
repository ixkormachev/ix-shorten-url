package com.ix.shorten.url.dto;

import org.springframework.http.HttpStatus;

public class RegisterResponseHolder {
    private RegisterResponse registerResponse = new RegisterResponse();
    private HttpStatus status = HttpStatus.NO_CONTENT;

    public RegisterResponse getRegisterResponse() {
        return registerResponse;
    }

    public void setRegisterResponse(RegisterResponse registerResponse) {
        this.registerResponse = registerResponse;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
