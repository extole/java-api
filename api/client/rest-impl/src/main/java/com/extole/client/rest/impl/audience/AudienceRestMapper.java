package com.extole.client.rest.impl.audience;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.audience.AudienceResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.id.Id;
import com.extole.model.entity.audience.Audience;

@Component
public class AudienceRestMapper {

    public AudienceResponse toAudienceResponse(Audience audience, ZoneId timeZone) {
        return new AudienceResponse(Id.valueOf(audience.getId().getValue()),
            audience.getName(),
            audience.getEnabled(),
            audience.getTags(),
            audience.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            audience.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

}
