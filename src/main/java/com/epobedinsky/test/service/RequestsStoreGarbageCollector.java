package com.epobedinsky.test.service;

import com.epobedinsky.test.RateCheckConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.epobedinsky.test.util.Util.now;

/**
 * Simple implementation running in scheduled threads
 */
@Service
public class RequestsStoreGarbageCollector {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RateCheckConfig config;

    @Autowired
    private RequestsStore store;

    @PostConstruct
    void postConstruct() {
        if (config.isGcEnabled()) {
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(this::perfromGC, 0, config.getGcPeriodSeconds(), TimeUnit.SECONDS);
        }
    }

    private void perfromGC() {
        logger.info("Request store GC. Time: " + now());
        store.purge(this::isRequestHistoryExpired);
    }

    private boolean isRequestHistoryExpired(RequestsStore.RequestsHistory rh) {
        return (now() - rh.getLastRequestMillis() >= config.getGcPeriodSeconds());
    }
}
