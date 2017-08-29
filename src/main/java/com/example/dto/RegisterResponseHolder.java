package com.example.dto;

import org.springframework.http.HttpStatus;

public class RegisterResponseHolder {
    private RegisterResponse responseResult = new RegisterResponse();
    private HttpStatus status = HttpStatus.OK;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

	public RegisterResponse getResponseResult() {
		return responseResult;
	}

	public void setResponseResult(RegisterResponse responseResult) {
		this.responseResult = responseResult;
	}
}
