package com.extole.client.rest.campaign.controller.trigger.client.domain;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerClientDomainValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerClientDomainValidationRestException> CLIENT_DOMAIN_NOT_FOUND =
            new ErrorCode<>("client_domain_not_found", 400, "Was unable to find client domain with ID",
                "client_domain_id");

    public static final ErrorCode<
        CampaignControllerTriggerClientDomainValidationRestException> MISSING_CLIENT_DOMAIN_IDS =
            new ErrorCode<>("missing_client_domain_ids", 400, "Missing client domaind IDs");

    public CampaignControllerTriggerClientDomainValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
