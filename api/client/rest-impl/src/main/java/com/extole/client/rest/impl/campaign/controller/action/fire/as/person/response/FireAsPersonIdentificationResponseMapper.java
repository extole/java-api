package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.response;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;

public interface FireAsPersonIdentificationResponseMapper<
    FROM extends com.extole.model.entity.campaign.FireAsPersonIdentification,
    CONFIGURATION extends com.extole.client.rest.campaign.configuration.FireAsPersonIdentification,
    RESPONSE extends FireAsPersonIdentification> {

    RESPONSE toResponse(FROM identification);

    CONFIGURATION toConfiguration(FROM identification);

    FireAsPersonIdenticationType getType();

}
