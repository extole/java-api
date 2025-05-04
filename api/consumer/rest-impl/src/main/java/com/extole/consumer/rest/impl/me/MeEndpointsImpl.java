package com.extole.consumer.rest.impl.me;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.PollingStatus;
import com.extole.consumer.rest.common.Scope;
import com.extole.consumer.rest.impl.ConsumerResponseMapper;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.AudienceMembershipResponse;
import com.extole.consumer.rest.me.FriendProfileResponse;
import com.extole.consumer.rest.me.FriendProfileResponse.FriendEvent;
import com.extole.consumer.rest.me.FriendReminderEmailResponse;
import com.extole.consumer.rest.me.MeCapabilityResponse;
import com.extole.consumer.rest.me.MeEndpoints;
import com.extole.consumer.rest.me.MyProfileResponse;
import com.extole.consumer.rest.me.MyProfileResponse.ConsentType;
import com.extole.consumer.rest.me.PartnerEventIdRequest;
import com.extole.consumer.rest.me.PartnerEventIdResponse;
import com.extole.consumer.rest.me.PublicPersonStepResponse;
import com.extole.consumer.rest.me.RelationshipResponse;
import com.extole.consumer.rest.me.ShareResponse;
import com.extole.consumer.rest.me.ShareRestException;
import com.extole.consumer.rest.me.StepQuality;
import com.extole.consumer.rest.me.StepResponse;
import com.extole.consumer.rest.me.VerificationEmailRequest;
import com.extole.consumer.rest.me.VerificationEmailResponse;
import com.extole.consumer.rest.person.PersonProfileUpdateRequest;
import com.extole.consumer.rest.person.PersonRestException;
import com.extole.consumer.rest.person.PublicPersonResponse;
import com.extole.consumer.rest.share.PublicShareResponse;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.ConsumerRequestMetadata;
import com.extole.consumer.service.ConsumerRequestMetadataService;
import com.extole.consumer.service.email.authentication.AuthenticationEmailService;
import com.extole.consumer.service.friend.Friend;
import com.extole.consumer.service.friend.FriendEmailReminderOperation;
import com.extole.consumer.service.friend.FriendService;
import com.extole.consumer.service.shareable.ConsumerShareable;
import com.extole.consumer.service.shareable.ConsumerShareableService;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmail;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.event.pending.operation.email.PersonEmailPendingOperationEvent;
import com.extole.id.Id;
import com.extole.model.service.shareable.ClientShareable;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonEmailAlreadyDefinedException;
import com.extole.person.service.profile.PersonFirstNameInvalidLengthException;
import com.extole.person.service.profile.PersonLastNameInvalidLengthException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonPartnerUserIdAlreadyDefinedException;
import com.extole.person.service.profile.PersonPartnerUserIdInvalidLengthException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.PublicPerson;
import com.extole.person.service.profile.audience.membership.PersonAudienceMembership;
import com.extole.person.service.profile.key.PersonKey;
import com.extole.person.service.profile.referral.PersonReferral;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.profile.step.PersonStep;
import com.extole.person.service.profile.step.PersonStepData;
import com.extole.person.service.profile.step.PublicPersonStep;
import com.extole.person.service.share.Channel;
import com.extole.person.service.share.PersonPublicShare;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.share.PersonShareNotFoundException;
import com.extole.person.service.share.PersonShareService;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableService;

