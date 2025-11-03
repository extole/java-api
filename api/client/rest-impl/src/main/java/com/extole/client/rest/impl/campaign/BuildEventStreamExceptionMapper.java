package com.extole.client.rest.impl.campaign;

import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.event.stream.built.BuildEventStreamException;
import com.extole.model.service.event.stream.built.InvalidEventStreamNameException;
import com.extole.model.service.event.stream.built.MissingEventStreamNameException;

public final class BuildEventStreamExceptionMapper {
    private static final BuildEventStreamExceptionMapper INSTANCE = new BuildEventStreamExceptionMapper();

    public static BuildEventStreamExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildEventStreamExceptionMapper() {
    }

    public EventStreamValidationRestException map(BuildEventStreamException exception) {
        return internalMap(exception);
    }

    private EventStreamValidationRestException internalMap(BuildEventStreamException exception) {
        if (exception instanceof InvalidEventStreamNameException castedException) {
            return RestExceptionBuilder.newBuilder(EventStreamValidationRestException.class)
                .withErrorCode(EventStreamValidationRestException.EVENT_STREAM_INVALID_NAME)
                .addParameter("name", castedException.getName())
                .withCause(castedException)
                .build();
        }
        if (exception instanceof MissingEventStreamNameException castedException) {
            return RestExceptionBuilder.newBuilder(EventStreamValidationRestException.class)
                .withErrorCode(EventStreamValidationRestException.EVENT_STREAM_MISSING_NAME)
                .withCause(castedException)
                .build();
        }
        return RestExceptionBuilder.newBuilder(EventStreamValidationRestException.class)
            .withErrorCode(EventStreamValidationRestException.EVENT_STREAM_BUILD_FAILED)
            .addParameter("event_stream_id", exception.getEventStreamId())
            .addParameter("evaluatable_name", exception.getEvaluatableName())
            .addParameter("evaluatable", exception.getEvaluatable())
            .withCause(exception)
            .build();
    }
}
