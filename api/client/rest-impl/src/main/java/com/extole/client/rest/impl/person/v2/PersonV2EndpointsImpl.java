package com.extole.client.rest.impl.person.v2;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.impl.person.relationship.v2.RelationshipV2RestMapper;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.PersonShareRestException;
import com.extole.client.rest.person.ProfileBlockAction;
import com.extole.client.rest.person.v2.IsSamePersonV2Response;
import com.extole.client.rest.person.v2.PartnerEventIdV2Response;
import com.extole.client.rest.person.v2.PersonGetV2Request;
import com.extole.client.rest.person.v2.PersonRewardV2Response;
import com.extole.client.rest.person.v2.PersonV2Endpoints;
import com.extole.client.rest.person.v2.PersonV2Request;
import com.extole.client.rest.person.v2.PersonV2Response;
import com.extole.client.rest.person.v2.PersonValidationV2RestException;
import com.extole.client.rest.person.v2.RelationshipV2Response;
import com.extole.client.rest.person.v2.ShareV2Response;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmail;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.event.client.ClientEvent;
import com.extole.event.client.ClientEventBuilder;
import com.extole.event.client.ClientEventService;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.user.User;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.model.shared.reward.supplier.ArchivedRewardSupplierCache;
import com.extole.model.shared.user.UserCache;
import com.extole.person.service.RewardSupplierHandle;
import com.extole.person.service.profile.InvalidProfileBlockReasonException;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonBuilder;
import com.extole.person.service.profile.PersonEmailAlreadyDefinedException;
import com.extole.person.service.profile.PersonFirstNameInvalidLengthException;
import com.extole.person.service.profile.PersonLastNameInvalidLengthException;
import com.extole.person.service.profile.PersonMergeException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonOperations;
import com.extole.person.service.profile.PersonPartnerUserIdAlreadyDefinedException;
import com.extole.person.service.profile.PersonPartnerUserIdInvalidLengthException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.ProfileBlock;
import com.extole.person.service.profile.key.PersonKey;
import com.extole.person.service.profile.reward.PersonReward;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.Channel;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.share.PersonShareService;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.running.service.partner.PartnerProfileKeyService;

@Provider
public class PersonV2EndpointsImpl implements PersonV2Endpoints {

    private static final Logger LOG = LoggerFactory.getLogger(PersonV2EndpointsImpl.class);

    private static final String SHARE_DEFAULT_PARTNER_ID_NAME = "partner_share_id";
    private static final String PROFILE_BLOCK_EVENT_NAME = "profile_block";
    private static final String PROFILE_UNBLOCK_EVENT_NAME = "profile_unblock";

