package com.extole.client.rest.impl.campaign.controller.action.webhook;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionWebhookConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.webhook.CampaignControllerActionWebhookValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.webhook.CampaignControllerActionWebhookBuilder;
import com.extole.model.service.campaign.controller.action.webhook.DataNameInvalidWebhookActionException;
import com.extole.model.service.campaign.controller.action.webhook.DataNameLengthInvalidWebhookActionException;
import com.extole.model.service.campaign.controller.action.webhook.DataValueInvalidWebhookActionException;
import com.extole.model.service.campaign.controller.action.webhook.DataValueLengthInvalidWebhookActionException;

@Component
public class CampaignControllerActionWebhookUploader
    implements CampaignControllerActionUploader<CampaignControllerActionWebhookConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionWebhookConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionWebhookValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerActionWebhookBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }
            action.getWebhookId().ifDefined((value) -> actionBuilder.withWebhookId(value));
            if (!action.getData().isEmpty()) {
                actionBuilder.withData(action.getData());
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
        } catch (DataNameInvalidWebhookActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionWebhookValidationRestException.class)
                .withErrorCode(CampaignControllerActionWebhookValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidWebhookActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionWebhookValidationRestException.class)
                .withErrorCode(CampaignControllerActionWebhookValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidWebhookActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionWebhookValidationRestException.class)
                .withErrorCode(CampaignControllerActionWebhookValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidWebhookActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionWebhookValidationRestException.class)
                .withErrorCode(CampaignControllerActionWebhookValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.WEBHOOK;
    }

}
