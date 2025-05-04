package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.PartnerUserIdFireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;

@Component
public class PartnerUserIdFireAsPersonIdentificationResponseMapper implements
    FireAsPersonIdentificationResponseMapper<com.extole.model.entity.campaign.PartnerUserIdFireAsPersonIdentification,
        com.extole.client.rest.campaign.configuration.PartnerUserIdFireAsPersonIdentification,
        PartnerUserIdFireAsPersonIdentification> {
    @Override
    public PartnerUserIdFireAsPersonIdentification
        toResponse(com.extole.model.entity.campaign.PartnerUserIdFireAsPersonIdentification identification) {
        return new PartnerUserIdFireAsPersonIdentification(identification.getValue());
    }

    @Override
    public com.extole.client.rest.campaign.configuration.PartnerUserIdFireAsPersonIdentification
        toConfiguration(com.extole.model.entity.campaign.PartnerUserIdFireAsPersonIdentification identification) {
        return new com.extole.client.rest.campaign.configuration.PartnerUserIdFireAsPersonIdentification(
            identification.getValue());
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.PARTNER_USER_ID;
    }
}
