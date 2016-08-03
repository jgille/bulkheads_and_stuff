package org.jon.gille.microconc.guice;

import org.jon.gille.microconc.guice.annotatation.Guarded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class ExampleService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicInteger hellos = new AtomicInteger();
    private final AtomicInteger worlds = new AtomicInteger();

    @Guarded(bulkhead = "single")
    public String hello() throws InterruptedException {
        Thread.sleep(200);
        logger.info("Hello {}", hellos.incrementAndGet());
        return "Hello ";
    }

    @Guarded(bulkhead = "multi")
    public String world() {
        logger.info("World {}", worlds.incrementAndGet());
        return "world";
    }
}
