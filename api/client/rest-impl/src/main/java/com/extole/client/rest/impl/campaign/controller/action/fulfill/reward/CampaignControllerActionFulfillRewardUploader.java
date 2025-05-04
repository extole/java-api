package com.extole.client.rest.impl.campaign.controller.action.fulfill.reward;

import java.time.ZoneId;

import com.google.common.base.Strings;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionFulfillRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardBuilder;
import com.extole.model.service.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardInvalidSpelExpressionException;
import com.extole.model.service.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardTooLongSpelExpressionException;
import com.extole.model.service.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardUndefinedSpelExpressionException;

@Component
public class CampaignControllerActionFulfillRewardUploader
    implements CampaignControllerActionUploader<CampaignControllerActionFulfillRewardConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionFulfillRewardConfiguration action, ZoneId timeZone)
        throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionFulfillRewardBuilder actionBuilder = context.get(step, action);
        try {
            if (!Strings.isNullOrEmpty(action.getRewardId())) {
                actionBuilder.withRewardId(action.getRewardId());
            }

            actionBuilder.withMessage(action.getMessage());

            actionBuilder.withPartnerRewardId(action.getPartnerRewardId());

            actionBuilder.withSuccess(action.getSuccess());

            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }

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

        } catch (CampaignControllerActionFulfillRewardInvalidSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionFulfillRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionFulfillRewardValidationRestException.INVALID_SPEL_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFulfillRewardTooLongSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionFulfillRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionFulfillRewardValidationRestException.TOO_LONG_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFulfillRewardUndefinedSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionFulfillRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionFulfillRewardValidationRestException.UNDEFINED_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.FULFILL_REWARD;
    }

}
