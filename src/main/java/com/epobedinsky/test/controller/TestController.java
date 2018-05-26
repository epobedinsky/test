package com.epobedinsky.test.controller;

import com.epobedinsky.test.aop.RateCheckAspect;
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
    public ResponseEntity<String> get(HttpServletRequest request) {
        return ResponseEntity.ok("\n");
    }

    @ExceptionHandler(RateCheckAspect.RateExceeededException.class)
    public ResponseEntity handleEntitlementException(RateCheckAspect.RateExceeededException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }

}