@Provider
public class MeEndpointsImpl implements MeEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(MeEndpointsImpl.class);

    private static final String SHARE_DEFAULT_PARTNER_ID_NAME = "partner_share_id";

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ConsumerRequestMetadataService consumerRequestMetadataService;
    private final ClientShareableService clientShareableService;
    private final AuthenticationEmailService authenticationEmailService;
    private final FriendService friendService;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final VerifiedEmailService verifiedEmailService;
    private final ConsumerShareableService consumerShareableService;
    private final ConsumerResponseMapper consumerResponseMapper;
    private final PersonShareService personShareService;
    private final ShareableService shareableService;

    @Inject
    public MeEndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        AuthenticationEmailService authenticationEmailService,
        ConsumerRequestMetadataService consumerRequestMetadataService,
        FriendService friendService,
        PersonService personService,
        ClientShareableService clientShareableService,
        ConsumerEventSenderService consumerEventSenderService,
        VerifiedEmailService verifiedEmailService,
        ConsumerShareableService consumerShareableService,
        ConsumerResponseMapper consumerResponseMapper,
        PersonShareService personShareService,
        ShareableService shareableService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.authenticationEmailService = authenticationEmailService;
        this.consumerRequestMetadataService = consumerRequestMetadataService;
        this.friendService = friendService;
        this.personService = personService;
        this.clientShareableService = clientShareableService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.verifiedEmailService = verifiedEmailService;
        this.consumerShareableService = consumerShareableService;
        this.consumerResponseMapper = consumerResponseMapper;
        this.personShareService = personShareService;
        this.shareableService = shareableService;
    }

    @Override
    public MyProfileResponse getMyProfile(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        Person person = authorization.getIdentity();

        return new MyProfileResponse(
            person.getId().getValue(),
            person.getEmail(),
            person.getFirstName(),
            person.getLastName(),
            Optional.ofNullable(person.getProfilePictureUrl()).map(URI::toString).orElse(null),
            person.getPartnerUserId(),
            person.getCookieConsentedAt().map(Instant::toString).orElse(null),
            person.getCookieConsentType().map(consentType -> ConsentType.valueOf(consentType.name()))
                .orElse(null),
            person.getProcessingConsent().map(Instant::toString).orElse(null),
            person.getProcessingConsentType()
                .map(consentType -> ConsentType.valueOf(consentType.name())).orElse(null),
            person.getData(),
            person.getPreferredLocale());
    }

    @Override
    public SuccessResponse updateMyProfile(String accessToken, PersonProfileUpdateRequest request)
        throws PersonRestException, AuthorizationRestException {
        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withEventName(ConsumerEventName.EXTOLE_PROFILE.getEventName())
            .withAccessToken(accessToken)
            .withEventProcessing(configurator -> configurator.withDisabledDefaultPrehandlers())
            .build();
        PersonAuthorization authorization = requestContext.getAuthorization();

        try {
            boolean updateProfilePictureUrl = request.getProfilePictureUrl() != null;
            URI profilePictureUrl =
                Strings.isNullOrEmpty(request.getProfilePictureUrl()) ? null : new URI(request.getProfilePictureUrl());

            String email = request.getEmail();
            VerifiedEmail verifiedEmail;
            if (Strings.isNullOrEmpty(email)) {
                verifiedEmail = null;
            } else {
                verifiedEmail = verifiedEmailService.verifyEmail(email);
            }

            consumerEventSenderService
                .createInputEvent(authorization, requestContext.getProcessedRawEvent(), authorization.getIdentity())
                .withLockDescription(new LockDescription("me-endpoints-update-profile"))
                .executeAndSend((personBuilder, person, inputEventBuilder) -> {
                    try {
                        if (verifiedEmail != null) {
                            personBuilder.withEmail(verifiedEmail.getEmail());
                            inputEventBuilder.addLogMessage("Person email update via consumer profile update endpoint");
                        }
                        if (!Strings.isNullOrEmpty(request.getPartnerUserId())) {
                            personBuilder.withPartnerUserId(request.getPartnerUserId());
                        }
                        if (request.getFirstName() != null) {
                            personBuilder.withFirstName(Strings.emptyToNull(request.getFirstName()));
                        }
                        if (request.getLastName() != null) {
                            personBuilder.withLastName(Strings.emptyToNull(request.getLastName()));
                        }
                        if (updateProfilePictureUrl) {
                            personBuilder.withProfilePictureUrl(profilePictureUrl);
                        }
                        Person updatedPerson = personBuilder.save();
                        return new InputEventLockClosureResult<>(updatedPerson);
                    } catch (PersonLastNameInvalidLengthException | PersonFirstNameInvalidLengthException
                        | PersonPartnerUserIdAlreadyDefinedException | PersonPartnerUserIdInvalidLengthException
                        | PersonEmailAlreadyDefinedException e) {
                        throw new LockClosureException(e);
                    }
                });
            return SuccessResponse.getInstance();
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonFirstNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                    .withErrorCode(PersonRestException.FIRST_NAME_INVALID_LENGTH)
                    .addParameter("first_name", request.getFirstName()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonLastNameInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                    .withErrorCode(PersonRestException.LAST_NAME_INVALID_LENGTH)
                    .addParameter("last_name", request.getLastName()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonPartnerUserIdAlreadyDefinedException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                    .withErrorCode(PersonRestException.PARTNER_USER_ID_ALREADY_DEFINED)
                    .addParameter("partner_user_id", request.getPartnerUserId()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonPartnerUserIdInvalidLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                    .withErrorCode(PersonRestException.PARTNER_USER_ID_INVALID_LENGTH)
                    .addParameter("partner_user_id", request.getPartnerUserId()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonEmailAlreadyDefinedException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                    .withErrorCode(PersonRestException.PERSON_EMAIL_ALREADY_DEFINED)
                    .addParameter("email", request.getEmail()).withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (URISyntaxException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.INVALID_PROFILE_PICTURE_URL)
                .addParameter("profile_picture_url", request.getProfilePictureUrl())
                .withCause(e).build();
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.INVALID_PERSON_EMAIL)
                .addParameter("email", request.getEmail())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e.getCause())
                .build();
        }
    }

    @Override
    public MeCapabilityResponse getMyCapabilities(String accessToken) throws AuthorizationRestException {
        getAuthorizationFromRequest(accessToken);
        return new MeCapabilityResponse(ImmutableSet.of(Scope.UPDATE_PROFILE));
    }

    @Override
    public VerificationEmailResponse sendVerificationEmail(String accessToken,
        Optional<VerificationEmailRequest> request) throws AuthorizationRestException {
        try {
            ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
                .withAccessToken(accessToken)
                .build();
            Optional<String> campaignId = Optional.empty();
            if (request.isPresent()) {
                campaignId = Optional.ofNullable(request.get().getCampaignId());
            }
            Id<PersonEmailPendingOperationEvent> operationId = authenticationEmailService
                .sendVerificationEmail(requestContext.getAuthorization(),
                    requestContext.getProcessedRawEvent().getClientDomain().getId(), campaignId);
            return new VerificationEmailResponse(operationId.getValue(), PollingStatus.PENDING);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public VerificationEmailResponse verificationEmailStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {

        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        PersonEmailPendingOperationEvent emailOperation =
            authenticationEmailService.getEmailStatus(authorization, Id.valueOf(pollingId));
        return new VerificationEmailResponse(pollingId, PollingStatus.valueOf(emailOperation.getStatus().name()));
    }

    @Override
    public FriendReminderEmailResponse sendFriendReminderEmail(String accessToken, String friendEmail)
        throws AuthorizationRestException {
        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build();
        ConsumerRequestMetadata metadata = consumerRequestMetadataService.createBuilder(requestContext).build();
        Id<FriendEmailReminderOperation> operationId =
            friendService.sendReminderEmail(requestContext.getAuthorization(), friendEmail, metadata);
        return new FriendReminderEmailResponse(operationId.getValue(), PollingStatus.PENDING);
    }

    @Override
    public FriendReminderEmailResponse friendReminderEmailStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);

        FriendEmailReminderOperation emailStatus =
            friendService.getReminderEmailStatus(authorization, Id.valueOf(pollingId));
        return new FriendReminderEmailResponse(pollingId, PollingStatus.valueOf(emailStatus.getStatus().name()));
    }

    @Override
    public List<FriendProfileResponse> getFriends(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            return friendService.getRecentFriendsFromAdvocateShares(authorization, authorization.getIdentity()).stream()
                .map(this::mapFriendToResponse)
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public List<ShareResponse> getShares(String accessToken, @Nullable String partnerShareId,
        @Nullable String partnerId) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);

        if (partnerShareId != null) {
            PartnerEventId partnerEventId = PartnerEventId.of(SHARE_DEFAULT_PARTNER_ID_NAME, partnerShareId);

            return getShareByPartnerId(authorization, partnerEventId)
                .map(personShare -> Collections.singletonList(mapShareToResponse(authorization, personShare)))
                .orElse(Collections.emptyList());
        }

        if (partnerId != null) {
            String partnerEventIdName = StringUtils.substringBefore(partnerId, ":");
            String partnerEventIdValue = StringUtils.substringAfter(partnerId, ":");
            if (StringUtils.isEmpty(partnerEventIdName) || StringUtils.isEmpty(partnerEventIdValue)) {
                return Collections.emptyList();
            }

            PartnerEventId partnerEventId = PartnerEventId.of(partnerEventIdName, partnerEventIdValue);

            return getShareByPartnerId(authorization, partnerEventId)
                .map(personShare -> Collections.singletonList(mapShareToResponse(authorization, personShare)))
                .orElse(Collections.emptyList());
        }

        return authorization.getIdentity().getShares().stream()
            .map(share -> mapShareToResponse(authorization, share))
            .collect(Collectors.toList());
    }

    @Override
    public ShareResponse getShare(String accessToken, String shareId) throws AuthorizationRestException,
        ShareRestException {
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            return mapShareToResponse(authorization, personShareService.getShare(authorization, Id.valueOf(shareId)));
        } catch (PersonShareNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareRestException.class)
                .withErrorCode(ShareRestException.SHARE_NOT_FOUND)
                .addParameter("share_id", shareId)
                .withCause(e).build();
        }
    }

    @Override
    public List<AudienceMembershipResponse> getAudienceMemberships(String accessToken, ZoneId timeZone)
        throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        return authorization.getIdentity()
            .getAudienceMemberships()
            .stream()
            .map(audienceMembership -> mapAudienceMembershipToResponse(audienceMembership, timeZone))
            .collect(Collectors.toList());
    }

    @Deprecated // TBD - OPEN TICKET Use getAssociatedAdvocates() instead
    @Override
    public List<RelationshipResponse> getAdvocateRelationships(String accessToken) throws AuthorizationRestException {
        return getAssociatedAdvocates(accessToken);
    }

    @Override
    public List<RelationshipResponse> getAssociatedAdvocates(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        Person person = authorization.getIdentity();
        return toRelationshipResponses(person, person.getIdentifiedAdvocates());
    }

    @Override
    public List<RelationshipResponse> getAssociatedFriends(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        Person person = authorization.getIdentity();
        return toRelationshipResponses(person, person.getIdentifiedFriends());
    }

    @Override
    public List<RelationshipResponse> getReferralsFromMe(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        Person person = authorization.getIdentity();
        return toRelationshipResponses(person, person.getFriends());
    }

    @Override
    public List<RelationshipResponse> getReferralsToMe(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        Person person = authorization.getIdentity();
        return toRelationshipResponses(person, person.getAdvocates());
    }

    @Override
    public List<StepResponse> getSteps(String accessToken, String campaignId, String programLabel, String stepName,
        StepQuality quality, PartnerEventIdRequest partnerEventId) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        Person person = authorization.getIdentity();
        return person.getSteps().stream()
            .filter(step -> StringUtils.isBlank(campaignId)
                || Objects.equal(campaignId, step.getCampaignId().map(Id::getValue).orElse(null)))
            .filter(step -> StringUtils.isBlank(programLabel)
                || programLabel.equalsIgnoreCase(step.getProgramLabel().orElse(null)))
            .filter(step -> StringUtils.isBlank(stepName) || stepName.equalsIgnoreCase(step.getStepName()))
            .filter(step -> quality == null || Objects.equal(quality, StepQuality.valueOf(step.getQuality().name())))
            .filter(step -> partnerEventId == null || StringUtils.isBlank(partnerEventId.getName()) ||
                (step.getPartnerEventId().isPresent() &&
                    StringUtils.equalsIgnoreCase(PartnerEventId.sanitizeName(partnerEventId.getName()),
                        step.getPartnerEventId().get().getName())))
            .filter(step -> partnerEventId == null || StringUtils.isBlank(partnerEventId.getValue()) ||
                (step.getPartnerEventId().isPresent()
                    && StringUtils.equalsIgnoreCase(PartnerEventId.sanitizeValue(partnerEventId.getValue()),
                        step.getPartnerEventId().get().getValue())))
            .map(this::mapStepToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<PublicPersonStepResponse> getPublicPersonSteps(String accessToken, String campaignId,
        String programLabel, String stepName) throws AuthorizationRestException {
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            List<PublicPersonStep> personSteps = personService
                .getPublicPerson(authorization.getClientId(), Id.valueOf(authorization.getIdentityId().getValue()))
                .getPublicSteps()
                .stream()
                .filter(step -> StringUtils.isBlank(campaignId) ||
                    Objects.equal(campaignId, step.getCampaignId().map(Id::getValue).orElse(null)))
                .filter(step -> StringUtils.isBlank(programLabel)
                    || Objects.equal(programLabel, step.getProgramLabel().orElse(null)))
                .filter(step -> StringUtils.isBlank(stepName) || Objects.equal(stepName, step.getStepName()))
                .collect(Collectors.toList());
            return consumerResponseMapper.toPublicPersonStepResponse(personSteps);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public List<PublicShareResponse> getAllShares(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        Person person = authorization.getIdentity();
        return person.getAllShares().stream()
            .map(personShare -> toPublicShareResponse(authorization, personShare))
            .collect(Collectors.toList());
    }

    private PublicShareResponse toPublicShareResponse(Authorization authorization, PersonPublicShare personShare) {
        Optional<ConsumerShareable> consumerShareable = Optional.empty();
        try {
            consumerShareable = Optional.of(consumerShareableService.get(
                authorization, Id.valueOf(personShare.getShareableId().getValue())));
        } catch (ShareableNotFoundException e) {
            LOG.warn("Unable to get shareable for personId={}, clientId={}, sharebleId={}",
                authorization.getIdentityId(), authorization.getClientId(), personShare.getShareableId());
        }
        return new PublicShareResponse(personShare.getId().getValue(), personShare.getCampaignId().getValue(),
            personShare.getChannel().map(Object::toString).orElse(null), personShare.getShareDate().toString(),
            consumerShareable.map(item -> item.getLink().toString()).orElse(null),
            personShare.getPartnerId().map(this::toPartnerEventIdResponse).orElse(null),
            consumerShareable.map(item -> item.getId().getValue()).orElse(null));
    }

    private PartnerEventIdResponse toPartnerEventIdResponse(PartnerEventId partnerEventId) {
        return new PartnerEventIdResponse(partnerEventId.getName(), partnerEventId.getValue());
    }

    private StepResponse mapStepToResponse(PersonStep step) {
        return new StepResponse(step.getCampaignId().map(Id::getValue).orElse(null),
            step.getProgramLabel().orElse(null),
            step.getContainer().getName(),
            step.getStepName(),
            step.getEventId().getValue(),
            step.getEventDate().toString(),
            step.getValue().map(this::formatBigDecimalValue).orElse(null),
            step.getPartnerEventId()
                .map(partnerEventId -> new PartnerEventIdResponse(partnerEventId.getName(), partnerEventId.getValue()))
                .orElse(null),
            StepQuality.valueOf(step.getQuality().name()),
            step.getData().stream()
                .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue)),
            step.getJourneyName().map(value -> value.getValue()).orElse(null));
    }

    private String formatBigDecimalValue(BigDecimal faceValue) {
        return faceValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private FriendProfileResponse mapFriendToResponse(Friend friend) {
        List<FriendEvent> events = friend.getEvents()
            .stream()
            .map(event -> new FriendEvent(event.getCampaignId().getValue(),
                event.getStepName(), event.getEventDate().toString(), event.getEmail()))
            .collect(Collectors.toList());
        return new FriendProfileResponse(friend.getId().getValue(), friend.getFirstName(),
            Optional.ofNullable(friend.getProfilePictureUrl()).map(URI::toString).orElse(null), events);
    }

    private ShareResponse mapShareToResponse(Authorization authorization, PersonShare share) {
        List<String> recipients = new ArrayList<>();
        PublicPersonResponse friend = null;
        if (share.getEmail().isPresent()) {
            recipients.add(share.getEmail().get());
            Optional<PublicPerson> friendPerson = personService
                .getPublicPersonByProfileLookupKey(authorization.getClientId(),
                    PersonKey.ofEmailType(share.getEmail().get()));
            if (friendPerson.isPresent()) {
                friend = mapPublicPersonToResponse(friendPerson.get());
            } else {
                LOG.warn("Unable to find friend person [clientId={}, email={}] for share response",
                    authorization.getClientId(), share.getEmail().get());
            }
        }
        String shareableLink = null;
        String shareableId = null;
        try {
            ClientShareable shareable =
                clientShareableService.get(authorization, Id.valueOf(share.getShareableId().getValue()));
            shareableId = shareable.getId().getValue();
            shareableLink = shareable.getLink().toString();
        } catch (ShareableNotFoundException e) {
            LOG.warn("Unable find shareable {} for share {} for client {}", share.getShareableId(), share.getId(),
                authorization.getClientId());
        }
        return new ShareResponse(share.getId().getValue(), share.getCampaignId().getValue(),
            share.getChannel().map(Channel::getName).orElse(null), share.getShareDate().toString(),
            recipients, share.getEmail().orElse(null), friend, shareableLink,
            share.getPartnerId().map(this::toPartnerEventIdResponse).orElse(null),
            share.getData(), shareableId, share.getMessage().orElse(null), share.getSubject().orElse(null));
    }

    private AudienceMembershipResponse mapAudienceMembershipToResponse(PersonAudienceMembership audienceMembership,
        ZoneId timeZone) {
        return new AudienceMembershipResponse(audienceMembership.getAudienceId().getValue(),
            audienceMembership.getCreatedDate().atZone(timeZone),
            audienceMembership.getUpdatedDate().atZone(timeZone));
    }

    private PublicPersonResponse mapPublicPersonToResponse(PublicPerson person) {
        return new PublicPersonResponse(person.getId().getValue(), person.getFirstName(),
            Optional.ofNullable(person.getProfilePictureUrl()).map(URI::toString).orElse(null),
            person.getPublicData());
    }

    private List<RelationshipResponse> toRelationshipResponses(Person person, List<PersonReferral> personReferrals) {
        List<RelationshipResponse> result = new ArrayList<>();
        for (PersonReferral personReferral : personReferrals) {
            try {
                PublicPerson otherPerson =
                    personService.getPublicPerson(person.getClientId(), personReferral.getOtherPersonId());

                // TODO get rid of shareable id, use only shareable code ENG-18496
                String shareableId = null;
                if (personReferral.getData().containsKey(PersonReferral.DATA_NAME_SHAREABLE_ID)) {
                    shareableId = personReferral.getData().get(PersonReferral.DATA_NAME_SHAREABLE_ID).toString();
                } else if (personReferral.getData().containsKey(PersonReferral.DATA_NAME_SHAREABLE_CODE)) {
                    String shareableCode =
                        personReferral.getData().get(PersonReferral.DATA_NAME_SHAREABLE_CODE).toString();
                    try {
                        shareableId = shareableService.getByCode(person.getClientId(), shareableCode)
                            .getShareableId()
                            .getValue();
                    } catch (ShareableNotFoundException e) {
                        LOG.warn("Unable to find shareable with code: {}, clientId: {} (associated with {} person: {})",
                            shareableCode, personReferral.getClientId(), personReferral.getMySide(), person.getId(), e);
                    }
                }

                result.add(new RelationshipResponse(
                    shareableId,
                    personReferral.getReason().name(), personReferral.getContainer().getName(),
                    personReferral.getUpdatedDate().toString(), mapPublicPersonToResponse(otherPerson)));
            } catch (PersonNotFoundException e) {
                LOG.warn("Unable to find person with id: {}, clientId: {} (associated with {} person: {})",
                    personReferral.getOtherPersonId(), personReferral.getClientId(), personReferral.getMySide(),
                    person.getId(), e);
            }
        }
        return result;
    }

    private PersonAuthorization getAuthorizationFromRequest(String accessToken) throws AuthorizationRestException {
        return consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
    }

    private Optional<PersonShare> getShareByPartnerId(Authorization authorization, PartnerEventId partnerEventId) {
        try {
            return Optional.of(personShareService.getShare(authorization, partnerEventId));
        } catch (PersonShareNotFoundException e) {
            return Optional.empty();
        }
    }
}
