package com.extole.client.rest.impl.campaign.controller.action.incentivize.status.update;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionIncentivizeStatusUpdateConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.DataNameInvalidIncentivizeStatusUpdateActionException;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.DataNameLengthInvalidIncentivizeStatusUpdateActionException;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.DataValueInvalidIncentivizeStatusUpdateActionException;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.DataValueLengthInvalidIncentivizeStatusUpdateActionException;

@Component
public class CampaignControllerActionIncentivizeStatusUpdateUploader
    implements CampaignControllerActionUploader<CampaignControllerActionIncentivizeStatusUpdateConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionIncentivizeStatusUpdateConfiguration action,
        ZoneId timeZone)
        throws CampaignComponentValidationRestException,
        CampaignControllerActionIncentivizeStatusUpdateValidationRestException {
        CampaignControllerActionIncentivizeStatusUpdateBuilder actionBuilder = context.get(step, action);
        if (action.getQuality() != null) {
            actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
        }
        action.getEnabled().ifDefined((value) -> actionBuilder.withEnabled(value));
        action.getLegacyActionId().ifDefined(value -> actionBuilder.withLegacyActionId(value));
        action.getEventType().ifDefined(value -> actionBuilder.withEventType(Evaluatables.remapNestedOptional(
            value, new TypeReference<>() {})));
        action.getIncentivizePartnerId().ifDefined(value -> actionBuilder.withPartnerEventId(value));
        action.getMessage().ifDefined(value -> actionBuilder.withMessage(value));
        action.getReviewStatus().ifDefined(value -> actionBuilder.withReviewStatus(value));

        applyData(action, actionBuilder);

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

        action.isMoveToPending()
            .ifDefined(moveToPending -> actionBuilder.withMoveToPending(moveToPending));
    }

    private void applyData(CampaignControllerActionIncentivizeStatusUpdateConfiguration action,
        CampaignControllerActionIncentivizeStatusUpdateBuilder actionBuilder)
        throws CampaignControllerActionIncentivizeStatusUpdateValidationRestException {
        if (!action.getData().isEmpty()) {
            try {
                actionBuilder.withData(action.getData());
            } catch (DataNameInvalidIncentivizeStatusUpdateActionException e) {
                throw RestExceptionBuilder
                    .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                    .withErrorCode(
                        CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_NAME_INVALID)
                    .withCause(e)
                    .build();
            } catch (DataValueInvalidIncentivizeStatusUpdateActionException e) {
                throw RestExceptionBuilder
                    .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                    .withErrorCode(
                        CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_VALUE_INVALID)
                    .addParameter("name", e.getDataName())
                    .withCause(e)
                    .build();
            } catch (DataNameLengthInvalidIncentivizeStatusUpdateActionException e) {
                throw RestExceptionBuilder
                    .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                    .withErrorCode(
                        CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_NAME_LENGTH_INVALID)
                    .addParameter("name", e.getDataName())
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            } catch (DataValueLengthInvalidIncentivizeStatusUpdateActionException e) {
                throw RestExceptionBuilder
                    .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                    .withErrorCode(
                        CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_VALUE_LENGTH_INVALID)
                    .addParameter("name", e.getDataName())
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            }
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.INCENTIVIZE_STATUS_UPDATE;
    }

}
