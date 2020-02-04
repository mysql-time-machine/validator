package com.booking.validator.service;

import com.booking.validator.service.task.ValidationTask;
import com.booking.validator.service.task.ValidationTaskResult;
import com.booking.validator.utils.CurrentTimestampProvider;
import com.booking.validator.utils.CurrentTimestampProviderImpl;
import com.booking.validator.utils.Retrier;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final Retrier<ValidationTask> retrier;

    private final RetryPolicy retryPolicy;

    private final CurrentTimestampProvider currentTimestampProvider;

    private final DiscrepancySinkFactory.DiscrepancySink discrepancySink;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResultConsumer(MetricRegistry registry, RetryPolicy retryPolicy, Retrier<ValidationTask> retrier, DiscrepancySinkFactory.DiscrepancySink discrepancySink) {
        this(registry, retryPolicy, retrier, new CurrentTimestampProviderImpl(), discrepancySink);
    }

    public ResultConsumer(MetricRegistry registry, RetryPolicy retryPolicy, Retrier<ValidationTask> retrier,
                          CurrentTimestampProvider currentTimestampProvider, DiscrepancySinkFactory.DiscrepancySink discrepancySink) {
        this.registry = registry;
        this.retrier = retrier;
        this.retryPolicy = retryPolicy;
        this.currentTimestampProvider = currentTimestampProvider;
        this.discrepancySink = discrepancySink;
        objectMapper.disable(MapperFeature.AUTO_DETECT_CREATORS,
                MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS,
                MapperFeature.AUTO_DETECT_IS_GETTERS);
    }

    public long getTimeElapsedMillis(long since) {
        return currentTimestampProvider.getCurrentTimeMillis() - since;
    }

    @Override
    public void accept(ValidationTaskResult result, Throwable t) {

        try {

            if (t != null) {

                LOGGER.error("Task fetching error:", t.getCause());

                registry.meter(name("tasks", "incorrect")).mark();

                return;

            }

            Throwable error = result.getError();

            String id = result.getId();
            String tag = result.getTag();

            if (tag == null || tag.isEmpty()) tag = "no-tag";

            if (error != null) {

                LOGGER.error("Task {} tagged {}, {} processing error:", id, tag, result.getTask(), error);

                registry.meter(name("tasks", tag, "failed")).mark();

                return;
            }

            ValidationTask task = result.getTask();

            if (result.isOk()) {

                LOGGER.info("Task {} tagged {} result is positive after {} tries, processed in {}s", id, tag,
                        task.getTriesCount(), getTimeElapsedMillis(task.getCreateTime())/1000);

                registry.meter(name("tasks", tag, "positive")).mark();
                registry.meter(name("tasks", tag, "positive_on_try_" + result.getTask().getTriesCount())).mark();

            } else {

                if (task.getRetriesCount() >= retryPolicy.getRetriesLimit()) {

                    LOGGER.warn("Task {} tagged {}, {} result is negative: {} after {} tries, processed in {}s",
                            id, tag, result.getTask(), result.getDicrepancy(), task.getTriesCount(),
                            getTimeElapsedMillis(task.getCreateTime())/1000);

                    registry.meter(name("tasks", tag, "negative")).mark();
                    sendDiscrepancyToSink(result);

                } else {

                    LOGGER.info("Task {} tagged {} result is negative: {}, will retry {} time in {}s",
                            id, tag, result.getDicrepancy(), task.getTriesCount(), retryPolicy.getDelayForRetry(task.getRetriesCount())/1000);

                    registry.meter(name("tasks", tag, "retries")).mark();

                    retrier.accept(task, retryPolicy.getDelayForRetry(task.getRetriesCount()));

                }

            }

        } catch (Exception e){

            // The reason is that consumer's code is being executed in a common thread pull where its exceptions are
            // silently eaten. An alternative would be to extend the concurrent pipeline with a handler of consumers exceptions.
            LOGGER.error("Error handling task completion", e);

        }
    }

    private void sendDiscrepancyToSink(ValidationTaskResult result) {
        try {
            if (discrepancySink != null) {
                discrepancySink.send(objectMapper.writeValueAsString(result));
            }
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }
}
