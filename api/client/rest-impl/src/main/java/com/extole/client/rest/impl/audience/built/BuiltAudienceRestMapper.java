package com.extole.client.rest.impl.audience.built;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.audience.built.BuiltAudienceResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.id.Id;
import com.extole.model.entity.audience.built.BuiltAudience;

@Component
public class BuiltAudienceRestMapper {

    public BuiltAudienceResponse toBuiltAudienceResponse(BuiltAudience audience, ZoneId timeZone) {
        return new BuiltAudienceResponse(Id.valueOf(audience.getId().getValue()),
            audience.getName(),
            audience.isEnabled(),
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
