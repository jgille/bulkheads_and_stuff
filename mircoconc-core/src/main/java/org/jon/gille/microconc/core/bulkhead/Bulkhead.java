package org.jon.gille.microconc.core.bulkhead;

import org.jon.gille.microconc.core.bulkhead.exception.BulkheadBusyException;
import org.jon.gille.microconc.core.bulkhead.exception.BulkheadTimeoutException;

import java.time.Duration;
import java.util.concurrent.*;

public class Bulkhead {

    private final String name;
    private final ThreadPoolExecutor executor;
    private final int timeout;

    private Bulkhead(Builder builder) {
        this.name = builder.name;
        this.timeout = builder.timeout;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(builder.queueLength);
        this.executor =
                new ThreadPoolExecutor(1, builder.concurrency, 60, TimeUnit.SECONDS, queue);
    }

    public <T> T call(Callable<T> callable) throws ExecutionException {
        Future<T> future = submit(callable);
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (TimeoutException e) {
            boolean cancelled = future.cancel(false);
            throw new BulkheadTimeoutException(Duration.ofMillis(timeout), cancelled, e);
        }
    }

    private <T> Future<T> submit(Callable<T> callable) {
        try {
            return executor.submit(callable);
        } catch (RejectedExecutionException e) {
            throw new BulkheadBusyException(e);
        }
    }

    public static Builder bulkhead() {
        return new Builder();
    }

    public void shutdown(long timeout, TimeUnit timeUnit) {
        executor.shutdown();
        try {
            executor.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private String name;
        private int concurrency = 1;
        private int timeout = 1000;
        private int queueLength;

        public Builder witName(String name) {
            this.name = name;
            return this;
        }

        public Builder withMaximumConcurrency(int concurrency) {
            this.concurrency = concurrency;
            return this;
        }

        public Builder withQueueCapacity(int queueLength) {
            this.queueLength = queueLength;
            return this;
        }

        public Builder withTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Bulkhead build() {
            return new Bulkhead(this);
        }
    }

}
