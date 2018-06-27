package com.epobedinsky.test.controller;

import com.epobedinsky.test.aop.RateCheckAspect;
import com.epobedinsky.test.aop.RateCheckable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RestController
@RequestMapping("/service")
public class TestController {

    @GetMapping
    @RateCheckable
    public ResponseEntity<String> get(HttpServletRequest request)  {
        return ResponseEntity.ok("\n");
    }

    @ExceptionHandler(RateCheckAspect.RateExceededException.class)
    public ResponseEntity handleEntitlementException(RateCheckAspect.RateExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }

}
