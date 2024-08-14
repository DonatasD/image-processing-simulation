package com.donatasd;

import com.donatasd.jobs.Read;
import com.donatasd.jobs.Process;
import com.donatasd.jobs.Write;
import com.donatasd.resources.Memory;
import com.donatasd.resources.Storage;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static final BlockingQueue<String> filesToRead = new LinkedBlockingQueue<>();
    public static final BlockingQueue<File> filesToProcess = new LinkedBlockingQueue<>();
    public static final BlockingQueue<File> filesToWrite = new LinkedBlockingQueue<>();


    public static final AtomicBoolean finishedReading = new AtomicBoolean(false);
    public static final AtomicBoolean finishedProcessing = new AtomicBoolean(false);
    public static final AtomicBoolean finishedWriting = new AtomicBoolean(false);

    public static final Integer IMAGES_COUNT = 200;
    public static final Integer FILE_SIZE = 1;

    public static void main(String[] args) throws InterruptedException {
        var totalThreads = Runtime.getRuntime().availableProcessors();
        var processorThreadCount = totalThreads - 2;
        var readThreadCount = 1;
        var writeThreadCount = 1;
        Memory memory = new Memory(new AtomicInteger(100));
        Storage storage = new Storage();
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        filesToRead.addAll(Arrays.asList(IntStream.rangeClosed(0, IMAGES_COUNT).mapToObj(Integer::toString).map((val) -> STR."fileName\{val}").toArray(String[]::new)));

        var startTime = System.currentTimeMillis();
        var tasks = Stream.of(
                Collections.nCopies(readThreadCount, Executors.callable(new Read(memory, storage, filesToRead, filesToProcess, finishedReading))),
                Collections.nCopies(processorThreadCount, Executors.callable(new Process(memory, filesToProcess, filesToWrite, finishedReading, finishedProcessing))),
                Collections.nCopies(writeThreadCount, Executors.callable(new Write(memory, storage, filesToWrite, finishedProcessing, finishedWriting)))
        ).flatMap(Collection::stream).collect(Collectors.toList());
        executorService.invokeAll(tasks);
        executorService.shutdown();

        var endTime = System.currentTimeMillis();
        System.out.println(STR."TotalTime:  \{endTime - startTime}ms");
    }
}