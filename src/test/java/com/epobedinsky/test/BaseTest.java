package com.epobedinsky.test;

import com.epobedinsky.test.aop.RateCheckAspect;
import com.epobedinsky.test.controller.TestController;
import org.junit.Assert;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

public class BaseTest {
    protected void blocking(HttpServletRequest mock, TestController controller)  {
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
    }

    protected void muteBlockedServiceCalls(HttpServletRequest mock, TestController controller)  {
        muteException(() -> controller.get(mock));
        muteException(() -> controller.get(mock));
    }

    protected void muteException(Runnable call) {
        try {
            call.run();
            Assert.assertTrue("Exception wasn't thrown", false);
        } catch (RateCheckAspect.RateExceededException e) {

        }
    }
}
