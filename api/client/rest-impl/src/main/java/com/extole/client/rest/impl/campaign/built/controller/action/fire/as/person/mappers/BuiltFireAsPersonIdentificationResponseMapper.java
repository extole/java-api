package com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person.mappers;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;
import com.extole.model.entity.campaign.built.BuiltFireAsPersonIdentification;

public interface BuiltFireAsPersonIdentificationResponseMapper<FROM extends BuiltFireAsPersonIdentification,
    RESPONSE extends FireAsPersonIdentification> {

    RESPONSE toResponse(FROM identification);

    FireAsPersonIdenticationType getType();

}
