package com.vinayak.java.kafka.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String homeEndpoint(){
        return "Welcome to Vinayak's Ecosystem";
    }
}