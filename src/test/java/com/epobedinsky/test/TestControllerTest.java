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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-test.properties")
@EnableConfigurationProperties
@EnableAutoConfiguration
public class TestControllerTest extends BaseTest {
    @Autowired
    private TestController controller;

    @Test
    public void testNonBLocking() throws InterruptedException {
        System.out.println("-------- testNonBLocking started -------------");
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip");
        HttpServletRequest mock2 = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock2.getRemoteAddr()).thenReturn("test_ip2");
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(controller.get(mock2).getStatusCode() == HttpStatus.OK);
        Thread.sleep(3000);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        System.out.println("-------- testNonBLocking finished -------------");
    }

    @Test(expected = RateCheckAspect.RateExceededException.class)
    public void testBlocking()  {
        try {
            System.out.println("-------- testBLocking started -------------");
            HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
            Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip3");
            blocking(mock, controller);
            controller.get(mock); //here exception should be thrown
        } finally {
            System.out.println("-------- testBLocking finished -------------");
        }
    }

    @Test(expected = RateCheckAspect.RateExceededException.class)
    public void testBlockingTwice() throws InterruptedException {
        try {
            System.out.println("-------- testBLockingTwice started -------------");
            HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
            Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip4");
            blocking(mock, controller);
            Thread.sleep(1000);
            muteBlockedServiceCalls(mock, controller);

            controller.get(mock); //here exception should be thrown
        } finally {
            System.out.println("-------- testBLockingTwice finished -------------");
        }
    }

    @Test
    public void testBlockUnblock() throws InterruptedException {
        System.out.println("-------- testBLockUnblock started -------------");
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip5");
        blocking(mock, controller);
        muteException(() -> controller.get(mock));
        Thread.sleep(3000);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);
        System.out.println("-------- testBLockUnblock finished -------------");
    }

    @Test
    public void testBlockingBetweenPeriods() throws InterruptedException {
        System.out.println("-------- testBlockBetweenPeriods started -------------");
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mock.getRemoteAddr()).thenReturn("test_ip6");
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK); //1
        Thread.sleep(2000);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK); //2
        Thread.sleep(1010); //3 - 3 seconds passed after the first request, the counter set to 0
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK); //this should block, but it doesn't
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK);  //4
        Thread.sleep(1010);
        muteException(() -> controller.get(mock)); //5
        Thread.sleep(2000);
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK); //6
        Assert.assertTrue(controller.get(mock).getStatusCode() == HttpStatus.OK); //7
        muteException(() -> controller.get(mock)); //here exception should be thrown
        System.out.println("-------- testBlockBetweenPeriods finished -------------");
    }


    public interface Node {
        void addChild(Node node);
        List<Node> getChildes();
        void setParent(Node node);
        Node getParent();
        void setName(String name);
        String getName();
    }

    public class Tree {
        private Node root;

        void setRoot(Node root) {

        }

        public Node findNodeByName(String name) {
            return innerFindNode(root, name);
        }

        private Node innerFindNode(Node node, String name) {
            if (name == null || node == null) {
                return null;
            }

            if (node.getName() != null && name.equals(node.getName())) {
                return node;
            }

            for (Node child : node.getChildes()) {
                Node result = innerFindNode(child, name);
                if (result != null) {
                    return result;
                }
            }

            return null;
        }
    }

    @Test
    public void testEmptyTree() throws InterruptedException {
        Tree tr = new Tree();
        Assert.assertEquals(null, tr.findNodeByName("test_val"));
    }
}
