package com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person.mappers;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.EmailFireAsPersonIdentification;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;
import com.extole.model.entity.campaign.built.BuiltEmailFireAsPersonIdentification;

@Component
public class BuiltEmailFireAsPersonIdentificationResponseMapper
    implements BuiltFireAsPersonIdentificationResponseMapper<BuiltEmailFireAsPersonIdentification,
        EmailFireAsPersonIdentification> {

    @Override
    public EmailFireAsPersonIdentification toResponse(BuiltEmailFireAsPersonIdentification identification) {
        return new EmailFireAsPersonIdentification(identification.getValue());
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.EMAIL;
    }
}
