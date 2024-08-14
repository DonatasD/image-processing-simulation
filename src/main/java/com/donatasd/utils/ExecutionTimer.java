package com.donatasd.utils;

public class ExecutionTimer {

    public static <T> Long measure(Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
    }
}