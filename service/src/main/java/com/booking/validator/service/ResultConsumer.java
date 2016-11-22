package com.booking.validator.service;

import com.booking.validator.service.task.ValidationTask;
import com.booking.validator.service.task.ValidationTaskResult;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by psalimov on 10/14/16.
 */
public class ResultConsumer implements BiConsumer<ValidationTaskResult, Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultConsumer.class);

    private final MetricRegistry registry;

    private final Consumer<ValidationTask> retrier;

    private final int retriesLimit;

    public ResultConsumer(MetricRegistry registry, int retriesLimit, Consumer<ValidationTask> retrier) {

        this.registry = registry;

        this.retrier = retrier;

        this.retriesLimit = retriesLimit;
    }

    @Override
    public void accept(ValidationTaskResult result, Throwable t) {

        if (t != null){

            LOGGER.error("Error fetching a task", t);

            registry.counter(name("tasks", "incorrect")).inc();

        } else {

            Throwable error = result.getError();

            String id = result.getId();
            String tag = result.getTag();

            if (tag == null || tag.isEmpty()) tag = "notag";

            if ( error != null ){

                LOGGER.error("Error processing the task {}", result.getTask(), error);

                registry.counter(name("tasks", tag, "failed")).inc();

            } else {

                if (result.isOk()){

                    LOGGER.info("Task {} tagged {} is processed successfully, the result is positive after {} tries", id, tag, result.getTask().getTriesCount());

                    registry.counter(name("tasks", tag, "positive")).inc();

                } else {

                    ValidationTask task = result.getTask();

                    if (task.getTriesCount() > retriesLimit) {

                        LOGGER.warn("Task {} result is still negative: {} after {} tries", result.getTask(), result.getDicrepancy(),retriesLimit+1);

                        registry.counter(name("tasks", tag, "negative")).inc();

                    } else {

                        retrier.accept(task);

                        registry.counter(name("tasks", tag, "retries")).inc();

                        LOGGER.info("Task {} tagged {} is processed successfully, the result is negative, will retry", id, tag);

                    }

                }

            }

        }

    }
}
