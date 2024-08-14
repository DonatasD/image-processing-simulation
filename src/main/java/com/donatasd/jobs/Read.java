package com.donatasd.jobs;

import com.donatasd.resources.File;
import com.donatasd.resources.Memory;
import com.donatasd.resources.Storage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.*;

public class Read implements Runnable {

    private final Memory memory;
    private final Storage storage;
    private final BlockingQueue<File> filesToRead;
    private final BlockingQueue<File> filesToProcessQueue;
    private final AtomicBoolean finishedReading;

    public Read(Memory memory, Storage storage, BlockingQueue<File> filesToRead, BlockingQueue<File> filesToProcessQueue, AtomicBoolean finishedReading) {
        this.memory = memory;
        this.storage = storage;
        this.filesToRead = filesToRead;
        this.filesToProcessQueue = filesToProcessQueue;
        this.finishedReading = finishedReading;
    }

    @Override
    public void run() {
        try {
            while (!finishedReading.get()) {
                if (memory.getUsagePercent() > 80) {
                    Thread.sleep(200);
                } else {
                    if (filesToRead.isEmpty()) {
                        finishedReading.set(true);
                    } else {
                        var fileToRead = filesToRead.poll(100, TimeUnit.MILLISECONDS);
                        try {
                            if (fileToRead != null) {
                                var fileToProcess = storage.read(fileToRead);
                                filesToProcessQueue.put(fileToProcess);
                            }
                        } catch (RuntimeException e) {
                            filesToRead.add(fileToRead);
                        }
                    }
                }
            }
        } catch (Exception e) {
            out.println("Failed to process READ");
            throw new RuntimeException("Failed to process READ", e);
        }
    }
}
