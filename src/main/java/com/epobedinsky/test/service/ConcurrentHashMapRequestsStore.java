package com.epobedinsky.test.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ConcurrentHashMapRequestsStore implements RequestsStore {
    private ConcurrentHashMap<String, Request> requests
            = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Request processNewRequest(String ip,
                                     BiFunction<String, Request, Request> processFunction) {
        return requests.compute(ip, processFunction);
    }
}
