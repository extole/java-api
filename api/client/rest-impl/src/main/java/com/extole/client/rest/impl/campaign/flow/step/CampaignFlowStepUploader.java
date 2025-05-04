package com.extole.client.rest.impl.campaign.flow.step;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepAppConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepMetricConfiguration;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepValidationRestException;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.flow.CampaignFlowStepWordsBuilder;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepBuilder;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepIconTypeLengthException;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppBuilder;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricBuilder;

@Component
public class CampaignFlowStepUploader {

    public void uploadFlowStep(CampaignUploadContext context, CampaignFlowStepConfiguration flowStep)
        throws CampaignFlowStepValidationRestException, CampaignComponentValidationRestException {
        CampaignFlowStepBuilder flowStepBuilder = context.get(flowStep);

        try {
            flowStep.getFlowPath().ifDefined((value) -> flowStepBuilder.withFlowPath(value));
            flowStep.getSequence().ifDefined((value) -> flowStepBuilder.withSequence(value));
            flowStep.getStepName().ifDefined((value) -> flowStepBuilder.withStepName(value));
            flowStep.getIconType().ifDefined((value) -> flowStepBuilder.withIconType(value));
            for (CampaignFlowStepMetricConfiguration flowStepMetric : flowStep.getMetrics()) {
                uploadFlowStepMetric(context.get(flowStep, flowStepMetric), flowStepMetric,
                    flowStep.getComponentReferences());
            }
            for (CampaignFlowStepAppConfiguration flowStepApp : flowStep.getApps()) {
                uploadFlowStepApp(context.get(flowStep, flowStepApp), flowStepApp, flowStep.getComponentReferences());
            }
            flowStep.getTags().ifDefined((value) -> flowStepBuilder.withTags(value));
            flowStep.getName().ifDefined((value) -> flowStepBuilder.withName(value));
            flowStep.getIconColor().ifDefined((value) -> flowStepBuilder.withIconColor(value));
            flowStep.getDescription().ifDefined((value) -> flowStepBuilder.withDescription(value));

            if (flowStep.getWords() != null) {
                CampaignFlowStepWordsBuilder wordsBuilder = flowStepBuilder.withWords();
                flowStep.getWords().getSingularNounName()
                    .ifDefined((value) -> wordsBuilder.withSingularNounName(value));
                flowStep.getWords().getPluralNounName().ifDefined((value) -> wordsBuilder.withPluralNounName(value));
                flowStep.getWords().getVerbName().ifDefined((value) -> wordsBuilder.withVerbName(value));
                flowStep.getWords().getRateName().ifDefined((value) -> wordsBuilder.withRateName(value));
                flowStep.getWords().getPersonCountingName()
                    .ifDefined((value) -> wordsBuilder.withPersonCountingName(value));
            }

            flowStepBuilder.clearComponentReferences();
            for (CampaignComponentReferenceConfiguration componentReference : flowStep.getComponentReferences()) {
                if (componentReference.getAbsoluteName() == null) {
                    throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                        .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                        .build();
                }
                CampaignComponentReferenceBuilder referenceBuilder =
                    flowStepBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
                referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
                referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
            }
        } catch (CampaignFlowStepIconTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFlowStepValidationRestException.class)
                .withErrorCode(CampaignFlowStepValidationRestException.ICON_TYPE_LENGTH_OUT_OF_RANGE)
                .addParameter("icon_type", e.getIconType())
                .withCause(e)
                .build();
        }
    }

    private void uploadFlowStepMetric(CampaignFlowStepMetricBuilder flowStepMetricBuilder,
        CampaignFlowStepMetricConfiguration flowStepMetric,
        List<CampaignComponentReferenceConfiguration> componentReferences)
        throws CampaignComponentValidationRestException {
        flowStepMetric.getName().ifDefined((value) -> flowStepMetricBuilder.withName(value));
        flowStepMetric.getDescription().ifDefined((value) -> flowStepMetricBuilder.withDescription(value));
        flowStepMetric.getExpression().ifDefined((value) -> flowStepMetricBuilder.withExpression(value));
        flowStepMetric.getUnit().ifDefined((value) -> flowStepMetricBuilder.withUnit(value));
        flowStepMetric.getTags().ifDefined((value) -> flowStepMetricBuilder.withTags(value));

        flowStepMetricBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : componentReferences) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                flowStepMetricBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }
    }

    private void uploadFlowStepApp(CampaignFlowStepAppBuilder flowStepAppBuilder,
        CampaignFlowStepAppConfiguration flowStepApp,
        List<CampaignComponentReferenceConfiguration> componentReferences)
        throws CampaignComponentValidationRestException {
        flowStepApp.getName().ifDefined((value) -> flowStepAppBuilder.withName(value));
        flowStepApp.getDescription().ifDefined((value) -> flowStepAppBuilder.withDescription(value));

        if (flowStepApp.getType() != null) {
            flowStepApp.getType().getName().ifDefined((value) -> flowStepAppBuilder.withType().withName(value));
        }

        flowStepAppBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : componentReferences) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                flowStepAppBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }
    }

}
