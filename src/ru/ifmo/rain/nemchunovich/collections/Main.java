package ru.ifmo.rain.nemchunovich.collections;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int[] array = {2, 2, 1, 1, 3, 5, 5};
        List<Integer> list = new ArrayList<>();
        for (int i : array) {
            list.add(i);
        }
        ArraySet<Integer> arraySet = new ArraySet<>(list);
        System.out.println(arraySet);
        System.out.println(arraySet.descendingSet());
        System.out.println(arraySet.descendingSet().descendingSet());
    }
}
