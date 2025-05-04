package com.extole.reporting.rest.audience.member;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AudienceMemberDownloadRestException extends ExtoleRestException {

    public static final ErrorCode<AudienceMemberDownloadRestException> AUDIENCE_MEMBER_LIST_FORMAT_NOT_SUPPORTED =
        new ErrorCode<>("audience_member_list_format_not_supported", 400, "Audience member list format not supported",
            "audience_id", "format");

    public static final ErrorCode<AudienceMemberDownloadRestException> AUDIENCE_MEMBER_LIST_CONTENT_TYPE_NOT_SUPPORTED =
        new ErrorCode<>("audience_member_list_content_type_not_supported", 400,
            "Audience member list content type not supported", "audience_id", "content_type");

    public AudienceMemberDownloadRestException(String uniqueId, ErrorCode<AudienceMemberDownloadRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
