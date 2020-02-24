package com.booking.validator.service;

import com.booking.validator.connectors.ActiveDataSourceConnections;
import com.booking.validator.service.supplier.data.source.QueryConnectorsForTask;
import com.booking.validator.service.supplier.task.cli.CommandLineTaskSupplier;
import com.booking.validator.service.supplier.task.kafka.KafkaTaskSupplier;
import com.booking.validator.task.Task;
import com.booking.validator.task.TaskComparisonResult;
import com.booking.validator.utils.CommandLineArguments;
import com.booking.validator.utils.Retrier;
import com.booking.validator.utils.RetryFriendlySupplier;
import com.booking.validator.utils.Service;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by psalimov on 9/2/16.
 */
public class Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    private static final String BIGTABLE = "bigtable";
    private static final String HBASE = "hbase";
    private static String STORAGE;
    private static final String CONST = "const";
    private static final String MYSQL = "mysql";
    private static final String KAFKA = "kafka";
    private static final String CONSOLE = "console";

    public static void main(String[] args) {

        LOGGER.info("Starting validator service...");

        CommandLineArguments command = new CommandLineArguments(args);
        STORAGE = command.getUseHbase() ? HBASE: BIGTABLE;
        ValidatorConfiguration validatorConfiguration;

        try {

            validatorConfiguration = ValidatorConfiguration.fromFile( command.getConfigurationPath() );

        } catch (IOException e) {

            LOGGER.error("Failed reading configuration file", e);

            return;

        }

        new Launcher( validatorConfiguration ).launch();

        LOGGER.info("Validator service started.");

        try {

            for(;;) Thread.sleep(Long.MAX_VALUE);

        } catch (InterruptedException e) {

            LOGGER.info("Validator service interrupted.");

            return;
        }

    }

    private final ValidatorConfiguration validatorConfiguration;

    public Launcher(ValidatorConfiguration validatorConfiguration){

        this.validatorConfiguration = validatorConfiguration;
    }

    public void launch(){

        MetricRegistry registry = getMetricRegistry();

        Service reporter =  (new ReporterServiceFactory()).produce(registry, validatorConfiguration.getReporter().getType() ,validatorConfiguration.getReporter().getConfiguration());

        LOGGER.info("Starting reporting service...");

        reporter.start();

        LOGGER.info("Reporting service started.");

        initDataSourceConnections(validatorConfiguration.getDataSources());
        RetryFriendlySupplier supplier = getTaskSupplier();

        new Validator( supplier, getResultConsumer(registry, supplier) ).start();

    }

    private void initDataSourceConnections(Iterable<ValidatorConfiguration.DataSource> dataSources) {
        ActiveDataSourceConnections activeDataSourceConnections = ActiveDataSourceConnections.getInstance();
        dataSources.forEach(dataSource -> activeDataSourceConnections.add(dataSource.getName(), dataSource.getType(), dataSource.getConfiguration()));
    }

    private MetricRegistry getMetricRegistry(){

        MetricRegistry registry = new MetricRegistry();

        registry.register(name("jvm", "gc"), new GarbageCollectorMetricSet());
        registry.register(name("jvm", "threads"), new ThreadStatesGaugeSet());
        registry.register(name("jvm", "classes"), new ClassLoadingGaugeSet());
        registry.register(name("jvm", "fd"), new FileDescriptorRatioGauge());
        registry.register(name("jvm", "memory"), new MemoryUsageGaugeSet());

        return registry;

    }

    private BiConsumer<TaskComparisonResult,Throwable> getResultConsumer(MetricRegistry registry, Retrier<Task> retrier){

        return new ResultConsumer(registry, validatorConfiguration.getRetryPolicy() ,retrier, getDiscrepancySink());
    }

    private DiscrepancySinkFactory.DiscrepancySink getDiscrepancySink() {
        ValidatorConfiguration.DiscrepancySink discrepancySink = validatorConfiguration.getDiscrepancySink();
        if (discrepancySink != null) {
            return DiscrepancySinkFactory.getDiscrepancySink(discrepancySink.getType(), discrepancySink.getConfiguration());
        }
        return null;
    }


    private RetryFriendlySupplier<QueryConnectorsForTask> getTaskSupplier(){

        ValidatorConfiguration.TaskSupplier supplierDescription = validatorConfiguration.getTaskSupplier();

        Supplier<Task> descriptionSupplier;
        String type = supplierDescription.getType();

        if (KAFKA.equals(type)){

            descriptionSupplier = getKafkaTaskDescriptionSupplier( supplierDescription.getConfiguration() );

        } else if (CONSOLE.equals(type)) {

            descriptionSupplier = new CommandLineTaskSupplier();

        } else {

            throw new RuntimeException("Unknown supplier type " + type);

        }

        Supplier<QueryConnectorsForTask> taskSupplier = new TaskSupplier(descriptionSupplier);

        RetryPolicy policy = validatorConfiguration.getRetryPolicy();

        return new RetryFriendlySupplier<>(taskSupplier, policy.getQueueSize());

    }

    private KafkaTaskSupplier getKafkaTaskDescriptionSupplier(Map<String,String> configuration ){

        String topic = configuration.remove("topic");

        String bufferSizeString = configuration.remove("buffer_size");
        if (bufferSizeString == null) bufferSizeString = "1024";

        Properties kafkaProperties = new Properties();

        configuration.entrySet().stream().forEach( x -> kafkaProperties.setProperty(x.getKey(), x.getValue()) );

        return KafkaTaskSupplier.getInstance( topic, Integer.valueOf(bufferSizeString), kafkaProperties);
    }
}
