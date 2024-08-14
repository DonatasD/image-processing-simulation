package com.donatasd.solutions;


import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BasicSolution extends Solution {

    public BasicSolution(Integer memorySize, Integer filesToReadCount) {
        super(memorySize, filesToReadCount);
    }

    @Override
    public void run() {
        var totalThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        var tasks = Collections.nCopies(totalThreads, Executors.callable(this.create())).stream().toList();

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }

    private Runnable create() {
        return () -> {
            while (!getFilesToReadQueue().isEmpty()) {
                try {
                    var file = getFilesToReadQueue().poll(100, TimeUnit.MILLISECONDS);
                    if (file != null) {
                        var fileToProcess = getStorage().read(file);
                        var processedFile = getProcessor().processFile(fileToProcess);
                        getStorage().write(processedFile);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
