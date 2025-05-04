package com.extole.client.rest.impl.campaign.controller.trigger.share;

import java.time.ZoneId;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerShareConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.entity.campaign.CampaignControllerTriggerShareChannel;
import com.extole.model.entity.campaign.ShareQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.share.CampaignControllerTriggerShareBuilder;

@Component
public class CampaignControllerTriggerShareUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerShareConfiguration> {

    @Override
    public void upload(CampaignUploadContext context,
        CampaignStepConfiguration step, CampaignControllerTriggerShareConfiguration trigger,
        ZoneId timeZone) throws CampaignComponentValidationRestException {
        CampaignControllerTriggerShareBuilder triggerBuilder = context.get(step, trigger);

        triggerBuilder
            .withChannels(trigger.getChannels().stream()
                .map(channel -> CampaignControllerTriggerShareChannel.valueOf(channel.name()))
                .collect(Collectors.toSet()));
        trigger.getTriggerPhase()
            .ifDefined(
                (value) -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(value, new TypeReference<>() {})));

        if (trigger.getQuality() != null) {
            triggerBuilder.withQuality(ShareQuality.valueOf(trigger.getQuality().name()));
        }

        trigger.getName().ifDefined((value) -> triggerBuilder.withName(value));
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
        return CampaignControllerTriggerType.SHARE;
    }

}
