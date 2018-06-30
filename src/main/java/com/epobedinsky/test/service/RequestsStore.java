package com.epobedinsky.test.service;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.epobedinsky.test.util.Util.*;

/**
 * The puspose of this interface extract - to allow future use of memcaached or Redis-based implementations
 */
public interface RequestsStore {
    /**
     * Process upcoming RequestsHistory object that can found in this store by ip param by processFunction applying
     * and get the result of processing
     *
     * @param ip ip address the new request came from
     * @param processFunction processing logic
     *        @see  BiFunction
     *
     * R apply(T t, U u) function of it has the following arguments:
     * t - ipAddress of a new request
     * u - existing RequestsHistory for this ip. null if there were no added RequestsHistory objects so far
     *
     * Return value (R) - RequestsHistory object after processing
     *
     * @return  RequestsHistory object after processing
     */
    RequestsHistory processNewRequest(String ip,
                                      BiFunction<String, RequestsHistory, RequestsHistory> processFunction);

    /**
     * Purge RequestStore according to specified whichToDelete predicate
     *
     * @param whichToDelete Should be not null
     */
    void purge(Predicate<RequestsHistory> whichToDelete);

    class RequestsHistory {
        private long firstRequestMillis;
        private int count;
        private boolean isBlocked;
        private NavigableSet<Long> requests = new TreeSet<>();

        public RequestsHistory() {
            count = 0;
            inc();
            firstRequestMillis = requests.first();
            isBlocked = false;
        }

        public void block() {
            isBlocked = true;
        }

        public void unblock() {
            isBlocked = false;
        }

        public long getFirstRequestMillis() {
            return firstRequestMillis;
        }

        public int getCount() {
            return count;
        }

        public void inc() {
            requests.add(now());
            count++;
        }

        public boolean isBlocked() {
            return isBlocked;
        }

        public void adjust(long millis) {
            Iterator<Long> it =  requests.iterator();
            boolean isContinue = true;
            while (it.hasNext() && isContinue) {
                Long r = it.next();
                if (r < millis) {
                    it.remove();
                    count--;
                } else {
                    isContinue = false;
                }
            }

            firstRequestMillis = requests.first();
        }

        public long getLastRequestMillis() {
            return requests.last();
        }
    }
}
