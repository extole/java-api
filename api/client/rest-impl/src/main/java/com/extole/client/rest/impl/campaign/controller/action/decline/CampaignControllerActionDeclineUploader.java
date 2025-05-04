package com.extole.client.rest.impl.campaign.controller.action.decline;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDeclineConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.decline.CampaignControllerActionDeclineValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineBuilder;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineCauseTypeLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineEventTypeLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineLegacyActionIdLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineNoteLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclinePartnerEventIdLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclinePollingIdLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclinePollingNameLengthException;

@Component
public class CampaignControllerActionDeclineUploader
    implements CampaignControllerActionUploader<CampaignControllerActionDeclineConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionDeclineConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionRestException, CampaignComponentValidationRestException {
        CampaignControllerActionDeclineBuilder actionBuilder = context.get(step, action);

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
            actionBuilder.withNote(action.getNote());
            if (action.getCauseType() != null) {
                actionBuilder.withCauseType(action.getCauseType());
            }
            actionBuilder.withPollingId(action.getPollingId());
            if (action.getPollingName() != null) {
                actionBuilder.withPollingName(action.getPollingName());
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
        } catch (CampaignControllerActionDeclineLegacyActionIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.LEGACY_ACTION_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineEventTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.EVENT_TYPE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePartnerEventIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.PARTNER_EVENT_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineNoteLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.NOTE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineCauseTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.CAUSE_TYPE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePollingIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.POLLING_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePollingNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.POLLING_NAME_LENGTH)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.DECLINE;
    }

}
