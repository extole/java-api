package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.PartnerEventIdFireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;

@Component
public class PartnerEventIdFireAsPersonIdentificationResponseMapper implements
    FireAsPersonIdentificationResponseMapper<com.extole.model.entity.campaign.PartnerEventIdFireAsPersonIdentification,
        com.extole.client.rest.campaign.configuration.PartnerEventIdFireAsPersonIdentification,
        PartnerEventIdFireAsPersonIdentification> {
    @Override
    public PartnerEventIdFireAsPersonIdentification
        toResponse(com.extole.model.entity.campaign.PartnerEventIdFireAsPersonIdentification identification) {
        return new PartnerEventIdFireAsPersonIdentification(
            identification.getPartnerEventKey(), identification.getValue());
    }

    @Override
    public com.extole.client.rest.campaign.configuration.PartnerEventIdFireAsPersonIdentification
        toConfiguration(com.extole.model.entity.campaign.PartnerEventIdFireAsPersonIdentification identification) {
        return new com.extole.client.rest.campaign.configuration.PartnerEventIdFireAsPersonIdentification(
            identification.getPartnerEventKey(), identification.getValue());
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.PARTNER_EVENT_ID;
    }
}
