package org.jon.gille.microconc.guice.annotatation;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@BindingAnnotation
@Target(METHOD)
@Retention(RUNTIME)
public @interface Guarded {

    String bulkhead() default "";
}
