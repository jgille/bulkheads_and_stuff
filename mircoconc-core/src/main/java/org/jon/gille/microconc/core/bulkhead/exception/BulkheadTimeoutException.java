package org.jon.gille.microconc.core.bulkhead.exception;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class BulkheadTimeoutException extends RuntimeException {
    public BulkheadTimeoutException(Duration timeout, boolean cancelled, TimeoutException cause) {
        super(String.format("Call timed out (timeout=%d ms). Call cancelled=%s,",
                timeout.toMillis(), cancelled), cause);
    }
}
