package com.booking.validator.service;

import com.booking.validator.data.constant.ConstDataPointerFactory;
import com.booking.validator.data.DataPointerFactory;
import com.booking.validator.data.hbase.HBaseDataPointerFactory;
import com.booking.validator.data.mysql.MysqlDataPointerFactory;
import com.booking.validator.service.protocol.ValidationTaskDescription;
import com.booking.validator.service.task.*;
import com.booking.validator.service.task.cli.CommandLineValidationTaskDescriptionSupplier;
import com.booking.validator.service.task.kafka.KafkaValidationTaskDescriptionSupplier;
import com.booking.validator.utils.CommandLineArguments;
import com.booking.validator.utils.Retrier;
import com.booking.validator.utils.RetryFriendlySupplier;
import com.booking.validator.utils.Service;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

        RetryFriendlySupplier supplier = getTaskSupplier();

        new Validator( supplier, getResultConsumer(registry, supplier) ).start();

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

    private BiConsumer<ValidationTaskResult,Throwable> getResultConsumer(MetricRegistry registry, Retrier<ValidationTask> retrier){

        return new ResultConsumer(registry, validatorConfiguration.getRetryPolicy() ,retrier, getDiscrepancySink());

    }

    private DiscrepancySinkFactory.DiscrepancySink getDiscrepancySink() {
        ValidatorConfiguration.DiscrepancySink discrepancySink = validatorConfiguration.getDiscrepancySink();
        if (discrepancySink != null) {
            return DiscrepancySinkFactory.getDiscrepancySink(discrepancySink.getType(), discrepancySink.getConfiguration());
        }
        return null;
    }


    private RetryFriendlySupplier<ValidationTask> getTaskSupplier(){

        ValidatorConfiguration.TaskSupplier supplierDescription = validatorConfiguration.getTaskSupplier();

        Supplier<ValidationTaskDescription> descriptionSupplier;
        String type = supplierDescription.getType();

        if (KAFKA.equals(type)){

            descriptionSupplier = getKafkaTaskDescriptionSupplier( supplierDescription.getConfiguration() );

        } else if (CONSOLE.equals(type)) {

            descriptionSupplier = new CommandLineValidationTaskDescriptionSupplier();

        } else {

            throw new RuntimeException("Unknown supplier type " + type);

        }

        Supplier<ValidationTask> taskSupplier = new TaskSupplier( descriptionSupplier, getDataPointers() );

        RetryPolicy policy = validatorConfiguration.getRetryPolicy();

        return new RetryFriendlySupplier<>(taskSupplier, policy.getQueueSize());

    }

    private KafkaValidationTaskDescriptionSupplier getKafkaTaskDescriptionSupplier(Map<String,String> configuration ){

        String topic = configuration.remove("topic");

        String bufferSizeString = configuration.remove("buffer_size");
        if (bufferSizeString == null) bufferSizeString = "1024";

        Properties kafkaProperties = new Properties();

        configuration.entrySet().stream().forEach( x -> kafkaProperties.setProperty(x.getKey(), x.getValue()) );

        return KafkaValidationTaskDescriptionSupplier.getInstance( topic, Integer.valueOf(bufferSizeString), kafkaProperties);
    }

    private DataPointerFactories getDataPointers(){

        Map<String, DataPointerFactory> knownFactories = new HashMap<>();

        Map<String, List<ValidatorConfiguration.DataSource>> sourcesByType = StreamSupport.stream( validatorConfiguration.getDataSources().spliterator(), false )
                .collect( Collectors.groupingBy( source -> source.getType() ) );

        knownFactories.put(HBASE, getHBaseFactory( sourcesByType.getOrDefault( STORAGE, Collections.EMPTY_LIST ) ));
        knownFactories.put(MYSQL, getMysqlFactory( sourcesByType.getOrDefault( MYSQL, Collections.EMPTY_LIST ) ));
        knownFactories.put(CONST, new ConstDataPointerFactory());

        return new DataPointerFactories(knownFactories);
    }

    private DataPointerFactory getMysqlFactory( Iterable<ValidatorConfiguration.DataSource> sources ){

        Map<String,Map<String,String>> configs = StreamSupport.stream( sources.spliterator(), false )
                .collect(Collectors.toMap(s->s.getName(), s->s.getConfiguration()));

        return MysqlDataPointerFactory.build(configs);

    }

    private DataPointerFactory getHBaseFactory( Iterable<ValidatorConfiguration.DataSource> sources ){

        Map<String,Configuration> hbaseConfigurations = StreamSupport.stream( sources.spliterator(), false )
                .collect( Collectors.toMap(
                        s -> s.getName(),
                        s-> {
                            Configuration configuration = HBaseConfiguration.create();
                            s.getConfiguration().forEach( (key,value) -> configuration.set(key,value) );
                            return configuration;
                        } ) );

        return HBaseDataPointerFactory.build(hbaseConfigurations);

    }

}
