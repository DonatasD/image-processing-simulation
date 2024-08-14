package com.donatasd.jobs;

import com.donatasd.resources.File;
import com.donatasd.resources.Processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.out;

public class Process implements Runnable {

    private final Processor processor;
    private final BlockingQueue<File> filesToProcessQueue;
    private final BlockingQueue<File> filesToWrite;
    private final AtomicBoolean finishedReading;
    private final AtomicBoolean finishedProcessing;

    public Process(Processor processor, BlockingQueue<File> filesToProcessQueue, BlockingQueue<File> filesToWrite, AtomicBoolean finishedReading, AtomicBoolean finishedProcessing) {
        this.processor = processor;
        this.filesToProcessQueue = filesToProcessQueue;
        this.filesToWrite = filesToWrite;
        this.finishedReading = finishedReading;
        this.finishedProcessing = finishedProcessing;
    }

    @Override
    public void run() {
        try {
            while (!finishedProcessing.get()) {
                if (finishedReading.get() && filesToProcessQueue.isEmpty()) {
                    finishedProcessing.set(true);
                } else {
                    File file = filesToProcessQueue.poll(100, TimeUnit.MILLISECONDS);
                    try {
                        if (file != null) {
                            var processedFile = processor.processFile(file);
                            filesToWrite.put(processedFile);
                        }
                    } catch (Exception e) {
                        out.println(STR."Error processing file: \{e.getMessage()}");
                        filesToProcessQueue.put(file);
                    }
                }
            }
        } catch (Exception e) {
            out.println("Failed to process PROCESS");
            throw new RuntimeException("Failed to process PROCESS", e);
        }
    }
}
