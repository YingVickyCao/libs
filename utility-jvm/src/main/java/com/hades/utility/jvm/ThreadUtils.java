package com.hades.utility.jvm;

public class ThreadUtils {
    public static String getThreadInfo() {
        return ",[thread =" + Thread.currentThread().getId() + "," + Thread.currentThread().getName() + "]";
    }
}
