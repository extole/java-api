package com.extole.common.log.filter;

import java.util.HashSet;
import java.util.Set;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.MatchingFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;

public class FolsomFilter extends MatchingFilter {

    private static final String FOLSOM_PACKAGE_NAME = "folsom";

    private final Set<String> loggers = new HashSet<>();
    private final Set<String> formats = new HashSet<>();

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (loggers.stream().noneMatch(loggerName -> logger.getName().startsWith(loggerName))) {
            return onMismatch;
        }
        if (StringUtils.isNotBlank(format) && formats.contains(format)) {
            return onMatch;
        }
        return onMismatch;
    }

    @Override
    public void start() {
        if (!loggers.isEmpty() && !formats.isEmpty()) {
            super.start();
        }
    }

    public void addLogger(String logger) {
        if (StringUtils.isNotBlank(logger) && logger.contains(FOLSOM_PACKAGE_NAME)) {
            loggers.add(logger);
        }
    }

    public void addFormat(String format) {
        if (StringUtils.isNotBlank(format)) {
            formats.add(format);
        }
    }
}
