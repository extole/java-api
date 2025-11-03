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
import com.extole.model.service.webhook.built.WebhookNameDuplicateException;
import com.extole.model.service.webhook.reward.filter.supplier.RewardSupplierWebhookStateFilterNotFoundException;

public final class BuildWebhookExceptionMapper {
    private static final BuildWebhookExceptionMapper INSTANCE = new BuildWebhookExceptionMapper();

    public static BuildWebhookExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildWebhookExceptionMapper() {
    }

    public BuildWebhookRestException map(BuildWebhookException exception) {
        return internalMap(exception);
    }

    private BuildWebhookRestException internalMap(BuildWebhookException exception) {
        if (exception instanceof InvalidWebhookTagException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_TAG)
                .addParameter("tag", castedException.getTag())
                .addParameter("tag_max_length", Integer.valueOf(castedException.getTagMaxLength()))
                .withCause(exception)
                .build();
        }
        if (exception instanceof ClientKeyNotFoundWebhookException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_CLIENT_KEY_NOT_FOUND)
                .addParameter("client_key_id", castedException.getClientKeyId().getValue())
                .withCause(exception)
                .build();
        }
        if (exception instanceof InvalidWebhookDefaultMethodException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_DEFAULT_METHOD)
                .addParameter("default_method", castedException.getDefaultMethod())
                .withCause(exception)
                .build();
        }
        if (exception instanceof InvalidWebhookDescriptionException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_DESCRIPTION)
                .addParameter("description", castedException.getDescription())
                .withCause(exception)
                .build();
        }
        if (exception instanceof InvalidWebhookNameException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_NAME)
                .addParameter("name", castedException.getName())
                .withCause(exception)
                .build();
        }
        if (exception instanceof WebhookNameDuplicateException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_NAME_DUPLICATE)
                .addParameter("webhook_id", castedException.getWebhookId())
                .addParameter("name", castedException.getName())
                .withCause(exception)
                .build();
        }
        if (exception instanceof LocalOrInternalWebhookUrlException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_INVALID_LOCAL_OR_INTERNAL_URL)
                .addParameter("url", castedException.getUrl())
                .withCause(exception)
                .build();
        }
        if (exception instanceof MalformedWebhookUrlException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_MALFORMED_URL)
                .addParameter("url", castedException.getUrl())
                .withCause(exception)
                .build();
        }
        if (exception instanceof MissingWebhookNameException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_MISSING_NAME)
                .withCause(exception)
                .build();
        }
        if (exception instanceof MissingWebhookUrlException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_MISSING_URL)
                .withCause(exception)
                .build();
        }

        if (exception instanceof RewardSupplierWebhookFilterMissingRewardSupplierIdException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(
                    BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_IS_MISSING)
                .addParameter("webhook_id", castedException.getWebhookId())
                .build();
        }
        if (exception instanceof RewardSupplierWebhookStateFilterNotFoundException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", castedException.getWebhookId())
                .addParameter("filter_id", castedException.getFilterId())
                .withCause(exception)
                .build();
        }

        if (exception instanceof RewardSupplierWebhookFilterNotFoundException castedException) {
            return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", castedException.getRewardSupplierId())
                .withCause(exception)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
            .withErrorCode(BuildWebhookRestException.WEBHOOK_BUILD_FAILED)
            .addParameter("webhook_id", exception.getWebhookId())
            .addParameter("evaluatable_name", exception.getEvaluatableName())
            .addParameter("evaluatable", exception.getEvaluatable().toString())
            .withCause(exception)
            .build();
    }
}
