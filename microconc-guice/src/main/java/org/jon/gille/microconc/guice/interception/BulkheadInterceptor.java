package org.jon.gille.microconc.guice.interception;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jon.gille.microconc.core.bulkhead.Bulkhead;
import org.jon.gille.microconc.guice.annotatation.Guarded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class BulkheadInterceptor implements MethodInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Bulkhead> namedBulkheads;

    @Inject
    public BulkheadInterceptor(Set<Bulkhead> bulkheads) {
        this.namedBulkheads = new HashMap<>(bulkheads.size());
        bulkheads.forEach(b -> namedBulkheads.put(b.getName(), b));
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Guarded guarded = methodInvocation.getMethod().getAnnotation(Guarded.class);
        Bulkhead bulkhead = namedBulkheads.get(guarded.bulkhead());
        if (bulkhead == null) {
            logger.warn("No bulkhead named '{}'", guarded.bulkhead());
            return methodInvocation.proceed();
        }

        try {
            logger.info("Intercepting {} in bulkhead {}", methodInvocation.getMethod().getName(),
                    bulkhead.getName());
            return bulkhead.call(() -> invokeMethod(methodInvocation));
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    private Object invokeMethod(MethodInvocation methodInvocation) throws Exception {
        try {
           return methodInvocation.proceed();
        } catch (Exception e) {
            throw e;
        }  catch (Throwable throwable) {
            // TODO: Handle this better
            throw new RuntimeException(throwable);
        }
    }
}
