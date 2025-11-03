package com.extole.client.rest.impl.campaign;

import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.built.InvalidClientKeyDescriptionException;
import com.extole.model.service.client.security.key.built.InvalidClientKeyNameException;
import com.extole.model.service.client.security.key.built.MissingClientKeyNameException;

public final class BuildClientKeyExceptionMapper {
    private static final BuildClientKeyExceptionMapper INSTANCE = new BuildClientKeyExceptionMapper();

    public static BuildClientKeyExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildClientKeyExceptionMapper() {
    }

    public BuildClientKeyRestException map(BuildClientKeyException exception) {
        return internalMap(exception);
    }

    private BuildClientKeyRestException internalMap(BuildClientKeyException exception) {
        if (exception instanceof InvalidClientKeyDescriptionException castedException) {
            return RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_INVALID_DESCRIPTION)
                .addParameter("description", castedException.getDescription())
                .withCause(exception)
                .build();
        }
        if (exception instanceof InvalidClientKeyNameException castedException) {
            return RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_INVALID_NAME)
                .addParameter("name", castedException.getName())
                .withCause(exception)
                .build();
        }
        if (exception instanceof MissingClientKeyNameException castedException) {
            return RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_MISSING_NAME)
                .withCause(castedException)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
            .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_BUILD_FAILED)
            .addParameter("client_key_id", exception.getClientKeyId())
            .addParameter("evaluatable_name", exception.getEvaluatableName())
            .addParameter("evaluatable", exception.getEvaluatable().toString())
            .withCause(exception)
            .build();
    }
}
