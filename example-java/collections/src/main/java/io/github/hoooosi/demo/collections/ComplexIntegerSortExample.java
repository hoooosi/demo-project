package io.github.hoooosi.demo.collections;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

public class ComplexIntegerSortExample {
    public static void main(String[] args) {

        // Define a custom Comparator using a Lambda expression.
        // The goal is to sort the integers with even numbers first, then odd numbers,
        // with both groups sorted internally in ascending order.
        Comparator<Integer> customComparator = (i1, i2) -> {
            boolean isEven1 = i1 % 2 == 0;
            boolean isEven2 = i2 % 2 == 0;

            // Rule 1: Even numbers take precedence over odd numbers.
            if (isEven1 && !isEven2) {
                return -1; // i1 is Even, i2 is Odd. i1 should come before i2.
            }
            if (!isEven1 && isEven2) {
                return 1;  // i1 is Odd, i2 is Even. i1 should come after i2.
            }

            // Rule 2/3: If both are the same (both even or both odd), sort in ascending order.
            // Ascending order is achieved by i1 - i2 (negative if i1 < i2).
            return i1 - i2;
        };

        // Create the TreeSet, passing the custom Comparator to the constructor.
        TreeSet<Integer> customSortedSet = new TreeSet<>(customComparator);

        Arrays.asList(7, 4, 2, 9, 6, 1, 8, 3, 5, 4).forEach(item -> {
            customSortedSet.add(item);
            System.out.println("Added " + item + ": " + customSortedSet);
        });
    }
}