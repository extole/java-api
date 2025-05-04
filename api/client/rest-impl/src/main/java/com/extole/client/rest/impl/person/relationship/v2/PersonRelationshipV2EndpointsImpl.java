package com.extole.client.rest.impl.person.relationship.v2;

import static com.extole.authorization.service.Authorization.Scope.CLIENT_SUPERUSER;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.relationship.v2.PersonRelationshipV2Endpoints;
import com.extole.client.rest.person.relationship.v2.PersonRelationshipV2RestException;
import com.extole.client.rest.person.relationship.v2.PersonRelationshipV2UpdateRequest;
import com.extole.client.rest.person.relationship.v2.PersonRelationshipValidationV2RestException;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonParameterInvalidLengthException;
import com.extole.person.service.profile.PersonParameterMissingException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.journey.Container;
import com.extole.person.service.profile.referral.PersonReferral;

@Provider
public class PersonRelationshipV2EndpointsImpl implements PersonRelationshipV2Endpoints {

    private static final Logger LOG = LoggerFactory.getLogger(PersonRelationshipV2EndpointsImpl.class);

    private static final LockDescription LOCK_DESCRIPTION = new LockDescription(
        "person-relationship-v2-endpoints-lock");
    private static final String EVENT_NAME_FOR_RELATIONSHIP_UPDATE = "extole.person.relationship.update";

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public PersonRelationshipV2EndpointsImpl(
        PersonService personService,
        ClientAuthorizationProvider authorizationProvider,
        ConsumerEventSenderService consumerEventSenderService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.personService = personService;
        this.authorizationProvider = authorizationProvider;
        this.consumerEventSenderService = consumerEventSenderService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public void updateRelationship(String accessToken, String personId, String role,
        String otherPersonId, String container, PersonRelationshipV2UpdateRequest updateRequest)
        throws UserAuthorizationRestException, PersonRestException, PersonRelationshipV2RestException,
        PersonRelationshipValidationV2RestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        verifySuperUserAuthorization(authorization);

        Container currentContainer = new Container(container);
        PersonReferral.Side otherPersonRole = parseRole(role);
        PersonReferral.Side personRole = otherPersonRole == PersonReferral.Side.ADVOCATE ? PersonReferral.Side.FRIEND
            : PersonReferral.Side.ADVOCATE;
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            Person otherPerson = personService.getPerson(authorization, Id.valueOf(otherPersonId));
            Optional<PersonReferral> referralToUpdate =
                getReferral(person, otherPerson, otherPersonRole, currentContainer);
            Optional<PersonReferral> counterReferralToUpdate =
                getReferral(otherPerson, person, personRole, currentContainer);

            if (referralToUpdate.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(PersonRelationshipV2RestException.class)
                    .withErrorCode(PersonRelationshipV2RestException.RELATIONSHIP_NOT_FOUND)
                    .addParameter("person_id", personId)
                    .addParameter("other_person_id", otherPersonId)
                    .addParameter("container", container)
                    .addParameter("role", role)
                    .build();
            }

            if (StringUtils.isEmpty(updateRequest.getContainer())) {
                return;
            }

            Container newContainer = new Container(updateRequest.getContainer());
            updateReferral(authorization, person, referralToUpdate.get(), newContainer);

            if (counterReferralToUpdate.isPresent()) {
                updateReferral(authorization, otherPerson, counterReferralToUpdate.get(), newContainer);
            } else {
                counterReferralToUpdate =
                    findReferralWhereOtherPersonMayBeADifferentDeviceProfile(authorization, otherPerson.getId(),
                        person.getId(), currentContainer, personRole);
                if (counterReferralToUpdate.isPresent()) {
                    updateReferral(authorization, otherPerson, counterReferralToUpdate.get(), newContainer);
                }
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
        } catch (LockClosureException e) {
            LOG.error("Unable to update person in lock context for clientId={}, personId={}",
                authorization.getClientId(), personId, e);
            throw RestExceptionBuilder.newBuilder(PersonRelationshipValidationV2RestException.class)
                .withErrorCode(PersonRelationshipValidationV2RestException.RELATIONSHIP_UPDATE_ERROR)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private Optional<PersonReferral> findReferralWhereOtherPersonMayBeADifferentDeviceProfile(
        Authorization authorization, Id<PersonHandle> personId, Id<PersonHandle> otherPersonId, Container container,
        PersonReferral.Side otherPersonRole)
        throws PersonNotFoundException, AuthorizationException {
        Person person = personService.getPerson(authorization, personId);
        if (otherPersonRole == PersonReferral.Side.ADVOCATE) {
            return person.getReferralsToMe().stream()
                .filter(referral -> personService.isSamePerson(authorization.getClientId(),
                    referral.getOtherPersonId(), otherPersonId))
                .filter(referral -> Objects.equals(referral.getContainer(), container))
                .findFirst();
        }
        return person.getReferralsFromMe().stream()
            .filter(referral -> personService.isSamePerson(authorization.getClientId(),
                referral.getOtherPersonId(), otherPersonId))
            .filter(referral -> Objects.equals(referral.getContainer(), container))
            .findFirst();
    }

    private void updateReferral(ClientAuthorization authorization, Person person,
        PersonReferral referral, Container container)
        throws AuthorizationException, LockClosureException, EventProcessorException {

        ProcessedRawEvent processedRawEvent = clientRequestContextService.createBuilder(authorization, servletRequest)
            .withEventName(EVENT_NAME_FOR_RELATIONSHIP_UPDATE)
            .withEventProcessing(processor -> {
                processor.addLogMessage("Person referral updated via client relationship v2 endpoints."
                    + " Original referral: " + referral + "."
                    + " Old container: " + referral.getContainer() + "."
                    + " New container: " + container + ".");
            }).build().getProcessedRawEvent();

        consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person)
            .withLockDescription(LOCK_DESCRIPTION)
            .executeAndSend((personBuilder, originalPerson, inputEventBuilder) -> {
                try {
                    personBuilder.updateReferralContainer(referral.getMySide(), referral.getOtherPersonId(),
                        referral.getContainer(), container);
                } catch (PersonParameterInvalidLengthException | PersonParameterMissingException e) {
                    throw new LockClosureException(e);
                }
                Person updatedPerson = personBuilder.save();
                return new InputEventLockClosureResult<>(updatedPerson);
            });
    }

    private Optional<PersonReferral> getReferral(Person person, Person otherPerson,
        PersonReferral.Side otherPersonRole, Container currentContainer) {

        if (personService.isSamePerson(person, otherPerson)) {
            return person
                .getSelfReferrals(otherPersonRole == PersonReferral.Side.FRIEND ? PersonReferral.Side.ADVOCATE
                    : PersonReferral.Side.FRIEND)
                .filter(referral -> Objects.equals(referral.getOtherPersonId(), otherPerson.getId()))
                .filter(referral -> Objects.equals(referral.getContainer(), currentContainer))
                .findFirst();
        }

        if (otherPersonRole == PersonReferral.Side.ADVOCATE) {
            return person.getReferralsToMe().stream()
                .filter(referral -> Objects.equals(referral.getOtherPersonId(), otherPerson.getId()))
                .filter(referral -> Objects.equals(referral.getContainer(), currentContainer))
                .findFirst();
        }
        return person.getReferralsFromMe().stream()
            .filter(referral -> Objects.equals(referral.getOtherPersonId(), otherPerson.getId()))
            .filter(referral -> Objects.equals(referral.getContainer(), currentContainer))
            .findFirst();
    }

    private static PersonReferral.Side parseRole(String role) throws PersonRelationshipV2RestException {
        try {
            return PersonReferral.Side.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(PersonRelationshipV2RestException.class)
                .withErrorCode(PersonRelationshipV2RestException.INVALID_ROLE)
                .addParameter("role", role)
                .withCause(e).build();
        }
    }

    private static void verifySuperUserAuthorization(Authorization authorization)
        throws UserAuthorizationRestException {
        if (!authorization.getScopes().contains(CLIENT_SUPERUSER)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }

}
