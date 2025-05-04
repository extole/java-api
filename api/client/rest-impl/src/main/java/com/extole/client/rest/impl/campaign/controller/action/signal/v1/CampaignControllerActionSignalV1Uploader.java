package com.extole.client.rest.impl.campaign.controller.action.signal.v1;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionSignalV1Configuration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.signal.CampaignControllerActionSignalValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1Builder;
import com.extole.model.service.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1DataAttributeNameInvalidException;
import com.extole.model.service.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1DataAttributeNameLengthException;
import com.extole.model.service.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1DataAttributeValueInvalidException;
import com.extole.model.service.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1DataAttributeValueLengthException;
import com.extole.model.service.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1PollingIdLengthException;

@Component
public class CampaignControllerActionSignalV1Uploader
    implements CampaignControllerActionUploader<CampaignControllerActionSignalV1Configuration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionSignalV1Configuration action,
        ZoneId timeZone)
        throws CampaignControllerActionSignalValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerActionSignalV1Builder actionBuilder = context.get(step, action);
        try {
            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }
            if (action.getSignalPollingId() != null) {
                actionBuilder.withSignalPollingId(action.getSignalPollingId());
            }
            if (action.getData() != null) {
                actionBuilder.withData(action.getData());
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
        } catch (CampaignControllerActionSignalV1DataAttributeNameInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionSignalValidationRestException.DATA_NAME_INVALID)
                .withCause(e).build();
        } catch (CampaignControllerActionSignalV1DataAttributeValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionSignalValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataAttributeName())
                .withCause(e).build();
        } catch (CampaignControllerActionSignalV1DataAttributeNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionSignalValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataAttributeName()).withCause(e).build();
        } catch (CampaignControllerActionSignalV1DataAttributeValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionSignalValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataAttributeName()).withCause(e).build();
        } catch (CampaignControllerActionSignalV1PollingIdLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionSignalValidationRestException.SIGNAL_POLLING_ID_LENGTH_INVALID)
                .addParameter("signal_polling_id", action.getSignalPollingId())
                .withCause(e).build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.SIGNAL_V1;
    }

}
