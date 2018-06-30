package com.epobedinsky.test;

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
@TestPropertySource("classpath:/application-gc-test.properties")
@EnableConfigurationProperties
@EnableAutoConfiguration
public class RequestsStoreGCTest extends BaseTest {
    @Autowired
    private TestController controller;

    @Test
    public void testCollect() throws InterruptedException {
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("gc_test_ip");
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Thread.sleep(2000);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        System.out.println("-------- testNonBLocking finished -------------");
    }

    @Test
    public void testNotCollect() throws InterruptedException {
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("gc_test_ip2");
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Thread.sleep(1000);
        muteException(() -> controller.get(mock));
        System.out.println("-------- testNonBLocking finished -------------");
    }
}
