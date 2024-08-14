package com.donatasd.resources;

import java.util.concurrent.Semaphore;

import static java.lang.System.out;

public class Storage {

    private final Memory memory;
    private final Integer readTime;
    private final Integer writeTime;

    private final Semaphore mutex = new Semaphore(1);

    public Storage(Memory memory) {
        this.memory = memory;
        this.readTime = 20;
        this.writeTime = 20;
    }

    public Storage(Memory memory, Integer readTime, Integer writeTime) {
        this.memory = memory;
        this.readTime = readTime;
        this.writeTime = writeTime;
    }

    public synchronized File read(File file) throws InterruptedException {
        try {
            out.println(STR."\{Thread.currentThread()} - READ \{file.fileName()}");
            mutex.acquire();
            memory.occupy(file.size());
            Thread.sleep(readTime);
            return new File(file.fileName(), file.size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public synchronized void write(File file) throws InterruptedException {
        try {
            out.println(STR."\{Thread.currentThread()} - WRITE \{file.fileName()}");
            mutex.acquire();
            Thread.sleep(writeTime);
            memory.release(file.size());
        } finally {
            mutex.release();
        }
    }
}
