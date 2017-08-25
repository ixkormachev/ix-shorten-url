package com.ix.shorten.url.dto;

import org.springframework.http.HttpStatus;

public class StatisticResponseHolder {
    private StatisticResponse statisticResponse = new StatisticResponse();
    private HttpStatus status = HttpStatus.NO_CONTENT;

    public StatisticResponse getStatisticResponse() {
        return statisticResponse;
    }

    public void setStatisticResponse(StatisticResponse statisticResponse) {
        this.statisticResponse = statisticResponse;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
