package com.extole.client.rest.campaign.program;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignProgramLabelRenameRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignProgramLabelRenameRestException> PROGRAM_LABEL_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("program_label_contains_illegal_character", 403,
            "Program label can only contain alphanumeric, dash and underscore characters", "name");

    public static final ErrorCode<CampaignProgramLabelRenameRestException> PROGRAM_LABEL_MISSING =
        new ErrorCode<>("program_label_missing", 403, "Program label is missing");

    public static final ErrorCode<CampaignProgramLabelRenameRestException> PROGRAM_LABEL_NOT_FOUND =
        new ErrorCode<>("program_label_not_found", 403, "Program label not found", "name");

    public static final ErrorCode<CampaignProgramLabelRenameRestException> PROGRAM_LABEL_ALREADY_IN_USE =
        new ErrorCode<>("program_label_already_in_use", 403, "Program label already in use", "name");

    public CampaignProgramLabelRenameRestException(String uniqueId,
        ErrorCode<CampaignProgramLabelRenameRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
