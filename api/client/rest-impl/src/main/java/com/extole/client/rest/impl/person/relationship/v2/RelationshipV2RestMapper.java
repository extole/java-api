package com.extole.client.rest.impl.person.relationship.v2;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.person.v2.PersonV2RestMapper;
import com.extole.client.rest.person.v2.RelationshipV2Response;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.referral.PersonReferral;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableService;

@Component
public class RelationshipV2RestMapper {
    private static final Logger LOG = LoggerFactory.getLogger(RelationshipV2RestMapper.class);

    private final PersonService personService;
    private final PersonV2RestMapper personV2RestMapper;
    private final ShareableService shareableService;

    @Autowired
    public RelationshipV2RestMapper(PersonService personService,
        PersonV2RestMapper personV2RestMapper,
        ShareableService shareableService) {
        this.personService = personService;
        this.personV2RestMapper = personV2RestMapper;
        this.shareableService = shareableService;
    }

    public List<RelationshipV2Response> toRelationshipResponses(Authorization authorization, Id<PersonHandle> personId,
        List<PersonReferral> personReferrals, ZoneId timeZone) throws AuthorizationException {
        List<RelationshipV2Response> responses = new ArrayList<>();
        for (PersonReferral personReferral : personReferrals) {
            try {
                Person otherPerson = personService.getPerson(authorization, personReferral.getOtherPersonId());

                // TODO get rid of shareable id, use only shareable code ENG-18496
                String shareableId = null;
                if (personReferral.getData().containsKey(PersonReferral.DATA_NAME_SHAREABLE_ID)) {
                    shareableId = personReferral.getData().get(PersonReferral.DATA_NAME_SHAREABLE_ID).toString();
                } else if (personReferral.getData().containsKey(PersonReferral.DATA_NAME_SHAREABLE_CODE)) {
                    String shareableCode =
                        personReferral.getData().get(PersonReferral.DATA_NAME_SHAREABLE_CODE).toString();
                    try {
                        shareableId = shareableService.getByCode(authorization.getClientId(), shareableCode)
                            .getShareableId()
                            .getValue();
                    } catch (ShareableNotFoundException e) {
                        LOG.warn("Unable to find shareable with code: {}, clientId: {} (associated with {} person: {})",
                            shareableCode, personReferral.getClientId(), personReferral.getMySide(), personId, e);
                    }
                }

                responses.add(new RelationshipV2Response(
                    shareableId,
                    personReferral.getReason().name(),
                    personReferral.getContainer().getName(),
                    personReferral.getUpdatedDate().atZone(timeZone),
                    personV2RestMapper.toPersonResponse(otherPerson, timeZone)));
            } catch (PersonNotFoundException e) {
                LOG.warn("Unable to find person with id: {}, clientId: {} (associated with {} person: {})",
                    personReferral.getOtherPersonId(), personReferral.getClientId(), personReferral.getMySide(),
                    personId, e);
            }
        }
        return responses;
    }
}
