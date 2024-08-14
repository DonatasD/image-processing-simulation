package com.donatasd.resources;

import static java.lang.System.out;

public class Processor {

    private final Memory memory;
    private final Integer processTime;

    public Processor(Memory memory) {
        this.memory = memory;
        this.processTime = 460;
    }

    public Processor(Memory memory, Integer processTime) {
        this.memory = memory;
        this.processTime = processTime;
    }

    public File processFile(File file) {
        try {
            out.println(STR."\{Thread.currentThread()} - PROCESS \{file.fileName()}");
            var processedFile = new File(STR."\{file.fileName()}_processed", file.size());
            memory.occupy(file.size());
            Thread.sleep(processTime);
            memory.release(processedFile.size());
            return processedFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
