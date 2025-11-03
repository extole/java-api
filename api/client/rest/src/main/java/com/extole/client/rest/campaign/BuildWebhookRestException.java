package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BuildWebhookRestException extends ExtoleRestException {

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_UNKNOWN_TYPE = new ErrorCode<>(
        "unknown_webhook_type", 400, "Specified webhook type is unknown", "type");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_INVALID_TAG =
        new ErrorCode<>("webhook_invalid_tag", 400, "Invalid webhook tag", "tag", "tag_max_length");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_MISSING_URL = new ErrorCode<>(
        "webhook_missing_url", 400, "Webhook required url is not specified");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_MISSING_NAME = new ErrorCode<>(
        "webhook_missing_name", 400, "Webhook required name is not specified");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_INVALID_NAME = new ErrorCode<>(
        "webhook_invalid_name", 400, "Webhook allowed name length is 255 containing ASCII characters", "name");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_NAME_DUPLICATE = new ErrorCode<>(
        "webhook_name_duplicate", 400, "Webhook name is already in use", "webhook_id", "name");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_INVALID_DESCRIPTION = new ErrorCode<>(
        "webhook_invalid_description", 400, "Webhook allowed description length is 1024 containing ASCII characters",
        "description");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_MALFORMED_URL = new ErrorCode<>(
        "webhook_malformed_url", 400, "Webhook url is malformed", "url");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_CLIENT_KEY_NOT_FOUND =
        new ErrorCode<>("webhook_client_key_not_found", 400, "Client key not found", "client_key_id");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_INVALID_DEFAULT_METHOD = new ErrorCode<>(
        "webhook_invalid_default_method", 400,
        "Specified webhook HTTP method is invalid, expected one of POST, GET, PUT", "default_method");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_INVALID_LOCAL_OR_INTERNAL_URL =
        new ErrorCode<>("webhook_local_or_internal_url", 400,
            "Specified webhook url is invalid because it is local or internal", "url");

    public static final ErrorCode<BuildWebhookRestException> WEBHOOK_BUILD_FAILED =
        new ErrorCode<>("webhook_build_failed", 400, "Webhook build failed",
            "webhook_id", "evaluatable_name", "evaluatable");

    public static final ErrorCode<BuildWebhookRestException> REWARD_SUPPLIER_WEBHOOK_FILTER_IS_MISSING =
        new ErrorCode<>("reward_supplier_webhook_filter_is_missing", 400,
            "Reward Supplier Webhook filter should specify reward supplier id",
            "webhook_id");

    public static final ErrorCode<BuildWebhookRestException> REWARD_SUPPLIER_WEBHOOK_FILTER_NOT_FOUND =
        new ErrorCode<>("reward_supplier_webhook_filter_not_found", 400, "Reward Supplier Webhook filter is not found",
            "webhook_id", "filter_id");

    public static final ErrorCode<BuildWebhookRestException> REWARD_SUPPLIER_NOT_FOUND = new ErrorCode<>(
        "reward_supplier_not_found", 403, "Reward supplier not found", "reward_supplier_id");

    public BuildWebhookRestException(String uniqueId, ErrorCode<BuildWebhookRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
