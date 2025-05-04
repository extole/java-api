package com.extole.client.rest.impl.campaign.controller.action.email;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionEmailConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.email.CampaignControllerActionEmailValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.email.CampaignControllerActionEmailBuilder;
import com.extole.model.service.campaign.controller.action.email.DataNameInvalidEmailActionException;
import com.extole.model.service.campaign.controller.action.email.DataNameLengthInvalidEmailActionException;
import com.extole.model.service.campaign.controller.action.email.DataValueInvalidEmailActionException;
import com.extole.model.service.campaign.controller.action.email.DataValueLengthInvalidEmailActionException;

@Component
public class CampaignControllerActionEmailUploader
    implements CampaignControllerActionUploader<CampaignControllerActionEmailConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionEmailConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionEmailValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerActionEmailBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }
            actionBuilder.withZoneName(action.getZoneName());
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
        } catch (DataNameInvalidEmailActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionEmailValidationRestException.class)
                .withErrorCode(CampaignControllerActionEmailValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidEmailActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionEmailValidationRestException.class)
                .withErrorCode(CampaignControllerActionEmailValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidEmailActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionEmailValidationRestException.class)
                .withErrorCode(CampaignControllerActionEmailValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidEmailActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionEmailValidationRestException.class)
                .withErrorCode(CampaignControllerActionEmailValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.EMAIL;
    }

}
