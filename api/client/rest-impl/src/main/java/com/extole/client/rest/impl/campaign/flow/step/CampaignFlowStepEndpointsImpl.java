package com.extole.client.rest.impl.campaign.flow.step;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepCreateRequest;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepEndpoints;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepResponse;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepRestException;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepUpdateRequest;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepValidationRestException;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepWordsRequest;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppCreateRequest;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppValidationRestException;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricCreateRequest;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.flow.step.BuiltCampaignFlowStepRestMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.Omissible;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignFlowStep;
import com.extole.model.entity.campaign.CampaignFlowStepApp;
import com.extole.model.entity.campaign.CampaignFlowStepMetric;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.InvalidComponentReferenceSocketNameException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStep;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.CampaignFlowStepWordsBuilder;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepBuilder;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepDescriptionLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepFlowPathInvalidException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepFlowPathLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepFlowPathMissingException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepIconColorLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepIconTypeLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepIconTypeMissingException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepMetricDuplicateException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepNameLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepSequenceInvalidException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepSequenceMissingException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepStepNameLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepStepNameMissingException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepWordLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppBuilder;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppDescriptionLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppNameLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppNameMissingException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppTypeNameLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppTypeNameMissingException;
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
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CampaignFlowStepEndpointsImpl implements CampaignFlowStepEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignFlowStepRestMapper campaignFlowStepRestMapper;
    private final BuiltCampaignFlowStepRestMapper builtCampaignFlowStepRestMapper;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignFlowStepEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignFlowStepRestMapper campaignFlowStepRestMapper,
        BuiltCampaignFlowStepRestMapper builtCampaignFlowStepRestMapper, CampaignService campaignService,
        CampaignProvider campaignProvider, ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignFlowStepRestMapper = campaignFlowStepRestMapper;
        this.builtCampaignFlowStepRestMapper = builtCampaignFlowStepRestMapper;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public CampaignFlowStepResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        CampaignFlowStepCreateRequest createRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignFlowStepMetricValidationRestException, CampaignFlowStepAppValidationRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            CampaignBuilder campaignBuilder;
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            CampaignFlowStepBuilder flowStepBuilder = campaignBuilder.addFlowStep();

            createRequest.getFlowPath().ifDefined((value) -> flowStepBuilder.withFlowPath(value));
            createRequest.getSequence().ifDefined((value) -> flowStepBuilder.withSequence(value));
            createRequest.getStepName().ifDefined((value) -> flowStepBuilder.withStepName(value));
            createRequest.getIconType().ifDefined((value) -> flowStepBuilder.withIconType(value));
            if (!createRequest.getMetrics().isEmpty()) {
                for (CampaignFlowStepMetricCreateRequest flowStepMetricCreateRequest : createRequest.getMetrics()) {
                    addFlowStepMetric(flowStepBuilder, flowStepMetricCreateRequest, createRequest.getComponentIds(),
                        createRequest.getComponentReferences());
                }
            }
            if (!createRequest.getApps().isEmpty()) {
                for (CampaignFlowStepAppCreateRequest flowStepAppCreateRequest : createRequest.getApps()) {
                    addFlowStepApp(flowStepBuilder, flowStepAppCreateRequest, createRequest.getComponentIds(),
                        createRequest.getComponentReferences());
                }
            }
            createRequest.getTags().ifDefined((value) -> flowStepBuilder.withTags(value));
            createRequest.getName().ifDefined((value) -> flowStepBuilder.withName(value));
            createRequest.getIconColor().ifDefined((value) -> flowStepBuilder.withIconColor(value));

            createRequest.getDescription().ifPresent(flowStepBuilder::withDescription);
            createRequest.getWords().ifPresent(words -> populateWords(flowStepBuilder, words));

            createRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(flowStepBuilder, componentIds);
            });

            createRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(flowStepBuilder, componentReferences);
            });

            return campaignFlowStepRestMapper.toFlowStepResponse(flowStepBuilder.save());

        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "flow_step")
                .addParameter("referencing_entity", "undefined")
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceSocketNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE_SOCKET_NAME)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .addParameter("socket_name", e.getSocketName())
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
        } catch (CampaignFlowStepException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void populateWords(CampaignFlowStepBuilder flowStepBuilder, CampaignFlowStepWordsRequest words) {
        CampaignFlowStepWordsBuilder builder = flowStepBuilder.withWords();
        words.getSingularNounName().ifPresent(builder::withSingularNounName);
        words.getPluralNounName().ifPresent(builder::withPluralNounName);
        words.getVerbName().ifPresent(builder::withVerbName);
        words.getRateName().ifPresent(builder::withRateName);
        words.getPersonCountingName().ifPresent(builder::withPersonCountingName);
    }

    @Override
    public CampaignFlowStepResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String flowStepId,
        CampaignFlowStepUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepValidationRestException, CampaignComponentValidationRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignFlowStepAppValidationRestException,
        CampaignFlowStepMetricValidationRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign;
        CampaignFlowStep flowStep;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            flowStep = getFlowStepById(campaign, flowStepId);
            campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
            CampaignFlowStepBuilder flowStepBuilder = campaignBuilder.updateFlowStep(flowStep);

            updateRequest.getFlowPath().ifPresent(flowPath -> {
                flowStepBuilder.withFlowPath(flowPath);
            });

            updateRequest.getSequence().ifPresent(sequence -> {
                flowStepBuilder.withSequence(sequence);
            });

            updateRequest.getStepName().ifPresent(stepName -> {
                flowStepBuilder.withStepName(stepName);
            });

            updateRequest.getIconType().ifPresent(iconType -> {
                flowStepBuilder.withIconType(iconType);
            });

            updateRequest.getMetrics().ifPresent(metrics -> {
                flowStepBuilder.removeMetrics();
                for (CampaignFlowStepMetricCreateRequest flowStepMetricCreateRequest : metrics) {
                    addFlowStepMetric(flowStepBuilder, flowStepMetricCreateRequest, updateRequest.getComponentIds(),
                        updateRequest.getComponentReferences());
                }
            });

            if (updateRequest.getApps().isPresent()) {
                flowStepBuilder.removeApps();
                for (CampaignFlowStepAppCreateRequest flowStepAppRequest : updateRequest.getApps().getValue()) {
                    addFlowStepApp(flowStepBuilder, flowStepAppRequest, updateRequest.getComponentIds(),
                        updateRequest.getComponentReferences());
                }
            }

            updateRequest.getTags().ifPresent(tags -> {
                flowStepBuilder.withTags(tags);
            });

            updateRequest.getName().ifPresent(name -> {
                flowStepBuilder.withName(name);
            });

            updateRequest.getIconColor().ifPresent(iconColor -> {
                flowStepBuilder.withIconColor(iconColor);
            });

            updateRequest.getDescription().ifPresent(flowStepBuilder::withDescription);
            updateRequest.getWords().ifPresent(words -> populateWords(flowStepBuilder, words));

            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(flowStepBuilder, componentIds);

                if (updateRequest.getMetrics().isOmitted()) {
                    for (CampaignFlowStepMetric flowStepMetric : flowStep.getMetrics()) {
                        CampaignFlowStepMetricBuilder flowStepMetricBuilder =
                            flowStepBuilder.updateFlowStepMetric(flowStepMetric);
                        handleComponentIds(flowStepMetricBuilder, componentIds);
                    }
                }

                if (updateRequest.getApps().isOmitted()) {
                    for (CampaignFlowStepApp flowStepApp : flowStep.getApps()) {
                        CampaignFlowStepAppBuilder flowStepAppBuilder =
                            flowStepBuilder.updateFlowStepApp(flowStepApp);
                        handleComponentIds(flowStepAppBuilder, componentIds);
                    }
                }
            });

            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(flowStepBuilder, componentReferences);

                if (updateRequest.getMetrics().isOmitted()) {
                    for (CampaignFlowStepMetric flowStepMetric : flowStep.getMetrics()) {
                        CampaignFlowStepMetricBuilder flowStepMetricBuilder =
                            flowStepBuilder.updateFlowStepMetric(flowStepMetric);
                        componentReferenceRequestMapper.handleComponentReferences(flowStepMetricBuilder,
                            componentReferences);
                    }
                }

                if (updateRequest.getApps().isOmitted()) {
                    for (CampaignFlowStepApp flowStepApp : flowStep.getApps()) {
                        CampaignFlowStepAppBuilder flowStepAppBuilder =
                            flowStepBuilder.updateFlowStepApp(flowStepApp);
                        componentReferenceRequestMapper.handleComponentReferences(flowStepAppBuilder,
                            componentReferences);
                    }
                }
            });
            return campaignFlowStepRestMapper.toFlowStepResponse(flowStepBuilder.save());
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "flow_step")
                .addParameter("referencing_entity", flowStepId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceSocketNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE_SOCKET_NAME)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .addParameter("socket_name", e.getSocketName())
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
        } catch (CampaignFlowStepException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignFlowStepResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String flowStepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        BuildCampaignRestException, CampaignUpdateRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign;
        CampaignFlowStep flowStep;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            flowStep = getFlowStepById(campaign, flowStepId);
            campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            campaignBuilder.removeFlowStep(flowStep).save();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignLabelMissingNameException | CampaignLabelDuplicateNameException
            | CreativeArchiveJavascriptException | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | InvalidComponentReferenceException
            | CampaignServiceNameMissingException | CampaignComponentNameDuplicateException
            | TransitionRuleAlreadyExistsForActionType
            | CampaignComponentException | CampaignFlowStepException | StepDataBuildException
            | CampaignScheduleException | CampaignGlobalDeleteException
            | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
        return campaignFlowStepRestMapper.toFlowStepResponse(flowStep);
    }

    @Override
    public CampaignFlowStepResponse get(String accessToken, String campaignId, String version, String flowStepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignFlowStep flowStep = getFlowStepById(campaign, flowStepId);

        return campaignFlowStepRestMapper.toFlowStepResponse(flowStep);
    }

    @Override
    public List<CampaignFlowStepResponse> list(String accessToken, String campaignId, String campaignVersion)
        throws UserAuthorizationRestException, CampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), campaignVersion);

        return campaign.getFlowSteps().stream().map(flowStep -> campaignFlowStepRestMapper.toFlowStepResponse(flowStep))
            .collect(Collectors.toList());
    }

    @Override
    public BuiltCampaignFlowStepResponse getBuilt(String accessToken, String campaignId, String version,
        String flowStepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        BuildCampaignRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);

        return builtCampaignFlowStepRestMapper.toBuiltFlowStepResponse(getFlowStepById(campaign, flowStepId));
    }

    @Override
    public List<BuiltCampaignFlowStepResponse> listBuilt(String accessToken, String campaignId, String campaignVersion,
        @Nullable String flowPath)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        BuiltCampaign builtCampaign =
            campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), campaignVersion);

        return builtCampaign.getFlowSteps()
            .stream()
            .filter(flowStep -> flowPath == null || isFlowStepPartOfTheFlow(flowStep, flowPath))
            .map(flowStep -> builtCampaignFlowStepRestMapper.toBuiltFlowStepResponse(flowStep))
            .collect(Collectors.toList());
    }

    public CampaignFlowStep getFlowStepById(Campaign campaign, String flowStepId) throws CampaignFlowStepRestException {
        return campaign.getFlowSteps().stream()
            .filter(flowStep -> flowStep.getId().equals(Id.valueOf(flowStepId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignFlowStepRestException.class)
                .withErrorCode(CampaignFlowStepRestException.INVALID_CAMPAIGN_FLOW_STEP_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("flow_step_id", flowStepId)
                .build());
    }

    // CHECKSTYLE.OFF: MethodLength
    private void throwValidationRestExceptionIfPossible(BuildCampaignEvaluatableException e)
        throws CampaignFlowStepValidationRestException, CampaignFlowStepMetricValidationRestException,
        CampaignFlowStepAppValidationRestException {
        if (e instanceof CampaignFlowStepIconTypeLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.ICON_TYPE_LENGTH_OUT_OF_RANGE)
                .addParameter("icon_type", ((CampaignFlowStepIconTypeLengthException) e).getIconType())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricDuplicateException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_DUPLICATE_EXISTS)
                .addParameter("metric_name", ((CampaignFlowStepMetricDuplicateException) e).getMetricName())
                .addParameter("flow_step_name", ((CampaignFlowStepMetricDuplicateException) e).getFlowStepName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepFlowPathMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.FLOW_PATH_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepSequenceMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.SEQUENCE_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepStepNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.STEP_NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepIconTypeMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.ICON_TYPE_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepSequenceInvalidException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.SEQUENCE_INVALID)
                .addParameter("sequence", ((CampaignFlowStepSequenceInvalidException) e).getSequence())
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
        if (e instanceof CampaignFlowStepIconColorLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.ICON_COLOR_LENGTH_OUT_OF_RANGE)
                .addParameter("icon_color", ((CampaignFlowStepIconColorLengthException) e).getIconColor())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepFlowPathLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.FLOW_PATH_LENGTH_OUT_OF_RANGE)
                .addParameter("flow_path", ((CampaignFlowStepFlowPathLengthException) e).getFlowPath())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepFlowPathInvalidException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.FLOW_PATH_INVALID)
                .addParameter("flow_path", ((CampaignFlowStepFlowPathInvalidException) e).getFlowPath())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepStepNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.STEP_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("step_name", ((CampaignFlowStepStepNameLengthException) e).getStepName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", ((CampaignFlowStepNameLengthException) e).getName())
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
        if (e instanceof CampaignFlowStepAppNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepAppTypeNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_TYPE_NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepAppNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("app_name", ((CampaignFlowStepAppNameLengthException) e).getName())
                .addParameter("min_length",
                    Integer.valueOf(((CampaignFlowStepAppNameLengthException) e).getMinLength()))
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppNameLengthException) e).getMaxLength()))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepAppTypeNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_TYPE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("app_type_name", ((CampaignFlowStepAppTypeNameLengthException) e).getName())
                .addParameter("min_length",
                    Integer.valueOf(((CampaignFlowStepAppTypeNameLengthException) e).getMinLength()))
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppTypeNameLengthException) e).getMaxLength()))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepAppDescriptionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("app_description", ((CampaignFlowStepAppDescriptionLengthException) e).getDescription())
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppDescriptionLengthException) e).getMaxLength()))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepDescriptionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", ((CampaignFlowStepDescriptionLengthException) e).getDescription())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepWordLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.WORLD_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getEvaluatableName())
                .addParameter("word", ((CampaignFlowStepWordLengthException) e).getWord())
                .withCause(e)
                .build();
        }
    }

    public BuiltCampaignFlowStep getFlowStepById(BuiltCampaign campaign, String flowStepId)
        throws CampaignFlowStepRestException {
        return campaign.getFlowSteps().stream()
            .filter(flowStep -> flowStep.getId().equals(Id.valueOf(flowStepId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignFlowStepRestException.class)
                .withErrorCode(CampaignFlowStepRestException.INVALID_CAMPAIGN_FLOW_STEP_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("flow_step_id", flowStepId)
                .build());
    }

    private void addFlowStepMetric(CampaignFlowStepBuilder flowStepBuilder,
        CampaignFlowStepMetricCreateRequest flowStepMetricCreateRequest,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences)
        throws CampaignComponentValidationRestException {
        if (flowStepMetricCreateRequest == null) {
            return;
        }
        CampaignFlowStepMetricBuilder flowStepMetricBuilder = flowStepBuilder.addFlowStepMetric();

        flowStepMetricCreateRequest.getName().ifDefined((value) -> flowStepMetricBuilder.withName(value));
        flowStepMetricBuilder.withDescription(flowStepMetricCreateRequest.getDescription());
        flowStepMetricCreateRequest.getExpression().ifDefined((value) -> flowStepMetricBuilder.withExpression(value));
        flowStepMetricCreateRequest.getUnit().ifDefined((value) -> flowStepMetricBuilder.withUnit(value));
        flowStepMetricCreateRequest.getTags().ifDefined((value) -> flowStepMetricBuilder.withTags(value));
        componentIds.ifPresent(candidates -> {
            handleComponentIds(flowStepMetricBuilder, candidates);
        });
        componentReferences.ifPresent(candidates -> {
            componentReferenceRequestMapper.handleComponentReferences(flowStepMetricBuilder, candidates);
        });
    }

    private void addFlowStepApp(CampaignFlowStepBuilder flowStepBuilder,
        CampaignFlowStepAppCreateRequest flowStepAppCreateRequest,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences)
        throws CampaignComponentValidationRestException, CampaignFlowStepAppValidationRestException {
        if (flowStepAppCreateRequest == null) {
            return;
        }

        CampaignFlowStepAppBuilder flowStepAppBuilder = flowStepBuilder.addFlowStepApp();

        flowStepAppCreateRequest.getName().ifDefined((value) -> flowStepAppBuilder.withName(value));
        flowStepAppCreateRequest.getDescription()
            .ifPresent(description -> flowStepAppBuilder.withDescription(description));

        if (flowStepAppCreateRequest.getType() != null) {
            flowStepAppCreateRequest.getType().getName()
                .ifDefined((value) -> flowStepAppBuilder.withType().withName(value));
        } else {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_TYPE_MISSING)
                .build();
        }
        componentIds.ifPresent(candidates -> {
            handleComponentIds(flowStepAppBuilder, candidates);
        });
        componentReferences.ifPresent(candidates -> {
            componentReferenceRequestMapper.handleComponentReferences(flowStepAppBuilder, candidates);
        });
    }

    private boolean isFlowStepPartOfTheFlow(BuiltCampaignFlowStep flowStep, String flowPath) {
        return StringUtils.equals(flowStep.getFlowPath(), flowPath)
            || flowStep.getFlowPath().startsWith(flowPath + "/");
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

    private CampaignBuilder getCampaignBuilder(String campaignId, Authorization authorization,
        String expectedCurrentVersion) throws CampaignRestException {
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }
        return campaignBuilder;
    }

}
