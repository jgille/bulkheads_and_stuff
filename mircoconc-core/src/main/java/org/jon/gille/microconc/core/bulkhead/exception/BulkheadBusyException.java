package org.jon.gille.microconc.core.bulkhead.exception;

import java.util.concurrent.RejectedExecutionException;

public class BulkheadBusyException extends RuntimeException {
    public BulkheadBusyException(RejectedExecutionException cause) {
        super("I'm busy", cause);
    }
}
