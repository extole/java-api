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

    public EventStreamValidationRestException map(BuildEventStreamException e) {
        return internalMap(e);
    }

    private EventStreamValidationRestException internalMap(BuildEventStreamException e) {
        if (e instanceof InvalidEventStreamNameException) {
            InvalidEventStreamNameException ex = (InvalidEventStreamNameException) e;
            return RestExceptionBuilder.newBuilder(EventStreamValidationRestException.class)
                .withErrorCode(EventStreamValidationRestException.EVENT_STREAM_INVALID_NAME)
                .addParameter("name", ex.getName())
                .withCause(ex)
                .build();
        }
        if (e instanceof MissingEventStreamNameException) {
            MissingEventStreamNameException ex = (MissingEventStreamNameException) e;
            return RestExceptionBuilder.newBuilder(EventStreamValidationRestException.class)
                .withErrorCode(EventStreamValidationRestException.EVENT_STREAM_MISSING_NAME)
                .withCause(ex)
                .build();
        }
        return RestExceptionBuilder.newBuilder(EventStreamValidationRestException.class)
            .withErrorCode(EventStreamValidationRestException.EVENT_STREAM_BUILD_FAILED)
            .addParameter("event_stream_id", e.getEventStreamId())
            .addParameter("evaluatable_name", e.getEvaluatableName())
            .addParameter("evaluatable", e.getEvaluatable())
            .withCause(e)
            .build();
    }
}
