package com.extole.client.rest.impl.person.v4;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.v4.PersonDataV4Response;
import com.extole.client.rest.person.v4.PersonJourneyRestV4Exception;
import com.extole.client.rest.person.v4.PersonJourneyV4Response;
import com.extole.client.rest.person.v4.RuntimePersonJourneyV4Endpoints;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.journey.JourneyName;
import com.extole.person.service.profile.journey.PersonJourney;
import com.extole.person.service.profile.referral.PersonReferralReason;

@Provider
public class RuntimePersonJourneyV4EndpointsImpl implements RuntimePersonJourneyV4Endpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;

    @Autowired
    public RuntimePersonJourneyV4EndpointsImpl(
        PersonService personService,
        ClientAuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
        this.personService = personService;
    }

    @Override
    public List<PersonJourneyV4Response> getJourneys(String accessToken, String personId, String container,
        String journeyName, ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return personService.getPerson(userAuthorization, Id.valueOf(personId))
                .getJourneys().stream()
                .filter(journey -> container == null || journey.getContainer().getName().equals(container))
                .filter(journey -> StringUtils.isBlank(journeyName)
                    || journey.getJourneyName().equals(JourneyName.valueOf(journeyName)))
                .map(personJourney -> toPersonJourneyResponse(personJourney, timeZone))
                .collect(Collectors.toList());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public PersonJourneyV4Response getJourney(String accessToken, String personId, String journeyId, ZoneId timeZone)
        throws PersonRestException, UserAuthorizationRestException, PersonJourneyRestV4Exception {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Optional<PersonJourneyV4Response> journeyResponse =
                personService.getPerson(userAuthorization, Id.valueOf(personId))
                    .getJourneys().stream()
                    .filter(journey -> journey.getId().getValue().equals(journeyId))
                    .map(personJourney -> toPersonJourneyResponse(personJourney, timeZone))
                    .findFirst();

            if (journeyResponse.isPresent()) {
                return journeyResponse.get();
            }

            throw RestExceptionBuilder.newBuilder(PersonJourneyRestV4Exception.class)
                .withErrorCode(PersonJourneyRestV4Exception.JOURNEY_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("journey_id", journeyId)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    private PersonJourneyV4Response toPersonJourneyResponse(PersonJourney journey, ZoneId timeZone) {
        Map<String, Object> privateData = journey.getPrivateData();
        Map<String, Object> publicData = journey.getPublicData();
        Map<String, Object> clientData = journey.getClientData();
        List<PersonDataV4Response> data = Lists.newArrayList();
        privateData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.PRIVATE, value)));
        publicData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.PUBLIC, value)));
        clientData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.CLIENT, value)));

        return new PersonJourneyV4Response(
            journey.getId().getValue(),
            journey.getCampaignId().getValue(),
            journey.getEntryLabel().orElse(null),
            journey.getContainer().getName(),
            journey.getJourneyName().getValue(),
            journey.getEntryReason().orElse(null),
            journey.getEntryZone().orElse(null),
            journey.getLastZone().orElse(null),
            journey.getEntryShareId().map(entryShareId -> entryShareId.getValue()).orElse(null),
            journey.getLastShareId().map(lastShareId -> lastShareId.getValue()).orElse(null),
            journey.getEntryShareableId().map(entryShareableId -> entryShareableId.getValue()).orElse(null),
            journey.getLastShareableId().map(lastShareableId -> lastShareableId.getValue()).orElse(null),
            journey.getEntryAdvocateCode().orElse(null),
            journey.getLastAdvocateCode().orElse(null),
            journey.getEntryPromotableCode().orElse(null),
            journey.getLastPromotableCode().orElse(null),
            journey.getEntryConsumerEventId().map(entryConsumerEventId -> entryConsumerEventId.getValue()).orElse(null),
            journey.getLastConsumerEventId().map(lastConsumerEventId -> lastConsumerEventId.getValue()).orElse(null),
            journey.getEntryProfileId() != null ? journey.getEntryProfileId().getValue() : null,
            journey.getLastProfileId() != null ? journey.getLastProfileId().getValue() : null,
            journey.getEntryAdvocatePartnerId().orElse(null),
            journey.getLastAdvocatePartnerId().orElse(null),
            journey.getEntryCouponCode().orElse(null),
            journey.getLastCouponCode().orElse(null),
            journey.getEntryReferralReason().map(PersonReferralReason::name).orElse(null),
            journey.getLastReferralReason().map(PersonReferralReason::name).orElse(null),
            journey.getCreatedDate().atZone(timeZone),
            journey.getUpdatedDate().atZone(timeZone),
            data);
    }

}
