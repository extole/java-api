package com.extole.client.rest.impl.campaign.controller.action.display;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDisplayConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.display.CampaignControllerActionDisplayBuilder;

@Component
public class CampaignControllerActionDisplayUploader
    implements CampaignControllerActionUploader<CampaignControllerActionDisplayConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionDisplayConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionDisplayBuilder actionBuilder = context.get(step, action);

        if (action.getQuality() != null) {
            actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
        }
        action.getHeaders().ifDefined(value -> actionBuilder.withHeaders(value));
        action.getBody().ifDefined(value -> actionBuilder.withBody(value));
        action.getResponse().ifDefined(value -> actionBuilder.withResponse(value));
        action.getEnabled().ifDefined((value) -> actionBuilder.withEnabled(value));
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
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.DISPLAY;
    }

}
