package com.extole.client.rest.impl.campaign.controller.action.approve;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionApproveConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.approve.CampaignControllerActionApproveValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApproveBuilder;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApproveCauseTypeLengthException;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApproveEventTypeLengthException;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApproveForceLengthException;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApproveLegacyActionIdLengthException;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApproveNoteLengthException;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApprovePartnerEventIdLengthException;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApprovePollingIdLengthException;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApprovePollingNameLengthException;

@Component
public class CampaignControllerActionApproveUploader
    implements CampaignControllerActionUploader<CampaignControllerActionApproveConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionApproveConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionApproveBuilder actionBuilder = context.get(step, action);

        try {

            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }
            actionBuilder.withLegacyActionId(action.getLegacyActionId());
            if (action.getEventType() != null) {
                actionBuilder.withEventType(action.getEventType());
            }
            if (action.getPartnerEventId() != null) {
                actionBuilder.withPartnerEventId(action.getPartnerEventId());
            }
            if (action.getForce() != null) {
                actionBuilder.withForce(action.getForce());
            }
            actionBuilder.withNote(action.getNote());
            if (action.getCauseType() != null) {
                actionBuilder.withCauseType(action.getCauseType());
            }
            actionBuilder.withPollingId(action.getPollingId());
            if (action.getPollingName() != null) {
                actionBuilder.withPollingName(action.getPollingName());
            }
            action.getEnabled().ifDefined((value) -> actionBuilder.withEnabled(value));
            action.getRewardTags().ifDefined(value -> actionBuilder.withRewardTags(value));

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
        } catch (CampaignControllerActionApproveLegacyActionIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionApproveValidationRestException.class)
                .withErrorCode(CampaignControllerActionApproveValidationRestException.LEGACY_ACTION_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionApproveEventTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionApproveValidationRestException.class)
                .withErrorCode(CampaignControllerActionApproveValidationRestException.EVENT_TYPE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionApprovePartnerEventIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionApproveValidationRestException.class)
                .withErrorCode(CampaignControllerActionApproveValidationRestException.PARTNER_EVENT_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionApproveForceLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionApproveValidationRestException.class)
                .withErrorCode(CampaignControllerActionApproveValidationRestException.FORCE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionApproveNoteLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionApproveValidationRestException.class)
                .withErrorCode(CampaignControllerActionApproveValidationRestException.NOTE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionApproveCauseTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionApproveValidationRestException.class)
                .withErrorCode(CampaignControllerActionApproveValidationRestException.CAUSE_TYPE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionApprovePollingIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionApproveValidationRestException.class)
                .withErrorCode(CampaignControllerActionApproveValidationRestException.POLLING_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionApprovePollingNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionApproveValidationRestException.class)
                .withErrorCode(CampaignControllerActionApproveValidationRestException.POLLING_NAME_LENGTH)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.APPROVE;
    }

}
