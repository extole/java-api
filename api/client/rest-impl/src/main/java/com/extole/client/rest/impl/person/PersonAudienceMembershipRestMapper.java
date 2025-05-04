package com.extole.client.rest.impl.person;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.person.v4.PersonAudienceMembershipV4Response;
import com.extole.id.Id;
import com.extole.person.service.profile.audience.membership.PersonAudienceMembership;

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
