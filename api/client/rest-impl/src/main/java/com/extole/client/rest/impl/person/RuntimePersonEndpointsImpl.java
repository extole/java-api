package com.extole.client.rest.impl.person;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.IsSamePersonResponse;
import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.person.PartnerEventIdResponse;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonLocaleResponse;
import com.extole.client.rest.person.PersonQueryRestException;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.PersonShareRestException;
import com.extole.client.rest.person.ProfileBlockAction;
import com.extole.client.rest.person.RuntimePersonEndpoints;
import com.extole.client.rest.person.StepQuality;
import com.extole.client.rest.person.StepScope;
import com.extole.client.rest.person.v4.PersonDataV4Response;
import com.extole.client.rest.person.v4.PersonGetV4Request;
import com.extole.client.rest.person.v4.PersonRelationshipV4Response;
import com.extole.client.rest.person.v4.PersonRelationshipV4RestException;
import com.extole.client.rest.person.v4.PersonRequestContextV4Response;
import com.extole.client.rest.person.v4.PersonRewardV4Response;
import com.extole.client.rest.person.v4.PersonShareV4Response;
import com.extole.client.rest.person.v4.PersonStepV4Response;
import com.extole.client.rest.person.v4.PersonV4Request;
import com.extole.client.rest.person.v4.PersonV4Response;
import com.extole.client.rest.person.v4.PersonValidationV4RestException;
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
import com.extole.model.shared.reward.supplier.ArchivedRewardSupplierCache;
import com.extole.model.shared.user.UserCache;
import com.extole.person.service.RewardSupplierHandle;
import com.extole.person.service.profile.InvalidProfileBlockReasonException;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonBuilder;
import com.extole.person.service.profile.PersonEmailAlreadyDefinedException;
import com.extole.person.service.profile.PersonFirstNameInvalidLengthException;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonLastNameInvalidLengthException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonOperations;
import com.extole.person.service.profile.PersonPartnerUserIdAlreadyDefinedException;
import com.extole.person.service.profile.PersonPartnerUserIdInvalidLengthException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.ProfileBlock;
import com.extole.person.service.profile.journey.Container;
import com.extole.person.service.profile.key.PersonKey;
import com.extole.person.service.profile.locale.PersonLocale;
import com.extole.person.service.profile.referral.PersonReferral;
import com.extole.person.service.profile.reward.PersonReward;
import com.extole.person.service.profile.reward.PersonRewardState;
import com.extole.person.service.profile.reward.PersonRewardSupplierType;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.profile.step.PersonStep;
import com.extole.person.service.profile.step.PersonStepData;
import com.extole.person.service.reward.PersonRewardService;
import com.extole.person.service.reward.RuntimePersonRewardQueryBuilder;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.share.PersonShareService;
import com.extole.running.service.partner.PartnerProfileKeyService;

