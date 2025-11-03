package com.extole.client.rest.campaign.component;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentUpgradeRestException extends ExtoleRestException {

    public static final ErrorCode<
        ComponentUpgradeRestException> NO_UPGRADES_AVAILABLE = new ErrorCode<>(
            "component_upgrade_no_upgrade_available", 404,
            "There are no available upgrades for this component.",
            "component_id");
    public static final ErrorCode<ComponentUpgradeRestException> COMPONENTS_WITH_SOCKET_SETTINGS_NOT_ALLOWED =
        new ErrorCode<>(
            "component_upgrade_with_socket_settings_not_allowed", 400,
            "Upgrade on components with socket settings unsupported.",
            "socket_settings_name",
            "component_id");

    public static final ErrorCode<ComponentUpgradeRestException> ROOT_COMPONENT_UPGRADE_NOT_ALLOWED = new ErrorCode<>(
        "root_component_upgrade_not_allowed",
        400,
        "Root component is not allowed for upgrade",
        "component_id");

    public ComponentUpgradeRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
