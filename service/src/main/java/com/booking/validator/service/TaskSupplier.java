package com.booking.validator.service;

import com.booking.validator.service.supplier.data.source.QueryConnectorsForTask;
import com.booking.validator.task.Task;
import com.booking.validator.task.TaskV1;
import com.booking.validator.utils.NonBlockingSupplier;
import com.booking.validator.utils.Service;

import java.util.function.Supplier;

/**
 * Created by psalimov on 9/16/16.
 */
public class TaskSupplier implements Supplier<QueryConnectorsForTask>, Service {

    private final Supplier<Task> fetcher;
    private final NonBlockingSupplier<Task> fetcherAsync;

    public TaskSupplier(Supplier<Task> fetcher) {
        this.fetcher = fetcher;
        this.fetcherAsync = new NonBlockingSupplier<>(fetcher);
    }

    @Override
    public void start() {
        if (fetcher instanceof Service) ((Service)fetcher).start();
    }

    @Override
    public QueryConnectorsForTask get() {
        Task task = fetcherAsync.get();
        if (task == null) {
            return new QueryConnectorsForTask(null); // dbatheja: Will return a completed future. This is done to ensure that the entire pipeline is non-blocking
        }
        TaskV1 taskV1 = (TaskV1) task;
        return new QueryConnectorsForTask(taskV1);
    }
}
