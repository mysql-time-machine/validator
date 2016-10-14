package com.booking.validator.service;

import com.booking.validator.service.task.ValidationTaskResult;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by psalimov on 10/14/16.
 */
public class ResultConsumer implements BiConsumer<ValidationTaskResult, Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultConsumer.class);

    private final MetricRegistry registry;

    public ResultConsumer(MetricRegistry registry) {

        this.registry = registry;

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

            if ( error != null ){

                LOGGER.error("Error processing the task {} tagged {}", id, tag, error);

                registry.counter(name("tasks", tag, "failed")).inc();

            } else {

                if (result.isOk()){

                    LOGGER.info("Task {} tagged {} is processed successfully, the result is positive", id, tag);

                    registry.counter(name("tasks", tag, "positive")).inc();

                } else {

                    LOGGER.warn("Task {} tagged {} is processed successfully, the result is negative", id, tag);

                    registry.counter(name("tasks", tag, "negative")).inc();

                }

            }

        }

    }
}
