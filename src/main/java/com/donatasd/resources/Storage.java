package com.donatasd.resources;

import com.donatasd.File;
import com.donatasd.Main;

import java.util.concurrent.Semaphore;

public class Storage {

    private final Semaphore mutex = new Semaphore(1);

    private final static Integer READ_DURATION_MS = 20;

    private final static Integer WRITE_DURATION_MS = 20;

    public synchronized File read(String fileName) throws InterruptedException {
        try {
            mutex.acquire();
            Thread.sleep(READ_DURATION_MS);
            return new File(fileName, Main.FILE_SIZE);
        } finally {
            mutex.release();
        }
    }

    public synchronized void write(File file) throws InterruptedException {
        try {
            mutex.acquire();
            Thread.sleep(WRITE_DURATION_MS);
        } finally {
            mutex.release();
        }
    }
}
