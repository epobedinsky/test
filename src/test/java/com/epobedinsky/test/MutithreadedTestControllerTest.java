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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties
@EnableAutoConfiguration
public class MutithreadedTestControllerTest extends BaseTest {
    @Autowired
    private TestController controller;

    @Test
    public void testMutithreaded() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        List<Future<ResponseEntity>> futures = service.invokeAll(new ArrayList<Callable<ResponseEntity>>(){{
            //the first thread: blocking, then unblock
            add(() -> {
                HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
                Mockito.when(mock.getRemoteAddr()).thenReturn("mt_test_ip");
                blocking(mock, controller);

                Thread.sleep(3000);

                return controller.get(mock);
            });

            //the first thread: blocking
            add(() -> {
                HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
                Mockito.when(mock.getRemoteAddr()).thenReturn("mt_test_ip2");
                blocking(mock, controller);
                muteException(() -> controller.get(mock));
                return ResponseEntity.ok().build();
            });

            //the third thread: blocking, blocking, unblocking
            add(() -> {
                HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
                Mockito.when(mock.getRemoteAddr()).thenReturn("mt_test_ip3");
                blocking(mock, controller);
                Thread.sleep(3000);
                blocking(mock, controller);
                Thread.sleep(3000);

                return controller.get(mock);
            });
        }});

        for (Future<ResponseEntity> future : futures) {
            if (future.isDone()) {
                Assert.assertTrue(future.get().getStatusCode() == HttpStatus.OK);
            }
        }
    }
}
