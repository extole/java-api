package com.extole.client.rest.impl.campaign.controller.action.incentivize;

import static com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_NAME_INVALID_EXPRESSION;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionIncentivizeConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.entity.campaign.IncentivizeActionOverrideType;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeActionNameInvalidExpressionException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeActionNameInvalidLengthException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeActionNameInvalidValueException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeOverrideValueInvalidException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeOverrideValueLengthException;
import com.extole.model.service.campaign.controller.action.incentivize.DataNameInvalidIncentivizeActionException;
import com.extole.model.service.campaign.controller.action.incentivize.DataNameLengthInvalidIncentivizeActionException;
import com.extole.model.service.campaign.controller.action.incentivize.DataValueInvalidIncentivizeActionException;
import com.extole.model.service.campaign.controller.action.incentivize.DataValueLengthInvalidIncentivizeActionException;

@Component
public class CampaignControllerActionIncentivizeUploader
    implements CampaignControllerActionUploader<CampaignControllerActionIncentivizeConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionIncentivizeConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionIncentivizeValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerActionIncentivizeBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }
            action.getIncentivizeActionType().ifDefined((value) -> actionBuilder.withIncentivizeActionType(
                Evaluatables.remapEnum(value, new TypeReference<>() {})));
            if (action.getOverrides() != null) {
                Map<IncentivizeActionOverrideType, String> overrides = new HashMap<>();
                action.getOverrides().forEach((key, value) -> overrides
                    .put(IncentivizeActionOverrideType.valueOf(key.name()), value));
                actionBuilder.withOverrides(overrides);
            }
            actionBuilder.withActionName(action.getActionName());
            action.getEnabled().ifDefined((value) -> actionBuilder.withEnabled(value));
            if (!action.getData().isEmpty()) {
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

            action.getReviewStatus().ifDefined((value) -> actionBuilder.withReviewStatus(
                Evaluatables.remapEnum(value, new TypeReference<>() {})));
        } catch (DataNameInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeOverrideValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.OVERRIDE_VALUE_INVALID)
                .addParameter("name", e.getOverrideType())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeOverrideValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.OVERRIDE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getOverrideType())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidValueException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_NAME_INVALID_VALUE)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidExpressionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(INCENTIVIZE_ACTION_NAME_INVALID_EXPRESSION)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_NAME_INVALID_LENGTH)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.INCENTIVIZE;
    }

}
