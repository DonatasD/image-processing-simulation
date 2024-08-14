package com.donatasd.jobs;

import com.donatasd.File;
import com.donatasd.resources.Memory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.out;

public class Process implements Runnable {

    private final Memory memory;
    private final BlockingQueue<File> filesToProcessQueue;
    private final BlockingQueue<File> filesToWrite;
    private final AtomicBoolean finishedReading;
    private final AtomicBoolean finishedProcessing;

    private static final Integer JOB_TIME_MS = 460;

    public Process(Memory memory, BlockingQueue<File> filesToProcessQueue, BlockingQueue<File> filesToWrite, AtomicBoolean finishedReading, AtomicBoolean finishedProcessing) {
        this.memory = memory;
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
                    File file = filesToProcessQueue.take();
                    out.println(STR."\{Thread.currentThread()} - PROCESS \{file.fileName()}");
                    var processedFile = new File(STR."\{file.fileName()}_processed", file.size());
                    memory.occupy(processedFile.size());
                    Thread.sleep(JOB_TIME_MS);
                    memory.release(file.size());
                    filesToWrite.put(processedFile);
                }
            }
        } catch (Exception e) {
            out.println("Failed to process PROCESS");
            throw new RuntimeException("Failed to process PROCESS", e);
        }
    }
}
