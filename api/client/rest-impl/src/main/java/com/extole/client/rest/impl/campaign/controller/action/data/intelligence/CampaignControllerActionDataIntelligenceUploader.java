package com.extole.client.rest.impl.campaign.controller.action.data.intelligence;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDataIntelligenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceBuilder;

@Component
public class CampaignControllerActionDataIntelligenceUploader
    implements CampaignControllerActionUploader<CampaignControllerActionDataIntelligenceConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionDataIntelligenceConfiguration action, ZoneId timeZone)
        throws CampaignComponentValidationRestException {
        CampaignControllerActionDataIntelligenceBuilder actionBuilder = context.get(step, action);

        if (action.getQuality() != null) {
            actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
        }

        actionBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : action.getComponentReferences()) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                actionBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }

        action.getEnabled().ifDefined((value) -> actionBuilder.withEnabled(value));
        action.getIntelligenceProvider()
            .ifDefined(value -> actionBuilder.withIntelligenceProvider(
                Evaluatables.remapEnum(value, new TypeReference<>() {})));
        action.getEventName().ifDefined(value -> actionBuilder.withEventName(value));
        action.getProfileRiskUpdateInterval().ifDefined(value -> actionBuilder.withProfileRiskUpdateInterval(value));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.DATA_INTELLIGENCE;
    }

}
