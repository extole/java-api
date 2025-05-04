package com.extole.client.rest.impl.campaign.controller.trigger.has.prior.step;

import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_CAMPAIGN_ID_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_CAMPAIGN_ID_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_EXPRESSION_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_EXPRESSION_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PARTNER_EVENT_ID_NAME_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PARTNER_EVENT_ID_NAME_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PARTNER_EVENT_ID_VALUE_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PARTNER_EVENT_ID_VALUE_LENGTH;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PROGRAM_LABEL_INVALID;
import static com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException.FILTER_PROGRAM_LABEL_LENGTH;

import java.time.ZoneId;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasPriorStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepValidationRestException;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterCampaignIdInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterCampaignIdLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterExpressionInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterExpressionLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterPartnerEventIdNameInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterPartnerEventIdNameLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterPartnerEventIdValueInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterPartnerEventIdValueLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterProgramLabelInvalidException;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepFilterProgramLabelLengthException;

@Component
public class CampaignControllerTriggerHasPriorStepUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerHasPriorStepConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerTriggerHasPriorStepConfiguration trigger, ZoneId timeZone)
        throws CampaignControllerTriggerHasPriorStepValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerTriggerHasPriorStepBuilder triggerBuilder = context.get(step, trigger);
        trigger.getTriggerPhase()
            .ifDefined(
                (value) -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        trigger.getName().ifDefined((value) -> triggerBuilder.withName(value));
        trigger.getEnabled().ifDefined((value) -> triggerBuilder.withEnabled(value));
        trigger.getFilterNames().ifDefined((value) -> triggerBuilder.withFilterNames(value));
        trigger.getFilterScope()
            .ifDefined(
                (value) -> triggerBuilder.withFilterScope(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        trigger.getNegated().ifDefined((negated) -> triggerBuilder.withNegated(negated));
        try {
            triggerBuilder.withFilterPartnerEventIdName(trigger.getFilterPartnerEventIdName());
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
        }
        try {
            triggerBuilder.withFilterPartnerEventIdValue(trigger.getFilterPartnerEventIdValue());
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
        }
        trigger.getFilterPartnerEventId().ifDefined((value) -> triggerBuilder.withFilterPartnerEventId(value));
        trigger.getFilterMinAge().ifDefined((value) -> triggerBuilder.withFilterMinAge(value));
        trigger.getFilterMaxAge().ifDefined((value) -> triggerBuilder.withFilterMaxAge(value));
        trigger.getFilterMinValue().ifDefined((value) -> triggerBuilder.withFilterMinValue(value));
        trigger.getFilterMaxValue().ifDefined((value) -> triggerBuilder.withFilterMaxValue(value));
        trigger.getFilterQuality()
            .ifDefined(
                (value) -> triggerBuilder.withFilterQuality(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        Set<String> filterExpressions = trigger.getFilterExpressions();
        if (!filterExpressions.isEmpty()) {
            try {
                triggerBuilder.withFilterExpressions(filterExpressions);
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
            }
        }
        trigger.getFilterExpression().ifDefined((value) -> triggerBuilder.withFilterExpression(value));
        try {
            triggerBuilder.withFilterProgramLabel(trigger.getFilterProgramLabel());
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
        }
        trigger.getFilterProgramLabels().ifDefined((value) -> triggerBuilder.withFilterProgramLabels(value));
        try {
            triggerBuilder.withFilterCampaignId(trigger.getFilterCampaignId());
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
        }
        trigger.getFilterCampaignIds().ifDefined((value) -> triggerBuilder.withFilterCampaignIds(value));
        trigger.getFilterMinDate().ifDefined((value) -> triggerBuilder.withFilterMinDate(value));
        trigger.getFilterMaxDate().ifDefined((value) -> triggerBuilder.withFilterMaxDate(value));
        trigger.getSumOfValueMin().ifDefined((value) -> triggerBuilder.withSumOfValueMin(value));
        trigger.getSumOfValueMax().ifDefined((value) -> triggerBuilder.withSumOfValueMax(value));
        trigger.getCountMin().ifDefined((value) -> triggerBuilder.withCountMin(value));
        trigger.getCountMax().ifDefined((value) -> triggerBuilder.withCountMax(value));
        trigger.getCountMatches().ifDefined((value) -> triggerBuilder.withCountMatches(value));
        trigger.getPersonId().ifDefined((value) -> triggerBuilder.withPersonId(value));
        triggerBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : trigger.getComponentReferences()) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                triggerBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.HAS_PRIOR_STEP;
    }

}
