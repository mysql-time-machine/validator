package com.booking.validator.service.supplier.kafka;

import com.booking.validator.task.Task;
import com.booking.validator.task.TaskV1;
import com.booking.validator.utils.Service;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

/**
 * Created by psalimov on 11/21/16.
 */
public class KafkaTaskSupplier implements Supplier<Task>, Service {

    private class Fetcher implements Runnable {

        @Override
        public void run() {

            for (;;){

                switch (state) {

                    case RUNNING:

                        int droppedRecords = 0;

                        try {

                            ConsumerRecords<String, Task> records = consumer.poll(100);

                            droppedRecords = records.count();

                            for (ConsumerRecord<String, Task> record : records) {
                                if (record.value() instanceof TaskV1) {
                                    ((TaskV1) record.value()).setId(record.key());
                                }
                                buffer.put(record.value());
                                droppedRecords--;

                                if (Thread.interrupted()) {
                                    throw new InterruptedException();
                                }
                            }

                        } catch (InterruptedException | WakeupException e){

                            LOGGER.info("Running fetcher thread interrupted. Tasks dropped: {}", droppedRecords);

                            return; // stop

                        }

                        break;

                    case PAUSED:

                        synchronized (lock){

                            try {

                                // double check cause possibly was resumed already
                                if (state == PAUSED) lock.wait();

                            } catch (InterruptedException e) {

                                LOGGER.info("Paused fetcher thread interrupted");

                                return; // stop

                            }

                        }

                        break;

                    default:
                        return;

                }

            }

        }

    }

    private static final int RUNNING = 0;
    private static final int PAUSED = 1;
    private static final int STOPPED = 2;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTaskSupplier.class);

    private final KafkaConsumer<String, Task> consumer;

    private final BlockingQueue<Task> buffer;

    private Thread fetcher;

    private final Object lock = new Object();
    private volatile int state;

    public static KafkaTaskSupplier getInstance(String topic, int bufferSize, Properties kafkaProperties){

        if (topic == null || topic.isEmpty()) throw new IllegalArgumentException("No topic is given for Kafka consumer");

        KafkaConsumer<String, Task> consumer = new KafkaConsumer<String, Task>( kafkaProperties, new StringDeserializer(), new KafkaTaskDeserializer());

        consumer.subscribe( Arrays.asList(topic) );

        return new KafkaTaskSupplier( consumer, bufferSize );

    }

    public KafkaTaskSupplier(KafkaConsumer<String, Task> consumer, int bufferSize) {
        this.consumer = consumer;
        buffer = new ArrayBlockingQueue<>(bufferSize);
    }


    @Override
    public Task get() {

        try {

            return buffer.take();

        } catch (InterruptedException e) {

            LOGGER.error("Fetching a task description interrupted");

            throw new RuntimeException(e);
        }

    }

    @Override
    public void start(){

        synchronized (lock) {

            if (fetcher != null) return;

            state = RUNNING;

            fetcher = new Thread(new Fetcher());

            fetcher.start();

        }

        LOGGER.info("Kafka task description supplying service started.");

    }

    @Override
    public void stop(){

        LOGGER.info("Stopping Kafka task description supplying service...");

        synchronized (lock) {

            try {

                state = STOPPED;

                consumer.wakeup(); // not sure if needed cause poll timeout is quite small and we have to call interrupt anyway

                fetcher.interrupt(); // interrupts blocked puts to buffer

                fetcher.join(); // wait till fully stopped

            } catch (InterruptedException e) {

                LOGGER.error("Stopping the Kafka task description supplying service interrupted");

                throw new RuntimeException(e);

            }

            fetcher = null;

        }

        LOGGER.info("Kafka task description supplying service stopped.");

    }

    @Override
    public void pause(){

        synchronized (lock){

            if (state == STOPPED || state == PAUSED) return;

            state = PAUSED;

            LOGGER.info("Kafka task description supplying service paused.");

        }

    }

    @Override
    public void resume(){

        synchronized (lock) {

            if (state == STOPPED || state == RUNNING) return;

            state = RUNNING;

            lock.notify();

            LOGGER.info("Kafka task description supplying service resumed.");

        }

    }

}
