package com.extole.common.hbase;

public class HbaseServiceRuntimeException extends RuntimeException {

    public HbaseServiceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HbaseServiceRuntimeException(String message) {
        super(message);
    }
}
