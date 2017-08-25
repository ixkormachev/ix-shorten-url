package com.ix.shorten.url.dto;

import java.util.HashMap;
import java.util.Map;

public class StatisticResponse {
    private Map<String, Long> redirectStatistics = new HashMap<>();

    public Map<String, Long> getRedirectStatistics() {
        return redirectStatistics;
    }

    public void setRedirectStatistics(Map<String, Long> redirectStatistics) {
        this.redirectStatistics = redirectStatistics;
    }
}
