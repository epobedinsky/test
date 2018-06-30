package com.epobedinsky.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConcurrentHashMapRequestsStore implements RequestsStore {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConcurrentHashMap<String, RequestsHistory> requests
            = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestsHistory processNewRequest(String ip,
                                             BiFunction<String, RequestsHistory, RequestsHistory> processFunction) {
        return requests.compute(ip, processFunction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purge(Predicate<RequestsHistory> whichTodelete) {
        Set<Map.Entry<String, RequestsHistory>> toDelete = requests.entrySet().stream().filter(entry -> whichTodelete.test(entry.getValue())).collect(Collectors.toSet());
        logger.info("Purge: {} are going to be deleted", toDelete.size());
        for (Map.Entry<String, RequestsHistory> entry : toDelete) {
            requests.remove(entry.getKey(), entry.getValue());
        }
    }
}
