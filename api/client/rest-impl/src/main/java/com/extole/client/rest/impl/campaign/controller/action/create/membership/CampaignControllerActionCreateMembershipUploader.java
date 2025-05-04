package com.extole.client.rest.impl.campaign.controller.action.create.membership;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCreateMembershipConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.create.membership.CampaignControllerActionCreateMembershipBuilder;

@Component
public class CampaignControllerActionCreateMembershipUploader
    implements CampaignControllerActionUploader<CampaignControllerActionCreateMembershipConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionCreateMembershipConfiguration action,
        ZoneId timeZone) throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionCreateMembershipBuilder actionBuilder = context.get(step, action);

        action.getAudienceId().ifDefined(value -> actionBuilder.withAudienceId(value));

        if (action.getQuality() != null) {
            actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
        }

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
        return CampaignControllerActionType.CREATE_MEMBERSHIP;
    }

}
