package com.donatasd.jobs;

import com.donatasd.File;
import com.donatasd.resources.Memory;
import com.donatasd.resources.Storage;

import java.util.concurrent.BlockingQueue;
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
                    var file = filesToWrite.take();
                    out.println(STR."\{Thread.currentThread()} - WRITE \{file.fileName()}");
                    storage.write(file);
                    memory.release(file.size());
                }
            }
        } catch (Exception e) {
            out.println("Failed to process WRITE");
        }
    }
}
