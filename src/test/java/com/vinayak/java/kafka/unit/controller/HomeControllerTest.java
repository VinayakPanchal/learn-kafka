package com.vinayak.java.kafka.unit.controller;

import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Test
public class HomeControllerTest {

    public void basicTest(){
        assertThat("Hello".equals("Hello"));
    }
}
