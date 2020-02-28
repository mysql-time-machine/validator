package com.booking.validator.service.supplier.data.source;

import com.booking.validator.connectors.ActiveDataSourceConnections;
import com.booking.validator.data.Data;
import com.booking.validator.data.source.DataSource;
import com.booking.validator.task.Task;
import com.booking.validator.task.TaskComparisonResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by dbatheja on 20/02/20.
 */
public class QueryConnectorsForTask implements Supplier<CompletableFuture<TaskComparisonResult>> {
    private final Task task;

    public QueryConnectorsForTask(Task task) {
        this.task = task;
    }

    private static Supplier<Data> getSupplier(DataSource dataSource) {
        return () -> ActiveDataSourceConnections.getInstance().query(dataSource);
    }

    public CompletableFuture<TaskComparisonResult> get(){
        if (task == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(QueryConnectorsForTask.getSupplier(task.getSource()))
                .thenCombineAsync(
                        CompletableFuture.supplyAsync(QueryConnectorsForTask.getSupplier(task.getTarget())), task::validate)
                .exceptionally( t -> new TaskComparisonResult(task, null, t));
    }
}
