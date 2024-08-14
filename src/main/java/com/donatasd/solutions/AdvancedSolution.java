package com.donatasd.solutions;

import com.donatasd.jobs.Process;
import com.donatasd.jobs.Read;
import com.donatasd.jobs.Write;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdvancedSolution extends Solution {

    public AdvancedSolution(Integer memorySize, Integer filesToReadCount) {
        super(memorySize, filesToReadCount);
    }

    @Override
    public void run() {
        var totalThreads = Runtime.getRuntime().availableProcessors();
        var readThreadCount = 1;
        var writeThreadCount = 1;
        var processorThreadCount = totalThreads - readThreadCount - writeThreadCount;

        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        var tasks = Stream.of(
                Collections.nCopies(readThreadCount, Executors.callable(new Read(getMemory(), getStorage(), getFilesToReadQueue(), getFilesToProcessQueue(), getFinishedReading()))),
                Collections.nCopies(processorThreadCount, Executors.callable(new Process(getProcessor(), getFilesToProcessQueue(), getFilesToWriteQueue(), getFinishedReading(), getFinishedProcessing()))),
                Collections.nCopies(writeThreadCount, Executors.callable(new Write(getMemory(), getStorage(), getFilesToWriteQueue(), getFinishedProcessing(), getFinishedWriting())))
        ).flatMap(Collection::stream).collect(Collectors.toList());

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }
}
