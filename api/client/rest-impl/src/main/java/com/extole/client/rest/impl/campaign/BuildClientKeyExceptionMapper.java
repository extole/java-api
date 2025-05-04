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

    public BuildClientKeyRestException map(BuildClientKeyException e) {
        return internalMap(e);
    }

    private BuildClientKeyRestException internalMap(BuildClientKeyException e) {
        if (e instanceof InvalidClientKeyDescriptionException) {
            InvalidClientKeyDescriptionException ex = (InvalidClientKeyDescriptionException) e;
            return RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_INVALID_DESCRIPTION)
                .addParameter("description", ex.getDescription())
                .withCause(e)
                .build();
        }
        if (e instanceof InvalidClientKeyNameException) {
            InvalidClientKeyNameException ex = (InvalidClientKeyNameException) e;
            return RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_INVALID_NAME)
                .addParameter("name", ex.getName())
                .withCause(e)
                .build();
        }
        if (e instanceof MissingClientKeyNameException) {
            MissingClientKeyNameException ex = (MissingClientKeyNameException) e;
            return RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_MISSING_NAME)
                .withCause(ex)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
            .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_BUILD_FAILED)
            .addParameter("client_key_id", e.getClientKeyId())
            .addParameter("evaluatable_name", e.getEvaluatableName())
            .addParameter("evaluatable", e.getEvaluatable().toString())
            .withCause(e)
            .build();
    }
}
