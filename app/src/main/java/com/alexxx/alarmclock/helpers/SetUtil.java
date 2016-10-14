package com.alexxx.alarmclock.helpers;

import java.util.Set;
import java.util.TreeSet;

public class SetUtil {
    public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
        Set<T> tmp = new TreeSet<>();
        for (T x : setA) {
            if (setB.contains(x)) {
                tmp.add(x);
            }
        }

        return tmp;
    }
}
