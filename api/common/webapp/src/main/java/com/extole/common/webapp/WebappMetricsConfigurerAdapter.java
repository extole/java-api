package com.extole.common.webapp;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.extole.common.lang.ExtoleThreadFactory;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Configuration
@PropertySource("classpath:common-webapp-${extole.environment:lo}.properties")
class WebappMetricsConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(WebappMetricsConfigurerAdapter.class);
    private static final long REPORT_TASK_MAX_WAIT_MS = 1000L;

    @Value("${graphite.host:metrics.${extole.environment:lo}.intole.net}")
    private String graphiteHost;
    @Value("${graphite.port:2003}")
    private int graphitePort;
    @Value("${extole.environment:lo}")
    private String environment;
    @Value("${git.commit.id:unknown}")
    private String gitCommitId;
    @Value("${extole.instance.name:lo}")
    private String instanceName;
    @Value("${metrics.report.period.seconds:120}")
    private int reportingPeriodSeconds;
    @Autowired
    private ApplicationContext applicationContext;
    private GraphiteReporter graphiteReporter;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private final MetricRegistry metricRegistry = new MetricRegistry();

    WebappMetricsConfigurerAdapter() {
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.thread-states", new ThreadStatesGaugeSet());
        metricRegistry.register("jvm.fd.usage", new FileDescriptorRatioGauge());
    }

    @PostConstruct
    public void configureReporters() {
        LOG.info("reporters are being configured with host {} port {}", graphiteHost, String.valueOf(graphitePort));
        String appName = applicationContext.getApplicationName();
        String trimmedAppName;
        if (appName != null && appName.length() > 0) {
            trimmedAppName = appName.substring(1);
        } else {
            trimmedAppName = "unspecified";
        }
        String metricsPrefix = MetricRegistry.name(environment, trimmedAppName, gitCommitId, instanceName);
        LOG.info("metrics prefix for graphite reporter:" + metricsPrefix);
        Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ExtoleThreadFactory("graphite-reporter"));
        graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
            .prefixedWith(metricsPrefix)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .filter(MetricFilter.ALL)
            .scheduleOn(scheduledThreadPoolExecutor)
            .shutdownExecutorOnStop(false)
            .build(graphite);
        graphiteReporter.report();

        graphiteReporter.start(reportingPeriodSeconds, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        Runnable nextTask = scheduledThreadPoolExecutor.getQueue().peek();
        boolean shutdown = false;
        Instant shutdownStartTime = Instant.now();
        try {
            // metrics are critical only for prod environment - lo prioritizes redeploy time
            if (environment.equals("pr") && nextTask instanceof RunnableScheduledFuture<?>) {
                long timeTillNextExecution = ((RunnableScheduledFuture<?>) nextTask).getDelay(TimeUnit.MILLISECONDS);
                LOG.warn("Waiting {}ms to allow for execution of next report task", timeTillNextExecution);
                Thread.sleep(timeTillNextExecution);
            }
            scheduledThreadPoolExecutor.shutdown();
            shutdown = scheduledThreadPoolExecutor.awaitTermination(REPORT_TASK_MAX_WAIT_MS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Metric scheduler interrupted on shutdown", e);
            scheduledThreadPoolExecutor.shutdownNow();
        }
        LOG.warn("metric reporter shutdown - {} after {}ms", shutdown,
            Duration.between(shutdownStartTime, Instant.now()));
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MetricRegistry metricRegistry() {
        return metricRegistry;
    }

    @Bean
    public ExtoleMetricRegistry extoleMetricRegistry() {
        return new ExtoleMetricRegistry(metricRegistry);
    }
}
