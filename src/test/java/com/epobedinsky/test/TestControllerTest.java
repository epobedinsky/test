package com.epobedinsky.test;

import com.epobedinsky.test.aop.RateCheckAspect;
import com.epobedinsky.test.controller.TestController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties
@EnableAutoConfiguration
public class TestControllerTest {
    @Autowired
    private TestController controller;

    @Test
    public void testNonBLocking() throws InterruptedException {
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip");
        HttpServletRequest mock2 = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock2.getRemoteAddr()).thenReturn("test_ip2");
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock2).getStatusCode() == HttpStatus.OK);
        Thread.sleep(3000);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
    }

    @Test(expected = RateCheckAspect.RateExceededException.class)
    public void testBlocking()  {
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip3");
        blocking(mock);
        controller.get(mock); //here exception should be thrown
    }

    @Test(expected = RateCheckAspect.RateExceededException.class)
    public void testBlockingTwice() throws InterruptedException {
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip4");
        blocking(mock);
        Thread.sleep(1000);
        muteBlockedServiceCalls(mock);

        controller.get(mock); //here exception should be thrown
    }

    @Test
    public void testBlockUnblock() throws InterruptedException {
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip5");
        blocking(mock);
        muteException(() -> controller.get(mock));
        Thread.sleep(3000);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
    }

    private void blocking(HttpServletRequest mock)  {
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
    }

    private void muteBlockedServiceCalls(HttpServletRequest mock)  {
        muteException(() -> controller.get(mock));
        muteException(() -> controller.get(mock));
    }

    private void muteException(Runnable call) {
        try {
            call.run();
            Assert.assertFalse("Exception wasn't thrown", false);
        } catch (RateCheckAspect.RateExceededException e) {

        }
    }
}
