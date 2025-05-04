package com.extole.client.rest.campaign.component.setting;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SettingRestException extends ExtoleRestException {

    public static final ErrorCode<SettingRestException> SOCKET_NOT_FOUND =
        new ErrorCode<>("socket_not_found", 400, "Socket is not found", "campaign_id", "socket_name");

    public static final ErrorCode<SettingRestException> SETTING_NOT_FOUND =
        new ErrorCode<>("setting_not_found", 400, "Setting is not found", "component_id", "setting_name");

    public static final ErrorCode<SettingRestException> VARIABLE_BATCH_UPDATE_HAS_DUPLICATED_VARIABLE_NAME =
        new ErrorCode<>("variable_batch_update_has_duplicated_variable_name", 400,
            "Batch update request has duplicated variable name for an absolute name", "absolute_name", "variable_name");

    public static final ErrorCode<SettingRestException> VARIABLE_BATCH_UPDATE_MISSING_TYPE =
        new ErrorCode<>("variable_batch_update_missing_type", 400, "Missing variables types", "variables");

    public SettingRestException(String uniqueId,
        ErrorCode<SettingRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
