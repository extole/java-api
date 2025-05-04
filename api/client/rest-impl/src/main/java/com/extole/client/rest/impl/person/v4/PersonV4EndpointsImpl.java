package com.extole.client.rest.impl.person.v4;

import static com.extole.authorization.service.Authorization.Scope.USER_SUPPORT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.impl.person.PersonAudienceMembershipRestMapper;
import com.extole.client.rest.impl.person.PersonShareRestMapper;
import com.extole.client.rest.impl.person.PersonShareableRestMapper;
import com.extole.client.rest.impl.person.RequestContextResponseMapper;
import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.person.PartnerEventIdResponse;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonLocaleResponse;
import com.extole.client.rest.person.PersonQueryRestException;
import com.extole.client.rest.person.PersonReferralRole;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.PersonStepsListRestException;
import com.extole.client.rest.person.StepQuality;
import com.extole.client.rest.person.StepScope;
import com.extole.client.rest.person.v4.PersonAudienceMembershipV4Response;
import com.extole.client.rest.person.v4.PersonDataV4Response;
import com.extole.client.rest.person.v4.PersonJourneyV4Response;
import com.extole.client.rest.person.v4.PersonRelationshipV4Response;
import com.extole.client.rest.person.v4.PersonRelationshipV4RestException;
import com.extole.client.rest.person.v4.PersonRelationshipsV4ListRequest;
import com.extole.client.rest.person.v4.PersonRequestContextV4Response;
import com.extole.client.rest.person.v4.PersonRewardV4Response;
import com.extole.client.rest.person.v4.PersonShareV4Response;
import com.extole.client.rest.person.v4.PersonShareableV4Response;
import com.extole.client.rest.person.v4.PersonStepV4Response;
import com.extole.client.rest.person.v4.PersonStepsV4ListRequest;
import com.extole.client.rest.person.v4.PersonV4Endpoints;
import com.extole.client.rest.person.v4.PersonV4Response;
import com.extole.client.rest.shareable.ShareableRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.shared.reward.supplier.ArchivedRewardSupplierCache;
import com.extole.person.service.RewardSupplierHandle;
import com.extole.person.service.StepName;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.InvalidFlowPersonStepQueryException;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonAudienceMembershipQueryBuilder;
import com.extole.person.service.profile.PersonData;
import com.extole.person.service.profile.PersonDataQueryBuilder;
import com.extole.person.service.profile.PersonJourneyQueryBuilder;
import com.extole.person.service.profile.PersonLocationQueryBuilder;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonRelationshipQueryBuilder;
import com.extole.person.service.profile.PersonRewardQueryBuilder;
import com.extole.person.service.profile.PersonShareQueryBuilder;
import com.extole.person.service.profile.PersonShareableQueryBuilder;
import com.extole.person.service.profile.StepQueryBuilder;
import com.extole.person.service.profile.audience.membership.PersonAudienceMembership;
import com.extole.person.service.profile.journey.Container;
import com.extole.person.service.profile.journey.JourneyName;
import com.extole.person.service.profile.journey.PersonJourney;
import com.extole.person.service.profile.locale.PersonLocale;
import com.extole.person.service.profile.referral.PersonReferral;
import com.extole.person.service.profile.referral.PersonReferralReason;
import com.extole.person.service.profile.reward.PersonReward;
import com.extole.person.service.profile.reward.PersonRewardState;
import com.extole.person.service.profile.reward.PersonRewardSupplierType;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.profile.step.PersonStep;
import com.extole.person.service.profile.step.PersonStepData;
import com.extole.person.service.profile.step.PersonStepVisitType;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableByCodeNotFoundException;

@Provider
public class PersonV4EndpointsImpl implements PersonV4Endpoints {
    private static final Logger LOG = LoggerFactory.getLogger(PersonV4EndpointsImpl.class);

    private static final String ALL_CONTAINERS = "*";
    private static final String SHARE_DEFAULT_PARTNER_ID_NAME = "partner_share_id";
    private static final String COMMA = ",";

    private final ClientAuthorizationProvider authorizationProvider;
    private final FullPersonService fullPersonService;
    private final ArchivedRewardSupplierCache archivedRewardSupplierCache;
    private final PersonShareRestMapper personShareRestMapper;
    private final PersonShareableRestMapper personShareableRestMapper;
    private final RequestContextResponseMapper requestContextResponseMapper;
    private final PersonAudienceMembershipRestMapper personAudienceMembershipRestMapper;

