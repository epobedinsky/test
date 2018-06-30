package com.epobedinsky.test;

import com.epobedinsky.test.service.RequestsStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.epobedinsky.test.util.Util.now;

@RunWith(SpringRunner.class)
public class RequestHistoryTest {
    private static final long delta = 5;

    @Test
    public void testAdding() throws InterruptedException {
        RequestsStore.RequestsHistory rh = new RequestsStore.RequestsHistory();
        long first = now();
        rh.inc();
        rh.inc();

        Thread.sleep(500);
        long last = now();
        rh.inc();

        Assert.assertTrue(rh.getCount() == 4);
        Assert.assertTrue(rh.getFirstRequestMillis() - first < delta);
        Assert.assertTrue(rh.getLastRequestMillis() - last < delta);
    }

    @Test
    public void testAdjust() throws InterruptedException {
        RequestsStore.RequestsHistory rh = new RequestsStore.RequestsHistory();
        Thread.sleep(200);
        long first = now();
        rh.inc();
        Thread.sleep(110);
        rh.inc();
        rh.adjust(now() - 300);

        Assert.assertEquals(2,rh.getCount());
        Assert.assertTrue(rh.getFirstRequestMillis() - first < delta);
    }
}
