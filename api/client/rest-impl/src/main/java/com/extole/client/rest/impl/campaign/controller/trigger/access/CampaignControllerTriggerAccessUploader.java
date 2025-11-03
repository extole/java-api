package com.extole.client.rest.impl.campaign.controller.trigger.access;

import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.Authorization.Scope;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAccessConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerUploader;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.trigger.access.CampaignControllerTriggerAccessBuilder;

@Component
public class CampaignControllerTriggerAccessUploader
    implements CampaignControllerTriggerUploader<CampaignControllerTriggerAccessConfiguration> {

    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerTriggerAccessConfiguration trigger,
        ZoneId timeZone) throws CampaignComponentValidationRestException {
        CampaignControllerTriggerAccessBuilder triggerBuilder = context.get(step, trigger);
        if (trigger.getTrustedScopes() != null) {
            Set<Scope> trustedScopes = trigger.getTrustedScopes().stream()
                .map(trustedScope -> Authorization.Scope.valueOf(trustedScope.name()))
                .collect(Collectors.toSet());

            triggerBuilder.withTrustedScopes(trustedScopes);
        }
        trigger.getTriggerPhase()
            .ifDefined(
                (value) -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(value, new TypeReference<>() {})));
        trigger.getName().ifDefined((value) -> triggerBuilder.withName(value));
        trigger.getParentTriggerGroupName()
            .ifDefined((value) -> triggerBuilder.withParentTriggerGroupName(value));
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
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.ACCESS;
    }

}
