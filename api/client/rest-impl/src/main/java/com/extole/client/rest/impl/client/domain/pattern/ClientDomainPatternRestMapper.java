package com.extole.client.rest.impl.client.domain.pattern;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternResponse;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternType;
import com.extole.id.Id;
import com.extole.model.entity.client.domain.pattern.ClientDomainPattern;

@Component
public class ClientDomainPatternRestMapper {

    public ClientDomainPatternResponse toClientDomainPatternResponse(ClientDomainPattern clientDomainPattern,
        ZoneId timeZone) {
        return new ClientDomainPatternResponse(Id.valueOf(clientDomainPattern.getId().getValue()),
            clientDomainPattern.getPattern(),
            ClientDomainPatternType.valueOf(clientDomainPattern.getType().name()),
            clientDomainPattern.getClientDomainId()
                .map(clientDomainId -> Id.valueOf(clientDomainId.getValue())),
            clientDomainPattern.getTest(),
            clientDomainPattern.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            clientDomainPattern.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

}
