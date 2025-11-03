package com.extole.common.hbase;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.Lists;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseConnectionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseConnectionFactory.class);

    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private final HBaseConfigurationProvider configurationProvider;
    private HBaseConnection connection;

    public HBaseConnectionFactory(HBaseConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public synchronized HBaseConnection getOrCreateSharedConnection()
        throws HBaseConnectionException {
        if (isShutdown.get()) {
            throw new HBaseConnectionException("Cannot create connection after shutdown.");
        }
        if (connection == null || connection.isClosed()) {
            connection = connect();
        }
        return connection;
    }

    @WithSpan
    public HBaseConnection connect() throws HBaseConnectionException {
        Configuration configuration = configurationProvider.getConfiguration();
        LOG.info("Opening hbase connection for configuration: {}", Lists.newArrayList(configuration.iterator()));
        try {
            return new HBaseConnection(ConnectionFactory.createConnection(configuration));
        } catch (IOException e) {
            throw new HBaseConnectionException(
                "Failed to establish connection with hbase " + configuration, e);
        }
    }

    public synchronized void shutdown() {
        isShutdown.set(true);
        if (connection != null && !connection.isClosed()) {
            try {
                LOG.debug("Closing hbase connection.");
                connection.close();
            } catch (HBaseException e) {
                LOG.error("Failed to close hbase connection", e);
            }
        }
    }
}
