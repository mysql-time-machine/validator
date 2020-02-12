package com.booking.validator.service.supplier.kafka;

import com.booking.validator.task.Task;
import com.booking.validator.task.TaskV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by psalimov on 10/3/16.
 */
public class KafkaTaskDeserializer implements Deserializer<Task> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTaskDeserializer.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> map, boolean b) {}

    @Override
    public Task deserialize(String id, byte[] bytes) {

        try {

            Task task = mapper.readValue(bytes, TaskV1.class);

            return task;

        } catch (IOException e) {

            LOGGER.error("Error deserializing task description", e);

            return null;

        }

    }

    @Override
    public void close() {}
}
