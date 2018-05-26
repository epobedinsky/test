package com.epobedinsky.test.service;

import java.util.function.BiFunction;

import static com.epobedinsky.test.util.Util.*;

/**
 * The puspose of this interface extract - to allow future use of memcaached or Redis-based implementations
 */
public interface RequestsStore {
    /**
     * Process upcoming Request object that can found in this store by ip param by processFunction applying
     * and get the result of processing
     *
     * @param ip ip address the new request came from
     * @param processFunction processing logic
     *        @see  BiFunction
     *
     * R apply(T t, U u) function of it has the following arguments:
     * t - ipAddress of a new request
     * u - existing Request for this ip. null if there were no added Request objects so far
     *
     * Return value (R) - Request object after processing
     *
     * @return  Request object after processing
     */
    Request processNewRequest(String ip,
                              BiFunction<String, Request, Request> processFunction);

    class Request {
        private long firstRequestMillis;
        private int count;
        private boolean isBlocked;

        public Request() {
            firstRequestMillis = now();
            count = 0;
            isBlocked = false;
        }

        public void block() {
            firstRequestMillis = now();
            count = 0;
            isBlocked = true;
        }

        public long getFirstRequestMillis() {
            return firstRequestMillis;
        }

        public int getCount() {
            return count;
        }

        public void inc() {
            count++;
        }

        public boolean isBlocked() {
            return isBlocked;
        }
    }
}
