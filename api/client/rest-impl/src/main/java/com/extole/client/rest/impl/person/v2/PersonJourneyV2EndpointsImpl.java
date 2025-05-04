package com.extole.client.rest.impl.person.v2;

import static com.extole.authorization.service.Authorization.Scope.CLIENT_SUPERUSER;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.data.PersonDataRequest;
import com.extole.client.rest.person.v2.PersonJourneyV2CreateRequest;
import com.extole.client.rest.person.v2.PersonJourneyV2Endpoints;
import com.extole.client.rest.person.v2.PersonJourneyV2Response;
import com.extole.client.rest.person.v2.PersonJourneyV2UpdateRequest;
import com.extole.client.rest.person.v2.PersonJourneyV2ValidationRestException;
import com.extole.client.rest.person.v4.PersonDataV4Response;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonBuilder;
import com.extole.person.service.profile.PersonData;
import com.extole.person.service.profile.PersonDataInvalidNameException;
import com.extole.person.service.profile.PersonDataInvalidValueException;
import com.extole.person.service.profile.PersonDataNameLengthException;
import com.extole.person.service.profile.PersonDataValueLengthException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonParameterInvalidLengthException;
import com.extole.person.service.profile.PersonParameterMissingException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.journey.Container;
import com.extole.person.service.profile.journey.JourneyKey;
import com.extole.person.service.profile.journey.JourneyName;
import com.extole.person.service.profile.journey.PersonJourney;
import com.extole.person.service.profile.journey.PersonJourneyBuilder;
import com.extole.person.service.profile.referral.PersonReferralReason;

@Provider
public class PersonJourneyV2EndpointsImpl implements PersonJourneyV2Endpoints {

    private static final Logger LOG = LoggerFactory.getLogger(PersonJourneyV2EndpointsImpl.class);
    private static final LockDescription LOCK_DESCRIPTION = new LockDescription("person-journey-endpoints-lock");

