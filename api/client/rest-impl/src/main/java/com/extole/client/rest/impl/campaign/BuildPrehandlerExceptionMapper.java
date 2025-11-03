package com.extole.client.rest.impl.campaign;

import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.prehandler.built.BuildPrehandlerException;
import com.extole.model.service.prehandler.built.PrehandlerIllegalCharacterInNameException;
import com.extole.model.service.prehandler.built.PrehandlerInvalidDescriptionLengthException;
import com.extole.model.service.prehandler.built.PrehandlerInvalidNameLengthException;
import com.extole.model.service.prehandler.built.PrehandlerNameAlreadyExistsException;
import com.extole.model.service.prehandler.built.PrehandlerNameMissingException;

public final class BuildPrehandlerExceptionMapper {
    private static final BuildPrehandlerExceptionMapper INSTANCE = new BuildPrehandlerExceptionMapper();

    public static BuildPrehandlerExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildPrehandlerExceptionMapper() {
    }

    public BuildPrehandlerRestException map(BuildPrehandlerException exception) {
        return internalMap(exception);
    }

    private BuildPrehandlerRestException internalMap(BuildPrehandlerException exception) {
        if (exception instanceof PrehandlerNameMissingException) {
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_NAME_MISSING)
                .withCause(exception)
                .build();
        }
        if (exception instanceof PrehandlerInvalidNameLengthException) {
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_NAME_LENGTH_OUT_OF_RANGE)
                .withCause(exception)
                .build();
        }
        if (exception instanceof PrehandlerInvalidDescriptionLengthException) {
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .withCause(exception)
                .build();
        }
        if (exception instanceof PrehandlerIllegalCharacterInNameException castedException) {
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", castedException.getName())
                .withCause(exception)
                .build();
        }
        if (exception instanceof PrehandlerNameAlreadyExistsException castedException) {
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_NAME_DUPLICATED)
                .addParameter("name", castedException.getName())
                .withCause(exception)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
            .withErrorCode(BuildPrehandlerRestException.PREHANDLER_BUILD_FAILED)
            .addParameter("prehandler_id", exception.getPrehandlerId())
            .addParameter("evaluatable_name", exception.getEvaluatableName())
            .addParameter("evaluatable", exception.getEvaluatable().toString())
            .withCause(exception)
            .build();
    }
}
