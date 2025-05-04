package com.extole.client.rest.impl.campaign.controller;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFrontendControllerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignJourneyEntryConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.configuration.StepDataConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.CampaignControllerBuilder;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.journey.entry.CampaignJourneyEntryBuilder;
import com.extole.model.service.campaign.step.data.StepDataBuilder;

@Component
public class CampaignStepUploader {

    private final Map<CampaignControllerActionType,
        CampaignControllerActionUploader<CampaignControllerActionConfiguration>> actionUploadersByType;
    private final Map<CampaignControllerActionType,
        CampaignControllerActionUploader<CampaignControllerActionConfiguration>> frontendActionUploadersByType;
    private final Map<CampaignControllerTriggerType,
        CampaignControllerTriggerUploader<CampaignControllerTriggerConfiguration>> triggerUploadersByType;

    @Autowired
    @SuppressWarnings({"rawtypes", "unchecked"})
    public CampaignStepUploader(List<CampaignControllerActionUploader> actionUploaders,
        List<CampaignControllerActionUploader> actionFrontendUploaders,
        List<CampaignControllerTriggerUploader> triggerUploaders) {
        Map<CampaignControllerActionType,
            CampaignControllerActionUploader<CampaignControllerActionConfiguration>> actionUploadersMap =
                new HashMap<>();
        for (CampaignControllerActionUploader uploader : actionUploaders) {
            actionUploadersMap.put(uploader.getActionType(), uploader);
        }
        Map<CampaignControllerActionType,
            CampaignControllerActionUploader<CampaignControllerActionConfiguration>> frontendActionUploadersMap =
                new HashMap<>();
        for (CampaignControllerActionUploader uploader : actionFrontendUploaders) {
            frontendActionUploadersMap.put(uploader.getActionType(), uploader);
        }
        Map<CampaignControllerTriggerType,
            CampaignControllerTriggerUploader<CampaignControllerTriggerConfiguration>> triggerUploadersMap =
                new HashMap<>();
        for (CampaignControllerTriggerUploader uploader : triggerUploaders) {
            triggerUploadersMap.put(uploader.getTriggerType(), uploader);
        }

        actionUploadersByType = ImmutableMap.copyOf(actionUploadersMap);
        frontendActionUploadersByType = ImmutableMap.copyOf(frontendActionUploadersMap);
        triggerUploadersByType = ImmutableMap.copyOf(triggerUploadersMap);
    }

    public void uploadStep(CampaignUploadContext context, CampaignStepConfiguration step, ZoneId timeZone)
        throws CampaignControllerActionRestException, CampaignComponentValidationRestException,
        CampaignControllerTriggerRestException, CreativeArchiveRestException {
        switch (step.getType()) {
            case FRONTEND_CONTROLLER:
                uploadFrontendController(context, (CampaignFrontendControllerConfiguration) step, timeZone);
                break;
            case CONTROLLER:
                uploadController(context, (CampaignControllerConfiguration) step, timeZone);
                break;
            case JOURNEY_ENTRY:
                uploadJourneyEntry(context, (CampaignJourneyEntryConfiguration) step, timeZone);
                break;
            default:
                throw new IllegalStateException("Unknown step type=" + step.getType());
        }
    }

    private void uploadFrontendController(CampaignUploadContext context,
        CampaignFrontendControllerConfiguration controller, ZoneId timeZone)
        throws CampaignComponentValidationRestException, CampaignControllerActionRestException,
        CampaignControllerTriggerRestException, CreativeArchiveRestException {

        FrontendControllerBuilder controllerBuilder = context.get(controller);

        controllerBuilder.withName(controller.getName());
        controller.getScope()
            .ifDefined((value) -> controllerBuilder.withScope(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        controller.getEnabled().ifDefined((value) -> controllerBuilder.withEnabled(value));
        controller.getAliases().ifDefined((value) -> controllerBuilder.withAliases(value));
        controller.getEnabledOnStates()
            .ifDefined((value) -> controllerBuilder
                .withEnabledOnStates(Evaluatables.remapEnumCollection(value, new TypeReference<>() {})));
        controller.getCategory().ifDefined(value -> controllerBuilder.withCategory(value));

        for (StepDataConfiguration stepData : controller.getData()) {
            uploadStepData(stepData, context.get(controller, stepData));
        }

        for (CampaignControllerActionConfiguration action : controller.getActions()) {
            uploadFrontendAction(context, controller, action, timeZone);
        }

        for (CampaignControllerTriggerConfiguration trigger : controller.getTriggers()) {
            uploadTrigger(context, controller, trigger, timeZone);
        }

        controllerBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : controller.getComponentReferences()) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                controllerBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }

        controller.getJourneyNames()
            .ifDefined((value) -> controllerBuilder
                .withJourneyNames(Evaluatables.remapCollection(value, new TypeReference<>() {})));

        controller.getSendPolicy()
            .ifDefined(
                (value) -> controllerBuilder.withSendPolicy(Evaluatables.remapEnum(value, new TypeReference<>() {})));
    }

