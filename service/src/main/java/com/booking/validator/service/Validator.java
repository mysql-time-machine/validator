package com.booking.validator.service;


import com.booking.validator.service.task.ValidationTaskResult;
import com.booking.validator.service.utils.ConcurrentPipeline;
import com.booking.validator.service.utils.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by psalimov on 9/6/16.
 */
public class Validator implements Service {

    private final Supplier<? extends Supplier<CompletableFuture<ValidationTaskResult>>> taskSupplier;

    private final BiConsumer<ValidationTaskResult, Throwable> resultConsumer;

    private final ConcurrentPipeline<ValidationTaskResult> pipeline;

    public Validator(Supplier<? extends Supplier<CompletableFuture<ValidationTaskResult>>> taskSupplier, BiConsumer<ValidationTaskResult, Throwable> resultConsumer) {

        this.taskSupplier = taskSupplier;

        this.resultConsumer = resultConsumer;

        pipeline = new ConcurrentPipeline<>(taskSupplier, resultConsumer, 16);
    }

    @Override
    public void start() {

        Arrays.asList(resultConsumer,taskSupplier).forEach( x -> {if (x instanceof Service) ((Service) x).start();}  );

        pipeline.start();

    }

    @Override
    public void stop() {

        Arrays.asList(taskSupplier,resultConsumer).forEach( x -> {if (x instanceof Service) ((Service) x).stop();}  );

        pipeline.stop();

    }

}
