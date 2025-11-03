package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.request;

import com.extole.client.rest.campaign.configuration.FireAsPersonIdentification;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException;
import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonIdenticationType;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationBuilder;

public interface FireAsPersonIdentificationRequestMapper<FROM extends com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification, FROM_CONFIGURATION extends FireAsPersonIdentification, BUILDER extends FireAsPersonIdentificationBuilder> {

    void upload(FROM identification, BUILDER asPersonIdentificationBuilder)
        throws CampaignControllerActionFireAsPersonIdentificationValidationRestException;

    void uploadConfiguration(FROM_CONFIGURATION identification, BUILDER asPersonIdentificationBuilder)
        throws CampaignControllerActionFireAsPersonIdentificationValidationRestException;

    FireAsPersonIdenticationType getType();

}
