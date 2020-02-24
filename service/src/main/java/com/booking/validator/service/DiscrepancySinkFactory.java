package com.booking.validator.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class DiscrepancySinkFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscrepancySinkFactory.class);

    private enum Types {
        KAFKA("kafka"),
        CONSOLE("console");

        private String value;
        Types(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public static Types fromString(String text) {
            for (Types b : Types.values()) {
                if (b.getValue().equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public static interface DiscrepancySink {
        public void send(String message);
    }

    public static DiscrepancySink getDiscrepancySink(String type, Map<String, String> config) {
        switch (Types.fromString(type.trim())) {
            case CONSOLE:
                ConsoleDiscrepancySink consoleDiscrepancySink = new ConsoleDiscrepancySink(config);
                return consoleDiscrepancySink;
            case KAFKA:
                KafkaDiscrepancySink kafkaDiscrepancySink = new KafkaDiscrepancySink(config);
                return kafkaDiscrepancySink;
            default:
                return null;
        }
    }

    private static class ConsoleDiscrepancySink implements DiscrepancySink {
        public ConsoleDiscrepancySink(Map<String, String> config) {}
        @Override
        public void send(String message) {
            System.out.println(message);
        }
    }

    private static class KafkaDiscrepancySink implements DiscrepancySink {
        private String topic;
        private Producer<String, String> producer;

        public static interface Configuration{
            static final String BROKER_LIST = "broker.list";
            static final String TOPIC = "topic";
        }

        public KafkaDiscrepancySink(Map<String, String> config) {
            String brokerList = config.get(Configuration.BROKER_LIST);
            String topic = config.get(Configuration.TOPIC);
            if (brokerList.isEmpty() || topic.isEmpty()) {
                throw new RuntimeException("Kafka Discrepancy Sink needs broker-list and topic");
            }
            this.topic = topic;
            this.producer = setupKafkaProducer(brokerList);
        }

        private Producer<String, String> setupKafkaProducer(String brokerList) {
            Producer<String,String> producer = null;
            Properties properties = new Properties();
            properties.put("bootstrap.servers", brokerList);
            try {
                producer = new KafkaProducer(properties, new StringSerializer(), new StringSerializer());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return producer;
        }

        @Override
        public void send(String message) {
            try{
                producer.send(new ProducerRecord<>(topic, message));
            } catch (Exception e) {
                LOGGER.error(e.getStackTrace().toString());
            }
        }
    }
}
