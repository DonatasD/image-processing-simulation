package com.donatasd;

import com.donatasd.jobs.Process;
import com.donatasd.jobs.Read;
import com.donatasd.jobs.Write;
import com.donatasd.resources.Memory;
import com.donatasd.resources.Storage;

//public class Job implements Runnable {
//
//    private final Read read;
//
//    private final Write write;
//
//    private final Process process;
//
//    public Job(Memory memory, Storage storage) {
//        this.read = new Read(memory, storage);
//        this.write = new Write(memory, storage);
//        this.process = new Process(memory);
//    }
//
//    @Override
//    public void run() {
//        while (!Main.filesToRead.isEmpty()) {
//            var fileName = Main.filesToRead.poll();
//            if (fileName == null) {
//                return;
//            }
//            var file = read.execute(fileName);
//            var proccesedFile = process.execute(file);
//            write.execute(proccesedFile);
//        }
//    }
//}
