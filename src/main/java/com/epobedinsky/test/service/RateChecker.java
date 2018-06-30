package com.epobedinsky.test.service;

import com.epobedinsky.test.RateCheckConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import static com.epobedinsky.test.util.Util.*;

/**
 * Requests rate check implementation (request from one ip address count per period of time).
 *
 * Is driven by
 * @see RateCheckConfig instance
 */
@Service
public class RateChecker {
    @Autowired
    private RateCheckConfig config;

    @Autowired
    private RequestsStore store;

    /**
     *
     * @param ipAddress the request is received from. Should be null or empty
     * @return true if rate threshold exceeded, false otherwise
     */
    public boolean isExceeded(@NotNull String ipAddress) {
        if (ipAddress.isEmpty()) {
            throw new IllegalArgumentException("ipAddress is empty");
        }

        RequestsStore.RequestsHistory res = store.processNewRequest(ipAddress, this::process);

        if (res != null) {
            return res.isBlocked();
        }

        return false;
    }

    private RequestsStore.RequestsHistory process(String ipAddress, RequestsStore.RequestsHistory existingRecord) {
        //There were no requests from this ip
        if (existingRecord == null) {
            return new RequestsStore.RequestsHistory();
        }

        long now = now();
        if (now - existingRecord.getFirstRequestMillis() < config.getPeriodMillis()) {
            //New request from recorded ip within check period
            existingRecord.inc();
            if (existingRecord.getCount() > config.getMaxRate()) {
                existingRecord.block();
            }
        } else {
            existingRecord.inc();
            existingRecord.adjust(now - config.getPeriodMillis());
            if (existingRecord.getCount() > config.getMaxRate()) {
                existingRecord.block();
            } else {
                existingRecord.unblock();
            }
            //Check period is over, reset requests for this ip
            //return new RequestsStore.RequestsHistory();
        }

        return existingRecord;
    }

}
