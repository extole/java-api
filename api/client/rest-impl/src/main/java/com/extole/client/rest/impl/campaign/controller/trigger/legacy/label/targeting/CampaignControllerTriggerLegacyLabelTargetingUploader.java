package com.extole.client.rest.impl.campaign.controller.trigger.legacy.label.targeting;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerLegacyLabelTargetingConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.legacy.label.targeting.CampaignControllerTriggerLegacyLabelTargetingBuilder;

@Component
public class CampaignControllerTriggerLegacyLabelTargetingUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerLegacyLabelTargetingConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerTriggerLegacyLabelTargetingConfiguration trigger, ZoneId timeZone)
        throws CampaignComponentValidationRestException {
        CampaignControllerTriggerLegacyLabelTargetingBuilder triggerBuilder = context.get(step, trigger);

        trigger.getName().ifDefined(name -> triggerBuilder.withName(name));
        trigger.getEnabled().ifDefined(enabled -> triggerBuilder.withEnabled(enabled));
        trigger.getTriggerPhase().ifDefined(
            triggerPhase -> triggerBuilder
                .withTriggerPhase(Evaluatables.remapEnum(triggerPhase, new TypeReference<>() {})));
        trigger.getNegated().ifDefined(negated -> triggerBuilder.withNegated(negated));

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
        return CampaignControllerTriggerType.LEGACY_LABEL_TARGETING;
    }

}
