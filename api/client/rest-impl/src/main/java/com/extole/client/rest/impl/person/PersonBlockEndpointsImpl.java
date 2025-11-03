package com.extole.client.rest.impl.person;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonBlockEndpoints;
import com.extole.client.rest.person.PersonBlockRequest;
import com.extole.client.rest.person.PersonBlockResponse;
import com.extole.client.rest.person.PersonBlockType;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.PersonValidationRestException;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.event.client.ClientEvent;
import com.extole.event.client.ClientEventBuilder;
import com.extole.event.client.ClientEventService;
import com.extole.id.Id;
import com.extole.model.entity.user.User;
import com.extole.model.shared.user.UserCache;
import com.extole.person.service.profile.InvalidProfileBlockReasonException;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonOperations;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.ProfileBlock;

@Provider
public class PersonBlockEndpointsImpl implements PersonBlockEndpoints {

    private static final String PROFILE_BLOCK_EVENT_NAME = "profile_block";
    private static final String PROFILE_UNBLOCK_EVENT_NAME = "profile_unblock";

    private final PersonService personService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final ClientEventService clientEventService;
    private final UserCache userCache;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public PersonBlockEndpointsImpl(
        PersonService personService,
        ClientAuthorizationProvider authorizationProvider,
        ConsumerEventSenderService consumerEventSenderService,
        ClientEventService clientEventService,
        UserCache userCache,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.personService = personService;
        this.authorizationProvider = authorizationProvider;
        this.consumerEventSenderService = consumerEventSenderService;
        this.clientEventService = clientEventService;
        this.userCache = userCache;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public PersonBlockResponse get(String accessToken, String personId, ZoneId timezone)
        throws UserAuthorizationRestException, PersonRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            return mapPersonToBlockResponse(person, timezone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public PersonBlockResponse update(String accessToken, String personId, PersonBlockRequest personBlockRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException, PersonValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonOperations personOperations =
                consumerEventSenderService.createConsumerEventSender()
                    .log("Person block updated via client /v5/persons endpoints. Person id: " + personId);
            Person updatedPerson = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-block-v5-endpoints-update"),
                (personBuilder, initialPerson) -> {
                    try {
                        if (personBlockRequest.getType() == PersonBlockType.NONE) {
                            personBuilder.removeBlock(authorization.getIdentityId(), personBlockRequest.getReason());
                        } else {
                            personBuilder.updateBlock(
                                com.extole.person.service.profile.PersonBlockType.valueOf(
                                    personBlockRequest.getType().name()),
                                authorization.getIdentityId(),
                                personBlockRequest.getReason().orElse(null));
                        }
                        return personBuilder.save();
                    } catch (InvalidProfileBlockReasonException e) {
                        throw new LockClosureException(e);
                    }
                }, personOperations);
            // ENG-19642 sending the input event separately just to have the same syntax as in the update method
            sendEvents(authorization, personBlockRequest, updatedPerson);
            return mapPersonToBlockResponse(updatedPerson, timeZone);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(InvalidProfileBlockReasonException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationRestException.class)
                    .withErrorCode(PersonValidationRestException.INVALID_BLOCK_REASON).withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private PersonBlockResponse mapPersonToBlockResponse(Person person, ZoneId timezone) {
        PersonBlockType personBlockType = PersonBlockType.NONE;
        if (person.isBlocked() || (person.isSelfRewardingBlocked() && person.isFriendRewardingBlocked())) {
            personBlockType = PersonBlockType.ALL_REWARDING;
        } else if (person.isSelfRewardingBlocked()) {
            personBlockType = PersonBlockType.SELF_REWARDING;
        } else if (person.isFriendRewardingBlocked()) {
            personBlockType = PersonBlockType.FRIEND_REWARDING;
        }
        Optional<String> reason = person.getProfileBlock().flatMap(ProfileBlock::getReason);
        Optional<Instant> updatedDate = person.getProfileBlock().map(ProfileBlock::getDate);
        if (reason.isEmpty() && person.isBlocked()) {
            reason = Optional.of("using old boolean blocked");
        }
        if (updatedDate.isEmpty()
            && (person.isBlocked() || person.isSelfRewardingBlocked() || person.isFriendRewardingBlocked())) {
            updatedDate = Optional.of(Instant.EPOCH);
        }

        return new PersonBlockResponse(personBlockType, reason,
            updatedDate.map(date -> ZonedDateTime.ofInstant(date, timezone)));
    }

    private void sendEvents(ClientAuthorization authorization, PersonBlockRequest request, Person person)
        throws EventProcessorException, AuthorizationException, PersonNotFoundException {
        boolean sendBlockEvent = false;
        if (request.getType() != PersonBlockType.NONE) {
            sendBlockEvent = true;
        }
        if (sendBlockEvent) {
            sendInputEvent(authorization, person, PROFILE_BLOCK_EVENT_NAME);
            sentProfileBlockClientEvent(authorization, PROFILE_BLOCK_EVENT_NAME, "Customer was blocked", person);
        } else {
            sendInputEvent(authorization, person, PROFILE_UNBLOCK_EVENT_NAME);
            sentProfileBlockClientEvent(authorization, PROFILE_UNBLOCK_EVENT_NAME, "Customer was unblocked", person);
        }
    }

    private void sendInputEvent(ClientAuthorization authorization, Person person, String eventName)
        throws EventProcessorException, AuthorizationException, PersonNotFoundException {
        ProcessedRawEvent processedRawEvent = clientRequestContextService.createBuilder(authorization, servletRequest)
            .withEventName(eventName)
            .withHttpRequestBodyCapturing(ClientRequestContextService.HttpRequestBodyCapturingType.LIMITED)
            .build().getProcessedRawEvent();
        consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person.getId()).send();
    }

    private void sentProfileBlockClientEvent(Authorization authorization, String eventName, String message,
        Person person) {
        ClientEventBuilder builder = clientEventService.createClientEventBuilder()
            .withClientId(authorization.getClientId())
            .withEventTime(Instant.now())
            .withName(eventName)
            .addTags()
            .withMessage(message)
            .withLevel(ClientEvent.Level.INFO)
            .withScope(com.extole.event.client.Scope.CLIENT_SUPERUSER)
            .addData("person_id", person.getId().getValue());
        person.getProfileBlock().flatMap(ProfileBlock::getReason)
            .ifPresent(reason -> builder.addData("reason", reason));
        userCache.getByAuthorization(authorization).map(User::getId).ifPresent(id -> builder.withUserId(id));
        builder.send();
    }
}
