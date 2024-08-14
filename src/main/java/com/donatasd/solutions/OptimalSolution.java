package com.donatasd.solutions;

import com.donatasd.jobs.Process;
import com.donatasd.jobs.ReadWrite;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptimalSolution extends Solution {

    public OptimalSolution(Integer memorySize, Integer filesToReadCount) {
        super(memorySize, filesToReadCount);
    }

    @Override
    public void run() {
        var totalThreads = Runtime.getRuntime().availableProcessors();
        var readAndWriteThreadCount = 1;
        var processorThreadCount = totalThreads - readAndWriteThreadCount;

        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        var tasks = Stream.of(
                Collections.nCopies(readAndWriteThreadCount, Executors.callable(new ReadWrite(getMemory(), getStorage(), getFilesToReadQueue(), getFilesToProcessQueue(), getFilesToWriteQueue(), getFinishedReading(), getFinishedProcessing(), getFinishedWriting()))),
                Collections.nCopies(processorThreadCount, Executors.callable(new Process(getProcessor(), getFilesToProcessQueue(), getFilesToWriteQueue(), getFinishedReading(), getFinishedProcessing())))
        ).flatMap(Collection::stream).collect(Collectors.toList());

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }
}
