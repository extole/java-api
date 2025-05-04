package com.extole.client.rest.impl.audience;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.audience.AudienceArchiveRestException;
import com.extole.client.rest.audience.AudienceComponentVariableResponse;
import com.extole.client.rest.audience.AudienceControllerActionResponse;
import com.extole.client.rest.audience.AudienceControllerTriggerResponse;
import com.extole.client.rest.audience.AudienceCreateRequest;
import com.extole.client.rest.audience.AudienceEndpoints;
import com.extole.client.rest.audience.AudienceQueryParams;
import com.extole.client.rest.audience.AudienceResponse;
import com.extole.client.rest.audience.AudienceRestException;
import com.extole.client.rest.audience.AudienceUpdateRequest;
import com.extole.client.rest.audience.AudienceValidationRestException;
import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.audience.built.BuiltAudienceQueryParams;
import com.extole.client.rest.audience.built.BuiltAudienceResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.audience.built.BuiltAudienceRestMapper;
import com.extole.client.rest.impl.campaign.BuildAudienceExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.audience.Audience;
import com.extole.model.entity.audience.built.BuiltAudience;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.service.audience.AudienceAssociatedWithAudienceMembershipControllerTriggerException;
import com.extole.model.service.audience.AudienceAssociatedWithAudienceMembershipEventControllerTriggerException;
import com.extole.model.service.audience.AudienceAssociatedWithComponentVariableException;
import com.extole.model.service.audience.AudienceAssociatedWithCreateMembershipControllerActionException;
import com.extole.model.service.audience.AudienceAssociatedWithRemoveMembershipControllerActionException;
import com.extole.model.service.audience.AudienceBuilder;
import com.extole.model.service.audience.AudienceComponentVariable;
import com.extole.model.service.audience.AudienceControllerAction;
import com.extole.model.service.audience.AudienceControllerTrigger;
import com.extole.model.service.audience.AudienceNotFoundException;
import com.extole.model.service.audience.AudienceQueryBuilder;
import com.extole.model.service.audience.AudienceService;
import com.extole.model.service.audience.InvalidAudienceTagException;
import com.extole.model.service.audience.built.BuildAudienceException;
import com.extole.model.service.audience.built.BuiltAudienceQueryBuilder;
import com.extole.model.service.audience.built.BuiltAudienceService;
import com.extole.model.service.audience.built.EnableFilter;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;

