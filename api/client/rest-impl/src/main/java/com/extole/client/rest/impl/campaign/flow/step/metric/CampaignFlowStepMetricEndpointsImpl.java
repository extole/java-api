package com.extole.client.rest.impl.campaign.flow.step.metric;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepMetricResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepRestException;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricCreateRequest;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricEndpoints;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricResponse;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricRestException;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricUpdateRequest;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.flow.step.metric.BuiltCampaignFlowStepMetricRestMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.flow.step.CampaignFlowStepProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignFlowStep;
import com.extole.model.entity.campaign.CampaignFlowStepMetric;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStep;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStepMetric;
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
import com.extole.model.service.campaign.flow.step.CampaignFlowStepMetricDuplicateException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricBuilder;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricDescriptionLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricExpressionInvalidException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricExpressionLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricExpressionMissingException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricNameLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricNameMissingException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricTagsMissingException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricUnitLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricUnitMissingException;

@Provider
public class CampaignFlowStepMetricEndpointsImpl implements CampaignFlowStepMetricEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignFlowStepMetricRestMapper campaignFlowStepMetricRestMapper;
    private final BuiltCampaignFlowStepMetricRestMapper builtCampaignFlowStepMetricRestMapper;
    private final CampaignFlowStepProvider campaignFlowStepProvider;
    private final CampaignFlowStepMetricProvider campaignFlowStepMetricProvider;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignFlowStepMetricEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignFlowStepMetricRestMapper campaignFlowStepMetricRestMapper,
        BuiltCampaignFlowStepMetricRestMapper builtCampaignFlowStepMetricRestMapper,
        CampaignFlowStepProvider campaignFlowStepProvider,
        CampaignFlowStepMetricProvider campaignFlowStepMetricProvider,
        CampaignService campaignService,
        CampaignProvider campaignProvider,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignFlowStepMetricRestMapper = campaignFlowStepMetricRestMapper;
        this.builtCampaignFlowStepMetricRestMapper = builtCampaignFlowStepMetricRestMapper;
        this.campaignFlowStepProvider = campaignFlowStepProvider;
        this.campaignFlowStepMetricProvider = campaignFlowStepMetricProvider;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public CampaignFlowStepMetricResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String flowStepId,
        CampaignFlowStepMetricCreateRequest createRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepMetricValidationRestException, CampaignComponentValidationRestException,
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
            CampaignFlowStepMetricBuilder flowStepMetricBuilder = campaignBuilder.updateFlowStep(campaignFlowStep)
                .addFlowStepMetric();

            createRequest.getName().ifDefined((value) -> flowStepMetricBuilder.withName(value));
            createRequest.getDescription().ifDefined(description -> flowStepMetricBuilder.withDescription(description));
            createRequest.getExpression().ifDefined((value) -> flowStepMetricBuilder.withExpression(value));
            createRequest.getUnit().ifDefined((value) -> flowStepMetricBuilder.withUnit(value));
            createRequest.getTags().ifDefined((value) -> flowStepMetricBuilder.withTags(value));

            createRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(flowStepMetricBuilder, componentIds);
            });

            createRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(flowStepMetricBuilder, componentReferences);
            });
            return campaignFlowStepMetricRestMapper.toFlowStepMetricResponse(flowStepMetricBuilder.save());
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
    public CampaignFlowStepMetricResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String flowStepId,
        String flowStepMetricId,
        CampaignFlowStepMetricUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepMetricValidationRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, BuildCampaignRestException, CampaignFlowStepMetricRestException {

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
            CampaignFlowStepMetric campaignFlowStepMetric =
                campaignFlowStepMetricProvider.getCampaignFlowStepMetric(campaign,
                    flowStepId, flowStepMetricId);

            CampaignFlowStepMetricBuilder flowStepMetricBuilder = campaignBuilder.updateFlowStep(campaignFlowStep)
                .updateFlowStepMetric(campaignFlowStepMetric);
            updateRequest.getName()
                .ifPresent(name -> name.ifDefined(definedName -> flowStepMetricBuilder.withName(definedName)));
            updateRequest.getDescription().ifPresent(description -> description
                .ifDefined(definedDescription -> flowStepMetricBuilder.withDescription(definedDescription)));
            updateRequest.getExpression().ifPresent(expression -> expression
                .ifDefined(definedExpression -> flowStepMetricBuilder.withExpression(definedExpression)));
            updateRequest.getTags()
                .ifPresent(tags -> tags.ifDefined(definedTags -> flowStepMetricBuilder.withTags(definedTags)));
            updateRequest.getUnit()
                .ifPresent(unit -> unit.ifDefined(definedUnit -> flowStepMetricBuilder.withUnit(definedUnit)));
            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(flowStepMetricBuilder, componentIds);
            });

            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(flowStepMetricBuilder, componentReferences);
            });
            return campaignFlowStepMetricRestMapper.toFlowStepMetricResponse(flowStepMetricBuilder.save());
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "flow_step_metric")
                .addParameter("referencing_entity", flowStepMetricId)
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
    public CampaignFlowStepMetricResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String flowStepId,
        String flowStepMetricId)
        throws CampaignRestException, CampaignUpdateRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignFlowStepRestException, CampaignFlowStepMetricRestException,
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
            CampaignFlowStepMetric campaignFlowStepMetric =
                campaignFlowStepMetricProvider.getCampaignFlowStepMetric(campaign,
                    flowStepId, flowStepMetricId);
            campaignBuilder.updateFlowStep(campaignFlowStep)
                .removeFlowStepMetric(campaignFlowStepMetric).save();
            return campaignFlowStepMetricRestMapper.toFlowStepMetricResponse(campaignFlowStepMetric);
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
    public CampaignFlowStepMetricResponse get(String accessToken,
        String campaignId,
        String campaignVersion,
        String flowStepId,
        String flowStepMetricId)
        throws CampaignRestException, UserAuthorizationRestException, CampaignFlowStepRestException,
        CampaignFlowStepMetricRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), campaignVersion);
        CampaignFlowStepMetric campaignFlowStepMetric =
            campaignFlowStepMetricProvider.getCampaignFlowStepMetric(campaign,
                flowStepId, flowStepMetricId);

        return campaignFlowStepMetricRestMapper.toFlowStepMetricResponse(campaignFlowStepMetric);
    }

    @Override
    public List<CampaignFlowStepMetricResponse> getAll(String accessToken,
        String campaignId,
        String campaignVersion,
        String flowStepId) throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), campaignVersion);
        CampaignFlowStep campaignFlowStep = campaignFlowStepProvider.getCampaignFlowStep(campaign, flowStepId);
        return campaignFlowStep.getMetrics()
            .stream()
            .map(flowStepMetric -> campaignFlowStepMetricRestMapper.toFlowStepMetricResponse(flowStepMetric))
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public BuiltCampaignFlowStepMetricResponse getBuilt(String accessToken,
        String campaignId,
        String campaignVersion,
        String flowStepId,
        String flowStepMetricId)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignFlowStepRestException, CampaignFlowStepMetricRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign builtCampaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId),
            campaignVersion);
        BuiltCampaignFlowStepMetric builtCampaignFlowStepMetric =
            campaignFlowStepMetricProvider.getBuiltCampaignFlowStepMetric(builtCampaign, flowStepId,
                flowStepMetricId);

        return builtCampaignFlowStepMetricRestMapper.toBuiltFlowStepMetricResponse(builtCampaignFlowStepMetric);
    }

    @Override
    public List<BuiltCampaignFlowStepMetricResponse> getAllBuilt(String accessToken,
        String campaignId,
        String campaignVersion,
        String flowStepId) throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignFlowStepRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign builtCampaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId),
            campaignVersion);
        BuiltCampaignFlowStep builtCampaignFlowStep =
            campaignFlowStepProvider.getBuiltCampaignFlowStep(builtCampaign, flowStepId);
        return builtCampaignFlowStep.getMetrics().stream()
            .map(builtFlowStepMetric -> builtCampaignFlowStepMetricRestMapper
                .toBuiltFlowStepMetricResponse(builtFlowStepMetric))
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

    private void throwValidationRestExceptionIfPossible(BuildCampaignEvaluatableException e)
        throws CampaignFlowStepMetricValidationRestException {
        if (e instanceof CampaignFlowStepMetricDuplicateException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_DUPLICATE_EXISTS)
                .addParameter("metric_name", ((CampaignFlowStepMetricDuplicateException) e).getMetricName())
                .addParameter("flow_step_name", ((CampaignFlowStepMetricDuplicateException) e).getFlowStepName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricUnitMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_UNIT_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricTagsMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_TAGS_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricExpressionMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_EXPRESSION_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricExpressionInvalidException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_EXPRESSION_INVALID)
                .addParameter("metric_expression",
                    ((CampaignFlowStepMetricExpressionInvalidException) e).getExpression())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricUnitLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_UNIT_LENGTH_OUT_OF_RANGE)
                .addParameter("unit", ((CampaignFlowStepMetricUnitLengthException) e).getUnit())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("metric_name", ((CampaignFlowStepMetricNameLengthException) e).getName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricDescriptionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("metric_description",
                    ((CampaignFlowStepMetricDescriptionLengthException) e).getDescription())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricExpressionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("metric_expression",
                    ((CampaignFlowStepMetricExpressionLengthException) e).getExpression())
                .withCause(e)
                .build();
        }
    }
}
