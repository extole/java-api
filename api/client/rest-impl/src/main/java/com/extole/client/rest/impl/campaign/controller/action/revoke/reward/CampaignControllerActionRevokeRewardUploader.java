package com.extole.client.rest.impl.campaign.controller.action.revoke.reward;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRevokeRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardBuilder;
import com.extole.model.service.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardInvalidSpelExpressionException;
import com.extole.model.service.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardTooLongSpelExpressionException;
import com.extole.model.service.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardUndefinedSpelExpressionException;

@Component
public class CampaignControllerActionRevokeRewardUploader
    implements CampaignControllerActionUploader<CampaignControllerActionRevokeRewardConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionRevokeRewardConfiguration action,
        ZoneId timeZone) throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionRevokeRewardBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getRewardId() != null) {
                actionBuilder.withRewardId(action.getRewardId());
            }

            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }

            actionBuilder.withMessage(action.getMessage());
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
        } catch (CampaignControllerActionRevokeRewardUndefinedSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRevokeRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRevokeRewardValidationRestException.UNDEFINED_EXPRESSION)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRevokeRewardTooLongSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRevokeRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRevokeRewardValidationRestException.TOO_LONG_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRevokeRewardInvalidSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRevokeRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRevokeRewardValidationRestException.INVALID_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.REVOKE_REWARD;
    }

}
