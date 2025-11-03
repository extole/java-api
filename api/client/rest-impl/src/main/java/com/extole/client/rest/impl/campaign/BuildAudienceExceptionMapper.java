package com.extole.client.rest.impl.campaign;

import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.audience.built.BuildAudienceException;
import com.extole.model.service.audience.built.InvalidAudienceNameException;
import com.extole.model.service.audience.built.MissingAudienceNameException;

public final class BuildAudienceExceptionMapper {
    private static final BuildAudienceExceptionMapper INSTANCE = new BuildAudienceExceptionMapper();

    public static BuildAudienceExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildAudienceExceptionMapper() {
    }

    public BuildAudienceRestException map(BuildAudienceException exception) {
        return internalMap(exception);
    }

    private BuildAudienceRestException internalMap(BuildAudienceException exception) {
        if (exception instanceof MissingAudienceNameException castedException) {
            return RestExceptionBuilder.newBuilder(BuildAudienceRestException.class)
                .withErrorCode(BuildAudienceRestException.MISSING_AUDIENCE_NAME)
                .withCause(castedException)
                .build();
        }
        if (exception instanceof InvalidAudienceNameException castedException) {
            return RestExceptionBuilder.newBuilder(BuildAudienceRestException.class)
                .withErrorCode(BuildAudienceRestException.INVALID_AUDIENCE_NAME)
                .addParameter("name", castedException.getName())
                .withCause(exception)
                .build();
        }
        return RestExceptionBuilder.newBuilder(BuildAudienceRestException.class)
            .withErrorCode(BuildAudienceRestException.AUDIENCE_BUILD_FAILED)
            .addParameter("audience_id", exception.getAudienceId())
            .addParameter("evaluatable_name", exception.getEvaluatableName())
            .addParameter("evaluatable", exception.getEvaluatable().toString())
            .withCause(exception)
            .build();
    }
}
