package com.extole.client.rest.impl.campaign.controller.trigger.zone.state;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerZoneStateConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateValidationRestException;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateBuilder;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateStepNameExpressionLengthException;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateZoneNameExpressionLengthException;

@Component
public class CampaignControllerTriggerZoneStateUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerZoneStateConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerTriggerZoneStateConfiguration trigger,
        ZoneId timeZone)
        throws CampaignControllerTriggerZoneStateValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerTriggerZoneStateBuilder triggerBuilder = context.get(step, trigger);
        try {
            triggerBuilder
                .withZoneName(trigger.getZoneName())
                .withStepName(trigger.getStepName())
                .withInvertMappingState(trigger.isInvertMappingState());
            trigger.getTriggerPhase()
                .ifDefined(
                    (value) -> triggerBuilder
                        .withTriggerPhase(Evaluatables.remapEnum(value, new TypeReference<>() {})));
            trigger.getName().ifDefined((value) -> triggerBuilder.withName(value));
            trigger.getParentTriggerGroupName()
                .ifDefined((value) -> triggerBuilder.withParentTriggerGroupName(value));
            trigger.getEnabled().ifDefined((value) -> triggerBuilder.withEnabled(value));
            trigger.getNegated().ifDefined((negated) -> triggerBuilder.withNegated(negated));

        } catch (CampaignControllerTriggerZoneStateZoneNameExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerZoneStateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerZoneStateValidationRestException.ZONE_NAME_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", e.getExpression())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerZoneStateStepNameExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerZoneStateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerZoneStateValidationRestException.STEP_NAME_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", e.getExpression())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
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
        return CampaignControllerTriggerType.ZONE_STATE;
    }

}
