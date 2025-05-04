package com.extole.client.rest.impl.campaign.controller.action.redeem.reward;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRedeemRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardBuilder;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardInvalidDataAttributeNameException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardInvalidDataAttributeValueException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardInvalidSpelExpressionException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardMissingDataAttributeNameException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardMissingDataAttributeValueException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardTooLongSpelExpressionException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardUndefinedSpelExpressionException;

@Component
public class CampaignControllerActionRedeemRewardUploader
    implements CampaignControllerActionUploader<CampaignControllerActionRedeemRewardConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionRedeemRewardConfiguration action,
        ZoneId timeZone) throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionRedeemRewardBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getRewardId() != null) {
                actionBuilder.withRewardId(action.getRewardId());
            }

            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }

            if (action.getData() != null) {
                actionBuilder.withData(action.getData());
            }

            actionBuilder.withPartnerEventId(action.getPartnerEventId());
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
        } catch (CampaignControllerActionRedeemRewardMissingDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardUndefinedSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.UNDEFINED_EXPRESSION)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardMissingDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardTooLongSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.TOO_LONG_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.INVALID_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.REDEEM_REWARD;
    }

}
