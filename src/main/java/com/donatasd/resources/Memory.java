package com.donatasd.resources;

import java.util.concurrent.atomic.AtomicInteger;

public class Memory {

    private final AtomicInteger capacity;
    private final AtomicInteger used;

    public Memory(AtomicInteger capacity) {
        this.capacity = capacity;
        this.used = new AtomicInteger(0);
    }

    public synchronized Integer getCapacity() {
        return capacity.get();
    }

    public synchronized Integer getUsed() {
        return used.get();
    }

    public synchronized void occupy(Integer amount) throws Exception {
        var result = this.used.updateAndGet(u -> u + amount);
        if (result > capacity.get()) {
            this.used.updateAndGet(u -> u - amount);
            throw new Exception();
        }
    }

    public synchronized void release(Integer amount) throws InterruptedException {
       this.used.updateAndGet(u -> u - amount);
    }

    public synchronized Integer getUsagePercent() throws InterruptedException {
        if (this.used.get() == 0) {
            return 0;
        }
        return (this.used.get() * 100 / this.capacity.get());
    }
}
