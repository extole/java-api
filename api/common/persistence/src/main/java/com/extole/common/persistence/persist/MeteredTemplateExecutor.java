package com.extole.common.persistence.persist;

import java.time.Instant;

import com.extole.common.metrics.ExtoleMetricRegistry;

public class MeteredTemplateExecutor {

    private final ExtoleMetricRegistry metricRegistry;
    private final DatabaseName databaseName;

    MeteredTemplateExecutor(ExtoleMetricRegistry metricRegistry, DatabaseName databaseName) {
        this.metricRegistry = metricRegistry;
        this.databaseName = databaseName;
    }

    public <C> C execute(Class<?> className, MetricType metricType, String sql, TemplateClosure<C> closure) {
        Instant startTime = Instant.now();
        try {
            return closure.execute();
        } finally {
            metricRegistry
                .histogram(
                    "persistence." + databaseName.getName() + "." + className.getSimpleName() + "."
                        + metricType.buildName(sql))
                .update(startTime, Instant.now());
        }
    }

    public <C> C execute(Class<?> className, MetricType metricType, TemplateClosure<C> closure) {
        Instant startTime = Instant.now();
        try {
            return closure.execute();
        } finally {
            metricRegistry
                .histogram("persistence." + databaseName.getName() + "." + className.getSimpleName() + "."
                    + metricType.name().toLowerCase())
                .update(startTime, Instant.now());
        }
    }

    public enum MetricType {
        INSERT("insert"), UPDATE("update"), DELETE("delete"), READ_BY_KEYS("read_by_keys"),
        READ_BY_IDS_AS_KEYS("read_by_ids_as_keys"), READ("read"), READ_ALL("read_all"),
        CONNECTION_CALLBACK("connection_callback"), CALLABLE("callable"), STATEMENT_CALLBACK("statement_callback"),
        PREPARED_STATEMENT_CALLBACK("prepared_statement_callback");

        private static final int MAX_LENGTH = 50;
        private String name;

        MetricType(String name) {
            this.name = name;
        }

        public String buildName(String input) {
            String processedInput = input.trim();
            if (processedInput.length() > MAX_LENGTH) {
                processedInput = processedInput.substring(0, MAX_LENGTH);
            }
            return name + "." + processedInput.replaceAll(" ", "-").replaceAll("\\.", "->");
        }
    }

    public interface TemplateClosure<C> {

        C execute();
    }
}
