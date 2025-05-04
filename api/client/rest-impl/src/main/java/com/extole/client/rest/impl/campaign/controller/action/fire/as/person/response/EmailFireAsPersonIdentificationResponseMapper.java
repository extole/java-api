package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.EmailFireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;

@Component
public class EmailFireAsPersonIdentificationResponseMapper implements
    FireAsPersonIdentificationResponseMapper<com.extole.model.entity.campaign.EmailFireAsPersonIdentification,
        com.extole.client.rest.campaign.configuration.EmailFireAsPersonIdentification,
        EmailFireAsPersonIdentification> {
    @Override
    public EmailFireAsPersonIdentification
        toResponse(com.extole.model.entity.campaign.EmailFireAsPersonIdentification identification) {
        return new EmailFireAsPersonIdentification(identification.getValue());
    }

    @Override
    public com.extole.client.rest.campaign.configuration.EmailFireAsPersonIdentification
        toConfiguration(com.extole.model.entity.campaign.EmailFireAsPersonIdentification identification) {
        return new com.extole.client.rest.campaign.configuration.EmailFireAsPersonIdentification(
            identification.getValue());
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.EMAIL;
    }
}
