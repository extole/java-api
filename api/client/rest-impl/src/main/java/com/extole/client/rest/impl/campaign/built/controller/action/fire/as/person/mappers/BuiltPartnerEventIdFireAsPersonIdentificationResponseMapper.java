package com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person.mappers;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.PartnerEventIdFireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;
import com.extole.model.entity.campaign.built.BuiltPartnerEventIdFireAsPersonIdentification;

@Component
public class BuiltPartnerEventIdFireAsPersonIdentificationResponseMapper implements
    BuiltFireAsPersonIdentificationResponseMapper<BuiltPartnerEventIdFireAsPersonIdentification,
        PartnerEventIdFireAsPersonIdentification> {

    @Override
    public PartnerEventIdFireAsPersonIdentification toResponse(
        BuiltPartnerEventIdFireAsPersonIdentification identification) {
        return new PartnerEventIdFireAsPersonIdentification(
            identification.getPartnerEventKey(), identification.getValue());
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.PARTNER_EVENT_ID;
    }
}
