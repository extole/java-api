package com.extole.common.persistence.persist;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * Persist specific subclass of {@code UncategorizedDataAccessException}, for Persist system errors that do
 * not match any concrete {@code org.springframework.dao} exceptions.
 */
public class PersistSQLException extends UncategorizedDataAccessException {

    public PersistSQLException(Throwable cause) {
        super(null, cause);
    }

    public PersistSQLException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
