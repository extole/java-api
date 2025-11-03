package com.extole.common.webapp.healthcheck;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

public class LoggingInterceptorAppender extends RollingFileAppender<ILoggingEvent> {

    @Override
    protected void append(final ILoggingEvent event) {
        LoggingInterceptor.intercept(event);
        super.append(event);
    }

}
