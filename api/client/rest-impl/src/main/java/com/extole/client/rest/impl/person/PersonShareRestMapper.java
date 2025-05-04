package com.extole.client.rest.impl.person;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.PartnerEventIdResponse;
import com.extole.client.rest.person.v4.PersonShareV4Response;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.key.PersonKey;
import com.extole.person.service.share.Channel;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.shareable.ShareableNotFoundException;

@Component
public class PersonShareRestMapper {

    private static final Logger LOG = LoggerFactory.getLogger(PersonShareRestMapper.class);

    private final ClientShareableService clientShareableService;
    private final PersonService personService;

    @Autowired
    public PersonShareRestMapper(ClientShareableService clientShareableService,
        PersonService personService) {
        this.clientShareableService = clientShareableService;
        this.personService = personService;
    }

    public PersonShareV4Response toPersonShareResponse(Authorization authorization, PersonShare share, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Optional<Id<PersonHandle>> friendPersonId = Optional.empty();
        try {
            if (share.getEmail().isPresent()) {
                Optional<Person> friendPerson = personService.getPersonByProfileLookupKey(authorization,
                    PersonKey.ofEmailType(share.getEmail().get()));
                if (friendPerson.isPresent()) {
                    friendPersonId = Optional.of(friendPerson.get().getIdentityId());
                } else {
                    LOG.warn("Unable to find friend person [clientId={}, email={}] for share response",
                        authorization.getClientId(), share.getEmail().get());
                }
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }

        String shareableLink = null;
        String shareableId = share.getShareableId().getValue();
        try {
            shareableLink = clientShareableService.get(authorization, Id.valueOf(shareableId)).getLink().toString();
        } catch (ShareableNotFoundException e) {
            LOG.warn("Can not find shareable {} for share {}", share.getShareableId(), share.getId());
        }

        String shareId = share.getId().getValue();
        String channel = share.getChannel().map(Channel::getName).orElse(null);
        String message = share.getMessage().orElse(null);
        ZonedDateTime shareDate = share.getShareDate().atZone(timeZone);
        String recipient = share.getEmail().orElse(null);
        Map<String, String> data = share.getData();
        PartnerEventIdResponse sharePartnerId = share.getPartnerId()
            .map(partnerId -> new PartnerEventIdResponse(partnerId.getName(), partnerId.getValue()))
            .orElse(null);

        return new PersonShareV4Response(
            shareId,
            shareableId,
            channel,
            message,
            shareDate,
            recipient,
            friendPersonId.map(Id::getValue).orElse(null),
            shareableLink,
            data,
            sharePartnerId,
            share.getSubject().orElse(null));
    }

}