@Provider
public class RuntimePersonEndpointsImpl implements RuntimePersonEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(RuntimePersonEndpointsImpl.class);

    private static final String SHARE_DEFAULT_PARTNER_ID_NAME = "partner_share_id";
    private static final String ALL_CONTAINERS = "*";
    private static final String COMMA = ",";
    private static final String PROFILE_BLOCK_EVENT_NAME = "profile_block";
    private static final String PROFILE_UNBLOCK_EVENT_NAME = "profile_unblock";

    private final PersonService personService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonRewardService personRewardService;
    private final PersonShareService shareService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final VerifiedEmailService verifiedEmailService;
    private final ArchivedRewardSupplierCache archivedRewardSupplierCache;
    private final PartnerProfileKeyService partnerProfileKeyService;
    private final PersonShareRestMapper personShareRestMapper;
    private final RequestContextResponseMapper requestContextResponseMapper;
    private final ClientEventService clientEventService;
    private final UserCache userCache;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public RuntimePersonEndpointsImpl(PersonService personService,
        ClientAuthorizationProvider authorizationProvider,
        PersonRewardService personRewardService,
        PersonShareService shareService,
        ConsumerEventSenderService consumerEventSenderService,
        VerifiedEmailService verifiedEmailService,
        ArchivedRewardSupplierCache archivedRewardSupplierCache,
        PartnerProfileKeyService partnerProfileKeyService,
        PersonShareRestMapper personShareRestMapper,
        RequestContextResponseMapper requestContextResponseMapper,
        ClientEventService clientEventService,
        UserCache userCache,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.personService = personService;
        this.authorizationProvider = authorizationProvider;
        this.personRewardService = personRewardService;
        this.shareService = shareService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.verifiedEmailService = verifiedEmailService;
        this.archivedRewardSupplierCache = archivedRewardSupplierCache;
        this.partnerProfileKeyService = partnerProfileKeyService;
        this.personShareRestMapper = personShareRestMapper;
        this.requestContextResponseMapper = requestContextResponseMapper;
        this.clientEventService = clientEventService;
        this.userCache = userCache;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public List<PersonV4Response> get(String accessToken, PersonGetV4Request request, ZoneId timeZone)
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
                    return Collections.singletonList(personToResponse(byEmail.get()));
                }
            } else if (searchByPartnerUserId) {
                Optional<Person> byPartnerUserId = personService.getPersonByProfileLookupKey(authorization,
                    PersonKey.ofPartnerUserIdType(request.getPartnerUserId()));
                if (byPartnerUserId.isPresent()) {
                    return Collections.singletonList(personToResponse(byPartnerUserId.get()));
                }
            } else if (searchByLastName) {
                return personService
                    .getPersonsByLastName(authorization, request.getLastName(), request.getLimit(), request.getOffset())
                    .stream()
                    .map(person -> personToResponse(person))
                    .collect(Collectors.toList());
            } else if (searchByPartnerIds) {
                List<PersonKey> personKeys = getPersonKeysFromPartnerIds(authorization, request.getPartnerIds());

                Builder<PersonV4Response> personsByPersonKeyListBuilder = ImmutableList.builder();
                for (PersonKey personKey : personKeys) {
                    personService.getPersonByProfileLookupKey(authorization, personKey)
                        .ifPresent(person -> personsByPersonKeyListBuilder.add(personToResponse(person)));
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
    public PersonV4Response create(String accessToken, PersonV4Request personRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonValidationV4RestException {
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
                .log("New person created via client /v4/runtime-persons endpoints");
            Person updatedPerson =
                personService.newPerson(authorization,
                    new LockDescription("person-v4-endpoints-create"), personBuilder -> {
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
                                personBuilder
                                    .withSelfRewardingBlocked(personRequest.getSelfRewardingBlocked().booleanValue());
                            }
                            if (personRequest.getFriendRewardingBlocked() != null) {
                                personBuilder.withFriendRewardingBlocked(
                                    personRequest.getFriendRewardingBlocked().booleanValue());
                            }
                            fillPictureUrl(personRequest.getPictureUrl(), personBuilder);
                            return personBuilder.save();
                        } catch (PersonPartnerUserIdAlreadyDefinedException | PersonPartnerUserIdInvalidLengthException
                            | PersonFirstNameInvalidLengthException | PersonLastNameInvalidLengthException
                            | PersonValidationV4RestException | PersonEmailAlreadyDefinedException
                            | InvalidProfileBlockReasonException e) {
                            throw new LockClosureException(e);
                        }
                    }, personOperations);
            // ENG-19642 we cannot send an input event when the person is not created yet
            sendEvents(authorization, personRequest, updatedPerson);
            return personToResponse(updatedPerson);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonPartnerUserIdInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.PARTNER_USER_ID_INVALID_LENGTH)
                    .addParameter("partner_user_id", personRequest.getPartnerUserId()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonFirstNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.FIRST_NAME_INVALID_LENGTH)
                    .addParameter("first_name", personRequest.getFirstName()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonLastNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.LAST_NAME_INVALID_LENGTH)
                    .addParameter("last_name", personRequest.getLastName()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(InvalidProfileBlockReasonException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.INVALID_BLOCK_REASON).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonValidationV4RestException.class)) {
                throw (PersonValidationV4RestException) cause;
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                .withErrorCode(PersonValidationV4RestException.EMAIL_INVALID)
                .addParameter("email", personRequest.getEmail())
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public PersonV4Response update(String accessToken, String personId, PersonV4Request personRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonValidationV4RestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            String email = personRequest.getEmail();
            VerifiedEmail verifiedEmail;
            if (Strings.isNullOrEmpty(email)) {
                verifiedEmail = null;
            } else {
                verifiedEmail = verifiedEmailService.verifyEmail(email);
            }
            PersonOperations personOperations =
                consumerEventSenderService.createConsumerEventSender()
                    .log("Person updated via client /v4/runtime-persons endpoints." + " Person id: " + personId);
            Person updatedPerson = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-v4-endpoints-update"), (personBuilder, originalPersonProfile) -> {
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
                            personBuilder
                                .withSelfRewardingBlocked(
                                    personRequest.getSelfRewardingBlocked().booleanValue());
                        }
                        if (personRequest.getFriendRewardingBlocked() != null) {
                            personBuilder
                                .withFriendRewardingBlocked(
                                    personRequest.getFriendRewardingBlocked().booleanValue());
                        }
                        fillPictureUrl(personRequest.getPictureUrl(), personBuilder);
                        return personBuilder.save();
                    } catch (PersonEmailAlreadyDefinedException | PersonPartnerUserIdInvalidLengthException
                        | PersonPartnerUserIdAlreadyDefinedException | PersonFirstNameInvalidLengthException
                        | PersonLastNameInvalidLengthException | InvalidProfileBlockReasonException
                        | PersonValidationV4RestException e) {
                        throw new LockClosureException(e);
                    }
                }, personOperations);
            // ENG-19642 sending the input event separately just to have the same syntax as in the update method
            sendEvents(authorization, personRequest, updatedPerson);
            return personToResponse(updatedPerson);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonEmailAlreadyDefinedException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.PERSON_EMAIL_ALREADY_DEFINED)
                    .addParameter("email", personRequest.getEmail()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonPartnerUserIdInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.PARTNER_USER_ID_INVALID_LENGTH)
                    .addParameter("partner_user_id", personRequest.getPartnerUserId()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonPartnerUserIdAlreadyDefinedException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.PERSON_PARTNER_USER_ID_ALREADY_DEFINED)
                    .addParameter("partner_user_id", personRequest.getPartnerUserId()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonFirstNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.FIRST_NAME_INVALID_LENGTH)
                    .addParameter("first_name", personRequest.getFirstName()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonLastNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.LAST_NAME_INVALID_LENGTH)
                    .addParameter("last_name", personRequest.getLastName()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonValidationV4RestException.class)) {
                throw (PersonValidationV4RestException) cause;
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
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                .withErrorCode(PersonValidationV4RestException.EMAIL_INVALID)
                .addParameter("email", personRequest.getEmail())
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public PersonV4Response getPerson(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personToResponse(personService.getPerson(authorization, Id.valueOf(personId)));
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND).addParameter("person_id", personId).withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public IsSamePersonResponse isSamePerson(String accessToken, String firstPersonId, String secondPersonId)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return new IsSamePersonResponse(Boolean.valueOf(personService.isSamePerson(authorization.getClientId(),
            Id.valueOf(firstPersonId), Id.valueOf(secondPersonId))));
    }

    @Override
    public List<PersonRelationshipV4Response> getRelationships(String accessToken, String personId, String role,
        Boolean excludeAnonymous, boolean includeDuplicateIdentities, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonRelationshipV4RestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));

            List<PersonReferral> relationships = Lists.newArrayList();
            Supplier<List<PersonReferral>> advocatesProvider;
            Supplier<List<PersonReferral>> friendsProvider;
            if (excludeAnonymous != null && excludeAnonymous.booleanValue()) {
                advocatesProvider = () -> person.getIdentifiedAdvocates();
                friendsProvider = () -> person.getIdentifiedFriends();
            } else {
                advocatesProvider = () -> person.getAdvocates();
                friendsProvider = () -> person.getFriends();
            }

            if (Strings.isNullOrEmpty(role)) {
                relationships.addAll(advocatesProvider.get());
                relationships.addAll(friendsProvider.get());
            } else {
                PersonReferral.Side parsedRole = parseRole(role);
                if (parsedRole == PersonReferral.Side.FRIEND) {
                    relationships.addAll(friendsProvider.get());
                } else {
                    relationships.addAll(advocatesProvider.get());
                }
            }

            if (!includeDuplicateIdentities) {
                relationships = selectLatestRelationshipPerOtherPerson(relationships);
            }

            relationships.sort(Comparator.comparing(PersonReferral::getUpdatedDate).reversed());

            List<PersonRelationshipV4Response> result = new ArrayList<>();
            for (PersonReferral referral : relationships) {
                result.add(toRelationshipResponse(timeZone, referral));
            }
            return result;
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
    public List<PersonRewardV4Response> getRewards(String accessToken, String personId, @Nullable String programLabel,
        @Nullable String campaignId, @Nullable String rewardStates, @Nullable String rewardTypes, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonQueryRestException {
        Authorization clientAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RuntimePersonRewardQueryBuilder builder =
                personRewardService.createRewardQueryBuilder(clientAuthorization, Id.valueOf(personId));
            if (!Strings.isNullOrEmpty(campaignId)) {
                builder.withCampaignId(Id.valueOf(campaignId));
            }
            if (!Strings.isNullOrEmpty(programLabel)) {
                builder.withProgramLabel(programLabel);
            }
            if (!Strings.isNullOrEmpty(rewardStates)) {
                builder.withRewardStates(parseRewardStates(rewardStates));
            }
            if (!Strings.isNullOrEmpty(rewardTypes)) {
                builder.withRewardTypes(parseRewardTypes(rewardTypes));
            }
            return builder.list().stream()
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
    public PersonShareV4Response getShare(String accessToken, String personId, String shareId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            PersonShare shareById = shareService.getShares(authorization, Id.valueOf(personId)).stream()
                .filter(share -> Objects.equal(share.getId(), Id.valueOf(shareId)))
                .findAny()
                .orElseThrow(() -> RestExceptionBuilder.newBuilder(PersonShareRestException.class)
                    .withErrorCode(PersonShareRestException.SHARE_NOT_FOUND)
                    .addParameter("share_id", shareId)
                    .addParameter("person_id", personId)
                    .build());
            return personShareRestMapper.toPersonShareResponse(authorization, shareById, timeZone);
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
    public List<PersonShareV4Response> getShares(String accessToken, String personId,
        @Nullable String partnerShareId, @Nullable String partnerId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            if (partnerShareId != null) {
                PartnerEventId partnerEventId = PartnerEventId.of(SHARE_DEFAULT_PARTNER_ID_NAME, partnerShareId);

                List<PersonShare> sharesByPartnerShareId =
                    shareService.getShares(authorization, Id.valueOf(personId)).stream()
                        .filter(share -> Objects.equal(share.getPartnerId().orElse(null), partnerEventId))
                        .collect(Collectors.toList());

                return toPersonShareResponses(authorization, sharesByPartnerShareId, timeZone);
            }

            if (partnerId != null) {
                Optional<PartnerEventId> partnerEventId = parsePartnerId(partnerId);

                List<PersonShare> sharesByPartnerId =
                    shareService.getShares(authorization, Id.valueOf(personId)).stream()
                        .filter(share -> !partnerEventId.isPresent() || partnerEventId.equals(share.getPartnerId()))
                        .collect(Collectors.toList());

                return toPersonShareResponses(authorization, sharesByPartnerId, timeZone);
            }

            List<PersonShare> allShares = shareService.getShares(authorization, Id.valueOf(personId));
            return toPersonShareResponses(authorization, allShares, timeZone);
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

    // CHECKSTYLE.OFF: ParameterNumber
    @Override
    public List<PersonStepV4Response> getSteps(String accessToken, String personId, String container, String campaignId,
        String programLabel, String stepName, StepQuality quality, String partnerId, String eventId,
        String causeEventId, String rootEventId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Optional<PartnerEventId> partnerEventId = parsePartnerId(partnerId);
            return personService.getPerson(userAuthorization, Id.valueOf(personId))
                .getSteps().stream()
                .filter(step -> StringUtils.isBlank(campaignId)
                    || Objects.equal(campaignId, step.getCampaignId().map(Id::getValue).orElse(null)))
                .filter(step -> ALL_CONTAINERS.equalsIgnoreCase(container)
                    || Optional.ofNullable(container).orElse(Container.DEFAULT.getName())
                        .equalsIgnoreCase(step.getContainer().getName()))
                .filter(step -> StringUtils.isBlank(programLabel)
                    || Objects.equal(programLabel, step.getProgramLabel().orElse(null)))
                .filter(step -> StringUtils.isBlank(stepName) || Objects.equal(stepName, step.getStepName()))
                .filter(
                    step -> quality == null || Objects.equal(quality, StepQuality.valueOf(step.getQuality().name())))
                .filter(step -> !partnerEventId.isPresent() || StringUtils.isBlank(partnerEventId.get().getName()) ||
                    partnerEventId.get().getName()
                        .equalsIgnoreCase(step.getPartnerEventId().map(PartnerEventId::getName).orElse(null)))
                .filter(step -> !partnerEventId.isPresent() || StringUtils.isBlank(partnerEventId.get().getValue()) ||
                    partnerEventId.get().getValue()
                        .equalsIgnoreCase(step.getPartnerEventId().map(PartnerEventId::getValue).orElse(null)))
                .filter(step -> eventId == null || Objects.equal(Id.valueOf(eventId), step.getEventId()))
                .filter(step -> StringUtils.isBlank(causeEventId)
                    || Objects.equal(causeEventId, step.getCauseEventId().getValue()))
                .filter(step -> StringUtils.isBlank(rootEventId)
                    || Objects.equal(rootEventId, step.getRootEventId().getValue()))
                .map(step -> toPersonStepResponse(step, timeZone))
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
    public List<PersonDataV4Response> getData(String accessToken, String personId, @Nullable String name,
        @Nullable PersonDataScope scope) throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Person person = personService.getPerson(userAuthorization, Id.valueOf(personId));
            Builder<PersonDataV4Response> responseBuilder = ImmutableList.builder();
            if (scope == null || scope == PersonDataScope.PUBLIC) {
                responseBuilder.addAll(person.getPublicData()
                    .entrySet().stream()
                    .map(dataEntry -> toPersonDataResponse(dataEntry, PersonDataScope.PUBLIC))
                    .collect(Collectors.toList()));
            }
            if (scope == null || scope == PersonDataScope.PRIVATE) {
                responseBuilder.addAll(person.getPrivateData()
                    .entrySet().stream()
                    .map(dataEntry -> toPersonDataResponse(dataEntry, PersonDataScope.PRIVATE))
                    .collect(Collectors.toList()));
            }
            if (scope == null || scope == PersonDataScope.CLIENT) {
                responseBuilder.addAll(person.getClientData()
                    .entrySet().stream()
                    .map(dataEntry -> toPersonDataResponse(dataEntry, PersonDataScope.CLIENT))
                    .collect(Collectors.toList()));
            }
            return responseBuilder.build().stream()
                .filter(data -> StringUtils.isBlank(name) || name.equalsIgnoreCase(data.getName()))
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
    public Set<String> getPartnerKeys(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (!authorization.isAuthorized(authorization.getClientId(), Authorization.Scope.USER_SUPPORT)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
        return getRecognizedDynamicKeyTypes(authorization);
    }

    @Override
    public List<PersonRequestContextV4Response> getRequestContexts(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personService.getPerson(authorization, Id.valueOf(personId)).getRecentRequestContexts()
                .stream()
                .map(item -> requestContextResponseMapper.toRequestContextV4Response(item, timeZone))
                .collect(Collectors.toList());
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

    private static PersonDataV4Response toPersonDataResponse(Map.Entry<String, Object> dataEntry,
        PersonDataScope scope) {
        return new PersonDataV4Response(dataEntry.getKey(), scope, dataEntry.getValue());
    }

    private static Optional<PartnerEventId> parsePartnerId(String partnerId) {
        PartnerEventId partnerEventId = null;
        if (partnerId != null) {
            String partnerEventIdName = StringUtils.substringBefore(partnerId, ":");
            String partnerEventIdValue = StringUtils.substringAfter(partnerId, ":");
            partnerEventId = PartnerEventId.of(partnerEventIdName, partnerEventIdValue);
        }
        return Optional.ofNullable(partnerEventId);
    }

    private PersonRewardV4Response toPersonRewardResponse(Id<ClientHandle> clientId, PersonReward reward,
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

        return new PersonRewardV4Response(
            reward.getId().getValue(),
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
            Lists.newArrayList(reward.getRewardSlots()),
            Lists.newArrayList(reward.getRewardSlots()),
            reward.getRewardName().orElse(null),
            reward.getValueOfRewardedEvent(),
            reward.getData(),
            reward.getExpiryDate().map(expiryDate -> expiryDate.atZone(timeZone)).orElse(null),
            reward.getJourneyName().getValue(),
            reward.getJourneyKey().map(value -> new JourneyKey(value.getName(), value.getValue())));
    }

    private PersonStepV4Response toPersonStepResponse(PersonStep step, ZoneId timeZone) {
        Map<String, Object> privateData = step.getPrivateData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue));
        Map<String, Object> publicData = step.getPublicData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue));
        Map<String, Object> clientData = step.getClientData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue));
        List<PersonDataV4Response> data = Lists.newArrayList();
        privateData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.PRIVATE, value)));
        publicData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.PUBLIC, value)));
        clientData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.CLIENT, value)));

        return new PersonStepV4Response(
            step.getId().getValue(),
            step.getCampaignId().map(Id::getValue).orElse(null),
            step.getProgramLabel().orElse(null),
            step.getContainer().getName(),
            step.getStepName(),
            step.getEventId().getValue(),
            step.getEventDate().atZone(timeZone),
            step.getValue().map(this::formatFaceValue).orElse(null),
            step.getPartnerEventId()
                .map(partnerEventId -> new PartnerEventIdResponse(partnerEventId.getName(), partnerEventId.getValue()))
                .orElse(null),
            StepQuality.valueOf(step.getQuality().name()),
            data,
            step.getJourneyName().map(value -> value.getValue()).orElse(null),
            StepScope.valueOf(step.getScope().name()),
            step.getCauseEventId().getValue(),
            step.getRootEventId().getValue());
    }

    private static void fillPictureUrl(String pictureUrl, PersonBuilder personBuilder)
        throws PersonValidationV4RestException {
        if (pictureUrl != null) {
            try {
                if (!pictureUrl.isEmpty()) {
                    personBuilder.withProfilePictureUrl(new URL(pictureUrl).toURI());
                } else {
                    personBuilder.withProfilePictureUrl(null);
                }
            } catch (URISyntaxException | MalformedURLException e) {
                throw RestExceptionBuilder.newBuilder(PersonValidationV4RestException.class)
                    .withErrorCode(PersonValidationV4RestException.PICTURE_URL_INVALID)
                    .addParameter("picture_url", pictureUrl).withCause(e).build();
            }
        }
    }

    private static PersonRelationshipV4Response toRelationshipResponse(ZoneId timeZone, PersonReferral personReferral) {
        PersonRelationshipV4Response.Builder builder = PersonRelationshipV4Response.builder()
            .withRole(personReferral.getMySide().name())
            .withIsParent(personReferral.getMySide() == PersonReferral.Side.ADVOCATE)
            .withReason(personReferral.getReason().name())
            .withContainer(personReferral.getContainer().getName())
            .withUpdatedAt(personReferral.getUpdatedDate().atZone(timeZone))
            .withOtherPersonId(personReferral.getOtherPersonId().getValue())
            .withCampaignId(personReferral.getCampaignId().map(Id::getValue))
            .withProgramLabel(personReferral.getProgramLabel())
            .withCauseEventId(personReferral.getCauseEventId().getValue())
            .withRootEventId(personReferral.getRootEventId().getValue())
            .withData(personReferral.getData());
        return builder.build();
    }

    private static PersonV4Response personToResponse(Person person) {
        return new PersonV4Response(
            person.getId().getValue(),
            person.getEmail(),
            person.getFirstName(),
            person.getLastName(),
            person.getProfilePictureUrl() != null ? person.getProfilePictureUrl().toString() : null,
            person.getPartnerUserId(),
            toPersonLocaleResponse(person.getLocale()),
            person.getVersion(),
            person.isBlocked());
    }

    private static PersonLocaleResponse toPersonLocaleResponse(PersonLocale locale) {
        return new PersonLocaleResponse(locale.getLastBrowser(), locale.getUserSpecified());
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

    private static List<PersonV4Response> subList(List<PersonV4Response> persons, Integer offset, Integer limit) {
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

    private static PersonReferral.Side parseRole(String role) throws PersonRelationshipV4RestException {
        try {
            return role == null ? null : PersonReferral.Side.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(PersonRelationshipV4RestException.class)
                .withErrorCode(PersonRelationshipV4RestException.INVALID_ROLE)
                .addParameter("role", role)
                .withCause(e).build();
        }
    }

    private List<PersonShareV4Response> toPersonShareResponses(Authorization authorization, List<PersonShare> shares,
        ZoneId timeZone) throws UserAuthorizationRestException {
        List<PersonShareV4Response> shareResponses = Lists.newArrayList();

        for (PersonShare share : shares) {
            PersonShareV4Response shareResponse =
                personShareRestMapper.toPersonShareResponse(authorization, share, timeZone);
            shareResponses.add(shareResponse);
        }

        return ImmutableList.copyOf(shareResponses);
    }

    private static List<PersonReferral> selectLatestRelationshipPerOtherPerson(List<PersonReferral> relationships) {
        Multimap<Pair<Id<PersonHandle>, Container>, PersonReferral> relationshipsPerIdentity =
            ArrayListMultimap.create();
        for (PersonReferral relationship : relationships) {
            relationshipsPerIdentity.put(Pair.of(relationship.getOtherPersonIdentityId(), relationship.getContainer()),
                relationship);
        }
        return Lists.newArrayList(relationshipsPerIdentity.entries().stream()
            .collect(Collectors.toMap(
                Entry::getKey,
                Entry::getValue,
                (relationship1,
                    relationship2) -> relationship1.getUpdatedDate().isAfter(relationship2.getUpdatedDate())
                        ? relationship1
                        : relationship2))
            .values());
    }

    private static List<PersonRewardState> parseRewardStates(String states) throws PersonQueryRestException {
        if (StringUtils.isBlank(states)) {
            return Collections.emptyList();
        }
        List<PersonRewardState> rewardStates = Lists.newArrayList();
        try {
            for (String rewardState : StringUtils.split(states, COMMA)) {
                rewardStates.add(PersonRewardState.valueOf(rewardState.toUpperCase()));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw RestExceptionBuilder.newBuilder(PersonQueryRestException.class)
                .withErrorCode(PersonQueryRestException.UNSUPPORTED_REWARD_STATES)
                .addParameter("reward_states", states)
                .withCause(e)
                .build();
        }
        return rewardStates;
    }

    private static List<PersonRewardSupplierType> parseRewardTypes(String types) throws PersonQueryRestException {
        if (StringUtils.isBlank(types)) {
            return Collections.emptyList();
        }
        List<PersonRewardSupplierType> rewardTypes = Lists.newArrayList();
        try {
            for (String rewardType : StringUtils.split(types, COMMA)) {
                rewardTypes.add(PersonRewardSupplierType.valueOf(rewardType.toUpperCase()));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw RestExceptionBuilder.newBuilder(PersonQueryRestException.class)
                .withErrorCode(PersonQueryRestException.UNSUPPORTED_REWARD_TYPES)
                .addParameter("reward_types", types)
                .withCause(e)
                .build();
        }
        return rewardTypes;
    }

    private void sendEvents(ClientAuthorization authorization, PersonV4Request request, Person person)
        throws EventProcessorException, AuthorizationException {
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
        throws EventProcessorException, AuthorizationException {
        ProcessedRawEvent processedRawEvent = clientRequestContextService.createBuilder(authorization, servletRequest)
            .withEventName(eventName)
            .withHttpRequestBodyCapturing(ClientRequestContextService.HttpRequestBodyCapturingType.LIMITED)
            .build().getProcessedRawEvent();
        consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person).send();
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
        person.getProfileBlock().flatMap(ProfileBlock::getReason).ifPresent(value -> builder.addData("reason", value));
        userCache.getByAuthorization(authorization).map(User::getId).ifPresent(id -> builder.withUserId(id));
        builder.send();
    }
}
