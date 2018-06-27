package com.epobedinsky.test.aop;

import com.epobedinsky.test.service.RateChecker;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
public class RateCheckAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RateChecker checker;

    @Before("@annotation(com.epobedinsky.test.aop.RateCheckable) && args(request,..)")
    public void before(JoinPoint joinPoint, HttpServletRequest request)  {
        logger.info("Check requests count for {}", joinPoint);

        if (checker.isExceeded(request.getRemoteAddr())) {
            logger.info("Rate exceeded for API {}", request.getRemoteAddr());
            throw new  RateExceededException();
        }
    }

    public static class RateExceededException extends RuntimeException {

    };
}
