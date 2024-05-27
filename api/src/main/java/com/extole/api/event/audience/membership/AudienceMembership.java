package com.extole.api.event.audience.membership;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface AudienceMembership {

    String getClientId();

    String getAudienceId();

    String getIdentityProfileId();

    String getPersonId();

    String getCreatedDate();

    String getUpdatedDate();

    @Nullable
    String getDeletedDate();
}
