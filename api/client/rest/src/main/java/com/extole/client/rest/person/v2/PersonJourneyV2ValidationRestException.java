package com.extole.client.rest.person.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonJourneyV2ValidationRestException extends ExtoleRestException {

    public static final ErrorCode<PersonJourneyV2ValidationRestException> JOURNEY_NOT_FOUND =
        new ErrorCode<>("journey_not_found", 400, "Journey not found", "person_id", "journey_id");

    public static final ErrorCode<PersonJourneyV2ValidationRestException> UNABLE_TO_CREATE_JOURNEY =
        new ErrorCode<>("unable_to_create_journey", 500,
            "Unable to create journey. Campaign id, journey name and container are mandatory", "campaign_id",
            "journey_name", "container");

    public static final ErrorCode<PersonJourneyV2ValidationRestException> UNABLE_TO_UPDATE_JOURNEY =
        new ErrorCode<>("unable_to_update_journey", 500, "Unable to update journey", "journey_id");

    public PersonJourneyV2ValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