    @Autowired
    public PersonV4EndpointsImpl(FullPersonService fullPersonService,
        ClientAuthorizationProvider authorizationProvider,
        ArchivedRewardSupplierCache archivedRewardSupplierCache,
        PersonShareRestMapper personShareRestMapper,
        PersonShareableRestMapper personShareableRestMapper,
        RequestContextResponseMapper requestContextResponseMapper,
        PersonAudienceMembershipRestMapper personAudienceMembershipRestMapper) {
        this.fullPersonService = fullPersonService;
        this.authorizationProvider = authorizationProvider;
        this.archivedRewardSupplierCache = archivedRewardSupplierCache;
        this.personShareRestMapper = personShareRestMapper;
        this.personShareableRestMapper = personShareableRestMapper;
        this.requestContextResponseMapper = requestContextResponseMapper;
        this.personAudienceMembershipRestMapper = personAudienceMembershipRestMapper;
    }

    @Override
    public PersonV4Response getPerson(String accessToken, String personId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personToResponse(fullPersonService.getPerson(authorization, Id.valueOf(personId)));
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
    public List<PersonStepV4Response> getSteps(String accessToken, String personId,
        @Nullable PersonStepsV4ListRequest personStepsListRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonStepsListRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            StepQueryBuilder builder =
                fullPersonService.createStepQueryBuilder(userAuthorization, Id.valueOf(personId));
            if (personStepsListRequest != null) {
                if (!Strings.isNullOrEmpty(personStepsListRequest.getStepName())) {
                    builder.addStepName(StepName.valueOf(personStepsListRequest.getStepName()));
                }
                if (Strings.isNullOrEmpty(personStepsListRequest.getContainer())) {
                    builder.withContainer(Container.DEFAULT);
                } else if (!ALL_CONTAINERS.equalsIgnoreCase(personStepsListRequest.getContainer())) {
                    builder.withContainer(new Container(personStepsListRequest.getContainer()));
                }
                if (!Strings.isNullOrEmpty(personStepsListRequest.getCampaignId())) {
                    builder.withCampaignId(Id.valueOf(personStepsListRequest.getCampaignId()));
                }
                if (!Strings.isNullOrEmpty(personStepsListRequest.getProgramLabel())) {
                    builder.withProgramLabel(personStepsListRequest.getProgramLabel());
                }
                if (!Strings.isNullOrEmpty(personStepsListRequest.getPartnerEventId())) {
                    String partnerEventIdName =
                        StringUtils.substringBefore(personStepsListRequest.getPartnerEventId(), ":");
                    String partnerEventIdValue =
                        StringUtils.substringAfter(personStepsListRequest.getPartnerEventId(), ":");
                    if (StringUtils.isEmpty(partnerEventIdName) || StringUtils.isEmpty(partnerEventIdValue)) {
                        return Collections.emptyList();
                    }
                    builder.withPartnerEventId(PartnerEventId.of(partnerEventIdName, partnerEventIdValue));
                }
                if (!Strings.isNullOrEmpty(personStepsListRequest.getFlowPath())) {
                    builder.withFlowPath(personStepsListRequest.getFlowPath());
                }
                if (!Strings.isNullOrEmpty(personStepsListRequest.getVisitType())) {
                    builder.withVisitType(parseVisitType(personStepsListRequest.getVisitType()));
                }
                if (personStepsListRequest.getOffset() != null) {
                    builder.withOffset(personStepsListRequest.getOffset().intValue());
                }
                if (personStepsListRequest.getLimit() != null) {
                    builder.withLimit(personStepsListRequest.getLimit().intValue());
                }
                if (!Strings.isNullOrEmpty(personStepsListRequest.getEventId())) {
                    builder.withEventId(Id.valueOf(personStepsListRequest.getEventId()));
                }
            }
            return builder.listWithoutRuntimeSteps().stream()
                .map(step -> toPersonStepResponse(step, timeZone))
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
        } catch (InvalidFlowPersonStepQueryException e) {
            throw RestExceptionBuilder.newBuilder(PersonStepsListRestException.class)
                .withErrorCode(PersonStepsListRestException.INVALID_FLOW_QUERY)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<PersonShareV4Response> getShares(String accessToken, String personId, @Nullable String campaignId,
        @Nullable String partnerShareId, @Nullable String partnerId, @Nullable Integer offset, @Nullable Integer limit,
        ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            PersonShareQueryBuilder builder =
                fullPersonService.createShareQueryBuilder(userAuthorization, Id.valueOf(personId));
            if (!Strings.isNullOrEmpty(campaignId)) {
                builder.withCampaignIds(List.of(Id.valueOf(campaignId)));
            }
            if (!Strings.isNullOrEmpty(partnerShareId)) {
                builder.withPartnerIds(List.of(PartnerEventId.of(SHARE_DEFAULT_PARTNER_ID_NAME, partnerShareId)));
            } else if (!Strings.isNullOrEmpty(partnerId)) {
                String partnerEventIdName = StringUtils.substringBefore(partnerId, ":");
                String partnerEventIdValue = StringUtils.substringAfter(partnerId, ":");
                if (StringUtils.isEmpty(partnerEventIdName) || StringUtils.isEmpty(partnerEventIdValue)) {
                    return Collections.emptyList();
                }
                builder.withPartnerIds(List.of(PartnerEventId.of(partnerEventIdName, partnerEventIdValue)));
            }
            if (offset != null) {
                builder.withOffset(offset.intValue());
            }
            if (limit != null) {
                builder.withLimit(limit.intValue());
            }
            return toPersonShareResponses(userAuthorization, builder.listWithoutRuntimeShares(), timeZone);
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
    public List<PersonAudienceMembershipV4Response> getAudienceMemberships(String accessToken, String personId,
        Optional<String> name, Optional<Integer> offset, Optional<Integer> limit, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            PersonAudienceMembershipQueryBuilder builder =
                fullPersonService.createAudienceMembershipsQueryBuilder(userAuthorization, Id.valueOf(personId));
            if (Strings.emptyToNull(name.orElse(null)) != null) {
                builder.withNames(List.of(name.get()));
            }
            offset.ifPresent(integer -> builder.withOffset(integer.intValue()));
            limit.ifPresent(integer -> builder.withLimit(integer.intValue()));
            return toPersonAudienceResponses(userAuthorization.getClientId(),
                builder.listWithoutRuntimeAudienceMemberships(), timeZone);
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
    public List<PersonShareableV4Response> getShareables(String accessToken, String personId, @Nullable Integer offset,
        @Nullable Integer limit) throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            PersonShareableQueryBuilder builder =
                fullPersonService.createShareableQueryBuilder(userAuthorization, Id.valueOf(personId));
            if (offset != null) {
                builder.withOffset(offset.intValue());
            }
            if (limit != null) {
                builder.withLimit(limit.intValue());
            }
            List<PersonShareableV4Response> response = new ArrayList<>();
            for (Shareable shareable : builder.listWithoutRuntimeShareables()) {
                try {
                    response.add(personShareableRestMapper.toPersonShareableV4Response(shareable));
                } catch (ProgramNotFoundException e) {
                    LOG.warn("Unable to find program domain for shareable: {}", shareable, e);
                }
            }
            return response;
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
    public PersonShareableV4Response getShareableByCode(String accessToken, String personId, String code)
        throws UserAuthorizationRestException, PersonRestException, ShareableRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            PersonShareableQueryBuilder builder =
                fullPersonService.createShareableQueryBuilder(userAuthorization, Id.valueOf(personId));
            builder.withCodes(List.of(code));

            List<PersonShareableV4Response> response = new ArrayList<>();
            for (Shareable shareable : builder.listWithoutRuntimeShareables()) {
                try {
                    response.add(personShareableRestMapper.toPersonShareableV4Response(shareable));
                } catch (ProgramNotFoundException e) {
                    LOG.warn("Unable to find program domain for shareable: {}", shareable, e);
                }
            }

            if (!response.isEmpty()) {
                return response.get(0);
            }
            throw new ShareableByCodeNotFoundException(userAuthorization.getClientId(), code);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ShareableByCodeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareableRestException.class)
                .withErrorCode(ShareableRestException.SHAREABLE_NOT_FOUND)
                .addParameter("code", e.getCode())
                .withCause(e).build();
        }
    }

    // CHECKSTYLE.OFF: ParameterNumber
    @Override
    public List<PersonJourneyV4Response> getJourneys(String accessToken, String personId, @Nullable String campaignId,
        @Nullable String programLabel, @Nullable String container, @Nullable String journeyName,
        @Nullable Integer offset, @Nullable Integer limit, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            PersonJourneyQueryBuilder builder =
                fullPersonService.createJourneyQueryBuilder(userAuthorization, Id.valueOf(personId));
            if (!ALL_CONTAINERS.equalsIgnoreCase(container)) {
                builder.withContainers(List.of(
                    new Container(Optional.ofNullable(container).orElse(Container.DEFAULT.getName()))));
            }
            if (!Strings.isNullOrEmpty(campaignId)) {
                builder.withCampaignIds(List.of(Id.valueOf(campaignId)));
            }
            if (!Strings.isNullOrEmpty(programLabel)) {
                builder.withPrograms(List.of(programLabel));
            }
            if (StringUtils.isNotBlank(journeyName)) {
                builder.withNames(List.of(JourneyName.valueOf(journeyName)));
            }
            if (offset != null) {
                builder.withOffset(offset.intValue());
            }
            if (limit != null) {
                builder.withLimit(limit.intValue());
            }
            return builder.listWithoutRuntimeJourneys().stream()
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

    // CHECKSTYLE.OFF: ParameterNumber
    @Override
    public List<PersonRewardV4Response> getRewards(String accessToken, String personId, @Nullable String programLabel,
        @Nullable String campaignId, @Nullable String rewardStates, @Nullable String rewardTypes,
        @Nullable Integer offset, @Nullable Integer limit, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonQueryRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            PersonRewardQueryBuilder builder =
                fullPersonService.createRewardQueryBuilder(userAuthorization, Id.valueOf(personId));
            if (!Strings.isNullOrEmpty(campaignId)) {
                builder.withCampaignIds(ImmutableList.of(Id.valueOf(campaignId)));
            }
            if (!Strings.isNullOrEmpty(programLabel)) {
                builder.withProgramLabels(ImmutableList.of(programLabel));
            }
            if (!Strings.isNullOrEmpty(rewardStates)) {
                builder.withRewardStates(parseRewardStates(rewardStates));
            }
            if (!Strings.isNullOrEmpty(rewardTypes)) {
                builder.withRewardTypes(parseRewardTypes(rewardTypes));
            }
            if (offset != null) {
                builder.withOffset(offset.intValue());
            }
            if (limit != null) {
                builder.withLimit(limit.intValue());
            }
            return builder.listWithoutRuntimeRewards().stream()
                .map(reward -> toPersonRewardResponse(userAuthorization.getClientId(), reward, timeZone))
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
    public List<PersonRelationshipV4Response> getRelationships(String accessToken, String personId,
        PersonRelationshipsV4ListRequest relationshipsListRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonRelationshipV4RestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            PersonRelationshipQueryBuilder builder =
                fullPersonService.createRelationshipQueryBuilder(userAuthorization, Id.valueOf(personId));
            if (relationshipsListRequest != null) {
                if (!PersonRelationshipsV4ListRequest.ALL_CONTAINERS
                    .equalsIgnoreCase(relationshipsListRequest.getContainer())) {
                    builder.withContainers(List.of(
                        new Container(Optional.ofNullable(relationshipsListRequest.getContainer())
                            .orElse(Container.DEFAULT.getName()))));
                }
                if (relationshipsListRequest.getRole() != null) {
                    builder.withMyRoles(List.of(getMyRole(relationshipsListRequest)));
                }
                builder.withExcludeAnonymous(relationshipsListRequest.isExcludeAnonymous())
                    .withIncludeDuplicateIdentities(relationshipsListRequest.isIncludeDuplicateIdentities())
                    .withIncludeSelfReferrals(relationshipsListRequest.isIncludeSelfReferrals());
                if (relationshipsListRequest.getOffset() != null) {
                    builder.withOffset(relationshipsListRequest.getOffset().intValue());
                } else {
                    builder.withOffset(PersonRelationshipsV4ListRequest.DEFAULT_OFFSET);
                }
                if (relationshipsListRequest.getLimit() != null) {
                    builder.withLimit(relationshipsListRequest.getLimit().intValue());
                } else {
                    builder.withLimit(PersonRelationshipsV4ListRequest.DEFAULT_LIMIT);
                }
            }
            return builder.listWithoutRuntimeRelationships().stream()
                .map(relationship -> toPersonRelationshipResponse(timeZone, relationship))
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

    private PersonReferral.Side getMyRole(PersonRelationshipsV4ListRequest relationshipsListRequest)
        throws PersonRelationshipV4RestException {
        return parseRole(relationshipsListRequest.getRole()) == PersonReferral.Side.ADVOCATE
            ? PersonReferral.Side.FRIEND
            : PersonReferral.Side.ADVOCATE;
    }

    @Override
    public List<PersonDataV4Response> getData(String accessToken, String personId,
        @Nullable String name, @Nullable PersonDataScope scope, @Nullable Integer offset,
        @Nullable Integer limit) throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(userAuthorization);
        try {
            PersonDataQueryBuilder builder =
                fullPersonService.createDataQueryBuilder(userAuthorization, Id.valueOf(personId));
            if (!Strings.isNullOrEmpty(name)) {
                builder.withNames(List.of(name));
            }
            if (scope != null) {
                builder.withScopes(List.of(PersonData.Scope.valueOf(scope.name())));
            }
            if (offset != null) {
                builder.withOffset(offset.intValue());
            }
            if (limit != null) {
                builder.withLimit(limit.intValue());
            }
            return builder.listWithoutRuntimeData().stream()
                .map(data -> toPersonDataResponse(data))
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
    public List<PersonRequestContextV4Response> getRequestContexts(String accessToken, String personId,
        @Nullable Integer offset, @Nullable Integer limit, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(authorization);
        try {
            PersonLocationQueryBuilder builder =
                fullPersonService.createLocationQueryBuilder(authorization, Id.valueOf(personId));
            if (offset != null) {
                builder.withOffset(offset.intValue());
            }
            if (limit != null) {
                builder.withLimit(limit.intValue());
            }
            return builder.listWithoutRuntimeLocations().stream()
                .map(
                    requestContext -> requestContextResponseMapper.toRequestContextV4Response(requestContext, timeZone))
                .collect(Collectors.toList());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND).addParameter("person_id", personId).withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
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

    private String formatFaceValue(BigDecimal faceValue) {
        return faceValue.setScale(2, RoundingMode.HALF_UP).toString();
    }

    // TODO remove auth check in endpoints as a later part of ENG-20074
    private void checkAccessRights(Authorization authorization) throws UserAuthorizationRestException {
        if (!authorization.getScopes().contains(USER_SUPPORT)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }

    private PersonReferral.Side parseRole(PersonReferralRole role) throws PersonRelationshipV4RestException {
        try {
            return role == null ? null : PersonReferral.Side.valueOf(role.name());
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(PersonRelationshipV4RestException.class)
                .withErrorCode(PersonRelationshipV4RestException.INVALID_ROLE)
                .addParameter("role", role)
                .withCause(e)
                .build();
        }
    }

    private PersonStepVisitType parseVisitType(String visitType) throws PersonStepsListRestException {
        try {
            return visitType == null ? null : PersonStepVisitType.valueOf(visitType);
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(PersonStepsListRestException.class)
                .withErrorCode(PersonStepsListRestException.STEP_INVALID_VISIT_TYPE)
                .addParameter("visit_type", visitType)
                .withCause(e)
                .build();
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
                clientId, rewardSupplierId);
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

    private PersonRelationshipV4Response toPersonRelationshipResponse(ZoneId timeZone, PersonReferral personReferral) {
        PersonRelationshipV4Response.Builder builder = PersonRelationshipV4Response.builder()
            .withRole(personReferral.getMySide().name())
            .withIsParent(personReferral.getMySide() == PersonReferral.Side.ADVOCATE)
            .withReason(personReferral.getReason().name())
            .withContainer(personReferral.getContainer().getName())
            .withUpdatedAt(personReferral.getUpdatedDate().atZone(timeZone))
            .withOtherPersonId(personReferral.getOtherPersonId().getValue())
            .withCampaignId(personReferral.getCampaignId().map(campaignId -> campaignId.getValue()))
            .withProgramLabel(personReferral.getProgramLabel())
            .withCauseEventId(personReferral.getCauseEventId().getValue())
            .withRootEventId(personReferral.getRootEventId().getValue())
            .withData(personReferral.getData());
        return builder.build();
    }

    private PersonV4Response personToResponse(Person person) {
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

    private PersonLocaleResponse toPersonLocaleResponse(PersonLocale locale) {
        return new PersonLocaleResponse(locale.getLastBrowser(), locale.getUserSpecified());
    }

    private PersonDataV4Response toPersonDataResponse(PersonData data) {
        return new PersonDataV4Response(data.getName(), PersonDataScope.valueOf(data.getScope().name()),
            data.getValue());
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

    private List<PersonAudienceMembershipV4Response> toPersonAudienceResponses(Id<ClientHandle> clientId,
        List<PersonAudienceMembership> audiences, ZoneId timeZone) {
        List<PersonAudienceMembershipV4Response> audienceResponses = Lists.newArrayList();

        for (PersonAudienceMembership audience : audiences) {
            audienceResponses.add(
                personAudienceMembershipRestMapper.toPersonAudienceMembershipResponse(audience, timeZone));
        }
        return ImmutableList.copyOf(audienceResponses);
    }

    private List<PersonRewardState> parseRewardStates(String states) throws PersonQueryRestException {
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
                .withCause(e).build();
        }
        return rewardStates;
    }

    private List<PersonRewardSupplierType> parseRewardTypes(String types) throws PersonQueryRestException {
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
                .withCause(e).build();
        }
        return rewardTypes;
    }
}
