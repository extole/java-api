package com.extole.common.log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PerformanceLogger {
    private final List<LogEntry> messagesWithDuration = new ArrayList<>();

    public void log(String message, long durationMillis) {
        messagesWithDuration.add(new LogEntry(message, durationMillis));
    }

    public List<String> getMessages() {
        return getMessages(entry -> true);
    }

    public List<String> getMessagesWithDurationAbove(long durationMillis) {
        return getMessages(entry -> entry.getDurationMillis() > durationMillis);
    }

    public List<String> getMessagesWithDurationBelow(long durationMillis) {
        return getMessages(entry -> entry.getDurationMillis() < durationMillis);
    }

    private List<String> getMessages(Predicate<LogEntry> filter) {
        return messagesWithDuration.stream().filter(filter).map(LogEntry::toString).collect(Collectors.toList());
    }

    private static final class LogEntry {
        private final String message;
        private final long durationMillis;

        LogEntry(String message, long durationMillis) {
            this.message = message;
            this.durationMillis = durationMillis;
        }

        public long getDurationMillis() {
            return this.durationMillis;
        }

        @Override
        public String toString() {
            return message + " (" + durationMillis + "ms)";
        }
    }
}
