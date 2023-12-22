package com.hades.utility.jvm;

public class MockHeavyWork {

    public static long sum(int upper) {
        long sum = 0;
        for (int i = 0; i < upper; i++) {
            sum += i;
        }
        return sum;
    }
}
