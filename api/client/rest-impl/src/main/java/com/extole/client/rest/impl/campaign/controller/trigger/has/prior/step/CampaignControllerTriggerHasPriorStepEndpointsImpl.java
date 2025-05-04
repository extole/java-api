package com.extole.client.rest.impl.campaign.controller.trigger.has.prior.step;

import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MATCHES_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MATCHES_NOT_IN_COUNT_RANGE;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MATCHES_WITH_SUM_OF_VALUE_MIN_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MAX_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MIN_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_CAMPAIGN_ID_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_CAMPAIGN_ID_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_EXPRESSION_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_EXPRESSION_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_MAX_AGE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_MAX_VALUE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_MIN_AGE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_MIN_VALUE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_NAME_CONTAINS_ILLEGAL_CHARACTER;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_NAME_LENGTH_OUT_OF_RANGE;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PARTNER_EVENT_ID_NAME_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PARTNER_EVENT_ID_NAME_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PARTNER_EVENT_ID_VALUE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PARTNER_EVENT_ID_VALUE_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PROGRAM_LABEL_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PROGRAM_LABEL_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.SUM_OF_VALUE_MAX_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.SUM_OF_VALUE_MIN_INVALID;

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
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCreateRequest;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepEndpoints;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepResponse;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepUpdateRequest;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException;
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
import com.extole.model.entity.campaign.CampaignControllerTriggerHasPriorStep;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
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
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerDescriptionLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameMissingException;
import com.extole.model.service.campaign.controller.trigger.TriggerTypeNotSupportedException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMatchesInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMatchesNotInCountRangeException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMatchesWithSumOfValueMinInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMaxInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMinInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterAgeRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterCampaignIdInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterCampaignIdLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterCampaignIdsInvalidSyntaxException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterDateRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterExpressionInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterExpressionInvalidSyntaxException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterExpressionLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterMaxAgeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterMaxValueInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterMinAgeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterMinValueInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterNameLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterPartnerEventIdNameInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterPartnerEventIdNameLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterPartnerEventIdValueInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterPartnerEventIdValueLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterProgramLabelInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterProgramLabelLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterProgramLabelsInvalidSyntaxException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterValueRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepIllegalCharacterInFilterNameException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepSumOfValueMaxInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepSumOfValueMinInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepSumOfValueRangeInvalidException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Provider
public class CampaignControllerTriggerHasPriorStepEndpointsImpl
    implements CampaignControllerTriggerHasPriorStepEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerTriggerHasPriorStepResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignControllerTriggerHasPriorStepEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerTriggerHasPriorStepResponseMapper responseMapper,
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
    public CampaignControllerTriggerHasPriorStepResponse get(String accessToken, String campaignId, String version,
        String controllerId, String triggerId, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerTriggerHasPriorStep trigger =
            campaignStepProvider.getHasPriorStepStepTrigger(campaign, controllerId, triggerId);

        return responseMapper.toResponse(trigger, timeZone);
    }

    // CHECKSTYLE.OFF: MethodLength
    @Override
    public CampaignControllerTriggerHasPriorStepResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerTriggerHasPriorStepCreateRequest createRequest,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerHasPriorStepValidationRestException, CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerHasPriorStepBuilder triggerBuilder;
        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            triggerBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion)
                .updateStep(step)
                .addTrigger(CampaignControllerTriggerType.HAS_PRIOR_STEP);
            createRequest.getTriggerPhase().ifPresent(
                phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            createRequest.getName().ifPresent(name -> triggerBuilder.withName(name));
            createRequest.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));
            createRequest.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
            createRequest.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
            createRequest.getFilterNames().ifPresent(filterNames -> triggerBuilder.withFilterNames(filterNames));
            createRequest.getFilterScope().ifPresent(filterScope -> triggerBuilder
                .withFilterScope(Evaluatables.remapEnum(filterScope, new TypeReference<>() {})));
            createRequest.getFilterPartnerEventIdName().ifPresent(
                filterPartnerEventIdName -> triggerBuilder.withFilterPartnerEventIdName(filterPartnerEventIdName));
            createRequest.getFilterPartnerEventIdValue().ifPresent(
                filterPartnerEventIdValue -> triggerBuilder.withFilterPartnerEventIdValue(filterPartnerEventIdValue));
            createRequest.getFilterPartnerEventId().ifPresent(
                filterPartnerEventId -> triggerBuilder.withFilterPartnerEventId(filterPartnerEventId));
            createRequest.getFilterMinAge().ifPresent(filterMinAge -> triggerBuilder.withFilterMinAge(filterMinAge));
            createRequest.getFilterMaxAge().ifPresent(filterMaxAge -> triggerBuilder.withFilterMaxAge(filterMaxAge));
            createRequest.getFilterMinValue()
                .ifPresent(filterMinValue -> triggerBuilder.withFilterMinValue(filterMinValue));
            createRequest.getFilterMaxValue()
                .ifPresent(filterMaxValue -> triggerBuilder.withFilterMaxValue(filterMaxValue));
            createRequest.getFilterQuality().ifPresent(filterQuality -> triggerBuilder
                .withFilterQuality(Evaluatables.remapEnum(filterQuality, new TypeReference<>() {})));
            createRequest.getFilterExpressions()
                .ifPresent(filterExpressions -> triggerBuilder.withFilterExpressions(filterExpressions));
            createRequest.getFilterExpression()
                .ifPresent(filterExpression -> triggerBuilder.withFilterExpression(filterExpression));
            createRequest.getFilterProgramLabel()
                .ifPresent(filterProgramLabel -> triggerBuilder.withFilterProgramLabel(filterProgramLabel));
            createRequest.getFilterCampaignId()
                .ifPresent(filterCampaignId -> triggerBuilder.withFilterCampaignId(filterCampaignId));
            createRequest.getFilterProgramLabels()
                .ifPresent(filterProgramLabels -> triggerBuilder.withFilterProgramLabels(filterProgramLabels));
            createRequest.getFilterCampaignIds()
                .ifPresent(filterCampaignIds -> triggerBuilder.withFilterCampaignIds(filterCampaignIds));
            createRequest.getFilterMinDate()
                .ifPresent(filterMinDate -> triggerBuilder.withFilterMinDate(filterMinDate));
            createRequest.getFilterMaxDate()
                .ifPresent(filterMaxDate -> triggerBuilder.withFilterMaxDate(filterMaxDate));
            createRequest.getSumOfValueMin()
                .ifPresent(sumOfValueMin -> triggerBuilder.withSumOfValueMin(sumOfValueMin));
            createRequest.getSumOfValueMax()
                .ifPresent(sumOfValueMax -> triggerBuilder.withSumOfValueMax(sumOfValueMax));
            createRequest.getCountMin().ifPresent(countMin -> triggerBuilder.withCountMin(countMin));
            createRequest.getCountMax().ifPresent(countMax -> triggerBuilder.withCountMax(countMax));
            createRequest.getCountMatches().ifPresent(countMatches -> triggerBuilder.withCountMatches(countMatches));
            createRequest.getPersonId().ifPresent(personId -> triggerBuilder.withPersonId(personId));
            createRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(triggerBuilder, componentIds);
            });
            createRequest.getComponentReferences().ifPresent(componentReferences -> {
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
        } catch (CampaignControllerTriggerDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                .withCause(e)
                .build();
        } catch (TriggerTypeNotSupportedException e) {
            throw TriggerTypeNotSupportedRestExceptionMapper.getInstance().map(e);
        } catch (BuildCampaignException e) {
            if (e instanceof BuildCampaignEvaluatableException) {
                throwValidationRestExceptionIfPossible((BuildCampaignEvaluatableException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
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
        } catch (CampaignControllerTriggerHasPriorStepFilterPartnerEventIdNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PARTNER_EVENT_ID_NAME_LENGTH)
                .addParameter("filter_partner_event_id_name", e.getFilterPartnerEventIdName())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterPartnerEventIdNameInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PARTNER_EVENT_ID_NAME_INVALID)
                .addParameter("filter_partner_event_id_name", e.getFilterPartnerEventIdName())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterPartnerEventIdValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PARTNER_EVENT_ID_VALUE_LENGTH)
                .addParameter("filter_partner_event_id_value", e.getFilterPartnerEventIdValue())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterPartnerEventIdValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PARTNER_EVENT_ID_VALUE_INVALID)
                .addParameter("filter_partner_event_id_value", e.getFilterPartnerEventIdValue())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterExpressionInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_EXPRESSION_INVALID)
                .addParameter("filter_expression", e.getFilterExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterExpressionLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_EXPRESSION_LENGTH)
                .addParameter("filter_expression", e.getFilterExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterProgramLabelLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PROGRAM_LABEL_LENGTH)
                .addParameter("filter_program_label", e.getFilterProgramLabel())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterProgramLabelInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PROGRAM_LABEL_INVALID)
                .addParameter("filter_program_label", e.getFilterProgramLabel())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterCampaignIdLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_CAMPAIGN_ID_LENGTH)
                .addParameter("filter_campaign_id", e.getFilterCampaignId())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterCampaignIdInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_CAMPAIGN_ID_INVALID)
                .addParameter("filter_campaign_id", e.getFilterCampaignId())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    // CHECKSTYLE.OFF: MethodLength
    @Override
    public CampaignControllerTriggerHasPriorStepResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        CampaignControllerTriggerHasPriorStepUpdateRequest updateRequest,
        ZoneId timeZone) throws UserAuthorizationRestException,
        CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerHasPriorStepValidationRestException, CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignStep step = campaignStepProvider.getStep(campaign, controllerId);
            CampaignControllerTriggerHasPriorStep trigger =
                campaignStepProvider.getHasPriorStepStepTrigger(campaign, controllerId, triggerId);

            CampaignControllerTriggerHasPriorStepBuilder triggerBuilder =
                getCampaignBuilder(campaignId, authorization, expectedCurrentVersion)
                    .updateStep(step)
                    .updateTrigger(trigger);

            updateRequest.getTriggerPhase().ifPresent(
                phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            updateRequest.getName().ifPresent(name -> triggerBuilder.withName(name));
            updateRequest.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));
            updateRequest.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
            updateRequest.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
            updateRequest.getFilterNames().ifPresent(filterNames -> triggerBuilder.withFilterNames(filterNames));
            updateRequest.getFilterScope().ifPresent(filterScope -> triggerBuilder
                .withFilterScope(Evaluatables.remapEnum(filterScope, new TypeReference<>() {})));
            updateRequest.getFilterPartnerEventIdName().ifPresent(
                filterPartnerEventIdName -> triggerBuilder.withFilterPartnerEventIdName(filterPartnerEventIdName));
            updateRequest.getFilterPartnerEventIdValue().ifPresent(
                filterPartnerEventIdValue -> triggerBuilder.withFilterPartnerEventIdValue(filterPartnerEventIdValue));
            updateRequest.getFilterPartnerEventId().ifPresent(
                filterPartnerEventId -> triggerBuilder.withFilterPartnerEventId(filterPartnerEventId));
            updateRequest.getFilterMinAge().ifPresent(filterMinAge -> triggerBuilder.withFilterMinAge(filterMinAge));
            updateRequest.getFilterMaxAge().ifPresent(filterMaxAge -> triggerBuilder.withFilterMaxAge(filterMaxAge));
            updateRequest.getFilterMinValue()
                .ifPresent(filterMinValue -> triggerBuilder.withFilterMinValue(filterMinValue));
            updateRequest.getFilterMaxValue()
                .ifPresent(filterMaxValue -> triggerBuilder.withFilterMaxValue(filterMaxValue));
            updateRequest.getFilterQuality().ifPresent(filterQuality -> triggerBuilder
                .withFilterQuality(Evaluatables.remapEnum(filterQuality, new TypeReference<>() {})));
            updateRequest.getFilterExpressions()
                .ifPresent(filterExpressions -> triggerBuilder.withFilterExpressions(filterExpressions));
            updateRequest.getFilterExpression()
                .ifPresent(filterExpression -> triggerBuilder.withFilterExpression(filterExpression));
            updateRequest.getFilterProgramLabel()
                .ifPresent(filterProgramLabel -> triggerBuilder.withFilterProgramLabel(filterProgramLabel));
            updateRequest.getFilterCampaignId()
                .ifPresent(filterCampaignId -> triggerBuilder.withFilterCampaignId(filterCampaignId));
            updateRequest.getFilterProgramLabels()
                .ifPresent(filterProgramLabels -> triggerBuilder.withFilterProgramLabels(filterProgramLabels));
            updateRequest.getFilterCampaignIds()
                .ifPresent(filterCampaignIds -> triggerBuilder.withFilterCampaignIds(filterCampaignIds));
            updateRequest.getFilterMinDate()
                .ifPresent(filterMinDate -> triggerBuilder.withFilterMinDate(filterMinDate));
            updateRequest.getFilterMaxDate()
                .ifPresent(filterMaxDate -> triggerBuilder.withFilterMaxDate(filterMaxDate));
            updateRequest.getSumOfValueMin()
                .ifPresent(sumOfValueMin -> triggerBuilder.withSumOfValueMin(sumOfValueMin));
            updateRequest.getSumOfValueMax()
                .ifPresent(sumOfValueMax -> triggerBuilder.withSumOfValueMax(sumOfValueMax));
            updateRequest.getCountMin().ifPresent(countMin -> triggerBuilder.withCountMin(countMin));
            updateRequest.getCountMax().ifPresent(countMax -> triggerBuilder.withCountMax(countMax));
            updateRequest.getCountMatches().ifPresent(countMatches -> triggerBuilder.withCountMatches(countMatches));
            updateRequest.getPersonId().ifPresent(personId -> triggerBuilder.withPersonId(personId));
            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(triggerBuilder, componentIds);
            });
            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
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
        } catch (CampaignControllerTriggerDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            if (e instanceof BuildCampaignEvaluatableException) {
                throwValidationRestExceptionIfPossible((BuildCampaignEvaluatableException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
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
        } catch (CampaignControllerTriggerHasPriorStepFilterPartnerEventIdNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PARTNER_EVENT_ID_NAME_LENGTH)
                .addParameter("filter_partner_event_id_name", e.getFilterPartnerEventIdName())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterPartnerEventIdNameInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PARTNER_EVENT_ID_NAME_INVALID)
                .addParameter("filter_partner_event_id_name", e.getFilterPartnerEventIdName())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterPartnerEventIdValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PARTNER_EVENT_ID_VALUE_LENGTH)
                .addParameter("filter_partner_event_id_value", e.getFilterPartnerEventIdValue())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterPartnerEventIdValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PARTNER_EVENT_ID_VALUE_INVALID)
                .addParameter("filter_partner_event_id_value", e.getFilterPartnerEventIdValue())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterExpressionInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_EXPRESSION_INVALID)
                .addParameter("filter_expression", e.getFilterExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterExpressionLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_EXPRESSION_LENGTH)
                .addParameter("filter_expression", e.getFilterExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterProgramLabelLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PROGRAM_LABEL_LENGTH)
                .addParameter("filter_program_label", e.getFilterProgramLabel())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterProgramLabelInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_PROGRAM_LABEL_INVALID)
                .addParameter("filter_program_label", e.getFilterProgramLabel())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterCampaignIdLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_CAMPAIGN_ID_LENGTH)
                .addParameter("filter_campaign_id", e.getFilterCampaignId())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorStepFilterCampaignIdInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_CAMPAIGN_ID_INVALID)
                .addParameter("filter_campaign_id", e.getFilterCampaignId())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerHasPriorStepResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignStep step = campaignStepProvider.getStep(campaign, controllerId);
            CampaignControllerTriggerHasPriorStep trigger =
                campaignStepProvider.getHasPriorStepStepTrigger(campaign, controllerId, triggerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

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

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    // CHECKSTYLE.OFF: MethodLength
    private void throwValidationRestExceptionIfPossible(BuildCampaignEvaluatableException e)
        throws CampaignControllerTriggerHasPriorStepValidationRestException, BuildCampaignRestException {
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterMinAgeInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_MIN_AGE_INVALID)
                .addParameter("filter_min_age",
                    ((CampaignControllerTriggerHasPriorStepFilterMinAgeInvalidException) e).getFilterMinAge())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterMaxAgeInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_MAX_AGE_INVALID)
                .addParameter("filter_max_age",
                    ((CampaignControllerTriggerHasPriorStepFilterMaxAgeInvalidException) e).getFilterMaxAge())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterNameLengthException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("filter_name",
                    ((CampaignControllerTriggerHasPriorStepFilterNameLengthException) e).getFilterName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepIllegalCharacterInFilterNameException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("filter_name",
                    ((CampaignControllerTriggerHasPriorStepIllegalCharacterInFilterNameException) e).getFilterName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterAgeRangeInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_AGE_RANGE_INVALID)
                .addParameter("filter_min_age",
                    ((CampaignControllerTriggerHasPriorStepFilterAgeRangeInvalidException) e).getFilterMinAge())
                .addParameter("filter_max_age",
                    ((CampaignControllerTriggerHasPriorStepFilterAgeRangeInvalidException) e).getFilterMaxAge())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterDateRangeInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_DATE_RANGE_INVALID)
                .addParameter("filter_min_date",
                    ((CampaignControllerTriggerHasPriorStepFilterDateRangeInvalidException) e).getFilterMinDate())
                .addParameter("filter_max_date",
                    ((CampaignControllerTriggerHasPriorStepFilterDateRangeInvalidException) e).getFilterMaxDate())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepSumOfValueRangeInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorStepValidationRestException.SUM_OF_VALUE_RANGE_INVALID)
                .addParameter("sum_of_value_min",
                    ((CampaignControllerTriggerHasPriorStepSumOfValueRangeInvalidException) e).getSumOfValueMin())
                .addParameter("sum_of_value_max",
                    ((CampaignControllerTriggerHasPriorStepSumOfValueRangeInvalidException) e).getSumOfValueMax())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepCountRangeInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_RANGE_INVALID)
                .addParameter("count_min",
                    ((CampaignControllerTriggerHasPriorStepCountRangeInvalidException) e).getCountMin())
                .addParameter("count_max",
                    ((CampaignControllerTriggerHasPriorStepCountRangeInvalidException) e).getCountMax())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepCountMatchesNotInCountRangeException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(COUNT_MATCHES_NOT_IN_COUNT_RANGE)
                .addParameter("count_matches",
                    ((CampaignControllerTriggerHasPriorStepCountMatchesNotInCountRangeException) e).getCountMatches())
                .addParameter("count_min",
                    ((CampaignControllerTriggerHasPriorStepCountMatchesNotInCountRangeException) e).getCountMin()
                        .map(Object::toString).orElse(""))
                .addParameter("count_max",
                    ((CampaignControllerTriggerHasPriorStepCountMatchesNotInCountRangeException) e).getCountMax()
                        .map(Object::toString).orElse(""))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepCountMatchesWithSumOfValueMinInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(COUNT_MATCHES_WITH_SUM_OF_VALUE_MIN_INVALID)
                .addParameter("count_matches",
                    ((CampaignControllerTriggerHasPriorStepCountMatchesWithSumOfValueMinInvalidException) e)
                        .getCountMatches())
                .addParameter("sum_of_value_min",
                    ((CampaignControllerTriggerHasPriorStepCountMatchesWithSumOfValueMinInvalidException) e)
                        .getSumOfValueMin())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepSumOfValueMinInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(SUM_OF_VALUE_MIN_INVALID)
                .addParameter("sum_of_value_min",
                    ((CampaignControllerTriggerHasPriorStepSumOfValueMinInvalidException) e).getSumOfValueMin())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepSumOfValueMaxInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(SUM_OF_VALUE_MAX_INVALID)
                .addParameter("sum_of_value_max",
                    ((CampaignControllerTriggerHasPriorStepSumOfValueMaxInvalidException) e).getSumOfValueMax())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepCountMinInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(COUNT_MIN_INVALID)
                .addParameter("count_min",
                    ((CampaignControllerTriggerHasPriorStepCountMinInvalidException) e).getCountMin())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepCountMaxInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(COUNT_MAX_INVALID)
                .addParameter("count_max",
                    ((CampaignControllerTriggerHasPriorStepCountMaxInvalidException) e).getCountMax())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepCountMatchesInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(COUNT_MATCHES_INVALID)
                .addParameter("count_matches",
                    ((CampaignControllerTriggerHasPriorStepCountMatchesInvalidException) e).getCountMatches())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterMinValueInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_MIN_VALUE_INVALID)
                .addParameter("filter_min_value",
                    ((CampaignControllerTriggerHasPriorStepFilterMinValueInvalidException) e).getFilterMinValue())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterMaxValueInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(FILTER_MAX_VALUE_INVALID)
                .addParameter("filter_max_value",
                    ((CampaignControllerTriggerHasPriorStepFilterMaxValueInvalidException) e).getFilterMaxValue())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterValueRangeInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorStepValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_VALUE_RANGE_INVALID)
                .addParameter("filter_min_value",
                    ((CampaignControllerTriggerHasPriorStepFilterValueRangeInvalidException) e).getFilterMinValue())
                .addParameter("filter_max_value",
                    ((CampaignControllerTriggerHasPriorStepFilterValueRangeInvalidException) e).getFilterMaxValue())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterProgramLabelsInvalidSyntaxException) {
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
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterCampaignIdsInvalidSyntaxException) {
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
        }
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterExpressionInvalidSyntaxException) {
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
        }
    }

}
