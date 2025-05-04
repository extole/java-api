package com.extole.client.rest.impl.campaign.controller.trigger.has.prior.reward;

import static com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_AGGREGATION_COUNT;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_AGGREGATION_COUNT_CONFIGURATION;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_AGGREGATION_SUM;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_AGGREGATION_SUM_CONFIGURATION;

import java.time.ZoneId;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardCreateRequest;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardEndpoints;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardResponse;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardUpdateRequest;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.client.rest.impl.campaign.controller.trigger.TriggerTypeNotSupportedRestExceptionMapper;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignControllerTriggerHasPriorReward;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerDescriptionLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameMissingException;
import com.extole.model.service.campaign.controller.trigger.TriggerTypeNotSupportedException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardFilterExpressionInvalidSyntaxException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidAggregationCountValueException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidAggregationSumValueException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidCountAggregationConfigurationException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidFilterNameException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidFilterTagException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidFilterTagLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidFilterTagsLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidSumAggregationConfigurationException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardMissingAggregationConditionException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardMissingFilterExpressionValueException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardMissingFilterNameException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Provider
public class CampaignControllerTriggerHasPriorRewardEndpointsImpl
    implements CampaignControllerTriggerHasPriorRewardEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerTriggerHasPriorRewardResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignControllerTriggerHasPriorRewardEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerTriggerHasPriorRewardResponseMapper responseMapper,
        CampaignProvider campaignProvider,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.campaignService = campaignService;
        this.responseMapper = responseMapper;
        this.campaignProvider = campaignProvider;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public CampaignControllerTriggerHasPriorRewardResponse get(String accessToken,
        String campaignId,
        String version,
        String controllerId,
        String triggerId,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerTriggerHasPriorReward trigger =
            campaignStepProvider.getHasPriorRewardStepTrigger(campaign, controllerId, triggerId);

        return responseMapper.toResponse(trigger, timeZone);
    }

    // CHECKSTYLE.OFF: MethodLength
    @Override
    public CampaignControllerTriggerHasPriorRewardResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerTriggerHasPriorRewardCreateRequest request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerHasPriorRewardValidationRestException,
        CampaignControllerTriggerValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign;
        CampaignStep step;
        CampaignBuilder campaignBuilder;
        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
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

        try {
            CampaignControllerTriggerHasPriorRewardBuilder triggerBuilder =
                campaignBuilder.updateStep(step)
                    .addTrigger(CampaignControllerTriggerType.HAS_PRIOR_REWARD);
            request.getTriggerPhase()
                .ifPresent(
                    phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            request.getName().ifPresent(name -> triggerBuilder.withName(name));
            request.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));

            request.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
            request.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
            request.getFilterNames().ifPresent(filterNames -> triggerBuilder.withFilterNames(filterNames));
            request.getFilterScope()
                .ifPresent(filterScopes -> triggerBuilder
                    .withFilterScope(Evaluatables.remapEnum(filterScopes, new TypeReference<>() {})));
            request.getFilterTags().ifPresent(filterTags -> triggerBuilder.withFilterTags(filterTags));
            request.getFilterMinAge().ifPresent(filterMinAge -> triggerBuilder.withFilterMinAge(filterMinAge));
            request.getFilterMaxAge().ifPresent(filterMaxAge -> triggerBuilder.withFilterMaxAge(filterMaxAge));
            request.getFilterMinDate().ifPresent(filterMinDate -> triggerBuilder.withFilterMinDate(filterMinDate));
            request.getFilterMaxDate().ifPresent(filterMaxDate -> triggerBuilder.withFilterMaxDate(filterMaxDate));
            request.getFilterRewardSupplierIds()
                .ifPresent(
                    filterRewardSuppliersIds -> triggerBuilder.withFilterRewardSupplierIds(filterRewardSuppliersIds));
            request.getFilterFaceValueTypes()
                .ifPresent(filterFaceValueTypes -> triggerBuilder.withFilterFaceValueTypes(
                    Evaluatables.remapEnumCollection(filterFaceValueTypes, new TypeReference<>() {})));
            request.getFilterStates()
                .ifPresent(filterStates -> triggerBuilder
                    .withFilterStates(Evaluatables.remapEnumCollection(filterStates, new TypeReference<>() {})));
            request.getFilterExpressions()
                .ifPresent(filterExpressions -> triggerBuilder.withFilterExpressions(filterExpressions));
            request.getFilterExpression()
                .ifPresent(filterExpression -> triggerBuilder.withFilterExpression(filterExpression));
            request.getSumOfFaceValueMax()
                .ifPresent(sumOfFaceValueMax -> triggerBuilder.withSumOfFaceValueMax(sumOfFaceValueMax));
            request.getSumOfFaceValueMin()
                .ifPresent(sumOfFaceValueMin -> triggerBuilder.withSumOfFaceValueMin(sumOfFaceValueMin));
            request.getCountMax().ifPresent(countMax -> triggerBuilder.withCountMax(countMax));
            request.getCountMin().ifPresent(countMin -> triggerBuilder.withCountMin(countMin));
            request.getCountMatches().ifPresent(countMatches -> triggerBuilder.withCountMatches(countMatches));
            request.getTaxYearStart().ifPresent(taxYearStart -> triggerBuilder.withTaxYearStart(taxYearStart));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(triggerBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(triggerBuilder, componentReferences);
            });
            return responseMapper.toResponse(triggerBuilder.save(), timeZone);
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
        } catch (CampaignControllerTriggerHasPriorRewardMissingAggregationConditionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.MISSING_AGGREGATION_CONDITION)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidSumAggregationConfigurationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(INVALID_AGGREGATION_SUM_CONFIGURATION)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidCountAggregationConfigurationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(INVALID_AGGREGATION_COUNT_CONFIGURATION)
                .build();
        } catch (CampaignControllerTriggerNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.MISSING_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.INVALID_NAME_LENGTH)
                .addParameter("name", e.getName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "trigger")
                .addParameter("referencing_entity", "undefined")
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterTagsLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_FILTER_TAGS_LENGTH)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_FILTER_EXPRESSION_LENGTH)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardMissingFilterExpressionValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.MISSING_FILTER_EXPRESSION_VALUE)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterTagLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_TAG_LENGTH)
                .addParameter("tag", e.getTag())
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterTagException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_TAG)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_FILTER_NAME_LENGTH)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardMissingFilterNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.MISSING_FILTER_NAME)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidAggregationSumValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(INVALID_AGGREGATION_SUM)
                .addParameter("value", e.getValue())
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidAggregationCountValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(INVALID_AGGREGATION_COUNT)
                .addParameter("value", e.getValue())
                .build();
        } catch (CampaignControllerTriggerDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardFilterExpressionInvalidSyntaxException e) {
            throw RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(BuildCampaignRestException.EXPRESSION_INVALID_SYNTAX)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_version", e.getCampaignVersion())
                .addParameter("entity", e.getEntity())
                .addParameter("entity_id", e.getEntityId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .addParameter("description", e.getMessage())
                .withCause(e)
                .build();
        } catch (TriggerTypeNotSupportedException e) {
            throw TriggerTypeNotSupportedRestExceptionMapper.getInstance().map(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    // CHECKSTYLE.OFF: MethodLength
    @Override
    public CampaignControllerTriggerHasPriorRewardResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        CampaignControllerTriggerHasPriorRewardUpdateRequest request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerHasPriorRewardValidationRestException,
        CampaignControllerTriggerValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerHasPriorReward trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getHasPriorRewardStepTrigger(campaign, controllerId, triggerId);
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

        try {
            CampaignControllerTriggerHasPriorRewardBuilder triggerBuilder =
                campaignBuilder.updateStep(step)
                    .updateTrigger(trigger);
            request.getTriggerPhase().ifPresent(
                phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            request.getName().ifPresent(name -> triggerBuilder.withName(name));
            request.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));

            request.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
            request.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
            request.getFilterNames().ifPresent(filterNames -> triggerBuilder.withFilterNames(filterNames));
            request.getFilterScope()
                .ifPresent(filterScopes -> triggerBuilder
                    .withFilterScope(Evaluatables.remapEnum(filterScopes, new TypeReference<>() {})));
            request.getFilterTags().ifPresent(filterTags -> triggerBuilder.withFilterTags(filterTags));
            request.getFilterMinAge().ifPresent(filterMinAge -> triggerBuilder.withFilterMinAge(filterMinAge));
            request.getFilterMaxAge().ifPresent(filterMaxAge -> triggerBuilder.withFilterMaxAge(filterMaxAge));
            request.getFilterMinDate().ifPresent(filterMinDate -> triggerBuilder.withFilterMinDate(filterMinDate));
            request.getFilterMaxDate().ifPresent(filterMaxDate -> triggerBuilder.withFilterMaxDate(filterMaxDate));
            request.getFilterRewardSupplierIds()
                .ifPresent(
                    filterRewardSuppliersIds -> triggerBuilder.withFilterRewardSupplierIds(filterRewardSuppliersIds));
            request.getFilterFaceValueTypes()
                .ifPresent(filterFaceValueTypes -> triggerBuilder.withFilterFaceValueTypes(
                    Evaluatables.remapEnumCollection(filterFaceValueTypes, new TypeReference<>() {})));
            request.getFilterStates()
                .ifPresent(filterStates -> triggerBuilder
                    .withFilterStates(Evaluatables.remapEnumCollection(filterStates, new TypeReference<>() {})));
            request.getFilterExpressions()
                .ifPresent(filterExpressions -> triggerBuilder.withFilterExpressions(filterExpressions));
            request.getFilterExpression()
                .ifPresent(filterExpression -> triggerBuilder.withFilterExpression(filterExpression));
            request.getSumOfFaceValueMax()
                .ifPresent(sumOfFaceValueMax -> triggerBuilder.withSumOfFaceValueMax(sumOfFaceValueMax));
            request.getSumOfFaceValueMin()
                .ifPresent(sumOfFaceValueMin -> triggerBuilder.withSumOfFaceValueMin(sumOfFaceValueMin));
            request.getCountMax().ifPresent(countMax -> triggerBuilder.withCountMax(countMax));
            request.getCountMin().ifPresent(countMin -> triggerBuilder.withCountMin(countMin));
            request.getCountMatches().ifPresent(countMatches -> triggerBuilder.withCountMatches(countMatches));
            request.getTaxYearStart().ifPresent(taxYearStart -> triggerBuilder.withTaxYearStart(taxYearStart));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(triggerBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(triggerBuilder, componentReferences);
            });

            return responseMapper.toResponse(triggerBuilder.save(), timeZone);
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
        } catch (CampaignControllerTriggerHasPriorRewardMissingAggregationConditionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.MISSING_AGGREGATION_CONDITION)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidSumAggregationConfigurationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(INVALID_AGGREGATION_SUM_CONFIGURATION)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidCountAggregationConfigurationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(INVALID_AGGREGATION_COUNT_CONFIGURATION)
                .build();
        } catch (CampaignControllerTriggerNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.MISSING_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.INVALID_NAME_LENGTH)
                .addParameter("name", e.getName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "trigger")
                .addParameter("referencing_entity", triggerId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterTagsLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_FILTER_TAGS_LENGTH)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_FILTER_EXPRESSION_LENGTH)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardMissingFilterExpressionValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.MISSING_FILTER_EXPRESSION_VALUE)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterTagLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_TAG_LENGTH)
                .addParameter("tag", e.getTag())
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterTagException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_TAG)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_FILTER_NAME_LENGTH)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardMissingFilterNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.MISSING_FILTER_NAME)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidAggregationSumValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(INVALID_AGGREGATION_SUM)
                .addParameter("value", e.getValue())
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidAggregationCountValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(INVALID_AGGREGATION_COUNT)
                .addParameter("value", e.getValue())
                .build();
        } catch (CampaignControllerTriggerDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardFilterExpressionInvalidSyntaxException e) {
            throw RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(BuildCampaignRestException.EXPRESSION_INVALID_SYNTAX)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_version", e.getCampaignVersion())
                .addParameter("entity", e.getEntity())
                .addParameter("entity_id", e.getEntityId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .addParameter("description", e.getMessage())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerHasPriorRewardResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        CampaignBuilder campaignBuilder;
        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerHasPriorReward trigger;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getHasPriorRewardStepTrigger(campaign, controllerId, triggerId);
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

        try {
            campaignBuilder.updateStep(step)
                .removeTrigger(trigger)
                .save();

            return responseMapper.toResponse(trigger, timeZone);
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
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (InvalidComponentReferenceException | CampaignStepBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
