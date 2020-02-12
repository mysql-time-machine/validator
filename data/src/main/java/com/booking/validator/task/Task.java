package com.booking.validator.task;


import java.util.concurrent.CompletableFuture;

public interface Task {
    CompletableFuture<TaskComparisonResult> get();
}
