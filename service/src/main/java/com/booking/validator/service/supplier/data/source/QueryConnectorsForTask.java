package com.booking.validator.service.supplier.data.source;

import com.booking.validator.connectors.ActiveDataSourceConnections;
import com.booking.validator.data.Data;
import com.booking.validator.data.source.DataSource;
import com.booking.validator.task.Task;
import com.booking.validator.task.TaskComparisonResult;
import com.booking.validator.task.TaskComparisonResultV1;
import com.booking.validator.task.TaskV1;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class QueryConnectorsForTask implements Supplier<CompletableFuture<TaskComparisonResult>> {
    private final Task task;

    public QueryConnectorsForTask(Task task) {
        this.task = task;
    }

    private Supplier<Data> getSupplier(DataSource dataSource) {
        return new Supplier<Data>() {
            @Override
            public Data get() {
                return ActiveDataSourceConnections.getInstance().query(dataSource);
            }
        };
    }

    public CompletableFuture<TaskComparisonResult> get(){
        return CompletableFuture.supplyAsync(getSupplier(((TaskV1) task).getSource()))
                .thenCombineAsync(
                        CompletableFuture.supplyAsync(getSupplier(((TaskV1) task).getTarget())), task::validate)
                .exceptionally( t -> new TaskComparisonResultV1(task, null, t));
    }
}
