package com.extole.client.rest.impl.campaign.controller.action.expression;

import java.time.ZoneId;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionExpressionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.expression.CampaignControllerActionExpressionValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.expression.CampaignControllerActionExpressionBuilder;
import com.extole.model.service.campaign.controller.action.expression.DataNameInvalidExpressionActionException;
import com.extole.model.service.campaign.controller.action.expression.DataNameLengthInvalidExpressionActionException;
import com.extole.model.service.campaign.controller.action.expression.DataValueInvalidExpressionActionException;
import com.extole.model.service.campaign.controller.action.expression.DataValueLengthInvalidExpressionActionException;
import com.extole.model.service.campaign.controller.action.expression.ExpressionLengthInvalidExpressionActionException;

@Component
public class CampaignControllerActionExpressionUploader
    implements CampaignControllerActionUploader<CampaignControllerActionExpressionConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionExpressionConfiguration action,
        ZoneId timeZone)
        throws CampaignControllerActionExpressionValidationRestException, CampaignComponentValidationRestException {
        CampaignControllerActionExpressionBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }
            action.getExpression().ifDefined((value) -> actionBuilder.withExpression(value));
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
        } catch (DataNameInvalidExpressionActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionExpressionValidationRestException.class)
                .withErrorCode(CampaignControllerActionExpressionValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidExpressionActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionExpressionValidationRestException.class)
                .withErrorCode(CampaignControllerActionExpressionValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidExpressionActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionExpressionValidationRestException.class)
                .withErrorCode(CampaignControllerActionExpressionValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidExpressionActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionExpressionValidationRestException.class)
                .withErrorCode(CampaignControllerActionExpressionValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (ExpressionLengthInvalidExpressionActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionExpressionValidationRestException.class)
                .withErrorCode(CampaignControllerActionExpressionValidationRestException.EXPRESSION_LENGTH_INVALID)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.EXPRESSION;
    }

}
