package org.jon.gille.microconc.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.jon.gille.microconc.guice.ExampleService;

public class ExampleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExampleService.class).in(Singleton.class);
    }
}
