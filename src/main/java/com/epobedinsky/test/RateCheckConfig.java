package com.epobedinsky.test;

import com.epobedinsky.test.service.ConcurrentHashMapRequestsStore;
import com.epobedinsky.test.service.RequestsStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RateCheckConfig {
    @Value("${rate-check-aspect.period:5}")
    private long periodMinutes;

    @Value("${rate-check-aspect.max-rate:50}")
    private int maxRate = 3;

    private long periodMillis = 0;


    @PostConstruct
    public void postContruct() {
        periodMillis = periodMinutes * 60000;
    }

    @Bean
    public RequestsStore store() {
        return new ConcurrentHashMapRequestsStore();
    }

    public int getMaxRate() {
        return maxRate;
    }

    public long getPeriodMillis() {
        return periodMillis;
    }
}
