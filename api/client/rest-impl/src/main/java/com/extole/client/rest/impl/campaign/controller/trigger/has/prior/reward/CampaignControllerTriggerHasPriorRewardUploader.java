package com.extole.client.rest.impl.campaign.controller.trigger.has.prior.reward;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasPriorRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardValidationRestException;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionLengthException;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardMissingFilterExpressionValueException;

@Component
public class CampaignControllerTriggerHasPriorRewardUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerHasPriorRewardConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerTriggerHasPriorRewardConfiguration trigger, ZoneId timeZone)
        throws CampaignControllerTriggerHasPriorRewardValidationRestException,
        CampaignComponentValidationRestException {
        CampaignControllerTriggerHasPriorRewardBuilder triggerBuilder = context.get(step, trigger);
        trigger.getTriggerPhase()
            .ifDefined(
                (value) -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        try {
            trigger.getFilterNames().ifDefined((value) -> triggerBuilder.withFilterNames(value));
            trigger.getFilterTags().ifDefined((value) -> triggerBuilder.withFilterTags(value));
            trigger.getFilterScope()
                .ifDefined(
                    (value) -> triggerBuilder.withFilterScope(Evaluatables.remapEnum(value, new TypeReference<>() {})));
            trigger.getFilterMinAge().ifDefined((value) -> triggerBuilder.withFilterMinAge(value));
            trigger.getFilterMaxAge().ifDefined((value) -> triggerBuilder.withFilterMaxAge(value));
            trigger.getFilterMinDate().ifDefined((value) -> triggerBuilder.withFilterMinDate(value));
            trigger.getFilterMaxDate().ifDefined((value) -> triggerBuilder.withFilterMaxDate(value));
            trigger.getFilterRewardSupplierIds()
                .ifDefined((value) -> triggerBuilder.withFilterRewardSupplierIds(value));
            trigger.getFilterFaceValueTypes().ifDefined(
                (value) -> triggerBuilder
                    .withFilterFaceValueTypes(Evaluatables.remapEnumCollection(value, new TypeReference<>() {})));
            trigger.getFilterStates()
                .ifDefined((value) -> triggerBuilder
                    .withFilterStates(Evaluatables.remapEnumCollection(value, new TypeReference<>() {})));
            if (!trigger.getFilterExpressions().isEmpty()) {
                triggerBuilder.withFilterExpressions(trigger.getFilterExpressions());
            }
            trigger.getFilterExpression().ifDefined((value) -> triggerBuilder.withFilterExpression(value));
            trigger.getSumOfFaceValueMax().ifDefined((value) -> triggerBuilder.withSumOfFaceValueMax(value));
            trigger.getSumOfFaceValueMin().ifDefined((value) -> triggerBuilder.withSumOfFaceValueMin(value));
            trigger.getCountMax().ifDefined((value) -> triggerBuilder.withCountMax(value));
            trigger.getCountMin().ifDefined((value) -> triggerBuilder.withCountMin(value));
            trigger.getCountMatches().ifDefined((value) -> triggerBuilder.withCountMatches(value));
            trigger.getTaxYearStart().ifDefined((value) -> triggerBuilder.withTaxYearStart(value));
            trigger.getName().ifDefined((value) -> triggerBuilder.withName(value));
            trigger.getParentTriggerGroupName()
                .ifDefined((value) -> triggerBuilder.withParentTriggerGroupName(value));
            trigger.getEnabled().ifDefined((value) -> triggerBuilder.withEnabled(value));
            trigger.getNegated().ifDefined((negated) -> triggerBuilder.withNegated(negated));
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_FILTER_EXPRESSION_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardMissingFilterExpressionValueException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerHasPriorRewardValidationRestException.MISSING_FILTER_EXPRESSION_VALUE)
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerHasPriorRewardInvalidFilterExpressionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerHasPriorRewardValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerHasPriorRewardValidationRestException.INVALID_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .withCause(e)
                .build();
        }
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
        return CampaignControllerTriggerType.HAS_PRIOR_REWARD;
    }

}
