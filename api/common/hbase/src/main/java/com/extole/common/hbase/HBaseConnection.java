package com.extole.common.hbase;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;

public class HBaseConnection implements AutoCloseable {

    private final Connection connection;
    private final Set<TableName> tablesWithCachedRegions = Sets.newHashSet();

    public HBaseConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isClosed() {
        return connection.isClosed();
    }

    public Configuration getConfiguration() {
        return connection.getConfiguration();
    }

    @WithSpan
    public synchronized Table getTable(TableName tableName) throws HBaseException, TableInactiveHBaseException {
        try {
            if (!isTableActive(tableName)) {
                throw new TableInactiveHBaseException(
                    "Table " + tableName.getNameAsString() + " either does not exist or is disabled");
            }
            Table table = connection.getTable(tableName);
            if (!tablesWithCachedRegions.contains(tableName)) {
                connection.getRegionLocator(table.getName()).getAllRegionLocations();
                tablesWithCachedRegions.add(tableName);
            }
            return table;
        } catch (IOException e) {
            throw new HBaseException("Failed to get " + tableName + " table", e);
        }
    }

    public boolean isTableActive(TableName tableName) throws HBaseException {
        try {
            return connection.getAdmin().tableExists(tableName)
                && connection.getAdmin().isTableEnabled(tableName);
        } catch (IOException e) {
            throw new HBaseException("Failed to connect to hbase to read table " + tableName, e);
        }
    }

    @Override
    public void close() throws HBaseException {
        try {
            connection.close();
        } catch (IOException e) {
            throw new HBaseException("Failed to close connection", e);
        }
    }
}