    private static final String EVENT_NAME_FOR_JOURNEY_CREATE = "extole.person.journey.create";
    private static final String EVENT_NAME_FOR_JOURNEY_UPDATE = "extole.person.journey.update";

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public PersonJourneyV2EndpointsImpl(PersonService personService,
        ClientAuthorizationProvider authorizationProvider,
        ConsumerEventSenderService consumerEventSenderService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.authorizationProvider = authorizationProvider;
        this.personService = personService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public List<PersonJourneyV2Response> getJourneys(String accessToken, String personId, String container,
        String journeyName, ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personService.getPerson(authorization, Id.valueOf(personId))
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
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PersonJourneyV2Response getJourney(String accessToken, String personId, String journeyId, ZoneId timeZone)
        throws PersonRestException, UserAuthorizationRestException, PersonJourneyV2ValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<PersonJourneyV2Response> journeyResponse =
                personService.getPerson(authorization, Id.valueOf(personId))
                    .getJourneys().stream()
                    .filter(journey -> journey.getId().getValue().equals(journeyId))
                    .map(personJourney -> toPersonJourneyResponse(personJourney, timeZone))
                    .findFirst();

            if (journeyResponse.isPresent()) {
                return journeyResponse.get();
            }
            throw RestExceptionBuilder.newBuilder(PersonJourneyV2ValidationRestException.class)
                .withErrorCode(PersonJourneyV2ValidationRestException.JOURNEY_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("journey_id", journeyId)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PersonJourneyV2Response createJourney(String accessToken, String personId,
        PersonJourneyV2CreateRequest journeyRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonJourneyV2ValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        verifySuperUserAuthorization(authorization);
        if (Strings.isNullOrEmpty(journeyRequest.getCampaignId()) ||
            Strings.isNullOrEmpty(journeyRequest.getContainer()) ||
            Strings.isNullOrEmpty(journeyRequest.getJourneyName())) {
            throw RestExceptionBuilder.newBuilder(PersonJourneyV2ValidationRestException.class)
                .withErrorCode(PersonJourneyV2ValidationRestException.UNABLE_TO_CREATE_JOURNEY)
                .addParameter("campaign_id", journeyRequest.getCampaignId())
                .addParameter("container", journeyRequest.getContainer())
                .addParameter("journey_name", journeyRequest.getJourneyName())
                .build();
        }
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));

            ProcessedRawEvent processedRawEvent =
                clientRequestContextService.createBuilder(authorization, servletRequest)
                    .withEventName(EVENT_NAME_FOR_JOURNEY_CREATE)
                    .build().getProcessedRawEvent();

            PersonJourney createdJourney =
                consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person)
                    .withLockDescription(LOCK_DESCRIPTION)
                    .executeAndSend((personBuilder, originalPerson, inputEventBuilder) -> {
                        try {
                            Optional<JourneyKey> journeyKey = journeyRequest.getKey()
                                .map(value -> JourneyKey.of(value.getName(), value.getValue()));

                            PersonJourneyBuilder journeyBuilder =
                                personBuilder.createOrUpdateJourney(Id.valueOf(journeyRequest.getCampaignId()),
                                    new Container(journeyRequest.getContainer()),
                                    JourneyName.valueOf(journeyRequest.getJourneyName()), journeyKey);
                            // TODO implement validation ENG-8230
                            if (!Strings.isNullOrEmpty(journeyRequest.getLabel())) {
                                journeyBuilder.withLabel(journeyRequest.getLabel());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getReason())) {
                                journeyBuilder.withReason(journeyRequest.getReason());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getZone())) {
                                journeyBuilder.withZone(journeyRequest.getZone());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getShareId())) {
                                journeyBuilder.withShareId(Id.valueOf(journeyRequest.getShareId()));
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getShareableId())) {
                                journeyBuilder.withShareableId(Id.valueOf(journeyRequest.getShareableId()));
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getAdvocateCode())) {
                                journeyBuilder.withAdvocateCode(journeyRequest.getAdvocateCode());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getPromotableCode())) {
                                journeyBuilder.withPromotableCode(journeyRequest.getPromotableCode());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getCouponCode())) {
                                journeyBuilder.withCouponCode(journeyRequest.getCouponCode());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getConsumerEventId())) {
                                journeyBuilder.withConsumerEventId(Id.valueOf(journeyRequest.getConsumerEventId()));
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getReferralReason())) {
                                journeyBuilder.withReferralReason(
                                    PersonReferralReason.valueOf(journeyRequest.getReferralReason()));
                            }

                            List<PersonDataRequest> data = journeyRequest.getData();
                            for (PersonDataRequest dataRequest : data) {
                                PersonData.Scope dataScope = dataRequest.getScope() != null
                                    ? PersonData.Scope.valueOf(dataRequest.getScope().name())
                                    : PersonData.Scope.PRIVATE;
                                journeyBuilder.addOrReplaceDataValue(dataRequest.getName())
                                    .withValue(dataRequest.getValue())
                                    .withScope(dataScope);
                            }

                            Person updatedPerson = personBuilder.save();

                            PersonJourney newJourney = updatedPerson.getJourneys().stream()
                                .filter(journey -> journey.getContainer().getName()
                                    .equalsIgnoreCase(journeyRequest.getContainer()))
                                .filter(journey -> journey.getCampaignId().getValue()
                                    .equals(journeyRequest.getCampaignId()))
                                .filter(journey -> journey.getJourneyName().equals(
                                    JourneyName.valueOf(journeyRequest.getJourneyName())))
                                .filter(journey -> journey.getKey().equals(journeyKey))
                                .findFirst()
                                .get();

                            inputEventBuilder.addLogMessage("Person journey created via client journey v2 endpoints."
                                + " Journey id: " + newJourney.getId() + "."
                                + " Campaign: " + newJourney.getCampaignId() + "."
                                + " Journey name: " + newJourney.getJourneyName() + "."
                                + " Container: " + newJourney.getContainer());

                            return new InputEventLockClosureResult<>(updatedPerson, newJourney);
                        } catch (PersonParameterInvalidLengthException | PersonParameterMissingException
                            | PersonDataInvalidValueException | PersonDataValueLengthException
                            | PersonDataNameLengthException | PersonDataInvalidNameException e) {
                            throw new LockClosureException(e);
                        }
                    }).getPreEventSendingResult();
            return toPersonJourneyResponse(createdJourney, timeZone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (LockClosureException | EventProcessorException e) {
            LOG.error("Unable to update person in lock context for clientId={}, personId={}",
                authorization.getClientId(), personId, e);
            throw RestExceptionBuilder.newBuilder(PersonJourneyV2ValidationRestException.class)
                .withErrorCode(PersonJourneyV2ValidationRestException.UNABLE_TO_CREATE_JOURNEY)
                .addParameter("person_id", personId)
                .withCause(e).build();
        }
    }

    @Override
    public PersonJourneyV2Response updateJourney(String accessToken, String personId, String journeyId,
        PersonJourneyV2UpdateRequest journeyRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonJourneyV2ValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        verifySuperUserAuthorization(authorization);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            PersonJourney initialJourney = person.getJourneys().stream()
                .filter(journey -> journey.getId().getValue().equals(journeyId))
                .findFirst().orElse(null);

            if (initialJourney == null) {
                throw RestExceptionBuilder.newBuilder(PersonJourneyV2ValidationRestException.class)
                    .withErrorCode(PersonJourneyV2ValidationRestException.JOURNEY_NOT_FOUND)
                    .addParameter("person_id", personId)
                    .addParameter("journey_id", journeyId)
                    .build();
            }

            if (isNoopUpdateRequest(journeyRequest)) {
                return toPersonJourneyResponse(initialJourney, timeZone);
            }

            ProcessedRawEvent processedRawEvent =
                clientRequestContextService.createBuilder(authorization, servletRequest)
                    .withEventName(EVENT_NAME_FOR_JOURNEY_UPDATE)
                    .withEventProcessing(processor -> {
                        processor.addLogMessage("Person journey update via client journey v2 endpoints."
                            + " Journey id: " + journeyId + ".");
                    }).build().getProcessedRawEvent();

            PersonJourney changedJourney =
                consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person)
                    .withLockDescription(LOCK_DESCRIPTION)
                    .executeAndSend((personBuilder, originalPerson, inputEventBuilder) -> {
                        try {
                            // TODO it is necessary implement validation ENG-8230
                            if (!Strings.isNullOrEmpty(journeyRequest.getLabel())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withLabel(journeyRequest.getLabel());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getReason())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withReason(journeyRequest.getReason());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getZone())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withZone(journeyRequest.getZone());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getShareId())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withShareId(Id.valueOf(journeyRequest.getShareId()));
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getShareableId())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withShareableId(Id.valueOf(journeyRequest.getShareableId()));
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getAdvocateCode())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withAdvocateCode(journeyRequest.getAdvocateCode());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getPromotableCode())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withPromotableCode(journeyRequest.getPromotableCode());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getCouponCode())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withCouponCode(journeyRequest.getCouponCode());
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getConsumerEventId())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder)
                                    .withConsumerEventId(Id.valueOf(journeyRequest.getConsumerEventId()));
                            }
                            if (!Strings.isNullOrEmpty(journeyRequest.getReferralReason())) {
                                getJourneyBuilderForJourney(initialJourney, personBuilder).withReferralReason(
                                    PersonReferralReason.valueOf(journeyRequest.getReferralReason()));
                            }
                            List<PersonDataRequest> data = journeyRequest.getData();
                            if (!data.isEmpty()) {
                                PersonJourneyBuilder journeyBuilder =
                                    getJourneyBuilderForJourney(initialJourney, personBuilder);
                                for (PersonDataRequest dataRequest : data) {
                                    PersonData.Scope dataScope = dataRequest.getScope() != null
                                        ? PersonData.Scope.valueOf(dataRequest.getScope().name())
                                        : PersonData.Scope.PRIVATE;
                                    journeyBuilder.addOrReplaceDataValue(dataRequest.getName())
                                        .withValue(dataRequest.getValue())
                                        .withScope(dataScope);
                                }
                            }

                            if (!Strings.isNullOrEmpty(journeyRequest.getContainer())) {
                                personBuilder.updateJourneyContainer(initialJourney.getCampaignId(),
                                    initialJourney.getContainer(), initialJourney.getJourneyName(),
                                    initialJourney.getKey(), new Container(journeyRequest.getContainer()));
                            }

                            Person updatedPerson = personBuilder.save();

                            PersonJourney updatedJourney = updatedPerson.getJourneys().stream()
                                .filter(journey -> journey.getId().getValue().equals(journeyId))
                                .findFirst()
                                .get();

                            return new InputEventLockClosureResult<>(updatedPerson, updatedJourney);
                        } catch (PersonParameterInvalidLengthException | PersonParameterMissingException
                            | PersonDataInvalidValueException | PersonDataValueLengthException
                            | PersonDataNameLengthException | PersonDataInvalidNameException e) {
                            throw new LockClosureException(e);
                        }
                    }).getPreEventSendingResult();
            return toPersonJourneyResponse(changedJourney, timeZone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (LockClosureException | EventProcessorException e) {
            LOG.error("Unable to update person in lock context for clientId={}, personId={}",
                authorization.getClientId(), personId, e);
            throw RestExceptionBuilder.newBuilder(PersonJourneyV2ValidationRestException.class)
                .withErrorCode(PersonJourneyV2ValidationRestException.UNABLE_TO_UPDATE_JOURNEY)
                .addParameter("journey_id", journeyId)
                .withCause(e).build();
        }
    }

    private static boolean isNoopUpdateRequest(PersonJourneyV2UpdateRequest journeyRequest) {
        return Strings.isNullOrEmpty(journeyRequest.getLabel()) &&
            Strings.isNullOrEmpty(journeyRequest.getReason()) &&
            Strings.isNullOrEmpty(journeyRequest.getZone()) &&
            Strings.isNullOrEmpty(journeyRequest.getShareId()) &&
            Strings.isNullOrEmpty(journeyRequest.getShareableId()) &&
            Strings.isNullOrEmpty(journeyRequest.getAdvocateCode()) &&
            Strings.isNullOrEmpty(journeyRequest.getPromotableCode()) &&
            Strings.isNullOrEmpty(journeyRequest.getCouponCode()) &&
            Strings.isNullOrEmpty(journeyRequest.getConsumerEventId()) &&
            Strings.isNullOrEmpty(journeyRequest.getReferralReason()) &&
            Strings.isNullOrEmpty(journeyRequest.getContainer()) &&
            journeyRequest.getData().isEmpty();
    }

    private static PersonJourneyBuilder getJourneyBuilderForJourney(PersonJourney journey, PersonBuilder personBuilder)
        throws PersonParameterInvalidLengthException, PersonParameterMissingException {
        return personBuilder.createOrUpdateJourney(journey.getCampaignId(), journey.getContainer(),
            journey.getJourneyName(), journey.getKey());
    }

    private static void verifySuperUserAuthorization(Authorization authorization)
        throws UserAuthorizationRestException {
        if (!authorization.getScopes().contains(CLIENT_SUPERUSER)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }

    private static PersonJourneyV2Response toPersonJourneyResponse(PersonJourney journey, ZoneId timeZone) {
        Map<String, Object> privateData = journey.getPrivateData();
        Map<String, Object> publicData = journey.getPublicData();
        Map<String, Object> clientData = journey.getClientData();
        List<PersonDataV4Response> data = Lists.newArrayList();
        privateData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.PRIVATE, value)));
        publicData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.PUBLIC, value)));
        clientData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.CLIENT, value)));

        return new PersonJourneyV2Response(
            journey.getId().getValue(),
            journey.getCampaignId().getValue(),
            journey.getEntryLabel().orElse(null),
            journey.getContainer().getName(),
            journey.getJourneyName().getValue(),
            journey.getEntryReason().orElse(null),
            journey.getEntryZone().orElse(null),
            journey.getLastZone().orElse(null),
            journey.getEntryShareId().map(Id::getValue).orElse(null),
            journey.getLastShareId().map(Id::getValue).orElse(null),
            journey.getEntryShareableId().map(Id::getValue).orElse(null),
            journey.getLastShareableId().map(Id::getValue).orElse(null),
            journey.getEntryAdvocateCode().orElse(null),
            journey.getLastAdvocateCode().orElse(null),
            journey.getEntryPromotableCode().orElse(null),
            journey.getLastPromotableCode().orElse(null),
            journey.getEntryConsumerEventId().map(Id::getValue).orElse(null),
            journey.getLastConsumerEventId().map(Id::getValue).orElse(null),
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
            data,
            journey.getKey()
                .map(value -> new com.extole.client.rest.person.JourneyKey(value.getName(), value.getValue())));
    }

}
