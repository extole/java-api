package com.extole.client.rest.impl.campaign.controller.trigger.score;

import java.time.ZoneId;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerScoreConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.score.CampaignControllerTriggerScoreValidationRestException;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.score.CampaignControllerTriggerScoreBuilder;
import com.extole.model.service.campaign.controller.trigger.score.CampaignControllerTriggerScoreCauseEventNameLengthException;
import com.extole.model.service.campaign.controller.trigger.score.CampaignControllerTriggerScoreIllegalCharacterInCauseEventNameException;

@Component
public class CampaignControllerTriggerScoreUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerScoreConfiguration> {

    @Override
    public void upload(CampaignUploadContext context,
        CampaignStepConfiguration step, CampaignControllerTriggerScoreConfiguration trigger,
        ZoneId timeZone) throws CampaignControllerTriggerRestException, CampaignComponentValidationRestException {
        try {
            CampaignControllerTriggerScoreBuilder triggerBuilder = context.get(step, trigger);
            triggerBuilder
                .withCauseEventName(trigger.getCauseEventName());
            if (trigger.getChannel() != null) {
                triggerBuilder.withChannel(trigger.getChannel());
            }
            if (trigger.getScoreResult() != null) {
                triggerBuilder.withScoreResult(trigger.getScoreResult());
            }
            trigger.getTriggerPhase()
                .ifDefined(
                    (value) -> triggerBuilder
                        .withTriggerPhase(Evaluatables.remapEnum(value, new TypeReference<>() {})));
            trigger.getName().ifDefined((value) -> triggerBuilder.withName(value));
            trigger.getEnabled().ifDefined((value) -> triggerBuilder.withEnabled(value));
            trigger.getNegated().ifDefined((negated) -> triggerBuilder.withNegated(negated));

            triggerBuilder.clearComponentReferences();
            for (CampaignComponentReferenceConfiguration componentReference : trigger.getComponentReferences()) {
                if (componentReference.getAbsoluteName() == null) {
                    throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                        .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                        .build();
                }
                CampaignComponentReferenceBuilder referenceBuilder =
                    triggerBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
                referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
                referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
            }
        } catch (CampaignControllerTriggerScoreIllegalCharacterInCauseEventNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerScoreValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerScoreValidationRestException.CAUSE_EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("cause_event_name", e.getCauseEventName())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerScoreCauseEventNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerScoreValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerScoreValidationRestException.CAUSE_EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("cause_event_name", e.getCauseEventName())
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.SCORE;
    }

}
