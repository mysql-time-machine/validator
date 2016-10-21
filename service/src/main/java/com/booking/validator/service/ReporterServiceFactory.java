package com.booking.validator.service;

import com.booking.validator.utils.Service;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by psalimov on 10/11/16.
 */
public class ReporterServiceFactory {

    private static class ReporterService implements Service {

        private final ScheduledReporter reporter;
        private final long period;

        private ReporterService(ScheduledReporter reporter, long period) {
            this.reporter = reporter;
            this.period = period;
        }

        @Override
        public void start(){
            reporter.start(period, TimeUnit.SECONDS);
        }

        @Override
        public void stop(){
            reporter.stop();
        }

    }

    private enum Property implements com.booking.validator.utils.Property {
        HOST("host"), PORT("port"), NAMESPACE("namespace"), PERIOD("period");

        private final String name;

        Property(String name) { this.name = name; }

        @Override
        public String getName(){ return name; }

    }

    public Service produce(MetricRegistry registry, String type, Map<String,String> config){

        if ("graphite".equals(type)){

            String host = Property.HOST.value(config);
            int port = Property.PORT.asInt(config);
            long period = Property.PERIOD.asLong(config);
            String namespace = Property.NAMESPACE.value(config);

            return new ReporterService(
                    GraphiteReporter.forRegistry(registry)
                        .prefixedWith(namespace)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.SECONDS)
                        .build(new Graphite(host, port)),
                    period
                );

        } else if ("console".equals(type)){

            long period = Property.PERIOD.asLong(config);

            return new ReporterService(
                    ConsoleReporter.forRegistry(registry)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.SECONDS)
                        .build(),
                    period
                );

        }

        throw new IllegalArgumentException("Unknown reporter type");

    }


}
