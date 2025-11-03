package com.extole.common.webapp;

import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.metrics.ExtoleMetricRegistry;

@Component
public class MetricAppender extends AppenderBase<ILoggingEvent> {
    private static final String DEFAULT_METRIC_NAME = "common";
    private static Map<String, String> loggerMetricNames;
    private static ExtoleMetricRegistry metricRegistry;

    @Autowired
    public void setExtoleMetricRegistry(ExtoleMetricRegistry metricRegistry) {
        MetricAppender.metricRegistry = metricRegistry;
    }

    @Value("#{${logger.metric.names:{:}}}")
    public void setLoggerMetricNames(Map<String, String> loggerMetricNames) {
        MetricAppender.loggerMetricNames = loggerMetricNames;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String metricName = loggerMetricNames.getOrDefault(eventObject.getLoggerName(), DEFAULT_METRIC_NAME);
        metricRegistry.counter("log." + metricName + "." + eventObject.getLevel().toString().toLowerCase()).increment();
    }

    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLoggerList().forEach(new Consumer<Logger>() {

            @Override
            public void accept(Logger logger) {
                logger.addAppender(MetricAppender.this);
            }
        });

        setContext(context);
        start();
    }
}
