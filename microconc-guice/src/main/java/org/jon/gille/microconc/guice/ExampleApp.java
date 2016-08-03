package org.jon.gille.microconc.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jon.gille.microconc.guice.module.ExampleModule;
import org.jon.gille.microconc.guice.module.GuardsModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExampleApp {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Injector injector = Guice.createInjector(new ExampleModule(), new GuardsModule());

        ExampleService service = injector.getInstance(ExampleService.class);

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futures.add(executorService.submit(service::hello));
            futures.add(executorService.submit(service::world));
        }

        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
