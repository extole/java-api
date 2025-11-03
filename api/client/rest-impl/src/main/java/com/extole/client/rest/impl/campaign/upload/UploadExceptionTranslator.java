package com.extole.client.rest.impl.campaign.upload;

import static com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleValidationRestException.DELAYS_AND_DATES_NOT_SUPPORTED_TOGETHER;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MATCHES_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MATCHES_NOT_IN_COUNT_RANGE;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MATCHES_WITH_SUM_OF_VALUE_MIN_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MAX_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.COUNT_MIN_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_MAX_AGE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_MAX_VALUE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_MIN_AGE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_MIN_VALUE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_NAME_CONTAINS_ILLEGAL_CHARACTER;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_NAME_LENGTH_OUT_OF_RANGE;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.SUM_OF_VALUE_MAX_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.SUM_OF_VALUE_MIN_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.send.reward.event.CampaignControllerTriggerSendRewardEventValidationRestException.REWARD_NAME_LENGTH_OUT_OF_RANGE;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.CampaignValidationRestException;
import com.extole.client.rest.campaign.GlobalCampaignRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetValidationRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignFrontendControllerValidationRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardValidationRestException;
import com.extole.client.rest.campaign.controller.action.email.CampaignControllerActionEmailValidationRestException;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonValidationRestException;
import com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.event.CampaignControllerTriggerEventValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.send.reward.event.CampaignControllerTriggerSendRewardEventValidationRestException;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepValidationRestException;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppValidationRestException;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricValidationRestException;
import com.extole.client.rest.campaign.label.CampaignLabelValidationRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.TranslatableVariableExceptionMapper;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.InvalidExternalComponentReferenceException;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.CampaignServicePendingChangeException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentAssetNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentNameMissingException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.CircularComponentReferenceException;
import com.extole.model.service.campaign.component.ComponentSocketMissingRequiredParameterException;
import com.extole.model.service.campaign.component.ExcessiveExternalComponentReferenceException;
import com.extole.model.service.campaign.component.MultipleComponentsInstalledIntoSingleSocketException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.component.SelfComponentReferenceException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentMissingException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameMissingException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardInvalidDataAttributeNameException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardMissingDataAttributeNameException;
import com.extole.model.service.campaign.controller.action.email.CampaignControllerActionEmailIllegalCharacterInZoneNameException;
import com.extole.model.service.campaign.controller.action.email.CampaignControllerActionEmailZoneNameLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonEventNameInvalidException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonEventNameLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIllegalCharacterInEventNameException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDelaysAndDatesNotSupportedTogetherException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleInvalidDelayException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleNegativeDelayException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerAliasLengthException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerDuplicateException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerIllegalCharacterInNameException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerInvalidEnabledOnStateException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerMissingJourneyNameException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerNameLengthException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerNameMissingException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerTooManyJourneyNamesException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameMissingException;
import com.extole.model.service.campaign.controller.trigger.event.CampaignControllerTriggerEventUnsupportedEventTypeException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMatchesInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMatchesNotInCountRangeException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMatchesWithSumOfValueMinInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMaxInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountMinInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepCountRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterAgeRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterDateRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterMaxAgeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterMaxValueInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterMinAgeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterMinValueInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterNameLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterValueRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepIllegalCharacterInFilterNameException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepSumOfValueMaxInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepSumOfValueMinInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepSumOfValueRangeInvalidException;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerRewardEventEmptyRewardStatesException;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventEventNameLengthException;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventIllegalCharacterInEventNameException;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventTagInvalidException;
import com.extole.model.service.campaign.controller.trigger.send.reward.event.CampaignControllerTriggerSendRewardEventRewardNameLengthException;
import com.extole.model.service.campaign.controller.trigger.send.reward.event.CampaignControllerTriggerSendRewardEventTagInvalidException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepDescriptionLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepFlowPathInvalidException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepFlowPathLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepFlowPathMissingException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepIconColorLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepIconTypeMissingException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepMetricDuplicateException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepNameLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepSequenceInvalidException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepSequenceMissingException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepStepNameLengthException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepStepNameMissingException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepWordLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppDescriptionLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppNameLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppTypeNameLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricDescriptionLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricExpressionInvalidException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricExpressionLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricExpressionMissingException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricNameLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricNameMissingException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricTagsMissingException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricUnitLengthException;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricUnitMissingException;
import com.extole.model.service.campaign.frontend.controller.FrontendControllerDuplicateException;
import com.extole.model.service.campaign.frontend.controller.FrontendControllerNameLengthException;
import com.extole.model.service.campaign.frontend.controller.FrontendControllerNameSameAsDefaultControllerNameException;
import com.extole.model.service.campaign.frontend.controller.IllegalCharacterInFrontendControllerNameException;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.setting.InvalidVariableTranslatableValueException;
import com.extole.model.service.campaign.setting.SettingNameDuplicateException;
import com.extole.model.service.campaign.setting.SettingNameMissingException;
import com.extole.model.service.campaign.setting.SettingValidationException;
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentFacetException;
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentTypeException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentFacetNameException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentFacetValueException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.step.data.StepDataDefaultValueExpressionLengthException;
import com.extole.model.service.campaign.step.data.StepDataDuplicateNameException;
import com.extole.model.service.campaign.step.data.StepDataMissingNameException;
import com.extole.model.service.campaign.step.data.StepDataMissingValueException;
import com.extole.model.service.campaign.step.data.StepDataNameLengthException;
import com.extole.model.service.campaign.step.data.StepDataValueExpressionLengthException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

