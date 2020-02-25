package com.booking.validator.utils;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Created by dbatheja on 25/02/20.
 */
public class NonBlockingSupplier<T> implements Supplier<T> {

    Queue<T> globalQueue = new ConcurrentLinkedQueue<T>();
    AtomicInteger atomicInteger = new AtomicInteger(0);
    public synchronized void add(T t) {
        globalQueue.add(t);
    }

    Supplier<T> fetcher = null;
    public NonBlockingSupplier(Supplier<T> fetcher) {
        this.fetcher = fetcher;
    }

    private synchronized void fetch() {
        if (atomicInteger.get() < 2) {
            CompletableFuture.supplyAsync(this.fetcher).thenAccept((t)->{this.add(t);});
            atomicInteger.incrementAndGet();
        }
    }

    @Override
    public T get() {
        fetch();
        T t = globalQueue.poll();
        if (t != null) atomicInteger.decrementAndGet();
        return t;
    }
}
