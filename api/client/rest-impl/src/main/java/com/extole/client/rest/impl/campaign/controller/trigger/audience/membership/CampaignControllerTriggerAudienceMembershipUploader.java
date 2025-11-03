package com.extole.client.rest.impl.campaign.controller.trigger.audience.membership;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAudienceMembershipConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipBuilder;

@Component
public class CampaignControllerTriggerAudienceMembershipUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerAudienceMembershipConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerTriggerAudienceMembershipConfiguration trigger, ZoneId timeZone)
        throws CampaignComponentValidationRestException {
        CampaignControllerTriggerAudienceMembershipBuilder triggerBuilder = context.get(step, trigger);

        trigger.getName().ifDefined(name -> triggerBuilder.withName(name));
        trigger.getParentTriggerGroupName()
            .ifDefined((value) -> triggerBuilder.withParentTriggerGroupName(value));
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

        trigger.getHavingAnyAudienceId()
            .ifDefined(havingAnyAudienceId -> triggerBuilder.withHavingAnyAudienceId(havingAnyAudienceId));
        trigger.getPersonId().ifDefined(personId -> triggerBuilder.withPersonId(personId));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP;
    }

}
