package com.extole.client.rest.impl.campaign.flow.step.app;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepAppResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepRestException;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppCreateRequest;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppEndpoints;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppResponse;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppRestException;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppTypeCreateRequest;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppUpdateRequest;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.flow.step.app.BuiltCampaignFlowStepAppRestMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.flow.step.CampaignFlowStepProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignFlowStep;
import com.extole.model.entity.campaign.CampaignFlowStepApp;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStep;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStepApp;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppBuilder;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppDescriptionLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppNameLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppNameMissingException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppTypeNameLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppTypeNameMissingException;

@Provider
public class CampaignFlowStepAppEndpointsImpl implements CampaignFlowStepAppEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignFlowStepAppRestMapper campaignFlowStepAppRestMapper;
    private final BuiltCampaignFlowStepAppRestMapper builtCampaignFlowStepAppRestMapper;
    private final CampaignFlowStepProvider campaignFlowStepProvider;
    private final CampaignFlowStepAppProvider campaignFlowStepAppProvider;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignFlowStepAppEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CampaignFlowStepAppRestMapper campaignFlowStepAppRestMapper,
        BuiltCampaignFlowStepAppRestMapper builtCampaignFlowStepAppRestMapper,
        CampaignFlowStepProvider campaignFlowStepProvider,
        CampaignFlowStepAppProvider campaignFlowStepAppProvider,
        CampaignService campaignService,
        CampaignProvider campaignProvider,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignFlowStepAppRestMapper = campaignFlowStepAppRestMapper;
        this.builtCampaignFlowStepAppRestMapper = builtCampaignFlowStepAppRestMapper;
        this.campaignFlowStepProvider = campaignFlowStepProvider;
        this.campaignFlowStepAppProvider = campaignFlowStepAppProvider;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public CampaignFlowStepAppResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String flowStepId,
        CampaignFlowStepAppCreateRequest createRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepAppValidationRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, BuildCampaignRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignVersion -> campaignBuilder.withExpectedVersion(campaignVersion));
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }

        try {
            CampaignFlowStep campaignFlowStep = campaignFlowStepProvider.getCampaignFlowStep(campaign, flowStepId);
            CampaignFlowStepAppBuilder flowStepAppBuilder = campaignBuilder.updateFlowStep(campaignFlowStep)
                .addFlowStepApp();
            createRequest.getName().ifDefined((value) -> flowStepAppBuilder.withName(value));
            createRequest.getDescription()
                .ifPresent(description -> flowStepAppBuilder.withDescription(description));
            if (createRequest.getType() != null) {
                createRequest.getType().getName()
                    .ifDefined((value) -> flowStepAppBuilder.withType().withName(value));
            } else {
                throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                    .withErrorCode(CampaignFlowStepAppValidationRestException.APP_TYPE_MISSING)
                    .build();
            }

            createRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(flowStepAppBuilder, componentIds);
            });

            createRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(flowStepAppBuilder, componentReferences);
            });
            return campaignFlowStepAppRestMapper.toFlowStepAppResponse(flowStepAppBuilder.save());
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            if (e instanceof BuildCampaignEvaluatableException) {
                throwValidationRestExceptionIfPossible((BuildCampaignEvaluatableException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignFlowStepAppResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String flowStepId,
        String flowStepAppId,
        CampaignFlowStepAppUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepAppValidationRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, BuildCampaignRestException, CampaignFlowStepAppRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignVersion -> campaignBuilder.withExpectedVersion(campaignVersion));
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }

        try {
            CampaignFlowStep campaignFlowStep = campaignFlowStepProvider.getCampaignFlowStep(campaign, flowStepId);
            CampaignFlowStepApp campaignFlowStepApp = campaignFlowStepAppProvider.getCampaignFlowStepApp(campaign,
                flowStepId, flowStepAppId);

            CampaignFlowStepAppBuilder flowStepAppBuilder = campaignBuilder.updateFlowStep(campaignFlowStep)
                .updateFlowStepApp(campaignFlowStepApp);
            updateRequest.getName()
                .ifPresent(nameEvaluatable -> nameEvaluatable.ifDefined(value -> flowStepAppBuilder.withName(value)));
            updateRequest.getDescription()
                .ifPresent(description -> flowStepAppBuilder.withDescription(description));
            if (updateRequest.getType().isPresent()) {
                CampaignFlowStepAppTypeCreateRequest type = updateRequest.getType().getValue();
                type.getName().ifDefined((value) -> flowStepAppBuilder.withType().withName(value));
            } else {
                throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                    .withErrorCode(CampaignFlowStepAppValidationRestException.APP_TYPE_MISSING)
                    .build();
            }
            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(flowStepAppBuilder, componentIds);
            });

            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(flowStepAppBuilder, componentReferences);
            });

            return campaignFlowStepAppRestMapper.toFlowStepAppResponse(flowStepAppBuilder.save());
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "flow_step_app")
                .addParameter("referencing_entity", flowStepAppId)
                .withCause(e)
                .build();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            if (e instanceof BuildCampaignEvaluatableException) {
                throwValidationRestExceptionIfPossible((BuildCampaignEvaluatableException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignFlowStepAppResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String flowStepId,
        String flowStepAppId)
        throws CampaignRestException, CampaignUpdateRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignFlowStepRestException, CampaignFlowStepAppRestException,
        UserAuthorizationRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignVersion -> campaignBuilder.withExpectedVersion(campaignVersion));
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }

        try {
            CampaignFlowStep campaignFlowStep = campaignFlowStepProvider.getCampaignFlowStep(campaign, flowStepId);
            CampaignFlowStepApp campaignFlowStepApp = campaignFlowStepAppProvider.getCampaignFlowStepApp(campaign,
                flowStepId, flowStepAppId);

            campaignBuilder.updateFlowStep(campaignFlowStep)
                .removeFlowStepApp(campaignFlowStepApp).save();
            return campaignFlowStepAppRestMapper.toFlowStepAppResponse(campaignFlowStepApp);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignFlowStepException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignFlowStepAppResponse get(String accessToken,
        String campaignId,
        String campaignVersion,
        String flowStepId,
        String flowStepAppId)
        throws CampaignRestException, UserAuthorizationRestException, CampaignFlowStepRestException,
        CampaignFlowStepAppRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), campaignVersion);
        CampaignFlowStepApp campaignFlowStepApp = campaignFlowStepAppProvider.getCampaignFlowStepApp(campaign,
            flowStepId, flowStepAppId);
        return campaignFlowStepAppRestMapper.toFlowStepAppResponse(campaignFlowStepApp);
    }

    @Override
    public List<CampaignFlowStepAppResponse> getAll(String accessToken,
        String campaignId,
        String campaignVersion,
        String flowStepId) throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), campaignVersion);
        CampaignFlowStep campaignFlowStep = campaignFlowStepProvider.getCampaignFlowStep(campaign, flowStepId);
        return campaignFlowStep.getApps()
            .stream()
            .map(flowStepApp -> campaignFlowStepAppRestMapper.toFlowStepAppResponse(flowStepApp))
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public BuiltCampaignFlowStepAppResponse getBuilt(String accessToken,
        String campaignId,
        String campaignVersion,
        String flowStepId,
        String flowStepAppId) throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignFlowStepRestException, CampaignFlowStepAppRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign builtCampaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId),
            campaignVersion);
        BuiltCampaignFlowStepApp builtCampaignFlowStepApp =
            campaignFlowStepAppProvider.getBuiltCampaignFlowStepApp(builtCampaign, flowStepId,
                flowStepAppId);
        return builtCampaignFlowStepAppRestMapper.toBuiltFlowStepAppResponse(builtCampaignFlowStepApp);
    }

    @Override
    public List<BuiltCampaignFlowStepAppResponse> getAllBuilt(String accessToken,
        String campaignId,
        String campaignVersion,
        String flowStepId) throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignFlowStepRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign builtCampaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId),
            campaignVersion);
        BuiltCampaignFlowStep builtCampaignFlowStep =
            campaignFlowStepProvider.getBuiltCampaignFlowStep(builtCampaign, flowStepId);
        return builtCampaignFlowStep.getApps().stream()
            .map(builtCampaignFlowStepApp -> builtCampaignFlowStepAppRestMapper
                .toBuiltFlowStepAppResponse(builtCampaignFlowStepApp))
            .collect(Collectors.toUnmodifiableList());
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

    private void throwValidationRestExceptionIfPossible(BuildCampaignEvaluatableException exception)
        throws CampaignFlowStepAppValidationRestException {
        if (exception instanceof CampaignFlowStepAppNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_NAME_MISSING)
                .withCause(exception)
                .build();
        }
        if (exception instanceof CampaignFlowStepAppTypeNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_TYPE_NAME_MISSING)
                .withCause(exception)
                .build();
        }
        if (exception instanceof CampaignFlowStepAppNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("app_name", ((CampaignFlowStepAppNameLengthException) exception).getName())
                .addParameter("min_length",
                    Integer.valueOf(((CampaignFlowStepAppNameLengthException) exception).getMinLength()))
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppNameLengthException) exception).getMaxLength()))
                .withCause(exception)
                .build();
        }
        if (exception instanceof CampaignFlowStepAppTypeNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_TYPE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("app_type_name", ((CampaignFlowStepAppTypeNameLengthException) exception).getName())
                .addParameter("min_length",
                    Integer.valueOf(((CampaignFlowStepAppTypeNameLengthException) exception).getMinLength()))
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppTypeNameLengthException) exception).getMaxLength()))
                .withCause(exception)
                .build();
        }
        if (exception instanceof CampaignFlowStepAppDescriptionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("app_description",
                    ((CampaignFlowStepAppDescriptionLengthException) exception).getDescription())
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppDescriptionLengthException) exception).getMaxLength()))
                .withCause(exception)
                .build();
        }
    }
}
