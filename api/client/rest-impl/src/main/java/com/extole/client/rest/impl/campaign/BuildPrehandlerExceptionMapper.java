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

    public BuildPrehandlerRestException map(BuildPrehandlerException e) {
        return internalMap(e);
    }

    private BuildPrehandlerRestException internalMap(BuildPrehandlerException e) {
        if (e instanceof PrehandlerNameMissingException) {
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof PrehandlerInvalidNameLengthException) {
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_NAME_LENGTH_OUT_OF_RANGE)
                .withCause(e)
                .build();
        }
        if (e instanceof PrehandlerInvalidDescriptionLengthException) {
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .withCause(e)
                .build();
        }
        if (e instanceof PrehandlerIllegalCharacterInNameException) {
            PrehandlerIllegalCharacterInNameException ex = (PrehandlerIllegalCharacterInNameException) e;
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", ex.getName())
                .withCause(e)
                .build();
        }
        if (e instanceof PrehandlerNameAlreadyExistsException) {
            PrehandlerNameAlreadyExistsException ex = (PrehandlerNameAlreadyExistsException) e;
            return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.PREHANDLER_NAME_DUPLICATED)
                .addParameter("name", ex.getName())
                .withCause(e)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
            .withErrorCode(BuildPrehandlerRestException.PREHANDLER_BUILD_FAILED)
            .addParameter("prehandler_id", e.getPrehandlerId())
            .addParameter("evaluatable_name", e.getEvaluatableName())
            .addParameter("evaluatable", e.getEvaluatable().toString())
            .withCause(e)
            .build();
    }
}
