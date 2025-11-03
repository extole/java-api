package com.extole.common.log.execution;

import static com.google.common.collect.Sets.newHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExecutionLoggerFactory {

    private ExecutionLoggerFactory() {
    }

    public static ExecutionLogger newInstance() {
        return new ExecutionLoggerImpl();
    }

    public static ExecutionLogger newInstance(String name) {
        return new ExecutionLoggerImpl(name);
    }

    public static ExecutionLogger newInstance(Class<?> clazz) {
        return new ExecutionLoggerImpl(clazz);
    }

    private static class ExecutionLoggerImpl implements ExecutionLogger {

        private static final Logger LOG = LoggerFactory.getLogger(ExecutionLoggerImpl.class);

        private final Logger logger;
        private final List<Pair<String, Level>> messages = new ArrayList<>();

        ExecutionLoggerImpl() {
            this.logger = LOG;
        }

        ExecutionLoggerImpl(String name) {
            this.logger = LoggerFactory.getLogger(name);
        }

        ExecutionLoggerImpl(Class<?> clazz) {
            this.logger = LoggerFactory.getLogger(clazz);
        }

        @Override
        public ExecutionLogger log(String message, Level level) {
            messages.add(Pair.of(message, level));
            switch (level) {
                case LOG:
                    logger.debug(message);
                    break;
                case TRACE:
                    logger.trace(message);
                    break;
                default:
                    // do nothing
                    break;
            }

            return this;
        }

        @Override
        public List<String> getMessages(Level... levels) {
            Set<Level> levelSet = newHashSet(levels);
            return messages.stream().filter(message -> levelSet.contains(message.getValue())).map(Pair::getKey)
                .collect(Collectors.toList());
        }

        @Override
        public List<String> getAllMessages() {
            return messages.stream().map(Pair::getKey).collect(Collectors.toList());
        }

    }

}
