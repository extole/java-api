package com.extole.client.rest.impl.campaign.controller.action.step.signal;

import java.time.ZoneId;

import com.google.common.base.Strings;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionStepSignalConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.step.signal.CampaignControllerActionStepSignalValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.step.signal.CampaignControllerActionStepSignalBuilder;
import com.extole.model.service.campaign.controller.action.step.signal.CampaignControllerActionStepSignalNameInvalidLengthException;
import com.extole.model.service.campaign.controller.action.step.signal.CampaignControllerActionStepSignalPollingIdInvalidExpressionException;
import com.extole.model.service.campaign.controller.action.step.signal.CampaignControllerActionStepSignalPollingIdLengthException;

@Component
public class CampaignControllerActionStepSignalUploader
    implements CampaignControllerActionUploader<CampaignControllerActionStepSignalConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionStepSignalConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionStepSignalValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerActionStepSignalBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }
            if (action.getPollingId() != null) {
                actionBuilder.withPollingId(action.getPollingId());
            }
            if (!Strings.isNullOrEmpty(action.getName())) {
                actionBuilder.withName(action.getName());
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
        } catch (CampaignControllerActionStepSignalNameInvalidLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionStepSignalValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionStepSignalValidationRestException.NAME_INVALID_LENGTH)
                .addParameter("name", action.getName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionStepSignalPollingIdInvalidExpressionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionStepSignalValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionStepSignalValidationRestException.POLLING_ID_INVALID_EXPRESSION)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionStepSignalPollingIdLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionStepSignalValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionStepSignalValidationRestException.POLLING_ID_INVALID_LENGTH)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.STEP_SIGNAL;
    }

}
