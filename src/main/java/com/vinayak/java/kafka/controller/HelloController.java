package com.vinayak.java.kafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/")
    public String homeEndpoint(){
        LOG.info("Reached Hello Controller");
        return "Welcome to Vinayak's Ecosystem";
    }
}