package com.booking.validator.service;

import com.booking.validator.data.DataPointer;
import com.booking.validator.service.task.ValidationTask;
import com.booking.validator.service.protocol.ValidationTaskDescription;
import com.booking.validator.utils.Service;

import java.util.function.Supplier;

/**
 * Created by psalimov on 9/16/16.
 */
public class TaskSupplier implements Supplier<ValidationTask>, Service {


    private final Supplier<ValidationTaskDescription> fetcher;
    private final DataPointerFactories factory;

    public TaskSupplier(Supplier<ValidationTaskDescription> fetcher, DataPointerFactories factory) {
        this.fetcher = fetcher;
        this.factory = factory;
    }

    @Override
    public void start() {

        if (fetcher instanceof Service) ((Service)fetcher).start();

    }

    @Override
    public ValidationTask get() {

        ValidationTaskDescription description = fetcher.get();

        DataPointer source = factory.produce(description.getSource(), description.getSourceTransformation());
        DataPointer target = factory.produce(description.getTarget(), description.getTargetTransformation());

        return new ValidationTask(description.getId(),description.getTag(),source,target);
    }
}
