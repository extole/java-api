package com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person.mappers;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.PersonIdFireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;
import com.extole.model.entity.campaign.built.BuiltPersonIdFireAsPersonIdentification;

@Component
public class BuiltPersonIdFireAsPersonIdentificationResponseMapper implements
    BuiltFireAsPersonIdentificationResponseMapper<BuiltPersonIdFireAsPersonIdentification,
        PersonIdFireAsPersonIdentification> {

    @Override
    public PersonIdFireAsPersonIdentification toResponse(BuiltPersonIdFireAsPersonIdentification identification) {
        return new PersonIdFireAsPersonIdentification(identification.getValue());
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.PERSON_ID;
    }
}
