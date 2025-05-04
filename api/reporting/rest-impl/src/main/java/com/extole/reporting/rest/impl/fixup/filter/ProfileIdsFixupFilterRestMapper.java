package com.extole.reporting.rest.impl.fixup.filter;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.id.Id;
import com.extole.reporting.entity.fixup.filter.ProfileIdsFixupFilter;
import com.extole.reporting.rest.fixup.filter.FixupFilterType;
import com.extole.reporting.rest.fixup.filter.ProfileIdsFixupFilterResponse;

@Component
public class ProfileIdsFixupFilterRestMapper
    implements FixupFilterRestMapper<ProfileIdsFixupFilter, ProfileIdsFixupFilterResponse> {

    @Override
    public ProfileIdsFixupFilterResponse toResponse(ProfileIdsFixupFilter filter) {
        return new ProfileIdsFixupFilterResponse(filter.getId().getValue(),
            FixupFilterType.valueOf(filter.getType().name()),
            filter.getProfileIds().stream().map(Id::getValue).collect(Collectors.toSet()));
    }

    @Override
    public com.extole.reporting.entity.fixup.filter.FixupFilterType getType() {
        return com.extole.reporting.entity.fixup.filter.FixupFilterType.PROFILE_IDS;
    }
}
