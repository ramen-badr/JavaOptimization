package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random random = new Random();
        List<ValueObject> values = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            values.add(new ValueObject(random.nextInt(100)));
        }

        System.out.println("Before sort: " + values);
        ManualValueSorter.sortAscendingByValue(values);
        System.out.println("After sort: " + values);
    }
}
