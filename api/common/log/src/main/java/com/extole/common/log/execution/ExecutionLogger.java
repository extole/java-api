package com.extole.common.log.execution;

import java.util.List;

public interface ExecutionLogger {

    enum Level {
        /** Default logging level. Produces DEBUG SL4J log messages. */
        LOG,

        /** Same level as LOG, but without SL4J logging. */
        LOG_SILENT,

        /** Trace logging level. Produces TRACE SL4J log messages. */
        TRACE,
    }

    ExecutionLogger log(String message, Level level);

    List<String> getMessages(Level... levels);

    List<String> getAllMessages();

    default ExecutionLogger log(String message) {
        return log(message, Level.LOG);
    }

    default ExecutionLogger log(List<String> messages) {
        return log(messages, Level.LOG);
    }

    default ExecutionLogger trace(String message) {
        return log(message, Level.TRACE);
    }

    default ExecutionLogger trace(List<String> messages) {
        return log(messages, Level.TRACE);
    }

    default ExecutionLogger log(List<String> messages, Level level) {
        for (String message : messages) {
            log(message, level);
        }
        return this;
    }

    default List<String> getLogMessages() {
        return getMessages(Level.LOG, Level.LOG_SILENT);
    }

    default ExecutionLogger withMessagePrefix(String prefix) {
        return new ExecutionLogger() {
            @Override
            public ExecutionLogger withMessagePrefix(String messagePrefix) {
                return ExecutionLogger.this.withMessagePrefix(messagePrefix);
            }

            @Override
            public ExecutionLogger log(String message, Level level) {
                ExecutionLogger.this.log(prefixMessage(prefix, message), level);
                return this;
            }

            @Override
            public List<String> getMessages(Level... levels) {
                return ExecutionLogger.this.getMessages(levels);
            }

            @Override
            public List<String> getAllMessages() {
                return ExecutionLogger.this.getAllMessages();
            }

            private String prefixMessage(String prefix, String message) {
                return prefix + ": " + message;
            }
        };
    }

}
