package com.extole.api.impl.person;

import com.extole.api.person.PersonAudienceMembership;
import com.extole.common.lang.ToString;

public class PersonAudienceMembershipImpl implements PersonAudienceMembership {

    private final String audienceId;

    public PersonAudienceMembershipImpl(
        com.extole.person.service.profile.audience.membership.PersonAudienceMembership personAudienceMembership) {
        audienceId = personAudienceMembership.getAudienceId().getValue();
    }

    public PersonAudienceMembershipImpl(String audienceId) {
        this.audienceId = audienceId;
    }

    @Override
    public String getAudienceId() {
        return audienceId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
