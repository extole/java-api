package com.extole.common.log.execution;

import java.util.Collections;
import java.util.List;

public final class NoOpExecutionLoggerFactory {

    private static final ExecutionLogger NO_OP_INSTANCE = new NoOpExecutionLogger();

    private NoOpExecutionLoggerFactory() {
    }

    public static ExecutionLogger newInstance() {
        return NO_OP_INSTANCE;
    }

    private static final class NoOpExecutionLogger implements ExecutionLogger {

        @Override
        public ExecutionLogger log(String message, Level level) {
            // do nothing
            return this;
        }

        @Override
        public List<String> getMessages(Level... levels) {
            return Collections.emptyList();
        }

        @Override
        public List<String> getAllMessages() {
            return Collections.emptyList();
        }

    }

}
