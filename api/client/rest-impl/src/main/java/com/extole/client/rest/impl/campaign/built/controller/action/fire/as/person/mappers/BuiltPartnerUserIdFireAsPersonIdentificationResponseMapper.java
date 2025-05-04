package com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person.mappers;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.PartnerUserIdFireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;
import com.extole.model.entity.campaign.built.BuiltPartnerUserIdFireAsPersonIdentification;

@Component
public class BuiltPartnerUserIdFireAsPersonIdentificationResponseMapper implements
    BuiltFireAsPersonIdentificationResponseMapper<BuiltPartnerUserIdFireAsPersonIdentification,
        PartnerUserIdFireAsPersonIdentification> {

    @Override
    public PartnerUserIdFireAsPersonIdentification toResponse(
        BuiltPartnerUserIdFireAsPersonIdentification identification) {
        return new PartnerUserIdFireAsPersonIdentification(identification.getValue());
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.PARTNER_USER_ID;
    }
}
