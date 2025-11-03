package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class BuildCampaignControllerRestException extends BuildCampaignRestException {

    public static final ErrorCode<BuildCampaignControllerRestException> CONTROLLER_BUILD_FAILED =
        new ErrorCode<>("controller_build_failed", 400,
            "Controller build failed", "campaign_id", "controller_id");

    public static final ErrorCode<BuildCampaignControllerRestException> TRIGGERS_WITH_DUPLICATE_NAME =
        new ErrorCode<>("triggers_with_duplicate_name", 400,
            "Triggers with the same name are not allowed", "campaign_id",
            "controller_id", "trigger_name", "other_trigger_id");

    public static final ErrorCode<BuildCampaignControllerRestException> NESTED_TRIGGERS_FOR_DISTINCT_PHASES =
        new ErrorCode<>("invalid_trigger_phase", 400,
            "All triggers in a group must share the same phase.", "campaign_id",
            "controller_id", "parent_trigger_group_name", "parent_trigger_phase", "trigger_phase");

    public static final ErrorCode<BuildCampaignControllerRestException> PARENT_TRIGGER_GROUP_NAME_NOT_A_GROUP =
        new ErrorCode<>("parent_trigger_group_name_is_not_a_group", 400,
            "Parent trigger group name does not point to a valid trigger group", "campaign_id",
            "controller_id", "parent_trigger_group_name", "parent_trigger_type");

    public static final ErrorCode<BuildCampaignControllerRestException> PARENT_TRIGGER_GROUP_NAME_DOES_NOT_EXIST =
        new ErrorCode<>("parent_trigger_group_name_does_not_exist", 400,
            "Parent trigger group name does not point to an existing trigger group", "campaign_id",
            "controller_id", "parent_trigger_group_name");

    public static final ErrorCode<BuildCampaignControllerRestException> TRIGGER_GROUP_IS_NOT_EMPTY =
        new ErrorCode<>("trigger_group_is_not_empty", 400,
            "Trigger group is not empty and cannot be deleted", "campaign_id", "controller_id");

    public static final ErrorCode<BuildCampaignControllerRestException> TRIGGER_CYCLE =
        new ErrorCode<>("trigger_belongs_to_a_cycle", 400, "Trigger belongs to a cycle", "campaign_id",
            "controller_id", "triggers_in_cycle");

    public BuildCampaignControllerRestException(String uniqueId, ErrorCode<BuildCampaignControllerRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
