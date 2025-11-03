package com.extole.common.hbase;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.hadoop.hbase.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.spring.StartFirstStopLast;

@Component
public class HBaseTableFactory implements StartFirstStopLast, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseTableFactory.class);

    private final ExtoleMetricRegistry metricRegistry;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final AtomicReference<HBaseConnection> connection = new AtomicReference<>();
    private final AtomicReference<HBaseConnectionFactory> hBaseConnectionFactory = new AtomicReference<>();
    private final HBaseConfigurationProvider hBaseConfigurationProvider;

    @Autowired
    public HBaseTableFactory(
        HBaseConfigurationProvider hBaseConfigurationProvider,
        ExtoleMetricRegistry metricRegistry) {
        this.hBaseConfigurationProvider = hBaseConfigurationProvider;
        this.metricRegistry = metricRegistry;
    }

    @PostConstruct
    @Override
    public void start() {
        try {
            getConnection();
        } catch (HbaseServiceRuntimeException e) {
            LOG.error("Failed to initialize HBaseTableFactory", e);
        }
    }

    @Override
    public void stop() {
        shutdown.set(true);
        try {
            if (connection.get() != null) {
                connection.get().close();
            }
        } catch (HBaseException e) {
            throw new HbaseServiceRuntimeException("Failed to close connections", e);
        }
    }

    @WithSpan
    public HBaseTable create(String name) {
        try {
            TableName tableName = TableName.valueOf(name);
            if (!isTableActive(tableName)) {
                throw new RuntimeException(
                    "Table " + tableName.getNameAsString() + " either does not exist or is disabled");
            }
            Instant start = Instant.now();
            try {
                getConnection().getConnection().getRegionLocator(tableName).getAllRegionLocations();
            } finally {
                LOG.debug("HBase table {} initialization took {} ms", name,
                    Long.valueOf(Instant.now().toEpochMilli() - start.toEpochMilli()));
            }
            return new HBaseTable(getConnection().getConnection(), tableName, metricRegistry);
        } catch (IOException e) {
            throw new HbaseServiceRuntimeException("Failed to initialize table " + name, e);
        }
    }

    @Override
    public void close() {
        stop();
    }

    private boolean isTableActive(TableName tableName) throws IOException {
        return getConnection().getConnection().getAdmin().tableExists(tableName)
            && getConnection().getConnection().getAdmin().isTableEnabled(tableName);
    }

    private HBaseConnection getConnection() {
        if (connection.get() == null) {
            connection.compareAndSet(null, createConnection());
        } else {
            HBaseConnection currentConnection = connection.get();
            if (currentConnection.isClosed()) {
                connection.compareAndSet(currentConnection, createConnection());
            }
        }

        return connection.get();
    }

    private HBaseConnection createConnection() {
        if (shutdown.get()) {
            throw new HbaseServiceRuntimeException("webapp is shutting down, cancelling hbase connection creation");
        }
        Instant start = Instant.now();
        try {
            return getHbaseConnectionFactory().getOrCreateSharedConnection();
        } catch (HBaseConnectionException e) {
            throw new HbaseServiceRuntimeException("Failed to establish connection", e);
        } finally {
            LOG.debug("HBase connection creation took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - start.toEpochMilli()));
        }
    }

    private HBaseConnectionFactory getHbaseConnectionFactory() {
        if (hBaseConnectionFactory.get() == null) {
            synchronized (hBaseConfigurationProvider) {
                hBaseConnectionFactory.compareAndSet(null, new HBaseConnectionFactory(hBaseConfigurationProvider));
            }
        }
        return hBaseConnectionFactory.get();
    }
}
