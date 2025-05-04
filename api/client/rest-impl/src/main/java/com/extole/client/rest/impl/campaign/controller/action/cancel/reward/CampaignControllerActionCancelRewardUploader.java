package com.extole.client.rest.impl.campaign.controller.action.cancel.reward;

import java.time.ZoneId;

import com.google.common.base.Strings;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCancelRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.cancel.reward.CampaignControllerActionCancelRewardValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.cancel.reward.CampaignControllerActionCancelRewardBuilder;
import com.extole.model.service.campaign.controller.action.cancel.reward.CampaignControllerActionCancelRewardInvalidSpelExpressionException;
import com.extole.model.service.campaign.controller.action.cancel.reward.CampaignControllerActionCancelRewardTooLongSpelExpressionException;
import com.extole.model.service.campaign.controller.action.cancel.reward.CampaignControllerActionCancelRewardUndefinedSpelExpressionException;

@Component
public class CampaignControllerActionCancelRewardUploader
    implements CampaignControllerActionUploader<CampaignControllerActionCancelRewardConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionCancelRewardConfiguration action,
        ZoneId timeZone) throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionCancelRewardBuilder actionBuilder = context.get(step, action);

        try {
            if (!Strings.isNullOrEmpty(action.getRewardId())) {
                actionBuilder.withRewardId(action.getRewardId());
            }

            actionBuilder.withMessage(action.getMessage());

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
        } catch (CampaignControllerActionCancelRewardUndefinedSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionCancelRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionCancelRewardValidationRestException.UNDEFINED_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionCancelRewardTooLongSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionCancelRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionCancelRewardValidationRestException.TOO_LONG_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionCancelRewardInvalidSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionCancelRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionCancelRewardValidationRestException.INVALID_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.CANCEL_REWARD;
    }

}
