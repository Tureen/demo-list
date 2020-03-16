package com.tulane.gctest.springboot.controller;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class MemLeaker {

    private List<Object> objs = new LinkedList<>();

//    @Scheduled(fixedRate = 1000)
    public void run(){
        for (int i = 0; i < 50000; i++) {
            objs.add(new Object());
        }
    }
}