    private void uploadController(CampaignUploadContext context, CampaignControllerConfiguration controller,
        ZoneId timeZone) throws CampaignControllerActionRestException, CampaignComponentValidationRestException,
        CampaignControllerTriggerRestException, CreativeArchiveRestException {
        CampaignControllerBuilder controllerBuilder = context.get(controller);

        controllerBuilder.withName(controller.getName());
        controller.getScope()
            .ifDefined((value) -> controllerBuilder.withScope(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        controller.getEnabled().ifDefined((value) -> controllerBuilder.withEnabled(value));
        controller.getAliases().ifDefined((value) -> controllerBuilder.withAliases(value));
        controller.getEnabledOnStates()
            .ifDefined((value) -> controllerBuilder
                .withEnabledOnStates(Evaluatables.remapEnumCollection(value, new TypeReference<>() {})));
        controller.getSelectors().ifDefined(
            (value) -> controllerBuilder
                .withSelectors(Evaluatables.remapEnumCollection(value, new TypeReference<>() {})));

        for (StepDataConfiguration stepData : controller.getData()) {
            uploadStepData(stepData, context.get(controller, stepData));
        }

        for (CampaignControllerActionConfiguration action : controller.getActions()) {
            uploadAction(context, controller, action, timeZone);
        }

        for (CampaignControllerTriggerConfiguration trigger : controller.getTriggers()) {
            uploadTrigger(context, controller, trigger, timeZone);
        }

        controllerBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : controller.getComponentReferences()) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                controllerBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }

        controller.getJourneyNames()
            .ifDefined((value) -> controllerBuilder
                .withJourneyNames(Evaluatables.remapCollection(value, new TypeReference<>() {})));
    }

    private void uploadJourneyEntry(CampaignUploadContext context, CampaignJourneyEntryConfiguration journeyEntry,
        ZoneId timeZone) throws CampaignControllerTriggerRestException, CampaignComponentValidationRestException {
        CampaignJourneyEntryBuilder journeyEntryBuilder = context.get(journeyEntry);

        journeyEntry.getJourneyName().ifDefined(value -> journeyEntryBuilder.withJourneyName(
            Evaluatables.remapClassToClass(value, new TypeReference<>() {})));
        journeyEntry.getPriority().ifDefined((value) -> journeyEntryBuilder.withPriority(value));
        journeyEntry.getEnabled().ifDefined((value) -> journeyEntryBuilder.withEnabled(value));

        for (CampaignControllerTriggerConfiguration trigger : journeyEntry.getTriggers()) {
            uploadTrigger(context, journeyEntry, trigger, timeZone);
        }

        for (StepDataConfiguration stepData : journeyEntry.getData()) {
            uploadStepData(stepData, context.get(journeyEntry, stepData));
        }

        journeyEntryBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : journeyEntry.getComponentReferences()) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                journeyEntryBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }

        if (journeyEntry.getKey().isPresent()) {
            journeyEntryBuilder.withKey()
                .withName(journeyEntry.getKey().get().getName())
                .withValue(journeyEntry.getKey().get().getValue());
        }
    }

    private void uploadTrigger(CampaignUploadContext context,
        CampaignStepConfiguration step, CampaignControllerTriggerConfiguration trigger,
        ZoneId timeZone) throws CampaignControllerTriggerRestException, CampaignComponentValidationRestException {
        triggerUploadersByType.get(CampaignControllerTriggerType.valueOf(trigger.getTriggerType().name()))
            .upload(context, step, trigger, timeZone);
    }

    private void uploadAction(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionConfiguration action,
        ZoneId timeZone) throws CampaignControllerActionRestException, CampaignComponentValidationRestException,
        CreativeArchiveRestException {
        actionUploadersByType.get(CampaignControllerActionType.valueOf(action.getActionType().name()))
            .upload(context, step, action, timeZone);
    }

    private void uploadFrontendAction(CampaignUploadContext context, CampaignFrontendControllerConfiguration controller,
        CampaignControllerActionConfiguration action,
        ZoneId timeZone) throws CampaignControllerActionRestException, CampaignComponentValidationRestException,
        CreativeArchiveRestException {

        frontendActionUploadersByType.get(CampaignControllerActionType.valueOf(action.getActionType().name()))
            .upload(context, controller, action, timeZone);
    }

    private void uploadStepData(StepDataConfiguration stepData, StepDataBuilder stepDataBuilder)
        throws CampaignComponentValidationRestException {

        stepData.getName().ifDefined((value) -> stepDataBuilder.withName(value));
        stepData.getValue().ifDefined((value) -> stepDataBuilder.withValue(value));
        stepData.getScope()
            .ifDefined((value) -> stepDataBuilder.withScope(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        stepData.isDimension().ifDefined((value) -> stepDataBuilder.withDimension(value));
        stepData.getPersistTypes()
            .ifDefined((value) -> stepDataBuilder
                .withPersistTypes(Evaluatables.remapEnumCollection(value, new TypeReference<>() {})));
        stepData.getDefaultValue().ifDefined((value) -> stepDataBuilder.withDefaultValue(value));
        stepData.getKeyType()
            .ifDefined((value) -> stepDataBuilder.withKeyType(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        stepData.getEnabled().ifDefined(enabled -> stepDataBuilder.withEnabled(enabled));
        stepDataBuilder.clearComponentReferences();
        for (CampaignComponentReferenceConfiguration componentReference : stepData.getComponentReferences()) {
            if (componentReference.getAbsoluteName() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                stepDataBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
            referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
            referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
        }
    }

}
