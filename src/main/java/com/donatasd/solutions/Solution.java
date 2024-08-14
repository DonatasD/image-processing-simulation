package com.donatasd.solutions;

import com.donatasd.resources.File;
import com.donatasd.resources.Memory;
import com.donatasd.resources.Processor;
import com.donatasd.resources.Storage;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public abstract class Solution implements Runnable {
    private final BlockingQueue<File> filesToReadQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<File> filesToProcessQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<File> filesToWriteQueue = new LinkedBlockingQueue<>();

    private final AtomicBoolean finishedReading = new AtomicBoolean(false);
    private final AtomicBoolean finishedProcessing = new AtomicBoolean(false);
    private final AtomicBoolean finishedWriting = new AtomicBoolean(false);

    private final Random random = new Random();

    private final Memory memory;
    private final Storage storage;
    private final Processor processor;

    public Solution(Integer memorySize, Integer filesToReadCount) {
        this.filesToReadQueue.addAll(generateFilesToRead(memorySize, filesToReadCount));
        var memory = new Memory(memorySize);
        this.memory = memory;
        this.storage = new Storage(memory);
        this.processor = new Processor(memory);
    }

    private List<File> generateFilesToRead(Integer memorySize, Integer count) {
        return IntStream
                .rangeClosed(0, count)
                .mapToObj(Integer::toString)
                .map((val) -> new File(STR."fileName\{val}", random.nextInt(memorySize / 1000 + 1)))
                .toList();
    }

    public BlockingQueue<File> getFilesToReadQueue() {
        return filesToReadQueue;
    }

    public BlockingQueue<File> getFilesToProcessQueue() {
        return filesToProcessQueue;
    }

    public BlockingQueue<File> getFilesToWriteQueue() {
        return filesToWriteQueue;
    }

    public AtomicBoolean getFinishedReading() {
        return finishedReading;
    }

    public AtomicBoolean getFinishedProcessing() {
        return finishedProcessing;
    }

    public AtomicBoolean getFinishedWriting() {
        return finishedWriting;
    }

    public Memory getMemory() {
        return memory;
    }

    public Storage getStorage() {
        return storage;
    }

    public Processor getProcessor() {
        return processor;
    }
}
