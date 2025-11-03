package com.extole.common.hbase;

import java.io.Serializable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.lang.ToString;

@Component
public class HBaseConfigurationProvider implements Serializable {
    public static final String PROPERTY_HBASE_MASTER_HOST_NAME = "emr.hbase.master.node.host.name";
    public static final String DEFAULT_HBASE_MASTER_HOST_NAME_TEMPLATE = "hbase.%s.intole.net";
    public static final String PROPERTY_HBASE_MASTER_PORT = "emr.hbase.master.node.host.port";
    public static final String DEFAULT_HBASE_MASTER_PORT = "2181";
    public static final String PROPERTY_HBASE_CONNECTION_THREADS_MAX = "hbase.hconnection.threads.max";
    public static final String DEFAULT_HBASE_CONNECTION_THREADS_MAX = "10";
    public static final String PROPERTY_HBASE_CLIENT_RETRIES_NUMBER = "hbase.client.retries.number";
    public static final String DEFAULT_HBASE_CLIENT_RETRIES_NUMBER = "5";
    public static final String PROPERTY_HBASE_RPC_TIMEOUT = "hbase.rpc.timeout";
    public static final String DEFAULT_HBASE_RPC_TIMEOUT_MS = "60000";
    public static final String PROPERTY_HBASE_CLIENT_SCANNER_TIMEOUT = "hbase.client.scanner.timeout.period";
    public static final String DEFAULT_HBASE_CLIENT_SCANNER_TIMEOUT_MS = "60000";
    public static final String PROPERTY_HBASE_CLIENT_OPERATION_TIMEOUT = "hbase.client.operation.timeout";
    public static final String DEFAULT_HBASE_CLIENT_OPERATION_TIMEOUT_MS = "60000";

    private final String masterNodeHostName;
    private final int masterNodePort;
    private final int maxConnectionThreads;
    private final int clientRetryAttempts;
    private final long rpcTimeoutMs;
    private final int clientScannerTimeoutMs;
    private final int clientOperationTimeoutMs;

    @Autowired
    public HBaseConfigurationProvider(
        @Value("${" + PROPERTY_HBASE_MASTER_HOST_NAME +
            ":" + DEFAULT_HBASE_MASTER_HOST_NAME_TEMPLATE + "}") String masterNodeHostName,
        @Value("${extole.environment:lo}") String environment,
        @Value("${" + PROPERTY_HBASE_MASTER_PORT +
            ":" + DEFAULT_HBASE_MASTER_PORT + "}") int masterNodePort,
        @Value("${" + PROPERTY_HBASE_CONNECTION_THREADS_MAX +
            ":" + DEFAULT_HBASE_CONNECTION_THREADS_MAX + "}") int maxConnectionThreads,
        @Value("${" + PROPERTY_HBASE_CLIENT_RETRIES_NUMBER +
            ":" + DEFAULT_HBASE_CLIENT_RETRIES_NUMBER + "}") int clientRetryAttempts,
        @Value("${" + PROPERTY_HBASE_RPC_TIMEOUT +
            ":" + DEFAULT_HBASE_RPC_TIMEOUT_MS + "}") long rpcTimeoutMs,
        @Value("${" + PROPERTY_HBASE_CLIENT_SCANNER_TIMEOUT +
            ":" + DEFAULT_HBASE_CLIENT_SCANNER_TIMEOUT_MS + "}") int clientScannerTimeoutMs,
        @Value("${" + PROPERTY_HBASE_CLIENT_OPERATION_TIMEOUT +
            ":" + DEFAULT_HBASE_CLIENT_OPERATION_TIMEOUT_MS + "}") int clientOperationTimeoutMs) {
        this.masterNodeHostName = String.format(masterNodeHostName, environment);
        this.masterNodePort = masterNodePort;
        this.maxConnectionThreads = maxConnectionThreads;
        this.clientRetryAttempts = clientRetryAttempts;
        this.rpcTimeoutMs = rpcTimeoutMs;
        this.clientScannerTimeoutMs = clientScannerTimeoutMs;
        this.clientOperationTimeoutMs = clientOperationTimeoutMs;
    }

    public Configuration getConfiguration() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", masterNodeHostName);
        configuration.set("hbase.zookeeper.property.clientPort", String.valueOf(masterNodePort));
        configuration.set(PROPERTY_HBASE_CONNECTION_THREADS_MAX, String.valueOf(maxConnectionThreads));
        configuration.set(PROPERTY_HBASE_CLIENT_RETRIES_NUMBER, String.valueOf(clientRetryAttempts));
        configuration.set(PROPERTY_HBASE_RPC_TIMEOUT, String.valueOf(rpcTimeoutMs));
        configuration.set(PROPERTY_HBASE_CLIENT_OPERATION_TIMEOUT, String.valueOf(clientOperationTimeoutMs));
        configuration.set(PROPERTY_HBASE_RPC_TIMEOUT, rpcTimeoutMs + "");
        configuration.set(PROPERTY_HBASE_CLIENT_SCANNER_TIMEOUT, clientScannerTimeoutMs + "");
        configuration.set(PROPERTY_HBASE_CLIENT_OPERATION_TIMEOUT, clientOperationTimeoutMs + "");
        return configuration;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
