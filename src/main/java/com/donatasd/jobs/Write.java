package com.donatasd.jobs;

import com.donatasd.resources.File;
import com.donatasd.resources.Memory;
import com.donatasd.resources.Storage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.out;

public class Write implements Runnable {

    private final Memory memory;
    private final Storage storage;
    private final BlockingQueue<File> filesToWrite;
    private final AtomicBoolean finishedProcessing;
    private final AtomicBoolean finishedWriting;

    public Write(Memory memory, Storage storage, BlockingQueue<File> filesToWrite, AtomicBoolean finishedProcessing, AtomicBoolean finishedWriting) {
        this.memory = memory;
        this.storage = storage;
        this.filesToWrite = filesToWrite;
        this.finishedProcessing = finishedProcessing;
        this.finishedWriting = finishedWriting;
    }

    public void run() {
        try {
            while (!finishedWriting.get()) {
                if (finishedProcessing.get() && filesToWrite.isEmpty()) {
                    finishedWriting.set(true);
                } else {
                    var file = filesToWrite.poll(100, TimeUnit.MILLISECONDS);
                    try {
                        if (file != null) {
                            storage.write(file);
                        }
                    } catch (InterruptedException e) {
                        out.println(STR."Error writing file: \{e.getMessage()}");
                        filesToWrite.put(file);
                    }
                }
            }
        } catch (Exception e) {
            out.println("Failed to process WRITE");
        }
    }
}
