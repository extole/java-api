package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.PersonIdFireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;

@Component
public class PersonIdFireAsPersonIdentificationResponseMapper implements
    FireAsPersonIdentificationResponseMapper<com.extole.model.entity.campaign.PersonIdFireAsPersonIdentification,
        com.extole.client.rest.campaign.configuration.PersonIdFireAsPersonIdentification,
        PersonIdFireAsPersonIdentification> {
    @Override
    public PersonIdFireAsPersonIdentification
        toResponse(com.extole.model.entity.campaign.PersonIdFireAsPersonIdentification identification) {
        return new PersonIdFireAsPersonIdentification(identification.getValue());
    }

    @Override
    public com.extole.client.rest.campaign.configuration.PersonIdFireAsPersonIdentification
        toConfiguration(com.extole.model.entity.campaign.PersonIdFireAsPersonIdentification identification) {
        return new com.extole.client.rest.campaign.configuration.PersonIdFireAsPersonIdentification(
            identification.getValue());
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.PERSON_ID;
    }
}
