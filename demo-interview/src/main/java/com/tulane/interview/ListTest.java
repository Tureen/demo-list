package com.tulane.interview;

import java.util.Arrays;
import java.util.List;

public class ListTest {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("123", "22", "333");
        for (String s : list) {
            System.out.println(s);
        }
    }
}
