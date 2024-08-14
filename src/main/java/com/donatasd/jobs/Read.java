package com.donatasd.jobs;

import com.donatasd.File;
import com.donatasd.resources.Memory;
import com.donatasd.resources.Storage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.*;

public class Read implements Runnable {

    private final Memory memory;
    private final Storage storage;
    private final BlockingQueue<String> filesToRead;
    private final BlockingQueue<File> filesToProcessQueue;
    private final AtomicBoolean finishedReading;

    public Read(Memory memory, Storage storage, BlockingQueue<String> filesToRead, BlockingQueue<File> filesToProcessQueue, AtomicBoolean finishedReading) {
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
                out.println(STR."Usage: \{memory.getUsagePercent()}");
                if (memory.getUsagePercent() > 50) {
                    Thread.sleep(200);
                } else {
                    if (filesToRead.isEmpty()) {
                        finishedReading.set(true);
                    } else {
                        var fileToRead = filesToRead.take();
                        try {
                            out.println(STR."\{Thread.currentThread()} - READ \{fileToRead}");
                            var file = this.storage.read(fileToRead);
                            memory.occupy(file.size());
                            filesToProcessQueue.put(file);
                        } catch (Exception e) {
                            filesToRead.put(fileToRead);
                            this.run();
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
