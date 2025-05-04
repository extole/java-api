package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BuildCampaignRestException extends ExtoleRestException {

    public static final ErrorCode<BuildCampaignRestException> CAMPAIGN_BUILD_FAILED = new ErrorCode<>(
        "campaign_build_failed", 400, "Campaign build failed", "campaign_id", "campaign_version",
        "entity", "entity_id", "evaluatable_name", "evaluatable");

    @Deprecated // TODO replace this with a dedicated validation exception in ENG-23826
    public static final ErrorCode<BuildCampaignRestException> BUILT_FRONTEND_CONTROLLER_STATE_MISCONFIGURATION =
        new ErrorCode<>("controller_state_misconfiguration", 400,
            "A controller has not properly configured state", "controller_id", "details", "entity");

    public static final ErrorCode<BuildCampaignRestException> EXPRESSION_INVALID_SYNTAX =
        new ErrorCode<>(
            "expression_invalid_syntax", 400, "Campaign build failed due expression invalid syntax", "campaign_id",
            "campaign_version", "entity", "entity_id", "evaluatable_name", "evaluatable", "description");

    public BuildCampaignRestException(String uniqueId, ErrorCode<BuildCampaignRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
