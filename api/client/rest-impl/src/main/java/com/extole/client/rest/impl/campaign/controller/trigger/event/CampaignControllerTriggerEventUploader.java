package com.extole.client.rest.impl.campaign.controller.trigger.event;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.event.CampaignControllerTriggerEventBuilder;

@Component
public class CampaignControllerTriggerEventUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerEventConfiguration> {

    @Override
    public void upload(CampaignUploadContext context,
        CampaignStepConfiguration step, CampaignControllerTriggerEventConfiguration trigger,
        ZoneId timeZone) throws CampaignComponentValidationRestException {
        CampaignControllerTriggerEventBuilder triggerBuilder = context.get(step, trigger);
        trigger.getEventNames().ifDefined((value) -> triggerBuilder.withEventNames(value));
        trigger.getEventType()
            .ifDefined(
                (value) -> triggerBuilder.withEventType(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        trigger.getTriggerPhase()
            .ifDefined(
                (value) -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        trigger.getName().ifDefined((value) -> triggerBuilder.withName(value));
        trigger.getParentTriggerGroupName()
            .ifDefined((value) -> triggerBuilder.withParentTriggerGroupName(value));
        trigger.getEnabled().ifDefined((value) -> triggerBuilder.withEnabled(value));
        trigger.getNegated().ifDefined((negated) -> triggerBuilder.withNegated(negated));

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
        return CampaignControllerTriggerType.EVENT;
    }

}
