package com.extole.common.persistence.persist;

import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import net.sf.persist.RuntimeSQLException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

class ExtolePersistExceptionTranslator {

    // the SQLState field from SQLException starts with 08 for connections errors
    // this is coming from SQL ISO - https://en.wikipedia.org/wiki/SQLSTATE
    private static final String SQL_CONNECTION_ERROR_PREFIX = "08";

    private static final String HOST_NAME = "Host_Name";
    private static final String HOST_IP = "Host_Ip";
    private static final String DB_HOST_NAME = "DB_Host_Name";
    private static final String DB_HOST_RESOLVED_IP = "DB_Host_Resolved_Ip";

    private final SQLExceptionTranslator sqlExceptionTranslator;

    ExtolePersistExceptionTranslator(DataSource dataSource) {
        this.sqlExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    public RuntimeException translateExceptionIfPossible(RuntimeException e, ConnectionWrapper connection) {
        return translateException(e, connection).orElse(e);
    }

    private Optional<RuntimeException> translateException(RuntimeException e, ConnectionWrapper connection) {
        if (e instanceof RuntimeSQLException) {
            if (e.getCause() instanceof SQLException) {
                SQLException sqlException = (SQLException) e.getCause();
                String message = sqlException.getMessage();
                if (isConnectionFailure(sqlException)) {
                    message = populateMessageWithConnectionDetails(message, connection);
                }
                return Optional.ofNullable(sqlExceptionTranslator.translate(message + "\n", null, sqlException));

            }
            return Optional.of(new PersistSQLException(e));
        }
        return Optional.of(e);
    }

    private boolean isConnectionFailure(SQLException sqlException) {
        return sqlException.getSQLState() != null && sqlException.getSQLState().startsWith(SQL_CONNECTION_ERROR_PREFIX);
    }

    private String populateMessageWithConnectionDetails(String message, ConnectionWrapper connection) {
        return message +
            " - Connection details: [ " +
            HOST_NAME + ":" + connection.getHost() + ", " +
            HOST_IP + ":" + connection.getHostIp() + ", " +
            DB_HOST_NAME + ":" + connection.getDatabaseUrl() + ", " +
            DB_HOST_RESOLVED_IP + ":" + connection.getDatabaseHostIp() +
            " ]";
    }
}
