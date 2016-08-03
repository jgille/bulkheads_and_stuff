package org.jon.gille.microconc.core.bulkhead;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class BulkheadTest {

    public static void main(String[] args) throws InterruptedException {
        Bulkhead bulkhead = Bulkhead.bulkhead().withMaximumConcurrency(2).withQueueCapacity(1).withTimeout(200).build();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            loop(bulkhead, executorService);
        } finally {
            executorService.shutdown();
            bulkhead.shutdown(1, TimeUnit.MINUTES);
        }
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    private static void loop(Bulkhead bulkhead, ExecutorService executorService) {
        IntStream.range(1, 10).forEach(i -> {
                    sleep(10);
                    executorService.submit(() -> callBulkHead(bulkhead, i));
                }
        );
    }

    private static int callBulkHead(Bulkhead bulkhead, int i) {
        try {
            Integer result = bulkhead.call(() -> {
                Thread.sleep(100);
                return i;
            });
            System.out.println(Thread.currentThread().getName() + ": " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}