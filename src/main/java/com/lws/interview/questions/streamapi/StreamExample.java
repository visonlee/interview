package com.lws.interview.questions.streamapi;

import java.util.*;
import java.util.stream.Collectors;

public class StreamExample {


    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 2, 5, 4, 6, 7, 7, 8, 9, 9);
        duplicateElements(numbers).forEach(System.out::println);

    }

//    How to find duplicate elements in a given integers list in java using Stream functions?
    public static List<Integer> duplicateElements(List<Integer> numbers) {

        Set<Integer> uniqueElements = new HashSet<>();
        List<Integer> duplicateElements = numbers.stream()
                .filter(n -> !uniqueElements.add(n))
                .distinct()
                .collect(Collectors.toList());

        return duplicateElements;
    }

    //Given a String, find the first repeated character in it using Stream functions
    public static char firstRepeatedCharacter(String input) {
        Set<Integer> seen = new HashSet<>();
        OptionalInt first = input.chars()
                .filter(i -> !seen.add(i))
                .findFirst();
        if (first.isPresent()) {
            return (char)first.getAsInt();
        }
        return ' ';
    }
}
