package com.extole.client.rest.impl.campaign.controller.action.earn.reward;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionEarnRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardBuilder;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardInvalidDataAttributeValueException;

@Component
public class CampaignControllerActionEarnRewardUploader
    implements CampaignControllerActionUploader<CampaignControllerActionEarnRewardConfiguration> {

    @Override
    public void upload(
        CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionEarnRewardConfiguration action,
        ZoneId timeZone) throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionEarnRewardBuilder actionBuilder = context.get(step, action);

        try {
            action.getRewardName().ifDefined(value -> actionBuilder.withRewardName(value));

            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }

            action.getRewardSupplierId().ifDefined((value) -> actionBuilder.withRewardSupplierId(value));

            if (action.getData() != null) {
                actionBuilder.withData(action.getData());
            }

            action.getTags().ifDefined(value -> actionBuilder.withTags(value));
            action.getEventTime().ifDefined((value) -> actionBuilder.withEventTime(value));
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

            action.getRewardActionId().ifDefined((value) -> actionBuilder.withRewardActionId(value));
            action.getExtraData().ifDefined(value -> actionBuilder.withExtraData(value));
        } catch (CampaignControllerActionEarnRewardInvalidDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.EARN_REWARD;
    }

}
