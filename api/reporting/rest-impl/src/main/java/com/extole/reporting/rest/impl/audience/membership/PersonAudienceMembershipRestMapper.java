package com.extole.reporting.rest.impl.audience.membership;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.id.Id;
import com.extole.person.service.profile.audience.membership.PersonAudienceMembership;
import com.extole.reporting.rest.audience.membership.v4.PersonAudienceMembershipV4Response;

@Component
public class PersonAudienceMembershipRestMapper {

    public PersonAudienceMembershipV4Response
        toPersonAudienceMembershipResponse(PersonAudienceMembership personAudienceMembership, ZoneId timeZone) {
        return new PersonAudienceMembershipV4Response(
            Id.valueOf(personAudienceMembership.getAudienceId().getValue()),
            personAudienceMembership.getCreatedDate().atZone(timeZone),
            personAudienceMembership.getUpdatedDate().atZone(timeZone));
    }

}
