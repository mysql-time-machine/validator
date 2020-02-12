package com.booking.validator.service;

import com.booking.validator.task.Task;
import com.booking.validator.task.TaskV1;
import com.booking.validator.utils.Service;

import java.util.function.Supplier;

/**
 * Created by psalimov on 9/16/16.
 */
public class TaskSupplier implements Supplier<Task>, Service {

    private final Supplier<Task> fetcher;

    public TaskSupplier(Supplier<Task> fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public void start() {
        if (fetcher instanceof Service) ((Service)fetcher).start();
    }

    @Override
    public Task get() {
        Task task = fetcher.get();
        TaskV1 taskV1 = (TaskV1) task;
        return taskV1;
    }
}
