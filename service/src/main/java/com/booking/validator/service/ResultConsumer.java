package com.booking.validator.service;

import com.booking.validator.data.DataPointerFactory;
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

        try {

            if (t != null) {

                if ( !(t.getCause() instanceof DataPointerFactory.InvalidDataPointerDescription) ) {
                    LOGGER.error("Task fetching error:", t);
                }

                registry.counter(name("tasks", "incorrect")).inc();

                return;

            }

            Throwable error = result.getError();

            String id = result.getId();
            String tag = result.getTag();

            if (tag == null || tag.isEmpty()) tag = "no-tag";

            if (error != null) {

                LOGGER.error("Task {} tagged {}, {} processing error:", id, tag, result.getTask(), error);

                registry.counter(name("tasks", tag, "failed")).inc();

                return;
            }

            if (result.isOk()) {

                LOGGER.info("Task {} tagged {} result is positive after {} tries", id, tag, result.getTask().getTriesCount());

                registry.counter(name("tasks", tag, "positive")).inc();

            } else {

                ValidationTask task = result.getTask();

                if (task.getTriesCount() > retriesLimit) {

                    LOGGER.warn("Task {} tagged {}, {} result is negative: {} after {} tries", id, tag, result.getTask(), result.getDicrepancy(), retriesLimit + 1);

                    registry.counter(name("tasks", tag, "negative")).inc();

                } else {

                    retrier.accept(task);

                    registry.counter(name("tasks", tag, "retries")).inc();

                    LOGGER.info("Task {} tagged {} result is negative, will retry", id, tag);

                }

            }


        } catch (Exception e){

            // The reason is that consumer's code is being executed in a common thread pull where its exceptions are
            // silently eaten. An alternative would be to extend the concurrent pipeline with a handler of consumers exceptions.
            LOGGER.error("Error handling task completion", e);

        }
    }
}
