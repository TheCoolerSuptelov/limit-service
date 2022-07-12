package com.github.thecoolersuptelov.limitsservice.controllers;

import com.github.thecoolersuptelov.limitsservice.Domain.Limits;
import com.github.thecoolersuptelov.limitsservice.configuration.Configuration;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
    @Autowired
    private Configuration configuration;

    @GetMapping("/sample-api")
    @Retry(name = "sample-api", fallbackMethod = "fallbackForSampleApi")
//    @CircuitBreaker(name = "default", fallbackMethod = "fallbackForSampleApi")
    public String sampleApi(){
        logger.info("Api call received");
        var someFailedRequest = new RestTemplate().getForEntity("http://localhost:9299/failedRequest", String.class);
        return someFailedRequest.getBody();
    }
    public String fallbackForSampleApi(Throwable e){
        return new Limits(configuration.getMinimum(), configuration.getMaximum()).toString();
    }
}