@Provider
public class AudienceEndpointsImpl implements AudienceEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final AudienceService audienceService;
    private final BuiltAudienceService builtAudienceService;
    private final ComponentService componentService;
    private final AudienceRestMapper audienceRestMapper;
    private final BuiltAudienceRestMapper builtAudienceRestMapper;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public AudienceEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AudienceService audienceService,
        BuiltAudienceService builtAudienceService,
        ComponentService componentService,
        AudienceRestMapper audienceRestMapper,
        BuiltAudienceRestMapper builtAudienceRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.audienceService = audienceService;
        this.builtAudienceService = builtAudienceService;
        this.componentService = componentService;
        this.audienceRestMapper = audienceRestMapper;
        this.builtAudienceRestMapper = builtAudienceRestMapper;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<AudienceResponse> list(String accessToken, AudienceQueryParams queryParams, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            AudienceQueryBuilder queryBuilder = audienceService.list(authorization);

            if (queryParams.getIncludeArchived()) {
                queryBuilder.includeArchived();
            }

            queryBuilder.withLimit(queryParams.getLimit());
            queryBuilder.withOffset(queryParams.getOffset());

            return queryBuilder.list()
                .stream()
                .sorted(Comparator.comparing(Audience::getCreatedDate).reversed())
                .map(audience -> audienceRestMapper.toAudienceResponse(audience, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceResponse get(String accessToken, Id<com.extole.api.audience.Audience> audienceId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Audience audience = audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));
            return audienceRestMapper.toAudienceResponse(audience, timeZone);
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceRestException.class)
                .withErrorCode(AudienceRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
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
    public List<BuiltAudienceResponse> listBuilt(String accessToken, BuiltAudienceQueryParams queryParams,
        ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltAudienceQueryBuilder queryBuilder = builtAudienceService.list(authorization);

            if (StringUtils.isNotBlank(queryParams.getName())) {
                queryBuilder.withName(queryParams.getName());
            }
            if (queryParams.getIncludeArchived()) {
                queryBuilder.includeArchived();
            }

            queryBuilder.withEnabled(EnableFilter.valueOf(queryParams.getEnabled().name()));
            queryBuilder.withLimit(queryParams.getLimit());
            queryBuilder.withOffset(queryParams.getOffset());

            List<BuiltAudience> builtAudiences = queryBuilder.list();
            return builtAudiences.stream()
                .sorted(Comparator.comparing(BuiltAudience::getCreatedDate).reversed())
                .map(audience -> builtAudienceRestMapper.toBuiltAudienceResponse(audience, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltAudienceResponse getBuilt(String accessToken, Id<com.extole.api.audience.Audience> audienceId,
        ZoneId timeZone) throws UserAuthorizationRestException, AudienceRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltAudience builtAudience =
                builtAudienceService.getById(authorization, Id.valueOf(audienceId.getValue()));
            return builtAudienceRestMapper.toBuiltAudienceResponse(builtAudience, timeZone);
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceRestException.class)
                .withErrorCode(AudienceRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
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
    public AudienceResponse create(String accessToken, AudienceCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceValidationRestException,
        CampaignComponentValidationRestException, BuildAudienceRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            AudienceBuilder audienceBuilder = audienceService.create(authorization)
                .withName(createRequest.getName());

            createRequest.getEnabled().ifPresent(enabled -> {
                audienceBuilder.withEnabled(enabled);
            });
            if (createRequest.getTags().isPresent()) {
                audienceBuilder.withTags(createRequest.getTags().getValue());
            }
            createRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(audienceBuilder, componentIds);
            });
            createRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(audienceBuilder, componentReferences);
            });

            Audience audience =
                audienceBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
            return audienceRestMapper.toAudienceResponse(audience, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceAssociatedWithAudienceMembershipControllerTriggerException
            | AudienceAssociatedWithAudienceMembershipEventControllerTriggerException
            | AudienceAssociatedWithRemoveMembershipControllerActionException
            | AudienceAssociatedWithCreateMembershipControllerActionException
            | AudienceAssociatedWithComponentVariableException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (InvalidAudienceTagException e) {
            throw RestExceptionBuilder.newBuilder(AudienceValidationRestException.class)
                .withErrorCode(AudienceValidationRestException.INVALID_AUDIENCE_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(AudienceValidationRestException.class)
                .withErrorCode(AudienceValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildAudienceException e) {
            throw BuildAudienceExceptionMapper.getInstance().map(e);
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public AudienceResponse update(String accessToken, Id<com.extole.api.audience.Audience> audienceId,
        AudienceUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceRestException, AudienceValidationRestException,
        CampaignComponentValidationRestException, BuildAudienceRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            AudienceBuilder audienceBuilder = audienceService.update(authorization, Id.valueOf(audienceId.getValue()));

            updateRequest.getName().ifPresent(name -> {
                audienceBuilder.withName(name);
            });
            updateRequest.getEnabled().ifPresent(enabled -> {
                audienceBuilder.withEnabled(enabled);
            });
            if (updateRequest.getTags().isPresent()) {
                audienceBuilder.withTags(updateRequest.getTags().getValue());
            }
            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(audienceBuilder, componentIds);
            });
            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(audienceBuilder, componentReferences);
            });

            Audience audience =
                audienceBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));

            return audienceRestMapper.toAudienceResponse(audience, timeZone);
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceRestException.class)
                .withErrorCode(AudienceRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceAssociatedWithAudienceMembershipControllerTriggerException
            | AudienceAssociatedWithAudienceMembershipEventControllerTriggerException
            | AudienceAssociatedWithRemoveMembershipControllerActionException
            | AudienceAssociatedWithCreateMembershipControllerActionException
            | AudienceAssociatedWithComponentVariableException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (InvalidAudienceTagException e) {
            throw RestExceptionBuilder.newBuilder(AudienceValidationRestException.class)
                .withErrorCode(AudienceValidationRestException.INVALID_AUDIENCE_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(AudienceValidationRestException.class)
                .withErrorCode(AudienceValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildAudienceException e) {
            throw BuildAudienceExceptionMapper.getInstance().map(e);
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public AudienceResponse archive(String accessToken, Id<com.extole.api.audience.Audience> audienceId,
        ZoneId timeZone) throws UserAuthorizationRestException, AudienceRestException, AudienceArchiveRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Audience audience = audienceService.archive(authorization, Id.valueOf(audienceId.getValue()));
            return audienceRestMapper.toAudienceResponse(audience, timeZone);
        } catch (AudienceAssociatedWithAudienceMembershipControllerTriggerException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_AUDIENCE_MEMBERSHIP_CONTROLLER_TRIGGER)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("audience_membership_controller_triggers",
                    toAssociatedAudienceControllerTriggerResponses(e.getAssociatedAudienceControllerTriggers()))
                .build();
        } catch (AudienceAssociatedWithAudienceMembershipEventControllerTriggerException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_AUDIENCE_MEMBERSHIP_EVENT_CONTROLLER_TRIGGER)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("audience_membership_event_controller_triggers",
                    toAssociatedAudienceControllerTriggerResponses(e.getAssociatedAudienceControllerTriggers()))
                .build();
        } catch (AudienceAssociatedWithRemoveMembershipControllerActionException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_REMOVE_MEMBERSHIP_CONTROLLER_ACTION)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("remove_membership_controller_actions",
                    toAssociatedAudienceControllerActionResponses(e.getAssociatedAudienceControllerActions()))
                .build();
        } catch (AudienceAssociatedWithCreateMembershipControllerActionException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_CREATE_MEMBERSHIP_CONTROLLER_ACTION)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("create_membership_controller_actions",
                    toAssociatedAudienceControllerActionResponses(e.getAssociatedAudienceControllerActions()))
                .build();
        } catch (AudienceAssociatedWithComponentVariableException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_COMPONENT_VARIABLE)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("component_variables",
                    toAssociatedAudienceComponentVariableResponses(e.getAssociatedAudienceComponentVariables()))
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceRestException.class)
                .withErrorCode(AudienceRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
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
    public AudienceResponse delete(String accessToken, Id<com.extole.api.audience.Audience> audienceId,
        ZoneId timeZone) throws UserAuthorizationRestException, AudienceRestException, AudienceArchiveRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Audience audience = audienceService.delete(authorization, Id.valueOf(audienceId.getValue()));
            return audienceRestMapper.toAudienceResponse(audience, timeZone);
        } catch (AudienceAssociatedWithAudienceMembershipControllerTriggerException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_AUDIENCE_MEMBERSHIP_CONTROLLER_TRIGGER)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("audience_membership_controller_triggers",
                    toAssociatedAudienceControllerTriggerResponses(e.getAssociatedAudienceControllerTriggers()))
                .build();
        } catch (AudienceAssociatedWithAudienceMembershipEventControllerTriggerException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_AUDIENCE_MEMBERSHIP_EVENT_CONTROLLER_TRIGGER)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("audience_membership_event_controller_triggers",
                    toAssociatedAudienceControllerTriggerResponses(e.getAssociatedAudienceControllerTriggers()))
                .build();
        } catch (AudienceAssociatedWithRemoveMembershipControllerActionException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_REMOVE_MEMBERSHIP_CONTROLLER_ACTION)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("remove_membership_controller_actions",
                    toAssociatedAudienceControllerActionResponses(e.getAssociatedAudienceControllerActions()))
                .build();
        } catch (AudienceAssociatedWithCreateMembershipControllerActionException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_CREATE_MEMBERSHIP_CONTROLLER_ACTION)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("create_membership_controller_actions",
                    toAssociatedAudienceControllerActionResponses(e.getAssociatedAudienceControllerActions()))
                .build();
        } catch (AudienceAssociatedWithComponentVariableException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_COMPONENT_VARIABLE)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("component_variables",
                    toAssociatedAudienceComponentVariableResponses(e.getAssociatedAudienceComponentVariables()))
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceRestException.class)
                .withErrorCode(AudienceRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
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
    public AudienceResponse unArchive(String accessToken, Id<com.extole.api.audience.Audience> audienceId,
        ZoneId timeZone) throws UserAuthorizationRestException, AudienceRestException, AudienceArchiveRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Audience audience = audienceService.unArchive(authorization, Id.valueOf(audienceId.getValue()));
            return audienceRestMapper.toAudienceResponse(audience, timeZone);
        } catch (AudienceAssociatedWithAudienceMembershipControllerTriggerException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_AUDIENCE_MEMBERSHIP_CONTROLLER_TRIGGER)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("audience_membership_controller_triggers",
                    toAssociatedAudienceControllerTriggerResponses(e.getAssociatedAudienceControllerTriggers()))
                .build();
        } catch (AudienceAssociatedWithAudienceMembershipEventControllerTriggerException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_AUDIENCE_MEMBERSHIP_EVENT_CONTROLLER_TRIGGER)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("audience_membership_event_controller_triggers",
                    toAssociatedAudienceControllerTriggerResponses(e.getAssociatedAudienceControllerTriggers()))
                .build();
        } catch (AudienceAssociatedWithRemoveMembershipControllerActionException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_REMOVE_MEMBERSHIP_CONTROLLER_ACTION)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("remove_membership_controller_actions",
                    toAssociatedAudienceControllerActionResponses(e.getAssociatedAudienceControllerActions()))
                .build();
        } catch (AudienceAssociatedWithCreateMembershipControllerActionException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(
                    AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_CREATE_MEMBERSHIP_CONTROLLER_ACTION)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("create_membership_controller_actions",
                    toAssociatedAudienceControllerActionResponses(e.getAssociatedAudienceControllerActions()))
                .build();
        } catch (AudienceAssociatedWithComponentVariableException e) {
            throw RestExceptionBuilder.newBuilder(AudienceArchiveRestException.class)
                .withErrorCode(AudienceArchiveRestException.AUDIENCE_ASSOCIATED_WITH_COMPONENT_VARIABLE)
                .withCause(e)
                .addParameter("audience_id", e.getAudienceId())
                .addParameter("component_variables",
                    toAssociatedAudienceComponentVariableResponses(e.getAssociatedAudienceComponentVariables()))
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceRestException.class)
                .withErrorCode(AudienceRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private List<AudienceControllerTriggerResponse> toAssociatedAudienceControllerTriggerResponses(
        List<AudienceControllerTrigger> associatedAudienceTriggers) {
        return associatedAudienceTriggers.stream()
            .map(value -> new AudienceControllerTriggerResponse(
                value.getCampaignId().getValue(),
                value.getControllerId().getValue(),
                value.getTriggerId().getValue(),
                value.getAudienceId().getValue()))
            .collect(Collectors.toList());
    }

    private List<AudienceControllerActionResponse> toAssociatedAudienceControllerActionResponses(
        List<AudienceControllerAction> associatedAudienceActions) {
        return associatedAudienceActions.stream()
            .map(value -> new AudienceControllerActionResponse(
                value.getCampaignId().getValue(),
                value.getControllerId().getValue(),
                value.getActionId().getValue(),
                value.getAudienceId().getValue()))
            .collect(Collectors.toList());
    }

    private List<AudienceComponentVariableResponse> toAssociatedAudienceComponentVariableResponses(
        List<AudienceComponentVariable> associatedAudienceComponentVariables) {
        return associatedAudienceComponentVariables.stream()
            .map(value -> new AudienceComponentVariableResponse(
                value.getCampaignId().getValue(),
                value.getComponentId().getValue(),
                value.getVariableName(),
                value.getAudienceId().getValue()))
            .collect(Collectors.toList());
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
