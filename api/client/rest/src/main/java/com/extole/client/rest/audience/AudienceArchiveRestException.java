package com.extole.client.rest.audience;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AudienceArchiveRestException extends ExtoleRestException {

    public static final ErrorCode<
        AudienceArchiveRestException> AUDIENCE_ASSOCIATED_WITH_AUDIENCE_MEMBERSHIP_CONTROLLER_TRIGGER =
            new ErrorCode<>("audience_associated_with_audience_membership_controller_trigger", 400,
                "Can't archive an audience associated with audience membership controller triggers", "audience_id",
                "audience_membership_controller_triggers");

    public static final ErrorCode<
        AudienceArchiveRestException> AUDIENCE_ASSOCIATED_WITH_AUDIENCE_MEMBERSHIP_EVENT_CONTROLLER_TRIGGER =
            new ErrorCode<>("audience_associated_with_audience_membership_event_controller_trigger", 400,
                "Can't archive an audience associated with audience membership event controller triggers",
                "audience_id",
                "audience_membership_event_controller_triggers");

    public static final ErrorCode<
        AudienceArchiveRestException> AUDIENCE_ASSOCIATED_WITH_CREATE_MEMBERSHIP_CONTROLLER_ACTION =
            new ErrorCode<>("audience_associated_with_create_membership_controller_action", 400,
                "Can't archive an audience associated with create membership controller actions", "audience_id",
                "create_membership_controller_actions");

    public static final ErrorCode<
        AudienceArchiveRestException> AUDIENCE_ASSOCIATED_WITH_REMOVE_MEMBERSHIP_CONTROLLER_ACTION =
            new ErrorCode<>("audience_associated_with_remove_membership_controller_action", 400,
                "Can't archive an audience associated with remove membership controller actions", "audience_id",
                "remove_membership_controller_actions");

    public static final ErrorCode<AudienceArchiveRestException> AUDIENCE_ASSOCIATED_WITH_COMPONENT_VARIABLE =
        new ErrorCode<>("audience_associated_with_component_variable", 400,
            "Can't archive an audience associated with component variables", "audience_id", "component_variables");

    public AudienceArchiveRestException(String uniqueId, ErrorCode<AudienceArchiveRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
