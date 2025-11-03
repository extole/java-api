package com.extole.client.rest.impl.campaign.controller.trigger.expression;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerExpressionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionValidationRestException;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionBuilder;
import com.extole.model.service.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionDataExpressionInvalidException;
import com.extole.model.service.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionDataInvalidException;
import com.extole.model.service.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionDataLengthException;

@Component
public class CampaignControllerTriggerExpressionUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerExpressionConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerTriggerExpressionConfiguration trigger,
        ZoneId timeZone)
        throws CampaignControllerTriggerExpressionValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerTriggerExpressionBuilder triggerBuilder = context.get(step, trigger);
        try {
            if (trigger.getData() != null) {
                triggerBuilder.withData(trigger.getData());
            }
            trigger.getExpression().ifDefined((value) -> triggerBuilder.withExpression(value));
            trigger.getTriggerPhase()
                .ifDefined(
                    (value) -> triggerBuilder
                        .withTriggerPhase(Evaluatables.remapEnum(value, new TypeReference<>() {})));
            trigger.getName().ifDefined((value) -> triggerBuilder.withName(value));
            trigger.getParentTriggerGroupName()
                .ifDefined((value) -> triggerBuilder.withParentTriggerGroupName(value));
            trigger.getEnabled().ifDefined((value) -> triggerBuilder.withEnabled(value));
            trigger.getNegated().ifDefined((negated) -> triggerBuilder.withNegated(negated));
        } catch (CampaignControllerTriggerExpressionDataInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerExpressionValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerExpressionValidationRestException.EXPRESSION_MISSING)
                .withCause(e)
                .addParameter("data", trigger.getData())
                .build();
        } catch (CampaignControllerTriggerExpressionDataLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerExpressionValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerExpressionValidationRestException.EXPRESSION_LENGTH_OUT_OF_RANGE)
                .withCause(e)
                .addParameter("data", trigger.getData())
                .build();
        } catch (CampaignControllerTriggerExpressionDataExpressionInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerExpressionValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerExpressionValidationRestException.INVALID_EXPRESSION)
                .withCause(e)
                .addParameter("expression", e.getExpression())
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
        return CampaignControllerTriggerType.EXPRESSION;
    }

}
