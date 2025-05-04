package com.extole.client.rest.impl.person;

import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonJourneyDataResponse;
import com.extole.client.rest.person.PersonJourneyResponse;
import com.extole.client.rest.person.PersonJourneyRestException;
import com.extole.client.rest.person.PersonJourneysEndpoints;
import com.extole.client.rest.person.PersonJourneysListRequest;
import com.extole.client.rest.person.PersonJourneysListRestException;
import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.CampaignHandle;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.PersonJourneyQueryBuilder;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.journey.Container;
import com.extole.person.service.profile.journey.JourneyName;
import com.extole.person.service.profile.journey.PersonJourney;

@Provider
public class PersonJourneysEndpointsImpl implements PersonJourneysEndpoints {

    private static final String ALL_CONTAINERS = "*";
    private final ClientAuthorizationProvider authorizationProvider;

    private final PersonService personService;
    private final FullPersonService fullPersonService;

    @Autowired
    public PersonJourneysEndpointsImpl(
        PersonService personService,
        FullPersonService fullPersonService,
        ClientAuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
        this.personService = personService;
        this.fullPersonService = fullPersonService;
    }

    @Override
    public List<PersonJourneyResponse> list(String accessToken, String personId, PersonJourneysListRequest listRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException, PersonJourneysListRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Multimap<String, Object> dataValues = parseKeyValuePairs(listRequest.getDataValues(), "data_values");
            Multimap<String, String> keyValues =
                parseKeyValuePairs(listRequest.getKeyValues(), "key_values").entries().stream()
                    .collect(toImmutableListMultimap(entry -> entry.getKey(), pair -> pair.getValue().toString()));

            PersonJourneyQueryBuilder builder = fullPersonService.createJourneyQueryBuilder(
                userAuthorization, Id.valueOf(personId))
                .withIds(listRequest.getIds().stream()
                    .map(id -> Id.<PersonJourney>valueOf(id))
                    .collect(Collectors.toList()))
                .withNames(listRequest.getNames().stream()
                    .map(JourneyName::valueOf)
                    .collect(Collectors.toList()))
                .withPrograms(listRequest.getPrograms())
                .withCampaignIds(listRequest.getCampaignIds().stream()
                    .map(id -> Id.<CampaignHandle>valueOf(id))
                    .collect(Collectors.toList()))
                .withDataKeys(listRequest.getDataKeys())
                .withDataValues(dataValues)
                .withOffset(listRequest.getOffset())
                .withLimit(listRequest.getLimit())
                .withKeyNames(listRequest.getKeyNames())
                .withKeyValues(keyValues);

            if (!listRequest.getContainers().contains(ALL_CONTAINERS)) {
                builder.withContainers(listRequest.getContainers().stream()
                    .map(Container::new)
                    .collect(Collectors.toList()));
            }
            return builder.list().stream()
                .map(step -> mapToResponse(step, timeZone))
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
    public PersonJourneyResponse get(String accessToken, String personId, String journeyId, ZoneId timeZone)
        throws PersonRestException, UserAuthorizationRestException, PersonJourneyRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Optional<PersonJourney> personJourney =
                personService.getPerson(authorization, Id.valueOf(personId))
                    .getJourneys().stream()
                    .filter(journey -> journey.getId().getValue().equals(journeyId))
                    .findFirst();

            if (personJourney.isEmpty()) {
                personJourney = fullPersonService.createJourneyQueryBuilder(authorization, Id.valueOf(personId))
                    .withIds(List.of(Id.valueOf(journeyId))).withLimit(1)
                    .list().stream()
                    .findFirst();
            }
            return personJourney.map(journey -> mapToResponse(journey, timeZone))
                .orElseThrow(() -> RestExceptionBuilder.newBuilder(PersonJourneyRestException.class)
                    .withErrorCode(PersonJourneyRestException.JOURNEY_NOT_FOUND)
                    .addParameter("person_id", personId)
                    .addParameter("journey_id", journeyId)
                    .build());
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

    private PersonJourneyResponse mapToResponse(PersonJourney journey, ZoneId timeZone) {
        Map<String, PersonJourneyDataResponse> data = Maps.newHashMap();
        addData(data, () -> journey.getPublicData(), PersonDataScope.PUBLIC);
        addData(data, () -> journey.getPrivateData(), PersonDataScope.PRIVATE);
        addData(data, () -> journey.getClientData(), PersonDataScope.CLIENT);
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_REASON, journey.getEntryReason());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_ZONE, journey.getEntryZone());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_ZONE, journey.getLastZone());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_SHARE_ID,
            journey.getEntryShareId().map(Id::getValue));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_SHARE_ID,
            journey.getLastShareId().map(Id::getValue));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_SHAREABLE_ID,
            journey.getEntryShareableId().map(Id::getValue));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_SHAREABLE_ID,
            journey.getLastShareableId().map(Id::getValue));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_ADVOCATE_CODE, journey.getEntryAdvocateCode());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_ADVOCATE_CODE, journey.getLastAdvocateCode());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_PROMOTABLE_CODE, journey.getEntryPromotableCode());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_PROMOTABLE_CODE, journey.getLastPromotableCode());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_CONSUMER_EVENT_ID,
            journey.getEntryConsumerEventId().map(Id::getValue));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_CONSUMER_EVENT_ID,
            journey.getLastConsumerEventId().map(Id::getValue));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_PROFILE_ID,
            Optional.of(journey.getEntryProfileId().getValue()));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_PROFILE_ID,
            Optional.of(journey.getLastProfileId().getValue()));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_ADVOCATE_PARTNER_ID,
            journey.getEntryAdvocatePartnerId());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_ADVOCATE_PARTNER_ID,
            journey.getLastAdvocatePartnerId());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_COUPON_CODE, journey.getEntryCouponCode());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_COUPON_CODE, journey.getLastCouponCode());
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_ENTRY_REFERRAL_REASON,
            journey.getEntryReferralReason().map(Enum::name));
        addDataIfNotNull(data, PersonJourneyResponse.DATA_NAME_LAST_REFERRAL_REASON,
            journey.getLastReferralReason().map(Enum::name));
        return new PersonJourneyResponse(
            journey.getId().getValue(),
            journey.getJourneyName().getValue(),
            journey.getEntryLabel().orElse(null),
            journey.getCampaignId().getValue(),
            journey.getContainer().getName(),
            data,
            journey.getCreatedDate().atZone(timeZone),
            journey.getUpdatedDate().atZone(timeZone),
            journey.getKey().map(value -> new JourneyKey(value.getName(), value.getValue())));
    }

    private void addDataIfNotNull(Map<String, PersonJourneyDataResponse> data, String name, Optional<String> value) {
        if (value.isEmpty()) {
            return;
        }
        data.put(name, new PersonJourneyDataResponse(name, PersonDataScope.PRIVATE, value.get()));
    }

    private void addData(Map<String, PersonJourneyDataResponse> data, Supplier<Map<String, Object>> dataSupplier,
        PersonDataScope scope) {
        dataSupplier.get().forEach((name, value) -> data.put(name, new PersonJourneyDataResponse(name, scope, value)));
    }

    private Multimap<String, Object> parseKeyValuePairs(List<String> valuesToParse, String parameterName)
        throws PersonJourneysListRestException {
        ImmutableListMultimap.Builder<String, Object> resultBuilder = ImmutableListMultimap.<String, Object>builder();

        for (String valueToParse : valuesToParse) {
            int delimiterLocation = valueToParse.indexOf(":");
            if (delimiterLocation > 0) {
                resultBuilder.put(valueToParse.substring(0, delimiterLocation),
                    valueToParse.substring(delimiterLocation + 1));
            } else {
                throw RestExceptionBuilder.newBuilder(PersonJourneysListRestException.class)
                    .withErrorCode(PersonJourneysListRestException.VALUE_DOES_NOT_FOLLOW_PATTERN)
                    .addParameter("parameter", parameterName)
                    .addParameter("value", valueToParse)
                    .build();
            }
        }

        return resultBuilder.build();
    }

}
