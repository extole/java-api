package com.extole.client.rest.impl.campaign.controller.action.schedule;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionScheduleConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatable;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleBackdatedDateException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleBuilder;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataAttributeNameInvalidException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataAttributeNameLengthException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataAttributeValueInvalidException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataAttributeValueLengthException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleInvalidDateException;

@Component
public class CampaignControllerActionScheduleUploader
    implements CampaignControllerActionUploader<CampaignControllerActionScheduleConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionScheduleConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionScheduleValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerActionScheduleBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }
            actionBuilder.withScheduleName(action.getScheduleName());
            actionBuilder.withForce(action.isForce());
            if (Evaluatable.isDefined(action.getDelays())) {
                actionBuilder.withScheduleDelays(action.getDelays());
            }

            if (action.getDates() != null) {
                actionBuilder.withScheduleDates(
                    action.getDates().stream().map(date -> date.toInstant()).collect(Collectors.toList()));
            }

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
        } catch (CampaignControllerActionScheduleDataAttributeNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionScheduleDataAttributeValueInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getDataAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionScheduleDataAttributeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getDataAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionScheduleDataAttributeValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getDataAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionScheduleInvalidDateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.INVALID_SCHEDULE_DATE)
                .addParameter("date", e.getScheduleDate())
                .addParameter("message", e.getMessage())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionScheduleBackdatedDateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.BACKDATED_DATE)
                .addParameter("date", e.getDate())
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.SCHEDULE;
    }

}
