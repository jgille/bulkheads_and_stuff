package org.jon.gille.microconc.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import org.aopalliance.intercept.MethodInterceptor;
import org.jon.gille.microconc.core.bulkhead.Bulkhead;
import org.jon.gille.microconc.guice.annotatation.Guarded;
import org.jon.gille.microconc.guice.interception.BulkheadInterceptor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GuardsModule extends AbstractModule {
    @Override
    protected void configure() {
        Set<Bulkhead> bulkheads = new HashSet<>(
                Arrays.asList(
                        Bulkhead.bulkhead()
                                .witName("single")
                                .withMaximumConcurrency(2)
                                .withQueueCapacity(2)
                                .withTimeout(1000)
                        .build(),
                        Bulkhead.bulkhead()
                                .witName("multi")
                                .withMaximumConcurrency(4)
                                .withQueueCapacity(10)
                                .withTimeout(500)
                                .build()
                )
        );

        MethodInterceptor interceptor = new BulkheadInterceptor(bulkheads);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Guarded.class).and(notSynthetic()), interceptor);
    }

    private Matcher<AnnotatedElement> notSynthetic() {
        return new AbstractMatcher<AnnotatedElement>() {
            @Override
            public boolean matches(AnnotatedElement annotatedElement) {
                return annotatedElement instanceof Method && !((Method) annotatedElement).isSynthetic();
            }
        };
    }
}