class UploadExceptionTranslator {

    private void rethrowBuildCampaignRestException(Id<Campaign> campaignId, BuildCampaignEvaluatableException e)
        throws BuildCampaignRestException, CampaignValidationRestException,
        CampaignControllerActionFireAsPersonValidationRestException, CampaignFlowStepValidationRestException,
        CampaignControllerValidationRestException,
        CampaignControllerTriggerEventValidationRestException, CampaignControllerActionEmailValidationRestException,
        CampaignControllerTriggerValidationRestException, CampaignControllerTriggerHasPriorStepValidationRestException,
        CampaignControllerTriggerSendRewardEventValidationRestException, CampaignFlowStepMetricValidationRestException,
        CampaignFlowStepAppValidationRestException, CampaignControllerTriggerRewardEventValidationRestException,
        CampaignControllerActionScheduleValidationRestException, CampaignFrontendControllerValidationRestException,
        CampaignControllerActionEarnRewardValidationRestException {

        if (e instanceof CampaignControllerAliasLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.INVALID_STEP_ALIAS)
                .addParameter("alias", ((CampaignControllerAliasLengthException) e).getControllerAlias()).withCause(e)
                .build();
        }

        throwFrontendControllerRelatedBuildCampaignRestExceptionIfPossible(e);
        throwControllerRelatedBuildCampaignRestExceptionIfPossible(e);
        throwFlowStepRelatedBuildCampaignRestExceptionIfPossible(e);
        if (e instanceof CampaignControllerActionEmailZoneNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEmailValidationRestException.class)
                .withErrorCode(CampaignControllerActionEmailValidationRestException.ZONE_NAME_LENGTH_INVALID)
                .addParameter("zone_name", ((CampaignControllerActionEmailZoneNameLengthException) e).getZoneName())
                .withCause(e).build();
        }
        if (e instanceof CampaignControllerActionEmailIllegalCharacterInZoneNameException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEmailValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionEmailValidationRestException.ZONE_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("zone_name",
                    ((CampaignControllerActionEmailIllegalCharacterInZoneNameException) e).getZoneName())
                .withCause(e).build();
        }
        if (e instanceof CampaignControllerTriggerEventUnsupportedEventTypeException) {
            CampaignControllerTriggerEventUnsupportedEventTypeException ex =
                (CampaignControllerTriggerEventUnsupportedEventTypeException) e;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerEventValidationRestException.UNSUPPORTED_EVENT_TYPE)
                .addParameter("event_type", ex.getEventType())
                .addParameter("allowed_event_types", ex.getAllowedEventTypes())
                .withCause(e).build();
        }
        throwHasPriorStepTriggerRelatedBuildCampaignRestExceptionIfPossible(e);
        throwHasPriorRewardTriggerRelatedBuildCampaignRestExceptionIfPossible(e);
        throwSendRewardEventTriggerRelatedBuildCampaignRestExceptionIfPossible(e);
        throwTriggerRewardEventRelatedBuildCampaignRestExceptionIfPossible(campaignId, e);
        throwActionScheduleRelatedBuildCampaignRestExceptionIfPossible(e);
        throwEarnRewardActionRelatedBuildCampaignRestExceptionIfPossible(e);

        if (e instanceof StepDataDuplicateNameException) {
            StepDataDuplicateNameException stepDataDuplicateNameException = (StepDataDuplicateNameException) e;
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.DUPLICATE_STEP_DATA_NAME)
                .addParameter("name", stepDataDuplicateNameException.getName())
                .withCause(stepDataDuplicateNameException).build();
        }
        if (e instanceof StepDataNameLengthException) {
            StepDataNameLengthException stepDataNameLengthException = (StepDataNameLengthException) e;
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.STEP_DATA_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", stepDataNameLengthException.getName()).withCause(stepDataNameLengthException)
                .build();
        }

        throw BuildCampaignRestExceptionMapper.getInstance().map(e);
    }

    private void throwFrontendControllerRelatedBuildCampaignRestExceptionIfPossible(
        BuildCampaignEvaluatableException e) throws CampaignFrontendControllerValidationRestException {

        if (e instanceof FrontendControllerDuplicateException) {
            FrontendControllerDuplicateException exception =
                (FrontendControllerDuplicateException) e;
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.DUPLICATE_FRONTEND_CONTROLLER)
                .addParameter("controller_name", exception.getControllerName())
                .addParameter("second_controller_id", exception.getSecondControllerId())
                .addParameter("journey_names", exception.getIntersectedJourneyNames())
                .withCause(e)
                .build();
        }
        if (e instanceof FrontendControllerNameLengthException) {
            FrontendControllerNameLengthException exception =
                (FrontendControllerNameLengthException) e;
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.CONTROLLER_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("evaluatable", exception.getEvaluatable())
                .addParameter("controller_name", exception.getName())
                .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                .withCause(e)
                .build();
        }
        if (e instanceof FrontendControllerNameSameAsDefaultControllerNameException) {
            FrontendControllerNameSameAsDefaultControllerNameException exception =
                (FrontendControllerNameSameAsDefaultControllerNameException) e;
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.RESERVED_NAME)
                .addParameter("evaluatable", exception.getEvaluatable())
                .addParameter("controller_name", exception.getName())
                .withCause(e)
                .build();
        }
        if (e instanceof IllegalCharacterInFrontendControllerNameException) {
            IllegalCharacterInFrontendControllerNameException exception =
                (IllegalCharacterInFrontendControllerNameException) e;
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("evaluatable", exception.getEvaluatable())
                .addParameter("controller_name", exception.getName())
                .withCause(e)
                .build();
        }
    }

    private void throwControllerRelatedBuildCampaignRestExceptionIfPossible(BuildCampaignEvaluatableException e)
        throws CampaignControllerActionFireAsPersonValidationRestException, CampaignControllerValidationRestException {
        if (e instanceof CampaignControllerActionFireAsPersonEventNameInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.MISSING_EVENT_NAME)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerActionFireAsPersonEventNameLengthException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name",
                    ((CampaignControllerActionFireAsPersonEventNameLengthException) e).getEventName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerActionFireAsPersonIllegalCharacterInEventNameException) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("event_name",
                    ((CampaignControllerActionFireAsPersonIllegalCharacterInEventNameException) e).getEventName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", ((CampaignControllerNameLengthException) e).getControllerName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerIllegalCharacterInNameException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", ((CampaignControllerIllegalCharacterInNameException) e).getControllerName())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerInvalidEnabledOnStateException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.INVALID_ENABLED_ON_STATE)
                .addParameter("enabled_on_states",
                    ((CampaignControllerInvalidEnabledOnStateException) e).getEnabledOnStates())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerDuplicateException) {
            CampaignControllerDuplicateException controllerDuplicateException =
                (CampaignControllerDuplicateException) e;
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.DUPLICATE_CONTROLLER)
                .addParameter("controller_name", controllerDuplicateException.getControllerName())
                .addParameter("first_controller_id", controllerDuplicateException.getFirstControllerId())
                .addParameter("first_controller_journey_names",
                    controllerDuplicateException.getFirstControllerJourneyNames())
                .addParameter("second_controller_id", controllerDuplicateException.getSecondControllerId())
                .addParameter("second_controller_journey_names",
                    controllerDuplicateException.getSecondControllerJourneyNames())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerMissingJourneyNameException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.MISSING_JOURNEY_NAME)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTooManyJourneyNamesException) {
            CampaignControllerTooManyJourneyNamesException exception =
                (CampaignControllerTooManyJourneyNamesException) e;
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.TOO_MANY_JOURNEY_NAMES)
                .addParameter("journey_names", exception.getJourneyNames())
                .addParameter("actual_count", Integer.valueOf(exception.getActualCount()))
                .addParameter("max_count", Integer.valueOf(exception.getMaxCount()))
                .withCause(e)
                .build();
        }
    }

    private void
        throwHasPriorRewardTriggerRelatedBuildCampaignRestExceptionIfPossible(BuildCampaignEvaluatableException e) {
        // TODO add exception handling - ENG-19686
    }

    private void throwSendRewardEventTriggerRelatedBuildCampaignRestExceptionIfPossible(
        BuildCampaignEvaluatableException e)
        throws CampaignControllerTriggerSendRewardEventValidationRestException {
        if (e instanceof CampaignControllerTriggerSendRewardEventRewardNameLengthException) {
            CampaignControllerTriggerSendRewardEventRewardNameLengthException eventNameLengthException =
                (CampaignControllerTriggerSendRewardEventRewardNameLengthException) e;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerSendRewardEventValidationRestException.class)
                .withErrorCode(REWARD_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("reward_name", eventNameLengthException.getRewardName())
                .withCause(e)
                .build();
        }

        if (e instanceof CampaignControllerTriggerSendRewardEventTagInvalidException) {
            CampaignControllerTriggerSendRewardEventTagInvalidException eventTagInvalidException =
                (CampaignControllerTriggerSendRewardEventTagInvalidException) e;

            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerSendRewardEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerSendRewardEventValidationRestException.INVALID_TAG)
                .addParameter("tag", eventTagInvalidException.getTag())
                .withCause(e)
                .build();
        }
    }

    private void throwTriggerRewardEventRelatedBuildCampaignRestExceptionIfPossible(
        Id<Campaign> campaignId, BuildCampaignEvaluatableException exception)
        throws CampaignControllerTriggerRewardEventValidationRestException {
        Throwable cause = exception.getCause();
        if (cause instanceof CampaignControllerTriggerRewardEventEventNameLengthException) {
            CampaignControllerTriggerRewardEventEventNameLengthException e =
                (CampaignControllerTriggerRewardEventEventNameLengthException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerRewardEventValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerRewardEventValidationRestException.EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } else if (cause instanceof CampaignControllerTriggerRewardEventIllegalCharacterInEventNameException) {
            CampaignControllerTriggerRewardEventIllegalCharacterInEventNameException e =
                (CampaignControllerTriggerRewardEventIllegalCharacterInEventNameException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerRewardEventValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerRewardEventValidationRestException.EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } else if (cause instanceof CampaignControllerRewardEventEmptyRewardStatesException) {
            CampaignControllerRewardEventEmptyRewardStatesException e =
                (CampaignControllerRewardEventEmptyRewardStatesException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerRewardEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerRewardEventValidationRestException.INVALID_REWARD_STATES)
                .withCause(e)
                .build();
        } else if (cause instanceof CampaignControllerTriggerRewardEventTagInvalidException) {
            CampaignControllerTriggerRewardEventTagInvalidException e =
                (CampaignControllerTriggerRewardEventTagInvalidException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerRewardEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerRewardEventValidationRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        }
    }

    private void throwActionScheduleRelatedBuildCampaignRestExceptionIfPossible(
        BuildCampaignEvaluatableException exception)
        throws CampaignControllerActionScheduleValidationRestException {
        Throwable cause = exception.getCause();
        if (cause instanceof CampaignControllerActionScheduleNegativeDelayException) {
            CampaignControllerActionScheduleNegativeDelayException e =
                (CampaignControllerActionScheduleNegativeDelayException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.NEGATIVE_SCHEDULE_DELAY)
                .addParameter("delay", e.getDelay())
                .withCause(e).build();
        } else if (cause instanceof CampaignControllerActionScheduleInvalidDelayException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.INVALID_SCHEDULE_DELAY)
                .withCause(cause).build();
        } else if (cause instanceof CampaignControllerActionScheduleDelaysAndDatesNotSupportedTogetherException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(DELAYS_AND_DATES_NOT_SUPPORTED_TOGETHER)
                .withCause(cause).build();
        }
    }

    private void throwFlowStepRelatedBuildCampaignRestExceptionIfPossible(
        BuildCampaignEvaluatableException e)
        throws CampaignFlowStepValidationRestException, CampaignFlowStepMetricValidationRestException,
        CampaignFlowStepAppValidationRestException {

        if (e instanceof CampaignFlowStepFlowPathLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.FLOW_PATH_LENGTH_OUT_OF_RANGE)
                .addParameter("flow_path", ((CampaignFlowStepFlowPathLengthException) e).getFlowPath()).withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepFlowPathInvalidException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.FLOW_PATH_INVALID)
                .addParameter("flow_path", ((CampaignFlowStepFlowPathInvalidException) e).getFlowPath()).withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepSequenceInvalidException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.SEQUENCE_INVALID)
                .addParameter("sequence", ((CampaignFlowStepSequenceInvalidException) e).getSequence()).withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepStepNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.STEP_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("step_name", ((CampaignFlowStepStepNameLengthException) e).getStepName()).withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", ((CampaignFlowStepNameLengthException) e).getName()).withCause(e).build();
        }
        if (e instanceof CampaignFlowStepIconColorLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.ICON_COLOR_LENGTH_OUT_OF_RANGE)
                .addParameter("icon_color", ((CampaignFlowStepIconColorLengthException) e).getIconColor()).withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("metric_name", ((CampaignFlowStepMetricNameLengthException) e).getName()).withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricDescriptionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("metric_description",
                    ((CampaignFlowStepMetricDescriptionLengthException) e).getDescription())
                .withCause(e).build();
        }
        if (e instanceof CampaignFlowStepMetricExpressionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("metric_expression",
                    ((CampaignFlowStepMetricExpressionLengthException) e).getExpression())
                .withCause(e).build();
        }
        if (e instanceof CampaignFlowStepMetricDuplicateException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_DUPLICATE_EXISTS)
                .addParameter("metric_name", ((CampaignFlowStepMetricDuplicateException) e).getMetricName())
                .addParameter("flow_step_name", ((CampaignFlowStepMetricDuplicateException) e).getFlowStepName())
                .withCause(e).build();
        }
        if (e instanceof CampaignFlowStepMetricNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_NAME_MISSING).withCause(e).build();
        }
        if (e instanceof CampaignFlowStepMetricUnitMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_UNIT_MISSING).withCause(e).build();
        }
        if (e instanceof CampaignFlowStepMetricTagsMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_TAGS_MISSING).withCause(e).build();
        }
        if (e instanceof CampaignFlowStepMetricExpressionMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_EXPRESSION_MISSING).withCause(e)
                .build();
        }
        if (e instanceof CampaignFlowStepMetricExpressionInvalidException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_EXPRESSION_INVALID)
                .addParameter("metric_expression",
                    ((CampaignFlowStepMetricExpressionInvalidException) e).getExpression())
                .withCause(e).build();
        }
        if (e instanceof CampaignFlowStepMetricUnitLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepMetricValidationRestException.class)
                .withErrorCode(CampaignFlowStepMetricValidationRestException.METRIC_UNIT_LENGTH_OUT_OF_RANGE)
                .addParameter("unit", ((CampaignFlowStepMetricUnitLengthException) e).getUnit()).withCause(e).build();
        }
        if (e instanceof CampaignFlowStepAppNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("app_name", ((CampaignFlowStepAppNameLengthException) e).getName())
                .addParameter("min_length",
                    Integer.valueOf(((CampaignFlowStepAppNameLengthException) e).getMinLength()))
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppNameLengthException) e).getMaxLength()))
                .withCause(e).build();
        }
        if (e instanceof CampaignFlowStepAppTypeNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_TYPE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("app_type_name", ((CampaignFlowStepAppTypeNameLengthException) e).getName())
                .addParameter("min_length",
                    Integer.valueOf(((CampaignFlowStepAppTypeNameLengthException) e).getMinLength()))
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppTypeNameLengthException) e).getMaxLength()))
                .withCause(e).build();
        }
        if (e instanceof CampaignFlowStepAppDescriptionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepAppValidationRestException.class)
                .withErrorCode(CampaignFlowStepAppValidationRestException.APP_DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("app_description", ((CampaignFlowStepAppDescriptionLengthException) e).getDescription())
                .addParameter("max_length",
                    Integer.valueOf(((CampaignFlowStepAppDescriptionLengthException) e).getMaxLength()))
                .withCause(e).build();
        }
        if (e instanceof CampaignFlowStepDescriptionLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", ((CampaignFlowStepDescriptionLengthException) e).getDescription())
                .withCause(e).build();
        }
        if (e instanceof CampaignFlowStepWordLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.WORLD_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getEvaluatableName())
                .addParameter("word", ((CampaignFlowStepWordLengthException) e).getWord()).withCause(e).build();
        }
    }

    private void throwHasPriorStepTriggerRelatedBuildCampaignRestExceptionIfPossible(
        BuildCampaignEvaluatableException e) throws CampaignControllerTriggerHasPriorStepValidationRestException,
        CampaignControllerTriggerValidationRestException {
        if (e instanceof CampaignControllerTriggerHasPriorStepFilterMinAgeInvalidException) {
            throw RestExceptionBuilder
                .newBuilder(
                    CampaignControllerTriggerHasPriorStepValidationRestException.class)
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
        if (e instanceof CampaignControllerTriggerNameMissingException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.MISSING_NAME)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignControllerTriggerNameLengthException) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.INVALID_NAME_LENGTH)
                .addParameter("name", ((CampaignControllerTriggerNameLengthException) e).getName())
                .addParameter("max_length",
                    Integer.valueOf(((CampaignControllerTriggerNameLengthException) e).getMaxLength()))
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
    }

    public CampaignValidationRestException handleStepDataExceptions(Exception exception) {
        if (exception instanceof StepDataMissingNameException) {
            return RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.MISSING_STEP_DATA_NAME)
                .withCause(exception)
                .build();
        }
        if (exception instanceof StepDataMissingValueException) {
            StepDataMissingValueException stepDataMissingValueException = (StepDataMissingValueException) exception;
            return RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.MISSING_STEP_DATA_VALUE)
                .addParameter("name", stepDataMissingValueException.getName())
                .withCause(stepDataMissingValueException)
                .build();
        }
        if (exception instanceof StepDataValueExpressionLengthException) {
            StepDataValueExpressionLengthException stepDataValueExpressionLengthException =
                (StepDataValueExpressionLengthException) exception;
            return RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.STEP_DATA_VALUE_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", stepDataValueExpressionLengthException.getExpression())
                .addParameter("name", stepDataValueExpressionLengthException.getName())
                .withCause(stepDataValueExpressionLengthException)
                .build();
        }
        if (exception instanceof StepDataDefaultValueExpressionLengthException) {
            StepDataDefaultValueExpressionLengthException stepDataDefaultValueExpressionLengthException =
                (StepDataDefaultValueExpressionLengthException) exception;
            return RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.STEP_DATA_DEFAULT_VALUE_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", stepDataDefaultValueExpressionLengthException.getExpression())
                .addParameter("name", stepDataDefaultValueExpressionLengthException.getName())
                .withCause(stepDataDefaultValueExpressionLengthException)
                .build();
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(exception)
            .build();
    }

    private CampaignFlowStepValidationRestException toCampaignFlowStepValidationRestException(Exception e) {

        if (e.getClass() == CampaignFlowStepFlowPathMissingException.class) {
            return RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.FLOW_PATH_MISSING)
                .withCause(e)
                .build();
        }
        if (e.getClass() == CampaignFlowStepSequenceMissingException.class) {
            return RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.SEQUENCE_MISSING)
                .withCause(e)
                .build();
        }
        if (e.getClass() == CampaignFlowStepStepNameMissingException.class) {
            return RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.STEP_NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e.getClass() == CampaignFlowStepIconTypeMissingException.class) {
            return RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.ICON_TYPE_MISSING)
                .withCause(e)
                .build();
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(e)
            .build();
    }

    private CampaignComponentValidationRestException toCampaignComponentValidationRestException(Exception e) {
        if (e.getClass() == CampaignComponentNameMissingException.class) {
            return RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e.getClass() == CampaignComponentNameDuplicateException.class) {
            return RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.NAME_ALREADY_IN_USE)
                .addParameter("name", ((CampaignComponentNameDuplicateException) e).getComponentName())
                .withCause(e)
                .build();
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(e)
            .build();
    }

    private CampaignLabelValidationRestException toCampaignLabelValidationRestException(Exception e) {
        if (e.getClass() == CampaignLabelMissingNameException.class) {
            return RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        }
        if (e.getClass() == CampaignLabelDuplicateNameException.class) {
            CampaignLabelDuplicateNameException duplicateNameException = (CampaignLabelDuplicateNameException) e;
            return RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_ALREADY_IN_USE)
                .addParameter("name", duplicateNameException.getLabelName())
                .withCause(e)
                .build();
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(e)
            .build();
    }

    private CreativeArchiveRestException toCreativeArchiveRestException(Exception e,
        FormDataContentDisposition contentDispositionHeader, String campaignName, Id<ClientHandle> clientId) {
        if (e.getClass() == CreativeArchiveJavascriptException.class) {
            CreativeArchiveJavascriptException creativeArchiveJavascriptException =
                (CreativeArchiveJavascriptException) e;

            return RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.JAVASCRIPT_ERROR)
                .addParameter("file", contentDispositionHeader.getFileName())
                .addParameter("creativeArchiveId",
                    creativeArchiveJavascriptException.getCreativeArchiveId().map(value -> value.getId().getValue())
                        .orElse(""))
                .addParameter("output", creativeArchiveJavascriptException.getOutput())
                .withCause(e)
                .build();
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(e)
            .build();
    }

    @SuppressWarnings("MethodLength")
    public void translateAndRethrow(Id<ClientHandle> clientId, Id<Campaign> campaignId, String campaignName,
        FormDataContentDisposition contentDispositionHeader, Exception original)
        throws CampaignValidationRestException, CampaignControllerValidationRestException,
        CampaignControllerActionRestException, CampaignControllerTriggerRestException,
        CampaignLabelValidationRestException, CreativeArchiveRestException, CampaignFlowStepValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, CampaignComponentValidationRestException,
        CampaignComponentAssetValidationRestException, CampaignFlowStepMetricValidationRestException,
        CampaignFlowStepAppValidationRestException, GlobalCampaignRestException,
        CampaignFrontendControllerValidationRestException, CampaignUpdateRestException {
        try {
            throw original;
        } catch (BuildCampaignEvaluatableException e) {
            rethrowBuildCampaignRestException(campaignId, e);
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
        } catch (CampaignComponentTypeValidationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.TYPE_VALIDATION_FAILED)
                .addParameter("validation_result", e.getValidationResult())
                .addParameter("name", e.getName())
                .addParameter("component_name", e.getComponentName())
                .addParameter("component_id", e.getComponentId().toString())
                .withCause(e)
                .build();
        } catch (CampaignServiceIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("campaign_name", campaignName)
                .withCause(e)
                .build();
        } catch (CampaignServiceNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getMinLength())
                .addParameter("max_length", e.getMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignServiceNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_IS_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignServicePendingChangeException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.CAMPAIGN_WITH_PENDING_CHANGES)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignComponentNameMissingException | CampaignComponentNameDuplicateException e) {
            throw toCampaignComponentValidationRestException(e);
        } catch (CreativeArchiveJavascriptException e) {
            throw toCreativeArchiveRestException(e, contentDispositionHeader, campaignName, clientId);
        } catch (CampaignLabelMissingNameException | CampaignLabelDuplicateNameException e) {
            throw toCampaignLabelValidationRestException(e);
        } catch (CampaignFlowStepException e) {
            throw toCampaignFlowStepValidationRestException(e);
        } catch (SettingNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ComponentSocketMissingRequiredParameterException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_MISSING_REQUIRED_PARAMETER)
                .addParameter("socket_parameter_name", e.getSocketParameterName())
                .addParameter("socket_parameter_type", e.getSocketParameterType())
                .addParameter("socket_name", e.getSocketName())
                .withCause(e)
                .build();
        } catch (MultipleComponentsInstalledIntoSingleSocketException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.MULTIPLE_COMPONENTS_INSTALLED_INTO_SINGLE_SOCKET)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("target_component_id", e.getTargetComponentId())
                .addParameter("installed_component_ids", e.getInstalledComponentIds())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetFilenameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignComponentAssetContentMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_MISSING)
                .withCause(e)
                .addParameter("asset_name", e.getAssetName())
                .build();
        } catch (ExcessiveExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.EXCESSIVE_ROOT_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (SelfComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.SELF_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "undefined")
                .addParameter("referencing_entity", "undefined")
                .withCause(e)
                .build();
        } catch (CircularComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.CIRCULAR_COMPONENT_REFERENCE)
                .addParameter("cycles", e.getCycles())
                .withCause(e)
                .build();
        } catch (InvalidExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (CampaignGlobalStateChangeException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignRestException.class)
                .withErrorCode(GlobalCampaignRestException.GLOBAL_STATE_CHANGE_EXCEPTION)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (StepDataMissingNameException | StepDataMissingValueException | StepDataValueExpressionLengthException
            | StepDataDefaultValueExpressionLengthException e) {
            throw handleStepDataExceptions(e);
        } catch (SocketFilterInvalidComponentTypeException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_INVALID_COMPONENT_TYPE)
                .addParameter("component_type", e.getComponentType())
                .withCause(e)
                .build();
        } catch (SocketFilterInvalidComponentFacetException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_INVALID_COMPONENT_FACET)
                .addParameter("facet_name", e.getComponentFacetName())
                .addParameter("facet_value", e.getComponentFacetValue())
                .withCause(e)
                .build();
        } catch (SocketFilterMissingComponentFacetNameException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_COMPONENT_FACET_NAME_MISSING)
                .withCause(e)
                .build();
        } catch (SocketFilterMissingComponentFacetValueException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_COMPONENT_FACET_VALUE_MISSING)
                .withCause(e)
                .build();
        } catch (CreativeVariableUnsupportedException | CampaignControllerTriggerBuildException
            | TransitionRuleAlreadyExistsForActionType | StepDataBuildException
            | CampaignScheduleException | CampaignGlobalDeleteException | CampaignGlobalArchiveException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.COMPONENT_FACETS_NOT_FOUND)
                .addParameter("facets", e.getFacets())
                .withCause(e)
                .build();
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (ReferencedExternalElementException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.EXTERNAL_ELEMENT_IS_REFERENCED)
                .addParameter("references", e.getReferences())
                .addParameter("element_type", e.getElementType().name())
                .addParameter("element_id", e.getElementId())
                .withCause(e)
                .build();
        } catch (Exception e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void throwEarnRewardActionRelatedBuildCampaignRestExceptionIfPossible(
        BuildCampaignEvaluatableException exception)
        throws CampaignControllerActionEarnRewardValidationRestException {
        Throwable cause = exception.getCause();

        if (cause instanceof CampaignControllerActionEarnRewardMissingDataAttributeNameException) {
            CampaignControllerActionEarnRewardMissingDataAttributeNameException e =
                (CampaignControllerActionEarnRewardMissingDataAttributeNameException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } else if (cause instanceof CampaignControllerActionEarnRewardInvalidDataAttributeNameException) {
            CampaignControllerActionEarnRewardInvalidDataAttributeNameException e =
                (CampaignControllerActionEarnRewardInvalidDataAttributeNameException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        }
    }

}
