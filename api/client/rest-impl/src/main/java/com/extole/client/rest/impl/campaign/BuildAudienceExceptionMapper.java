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

    public BuildAudienceRestException map(BuildAudienceException e) {
        return internalMap(e);
    }

    private BuildAudienceRestException internalMap(BuildAudienceException e) {
        if (e instanceof MissingAudienceNameException) {
            MissingAudienceNameException ex = (MissingAudienceNameException) e;
            return RestExceptionBuilder.newBuilder(BuildAudienceRestException.class)
                .withErrorCode(BuildAudienceRestException.MISSING_AUDIENCE_NAME)
                .withCause(ex)
                .build();
        }
        if (e instanceof InvalidAudienceNameException) {
            InvalidAudienceNameException ex = (InvalidAudienceNameException) e;
            return RestExceptionBuilder.newBuilder(BuildAudienceRestException.class)
                .withErrorCode(BuildAudienceRestException.INVALID_AUDIENCE_NAME)
                .addParameter("name", ex.getName())
                .withCause(e)
                .build();
        }
        return RestExceptionBuilder.newBuilder(BuildAudienceRestException.class)
            .withErrorCode(BuildAudienceRestException.AUDIENCE_BUILD_FAILED)
            .addParameter("audience_id", e.getAudienceId())
            .addParameter("evaluatable_name", e.getEvaluatableName())
            .addParameter("evaluatable", e.getEvaluatable().toString())
            .withCause(e)
            .build();
    }
}
