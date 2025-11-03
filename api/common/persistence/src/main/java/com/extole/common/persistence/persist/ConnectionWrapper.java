package com.extole.common.persistence.persist;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

class ConnectionWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionWrapper.class);

    private static final String JDBC_URL_PREFIX = "jdbc:";

    protected final DataSource dataSource;
    protected final Connection connection;
    private Optional<InetAddress> inetAddress = Optional.empty();
    private Optional<String> databaseUrl = Optional.empty();

    ConnectionWrapper(DataSource dataSource) {
        this.dataSource = dataSource;
        this.connection = DataSourceUtils.getConnection(dataSource);

        try {
            this.databaseUrl = Optional.ofNullable(connection.getMetaData().getURL());
        } catch (SQLException e) {
            LOG.warn("Could not get DB url", e);
        }
        try {
            this.inetAddress = Optional.of(InetAddress.getLocalHost());
        } catch (Exception e) {
            LOG.warn("Could not get local host", e);
        }
    }

    Optional<String> getHost() {
        return inetAddress.map(InetAddress::getHostName);
    }

    Optional<String> getHostIp() {
        return inetAddress.map(InetAddress::getHostAddress);
    }

    Optional<String> getDatabaseUrl() {
        return databaseUrl;
    }

    Optional<String> getDatabaseHostIp() {
        Optional<String> formattedDbUrl = databaseUrl.map(url -> url.replace(JDBC_URL_PREFIX, ""));
        if (formattedDbUrl.isPresent()) {
            URI dbUri;
            try {
                dbUri = new URI(formattedDbUrl.get());
            } catch (URISyntaxException e) {
                LOG.warn("Could not parse DB url", e);
                return Optional.empty();
            }
            try {
                InetAddress dbHost = InetAddress.getByName(dbUri.getHost());
                return Optional.ofNullable(dbHost.getHostAddress());
            } catch (UnknownHostException e) {
                LOG.warn("Could not resolve DB host", e);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
