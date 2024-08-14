package com.donatasd.jobs;

import com.donatasd.resources.File;
import com.donatasd.resources.Memory;
import com.donatasd.resources.Storage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.out;

public class ReadWrite implements Runnable {

    private final Memory memory;
    private final Storage storage;
    private final BlockingQueue<File> filesToReadQueue;
    private final BlockingQueue<File> filesToProcessQueue;
    private final BlockingQueue<File> filesToWriteQueue;
    private final AtomicBoolean finishedReading;
    private final AtomicBoolean finishedProcessing;
    private final AtomicBoolean finishedWriting;

    private JobMode mode = JobMode.Read;

    public ReadWrite(Memory memory, Storage storage, BlockingQueue<File> filesToReadQueue, BlockingQueue<File> filesToProcessQueue, BlockingQueue<File> filesToWriteQueue, AtomicBoolean finishedReading, AtomicBoolean finishedProcessing, AtomicBoolean finishedWriting) {
        this.memory = memory;
        this.storage = storage;
        this.filesToReadQueue = filesToReadQueue;
        this.filesToProcessQueue = filesToProcessQueue;
        this.filesToWriteQueue = filesToWriteQueue;
        this.finishedReading = finishedReading;
        this.finishedProcessing = finishedProcessing;
        this.finishedWriting = finishedWriting;
    }

    @Override
    public void run() {
        while(!finishedReading.get() || !finishedWriting.get()) {
            try {
                if (memory.getUsagePercent() > 80 && this.mode == JobMode.Read) {
                    this.mode = JobMode.Write;
                }
                if (!finishedReading.get() && this.mode == JobMode.Write && memory.getUsagePercent() < 40) {
                    this.mode = JobMode.Read;
                }

                switch (mode) {
                    case Write -> {
                        if (finishedProcessing.get() && filesToWriteQueue.isEmpty()) {
                            finishedWriting.set(true);
                        } else {
                            write();
                        }
                    }
                    case Read -> {
                        if (filesToReadQueue.isEmpty()) {
                            finishedReading.set(true);
                            this.mode = JobMode.Write;
                        } else {
                            read();
                        }
                    }
                }
            } catch (InterruptedException e) {
                out.println("Failed to process WRITE");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void read() {
        try {
            var fileToRead = filesToReadQueue.poll(100, TimeUnit.MILLISECONDS);
            try {
                if (fileToRead != null) {
                    var fileToProcess = this.storage.read(fileToRead);
                    filesToProcessQueue.put(fileToProcess);
                }
            } catch (Exception e) {
                // Return back to queue
                out.println(STR."Error reading file: \{e.getMessage()}");
                filesToReadQueue.add(fileToRead);
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            out.println("READ FAILED");
            throw new RuntimeException(e);
        }
    }

    private void write() {
        try {
            var fileToWrite = filesToWriteQueue.poll(100, TimeUnit.MILLISECONDS);
            try {
                if (fileToWrite != null) {
                    storage.write(fileToWrite);
                }
            } catch (Exception e) {
                // Return back to queue
                out.println(STR."Error writing file: \{e.getMessage()}");
                filesToWriteQueue.add(fileToWrite);
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            out.println("WRITE FAILED");
            throw new RuntimeException(e);
        }
    }


}
