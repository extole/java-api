package com.extole.common.webapp.healthcheck;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

public class ConsoleLoggingInterceptorAppender extends ConsoleAppender<ILoggingEvent> {

    @Override
    protected void append(final ILoggingEvent event) {
        LoggingInterceptor.intercept(event);
        super.append(event);
    }

}
