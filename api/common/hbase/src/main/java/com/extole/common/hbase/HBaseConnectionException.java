package com.extole.common.hbase;

public class HBaseConnectionException extends Exception {
    public HBaseConnectionException(String message) {
        super(message);
    }

    public HBaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
