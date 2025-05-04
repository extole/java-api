package com.extole.client.rest.impl.campaign;

import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.webhook.InvalidWebhookTagException;
import com.extole.model.service.webhook.built.BuildWebhookException;
import com.extole.model.service.webhook.built.ClientKeyNotFoundWebhookException;
import com.extole.model.service.webhook.built.InvalidWebhookDefaultMethodException;
import com.extole.model.service.webhook.built.InvalidWebhookDescriptionException;
import com.extole.model.service.webhook.built.InvalidWebhookNameException;
import com.extole.model.service.webhook.built.LocalOrInternalWebhookUrlException;
import com.extole.model.service.webhook.built.MalformedWebhookUrlException;
import com.extole.model.service.webhook.built.MissingWebhookNameException;
import com.extole.model.service.webhook.built.MissingWebhookUrlException;
import com.extole.model.service.webhook.built.RewardSupplierWebhookFilterMissingRewardSupplierIdException;
import com.extole.model.service.webhook.built.RewardSupplierWebhookFilterNotFoundException;
import com.extole.model.service.webhook.reward.filter.supplier.RewardSupplierWebhookStateFilterNotFoundException;

public final class BuildWebhookExceptionMapper {
    private static final BuildWebhookExceptionMapper INSTANCE = new BuildWebhookExceptionMapper();

    public static BuildWebhookExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildWebhookExceptionMapper() {
    }

    public BuildWebhookRestException map(BuildWebhookException e) {
        return internalMap(e);
    }

    private BuildWebhookRestException internalMap(BuildWebhookException e) {
        if (e instanceof InvalidWebhookTagException) {
            InvalidWebhookTagException ex = (InvalidWebhookTagException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_TAG)
                .addParameter("tag", ex.getTag())
                .addParameter("tag_max_length", Integer.valueOf(ex.getTagMaxLength()))
                .withCause(e)
                .build();
        }
        if (e instanceof ClientKeyNotFoundWebhookException) {
            ClientKeyNotFoundWebhookException ex = (ClientKeyNotFoundWebhookException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_CLIENT_KEY_NOT_FOUND)
                .addParameter("client_key_id", ex.getClientKeyId().getValue())
                .withCause(e)
                .build();
        }
        if (e instanceof InvalidWebhookDefaultMethodException) {
            InvalidWebhookDefaultMethodException ex = (InvalidWebhookDefaultMethodException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_DEFAULT_METHOD)
                .addParameter("default_method", ex.getDefaultMethod())
                .withCause(e)
                .build();
        }
        if (e instanceof InvalidWebhookDescriptionException) {
            InvalidWebhookDescriptionException ex = (InvalidWebhookDescriptionException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_DESCRIPTION)
                .addParameter("description", ex.getDescription())
                .withCause(e)
                .build();
        }
        if (e instanceof InvalidWebhookNameException) {
            InvalidWebhookNameException ex = (InvalidWebhookNameException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_NAME)
                .addParameter("name", ex.getName())
                .withCause(e)
                .build();
        }
        if (e instanceof LocalOrInternalWebhookUrlException) {
            LocalOrInternalWebhookUrlException ex = (LocalOrInternalWebhookUrlException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_LOCAL_OR_INTERNAL_URL)
                .addParameter("url", ex.getUrl())
                .withCause(e)
                .build();
        }
        if (e instanceof MalformedWebhookUrlException) {
            MalformedWebhookUrlException ex = (MalformedWebhookUrlException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_MALFORMED_URL)
                .addParameter("url", ex.getUrl())
                .withCause(e)
                .build();
        }
        if (e instanceof MissingWebhookNameException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_MISSING_NAME)
                .withCause(e)
                .build();
        }
        if (e instanceof MissingWebhookUrlException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_MISSING_URL)
                .withCause(e)
                .build();
        }

        if (e instanceof RewardSupplierWebhookFilterMissingRewardSupplierIdException) {
            RewardSupplierWebhookFilterMissingRewardSupplierIdException ex =
                (RewardSupplierWebhookFilterMissingRewardSupplierIdException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(
                    BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_IS_MISSING)
                .addParameter("webhook_id", ex.getWebhookId())
                .build();
        }
        if (e instanceof RewardSupplierWebhookStateFilterNotFoundException) {
            RewardSupplierWebhookStateFilterNotFoundException ex =
                (RewardSupplierWebhookStateFilterNotFoundException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", ex.getWebhookId())
                .addParameter("filter_id", ex.getFilterId())
                .withCause(e)
                .build();
        }

        if (e instanceof RewardSupplierWebhookFilterNotFoundException) {
            RewardSupplierWebhookFilterNotFoundException ex =
                (RewardSupplierWebhookFilterNotFoundException) e;
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", ex.getRewardSupplierId())
                .withCause(e)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
            .withErrorCode(BuildWebhookRestException.WEBHOOK_BUILD_FAILED)
            .addParameter("webhook_id", e.getWebhookId())
            .addParameter("evaluatable_name", e.getEvaluatableName())
            .addParameter("evaluatable", e.getEvaluatable().toString())
            .withCause(e)
            .build();
    }
}
