package com.lws.demo;

import java.util.HashMap;
import java.util.Map;

public class Demo {

    public static void main(String[] args) {
        String s ="aaaabbcccdd";

        Map<Character,Integer> map = new HashMap<>();

        for (Character c : s.toCharArray()) {
//            System.out.println(c);

            if (map.containsKey(c)) {
                map.put(c,  map.get(c) + 1);
            }else {
                map.put(c, 1);
            }
//            map.put(c, map.getOrDefault(c, 0) + 1);

        }

        System.out.println(map);
       // o/p : a-4,b2,c-3,d-2

    }
}
