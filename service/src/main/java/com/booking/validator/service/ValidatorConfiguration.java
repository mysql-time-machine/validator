package com.booking.validator.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by psalimov on 9/16/16.
 */
public class ValidatorConfiguration {

    public static class DataSource {

        private String name;
        private String type;

        private Map<String,String> configuration;

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Map<String, String> getConfiguration() {
            return configuration;
        }

    }

    public static class TaskSupplier {

        private String type;
        private Map<String,String> configuration;

        public String getType() {
            return type;
        }

        public Map<String, String> getConfiguration() {
            return configuration;
        }
    }

    public static class Reporter {

        private String type;
        private Map<String,String> configuration;

        public String getType() {
            return type;
        }

        public Map<String, String> getConfiguration() {
            return configuration;
        }
    }

    public static class RetryPolicy {

        @JsonProperty("delay")
        private long[] delay = {3000, 6000, 12000};

        @JsonProperty("queue_size")
        private int queueSize = 1000;

    }

    public static class DiscrepancySink {

        private String type;
        private Map<String,String> configuration;

        public String getType() {
            return type;
        }

        public Map<String, String> getConfiguration() {
            return configuration;
        }
    }

    public static ValidatorConfiguration fromFile(String path) throws IOException {

        if (path == null) throw new IllegalArgumentException("The path to file to is missing");

        StringSubstitutor stringSubstitutor = new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup());

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        String contents = stringSubstitutor.replace(new String(Files.readAllBytes(Paths.get(path))));

        return mapper.readValue(contents, ValidatorConfiguration.class);
    }

    @JsonProperty("data_sources")
    private Iterable<DataSource> dataSources;

    @JsonProperty("task_supplier")
    private TaskSupplier taskSupplier;

    @JsonProperty("reporter")
    private Reporter reporter;

    @JsonProperty("discrepancy_sink")
    private DiscrepancySink discrepancySink;

    @JsonProperty("retry_policy")
    private RetryPolicy retryPolicy = new RetryPolicy();

    public Iterable<DataSource> getDataSources() {
        return dataSources;
    }

    public TaskSupplier getTaskSupplier() {
        return taskSupplier;
    }

    public Reporter getReporter() { return reporter; }

    public DiscrepancySink getDiscrepancySink() { return discrepancySink; }

    public com.booking.validator.service.RetryPolicy getRetryPolicy() {
        return new com.booking.validator.service.RetryPolicy(
                retryPolicy.delay, retryPolicy.queueSize, retryPolicy.delay.length
        );
    }

}