    private final PersonService personService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonAuthorizationService personAuthorizationService;
    private final PersonShareService shareService;
    private final ClientShareableService clientShareableService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final VerifiedEmailService verifiedEmailService;
    private final ArchivedRewardSupplierCache archivedRewardSupplierCache;
    private final PartnerProfileKeyService partnerProfileKeyService;
    private final RelationshipV2RestMapper relationshipV2RestMapper;
    private final PersonV2RestMapper personV2RestMapper;
    private final ClientEventService clientEventService;
    private final UserCache userCache;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public PersonV2EndpointsImpl(
        @Context HttpServletRequest servletRequest,
        PersonService personService,
        ClientAuthorizationProvider authorizationProvider,
        PersonAuthorizationService personAuthorizationService,
        PersonShareService shareService,
        ClientShareableService clientShareableService,
        ConsumerEventSenderService consumerEventSenderService,
        VerifiedEmailService verifiedEmailService,
        ArchivedRewardSupplierCache archivedRewardSupplierCache,
        PartnerProfileKeyService partnerProfileKeyService,
        RelationshipV2RestMapper relationshipV2RestMapper,
        PersonV2RestMapper personV2RestMapper,
        ClientEventService clientEventService,
        UserCache userCache,
        ClientRequestContextService clientRequestContextService) {
        this.servletRequest = servletRequest;
        this.personService = personService;
        this.authorizationProvider = authorizationProvider;
        this.personAuthorizationService = personAuthorizationService;
        this.shareService = shareService;
        this.clientShareableService = clientShareableService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.verifiedEmailService = verifiedEmailService;
        this.archivedRewardSupplierCache = archivedRewardSupplierCache;
        this.partnerProfileKeyService = partnerProfileKeyService;
        this.relationshipV2RestMapper = relationshipV2RestMapper;
        this.personV2RestMapper = personV2RestMapper;
        this.clientEventService = clientEventService;
        this.userCache = userCache;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public List<PersonV2Response> get(String accessToken, PersonGetV2Request request, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        boolean searchByEmail = !Strings.isNullOrEmpty(request.getEmail());
        boolean searchByPartnerUserId = !Strings.isNullOrEmpty(request.getPartnerUserId());
        boolean searchByLastName = !Strings.isNullOrEmpty(request.getLastName());
        boolean searchByPartnerIds = request.getPartnerIds() != null && !request.getPartnerIds().isEmpty()
            && request.getPartnerIds().stream()
                .anyMatch(partnerId -> !Strings.isNullOrEmpty(partnerId));

        try {
            if (searchByEmail) {
                Optional<Person> byEmail =
                    personService.getPersonByProfileLookupKey(authorization, PersonKey.ofEmailType(request.getEmail()));
                if (byEmail.isPresent()) {
                    return Collections
                        .singletonList(personV2RestMapper.toPersonResponse(byEmail.get(), timeZone));
                }
            } else if (searchByPartnerUserId) {
                Optional<Person> byPartnerUserId = personService.getPersonByProfileLookupKey(authorization,
                    PersonKey.ofPartnerUserIdType(request.getPartnerUserId()));
                if (byPartnerUserId.isPresent()) {
                    return Collections
                        .singletonList(personV2RestMapper.toPersonResponse(byPartnerUserId.get(), timeZone));
                }
            } else if (searchByLastName) {
                return personService
                    .getPersonsByLastName(authorization, request.getLastName(), request.getLimit(), request.getOffset())
                    .stream()
                    .map(person -> personV2RestMapper.toPersonResponse(person, timeZone))
                    .collect(Collectors.toList());
            } else if (searchByPartnerIds) {
                List<PersonKey> personKeys = getPersonKeysFromPartnerIds(authorization, request.getPartnerIds());

                Builder<PersonV2Response> personsByPersonKeyListBuilder = ImmutableList.builder();
                for (PersonKey personKey : personKeys) {
                    personService.getPersonByProfileLookupKey(authorization, personKey)
                        .ifPresent(
                            person -> personsByPersonKeyListBuilder.add(personV2RestMapper.toPersonResponse(person,
                                timeZone)));
                }
                return subList(personsByPersonKeyListBuilder.build(), request.getOffset(), request.getLimit());
            } else {
                LOG.debug("No search criteria provided, returning empty result");
                return Collections.emptyList();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
        return Collections.emptyList();
    }

    @Override
    public PersonV2Response create(String accessToken, PersonV2Request personRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonValidationV2RestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            String email = personRequest.getEmail();
            VerifiedEmail verifiedEmail;
            if (Strings.isNullOrEmpty(email)) {
                verifiedEmail = null;
            } else {
                verifiedEmail = verifiedEmailService.verifyEmail(email);
            }

            PersonOperations personOperations = consumerEventSenderService.createConsumerEventSender()
                .log("New person created via client person endpoints");
            Person updatedPerson =
                personService.newPerson(authorization,
                    new LockDescription("person-endpoints-create"), personBuilder -> {
                        try {
                            if (verifiedEmail != null) {
                                personBuilder.withEmail(verifiedEmail.getEmail());
                                personOperations.log("Person has email: " + email);
                            }
                            if (!Strings.isNullOrEmpty(personRequest.getPartnerUserId())) {
                                personBuilder.withPartnerUserId(personRequest.getPartnerUserId());
                            }
                            if (!Strings.isNullOrEmpty(personRequest.getFirstName())) {
                                personBuilder.withFirstName(personRequest.getFirstName());
                            }
                            if (!Strings.isNullOrEmpty(personRequest.getLastName())) {
                                personBuilder.withLastName(personRequest.getLastName());
                            }
                            if (personRequest.getBlocked() != null) {
                                if (personRequest.getBlocked().booleanValue()) {
                                    personBuilder.withProfileBlock(authorization.getIdentityId(),
                                        "using old boolean blocked");
                                } else {
                                    personBuilder.removeProfileBlock();
                                }
                            }
                            if (personRequest.getProfileBlock() != null) {
                                if (ProfileBlockAction.BLOCK == personRequest.getProfileBlock().getAction()) {
                                    personBuilder.withProfileBlock(authorization.getIdentityId(),
                                        personRequest.getProfileBlock().getBlockReason());
                                } else {
                                    personBuilder.removeProfileBlock();
                                }
                            }
                            if (personRequest.getSelfRewardingBlocked() != null) {
                                personBuilder.withSelfRewardingBlocked(
                                    personRequest.getSelfRewardingBlocked().booleanValue());
                            }
                            if (personRequest.getFriendRewardingBlocked() != null) {
                                personBuilder.withFriendRewardingBlocked(
                                    personRequest.getFriendRewardingBlocked().booleanValue());
                            }
                            fillPictureUrl(personRequest.getPictureUrl(), personBuilder);
                            return personBuilder.save();
                        } catch (PersonPartnerUserIdAlreadyDefinedException | PersonPartnerUserIdInvalidLengthException
                            | PersonFirstNameInvalidLengthException | PersonLastNameInvalidLengthException
                            | PersonValidationV2RestException | PersonEmailAlreadyDefinedException
                            | InvalidProfileBlockReasonException e) {
                            throw new LockClosureException(e);
                        }
                    }, personOperations);
            // ENG-19642 we cannot send an input event when the person is not created yet
            sendEvents(authorization, personRequest, updatedPerson);
            return personV2RestMapper.toPersonResponse(updatedPerson, timeZone);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonPartnerUserIdInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.PARTNER_USER_ID_INVALID_LENGTH)
                    .addParameter("partner_user_id", personRequest.getPartnerUserId()).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonFirstNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.FIRST_NAME_INVALID_LENGTH)
                    .addParameter("first_name", personRequest.getFirstName()).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonLastNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.LAST_NAME_INVALID_LENGTH)
                    .addParameter("last_name", personRequest.getLastName()).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(InvalidProfileBlockReasonException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.INVALID_BLOCK_REASON).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonValidationV2RestException.class)) {
                throw (PersonValidationV2RestException) cause;
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                .withErrorCode(PersonValidationV2RestException.EMAIL_INVALID)
                .addParameter("email", personRequest.getEmail())
                .withCause(e).build();
        } catch (EventProcessorException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public PersonV2Response update(String accessToken, String personId, PersonV2Request personRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonValidationV2RestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            String email = personRequest.getEmail();
            VerifiedEmail verifiedEmail;
            if (Strings.isNullOrEmpty(email)) {
                verifiedEmail = null;
            } else {
                verifiedEmail = verifiedEmailService.verifyEmail(email);
            }
            PersonOperations personOperations = consumerEventSenderService.createConsumerEventSender()
                .log("Person updated via client person endpoints." + " Person id: " + personId);
            Person updatedPerson = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-endpoints-update"),
                (personBuilder, initialPerson) -> {
                    try {
                        if (verifiedEmail != null) {
                            personBuilder.withEmail(verifiedEmail.getEmail());
                            personOperations.log("Email updated: " + email);
                        }
                        if (!Strings.isNullOrEmpty(personRequest.getPartnerUserId())) {
                            personBuilder.withPartnerUserId(personRequest.getPartnerUserId());
                        }
                        if (personRequest.getFirstName() != null) {
                            personBuilder.withFirstName(Strings.emptyToNull(personRequest.getFirstName()));
                        }
                        if (personRequest.getLastName() != null) {
                            personBuilder.withLastName(Strings.emptyToNull(personRequest.getLastName()));
                        }
                        if (personRequest.getBlocked() != null) {
                            if (personRequest.getBlocked().booleanValue()) {
                                personBuilder.withProfileBlock(authorization.getIdentityId(),
                                    "using old boolean blocked");
                            } else {
                                personBuilder.removeProfileBlock();
                            }
                        }
                        if (personRequest.getProfileBlock() != null) {
                            if (ProfileBlockAction.BLOCK == personRequest.getProfileBlock().getAction()) {
                                personBuilder.withProfileBlock(authorization.getIdentityId(),
                                    personRequest.getProfileBlock().getBlockReason());
                            } else {
                                personBuilder.removeProfileBlock();
                            }
                        }
                        if (personRequest.getSelfRewardingBlocked() != null) {
                            personBuilder.withSelfRewardingBlocked(
                                personRequest.getSelfRewardingBlocked().booleanValue());
                        }
                        if (personRequest.getFriendRewardingBlocked() != null) {
                            personBuilder.withFriendRewardingBlocked(
                                personRequest.getFriendRewardingBlocked().booleanValue());
                        }
                        fillPictureUrl(personRequest.getPictureUrl(), personBuilder);
                        return personBuilder.save();
                    } catch (PersonEmailAlreadyDefinedException | PersonPartnerUserIdInvalidLengthException
                        | PersonFirstNameInvalidLengthException | PersonLastNameInvalidLengthException
                        | PersonPartnerUserIdAlreadyDefinedException | PersonValidationV2RestException
                        | InvalidProfileBlockReasonException e) {
                        throw new LockClosureException(e);
                    }
                }, personOperations);
            // ENG-19642 sending the input event separately just to have the same syntax as in the update method
            sendEvents(authorization, personRequest, updatedPerson);
            return personV2RestMapper.toPersonResponse(updatedPerson, timeZone);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonEmailAlreadyDefinedException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.PERSON_EMAIL_ALREADY_DEFINED)
                    .addParameter("email", personRequest.getEmail()).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonPartnerUserIdInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.PARTNER_USER_ID_INVALID_LENGTH)
                    .addParameter("partner_user_id", personRequest.getPartnerUserId()).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonFirstNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.FIRST_NAME_INVALID_LENGTH)
                    .addParameter("first_name", personRequest.getFirstName()).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonLastNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.LAST_NAME_INVALID_LENGTH)
                    .addParameter("last_name", personRequest.getLastName()).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonPartnerUserIdAlreadyDefinedException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.PERSON_PARTNER_USER_ID_ALREADY_DEFINED)
                    .addParameter("partner_user_id", personRequest.getPartnerUserId()).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonValidationV2RestException.class)) {
                throw (PersonValidationV2RestException) cause;
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                .withErrorCode(PersonValidationV2RestException.EMAIL_INVALID)
                .addParameter("email", personRequest.getEmail())
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public PersonV2Response getPerson(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personV2RestMapper
                .toPersonResponse(personService.getPerson(authorization, Id.valueOf(personId)), timeZone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public IsSamePersonV2Response isSamePerson(String accessToken, String firstPersonId, String secondPersonId)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return new IsSamePersonV2Response(Boolean.valueOf(personService.isSamePerson(authorization.getClientId(),
            Id.valueOf(firstPersonId), Id.valueOf(secondPersonId))));
    }

    @Override
    public List<RelationshipV2Response> getAssociatedAdvocates(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            return relationshipV2RestMapper.toRelationshipResponses(authorization, person.getId(),
                person.getIdentifiedAdvocates(), timeZone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public List<RelationshipV2Response> getAssociatedFriends(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            return relationshipV2RestMapper.toRelationshipResponses(authorization, person.getId(),
                person.getIdentifiedFriends(), timeZone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public List<RelationshipV2Response> getReferralsFromPerson(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            return relationshipV2RestMapper.toRelationshipResponses(authorization, person.getId(), person.getFriends(),
                timeZone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public List<RelationshipV2Response> getReferralsToPerson(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            return relationshipV2RestMapper.toRelationshipResponses(authorization, person.getId(),
                person.getAdvocates(), timeZone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public List<PersonRewardV2Response> getRewards(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization clientAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personService.getPerson(clientAuthorization, Id.valueOf(personId)).getRewards().stream()
                .map(personReward -> toPersonRewardResponse(clientAuthorization.getClientId(), personReward, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        }
    }

    @Override
    public ShareV2Response getShare(String accessToken, String personId, String shareId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return shareService.getShares(authorization, Id.valueOf(personId)).stream()
                .filter(share -> Objects.equal(share.getId(), Id.valueOf(shareId)))
                .map(share -> actionToShareResponse(authorization, share, timeZone))
                .findAny()
                .orElseThrow(() -> RestExceptionBuilder.newBuilder(PersonShareRestException.class)
                    .withErrorCode(PersonShareRestException.SHARE_NOT_FOUND)
                    .addParameter("share_id", shareId)
                    .addParameter("person_id", personId)
                    .build());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<ShareV2Response> getShares(String accessToken, String personId, @Nullable String partnerShareId,
        @Nullable String partnerId, ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            if (partnerShareId != null) {
                PartnerEventId partnerEventId = PartnerEventId.of(SHARE_DEFAULT_PARTNER_ID_NAME, partnerShareId);

                return shareService.getShares(authorization, Id.valueOf(personId)).stream()
                    .filter(share -> Objects.equal(share.getPartnerId().orElse(null), partnerEventId))
                    .map(share -> actionToShareResponse(authorization, share, timeZone))
                    .collect(Collectors.toList());
            }

            if (partnerId != null) {
                String partnerEventIdName = StringUtils.substringBefore(partnerId, ":");
                String partnerEventIdValue = StringUtils.substringAfter(partnerId, ":");
                if (StringUtils.isEmpty(partnerEventIdName) || StringUtils.isEmpty(partnerEventIdValue)) {
                    return Collections.emptyList();
                }

                PartnerEventId partnerEventId = PartnerEventId.of(partnerEventIdName, partnerEventIdValue);

                return shareService.getShares(authorization, Id.valueOf(personId)).stream()
                    .filter(share -> Objects.equal(share.getPartnerId().orElse(null), partnerEventId))
                    .map(share -> actionToShareResponse(authorization, share, timeZone))
                    .collect(Collectors.toList());
            }

            return shareService.getShares(authorization, Id.valueOf(personId)).stream()
                .map(share -> actionToShareResponse(authorization, share, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        }
    }

    @Override
    public PersonV2Response mergeDeviceToIdentity(String accessToken, String personId, String identityId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            Authorization consumerAuthorization = personAuthorizationService.authorize(authorization, person);

            PersonOperations personOperations = consumerEventSenderService.createConsumerEventSender()
                .log("Device: " + personId + " was merged to new identity: " + identityId
                    + " via client person endpoints");

            Person updatedPerson = personService.mergeDeviceToIdentity(consumerAuthorization, Id.valueOf(personId),
                Id.valueOf(identityId), personOperations);
            return personV2RestMapper.toPersonResponse(updatedPerson, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId).withCause(e)
                .build();
        } catch (PersonMergeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e.getCause())
                .build();
        }
    }

    @Override
    public PersonV2Response mergeIdentityToIdentity(String accessToken, String personId, String identityId,
        ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            Authorization consumerAuthorization = personAuthorizationService.authorize(authorization, person);

            PersonOperations personOperations = consumerEventSenderService.createConsumerEventSender()
                .log("Identity: " + personId + " was turned into a device and merged to new identity: " + identityId
                    + " via client person endpoints");

            Person updatedPerson = personService.mergeIdentityToIdentity(consumerAuthorization, Id.valueOf(personId),
                Id.valueOf(identityId), personOperations);
            return personV2RestMapper.toPersonResponse(updatedPerson, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId).withCause(e)
                .build();
        } catch (PersonMergeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e.getCause())
                .build();
        }
    }

    private PersonRewardV2Response toPersonRewardResponse(Id<ClientHandle> clientId, PersonReward reward,
        ZoneId timeZone) {
        Id<RewardSupplierHandle> rewardSupplierId = reward.getRewardSupplierId();
        Optional<String> partnerRewardSupplierId = Optional.empty();
        try {
            RewardSupplier rewardSupplier = archivedRewardSupplierCache.getRewardSupplier(clientId,
                Id.valueOf(rewardSupplierId.getValue()));
            partnerRewardSupplierId = rewardSupplier.getPartnerRewardSupplierId();
        } catch (RewardSupplierNotFoundException e) {
            LOG.warn("Unable to find rewardSupplierById for client_id = {}, rewardSupplierId = {}",
                clientId, rewardSupplierId, e);
        }

        return new PersonRewardV2Response(reward.getId().getValue(),
            reward.getRewardId().getValue(),
            partnerRewardSupplierId.orElse(null),
            reward.getRewardSupplierId().getValue(),
            formatFaceValue(reward.getFaceValue()),
            formatFaceValue(reward.getFaceValue()),
            reward.getFaceValueType().name(),
            reward.getRewardedDate().atZone(timeZone),
            reward.getState().name().toLowerCase(),
            reward.getCampaignId().getValue(),
            reward.getProgramLabel(),
            reward.getSandbox(),
            reward.getPartnerRewardId().orElse(null),
            reward.getRewardedDate().atZone(timeZone),
            reward.getRewardSlots().stream().collect(Collectors.toList()),
            new ArrayList<>(reward.getRewardSlots()),
            reward.getRewardName().orElse(null),
            reward.getValueOfRewardedEvent(),
            reward.getData(),
            reward.getExpiryDate().map(expiryDate -> expiryDate.atZone(timeZone)).orElse(null),
            reward.getRedeemedDate().map(redeemedDate -> redeemedDate.atZone(timeZone)));
    }

    private static void fillPictureUrl(String pictureUrl, PersonBuilder personBuilder)
        throws PersonValidationV2RestException {
        if (pictureUrl != null) {
            try {
                if (!pictureUrl.isEmpty()) {
                    personBuilder.withProfilePictureUrl(new URL(pictureUrl).toURI());
                } else {
                    personBuilder.withProfilePictureUrl(null);
                }
            } catch (URISyntaxException | MalformedURLException e) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV2RestException.class)
                    .withErrorCode(PersonValidationV2RestException.PICTURE_URL_INVALID)
                    .addParameter("picture_url", pictureUrl).withCause(e).build();
            }
        }
    }

    private ShareV2Response actionToShareResponse(Authorization authorization, PersonShare share, ZoneId timeZone) {
        PersonV2Response friend = null;
        try {
            if (share.getEmail().isPresent()) {
                Optional<Person> friendPerson = personService.getPersonByProfileLookupKey(authorization,
                    PersonKey.ofEmailType(share.getEmail().get()));
                if (friendPerson.isPresent()) {
                    friend = personV2RestMapper.toPersonResponse(friendPerson.get(), timeZone);
                } else {
                    LOG.warn("Unable to find friend person [clientId={}, email={}] for share response",
                        authorization.getClientId(), share.getEmail().get());
                }
            }
        } catch (AuthorizationException ignored) {
            // should not happen
        }

        String shareableLink = null;
        try {
            shareableLink = clientShareableService
                .get(authorization, Id.valueOf(share.getShareableId().getValue())).getLink().toString();
        } catch (ShareableNotFoundException e) {
            LOG.warn("Can not find shareable {} for share {}", share.getShareableId(), share.getId(), e);
        }

        PartnerEventIdV2Response sharePartnerId = share.getPartnerId()
            .map(partnerId -> new PartnerEventIdV2Response(partnerId.getName(), partnerId.getValue()))
            .orElse(null);

        return new ShareV2Response(share.getChannel().map(Channel::getName).orElse(null),
            share.getShareDate().atZone(timeZone), share.getEmail().orElse(null), friend, shareableLink,
            sharePartnerId, share.getMessage().orElse(null), share.getSubject().orElse(null));
    }

    private String formatFaceValue(BigDecimal faceValue) {
        return faceValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private List<PersonKey> getPersonKeysFromPartnerIds(Authorization authorization, List<String> partnerIds) {
        Set<String> recognizedDynamicKeyTypes = getRecognizedDynamicKeyTypes(authorization);

        return partnerIds.stream()
            .map(partnerId -> Pair.of(StringUtils.substringBefore(partnerId, ":"),
                StringUtils.substringAfter(partnerId, ":")))
            .filter(pair -> StringUtils.isNotBlank(pair.getLeft()))
            .filter(pair -> StringUtils.isNotBlank(pair.getRight()))
            .filter(pair -> recognizedDynamicKeyTypes.contains(pair.getKey().trim().toUpperCase()))
            .map(pair -> PersonKey.ofType(pair.getKey(), pair.getValue()))
            .collect(Collectors.toList());
    }

    private Set<String> getRecognizedDynamicKeyTypes(Authorization authorization) {
        return partnerProfileKeyService.getPartnerProfileKeyNames(authorization.getClientId()).stream()
            .map(String::toUpperCase)
            .collect(Collectors.toSet());
    }

    private static List<PersonV2Response> subList(List<PersonV2Response> persons, Integer offset, Integer limit) {
        if (offset.intValue() < 0 || limit.intValue() < 1) {
            return Collections.emptyList();
        }

        int fromIndex = offset.intValue();
        int toIndex = offset.intValue() + limit.intValue();

        if (fromIndex >= persons.size()) {
            return Collections.emptyList();
        }

        if (toIndex > persons.size()) {
            toIndex = persons.size();
        }

        return persons.subList(fromIndex, toIndex);
    }

    private void sendEvents(ClientAuthorization authorization, PersonV2Request request, Person person)
        throws EventProcessorException, AuthorizationException, PersonNotFoundException {
        boolean sendBlockEvent = false;
        boolean sendUnBlockEvent = false;
        if (request.getBlocked() != null) {
            if (request.getBlocked().booleanValue()) {
                sendBlockEvent = true;
            } else {
                sendUnBlockEvent = true;
            }
        } else if (request.getProfileBlock() != null) {
            if (request.getProfileBlock().getAction().equals(ProfileBlockAction.BLOCK)) {
                sendBlockEvent = true;
            } else {
                sendUnBlockEvent = true;
            }
        }
        if (sendBlockEvent) {
            sendInputEvent(authorization, person, PROFILE_BLOCK_EVENT_NAME);
            sentProfileBlockClientEvent(authorization, PROFILE_BLOCK_EVENT_NAME, "Customer was blocked", person);
        } else if (sendUnBlockEvent) {
            sendInputEvent(authorization, person, PROFILE_UNBLOCK_EVENT_NAME);
            sentProfileBlockClientEvent(authorization, PROFILE_UNBLOCK_EVENT_NAME, "Customer was unblocked", person);
        } else {
            sendInputEvent(authorization, person, ConsumerEventName.EXTOLE_PROFILE.getEventName());
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
            .withScope(com.extole.event.client.Scope.CLIENT_SUPERUSER);
        builder.addData("person_id", person.getId().getValue());
        person.getProfileBlock().flatMap(ProfileBlock::getReason)
            .ifPresent(reason -> builder.addData("reason", reason));
        userCache.getByAuthorization(authorization).map(User::getId).ifPresent(id -> builder.withUserId(id));
        builder.send();
    }

}
