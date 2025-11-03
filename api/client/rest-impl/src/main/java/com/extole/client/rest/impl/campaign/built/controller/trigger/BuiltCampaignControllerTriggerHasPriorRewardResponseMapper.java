package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerHasPriorRewardResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.RewardFilterScope;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.RewardState;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerHasPriorReward;

@Component
public class BuiltCampaignControllerTriggerHasPriorRewardResponseMapper implements
    BuiltCampaignControllerTriggerResponseMapper<BuiltCampaignControllerTriggerHasPriorReward,
        BuiltCampaignControllerTriggerHasPriorRewardResponse> {

    @Override
    public BuiltCampaignControllerTriggerHasPriorRewardResponse toResponse(
        BuiltCampaignControllerTriggerHasPriorReward trigger,
        ZoneId timeZone) {
        return new BuiltCampaignControllerTriggerHasPriorRewardResponse(trigger.getId().getValue(),
            CampaignControllerTriggerPhase.valueOf(trigger.getPhase().name()),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getFilterNames(),
            RewardFilterScope.valueOf(trigger.getFilterScope().name()),
            trigger.getFilterTags(),
            trigger.getFilterMinAge(),
            trigger.getFilterMaxAge(),
            trigger.getFilterMinDate().map(zonedDateTime -> zonedDateTime.withZoneSameInstant(timeZone)),
            trigger.getFilterMaxDate().map(zonedDateTime -> zonedDateTime.withZoneSameInstant(timeZone)),
            trigger.getFilterRewardSupplierIds().stream().map(Id::getValue).collect(Collectors.toSet()),
            trigger.getFilterFaceValueTypes().stream().map(item -> FaceValueType.valueOf(item.name()))
                .collect(Collectors.toSet()),
            trigger.getFilterStates().stream().map(item -> RewardState.valueOf(item.name()))
                .collect(Collectors.toSet()),
            trigger.getFilterExpressions(),
            trigger.getSumOfFaceValueMax().orElse(null),
            trigger.getSumOfFaceValueMin().orElse(null),
            trigger.getCountMax().orElse(null),
            trigger.getCountMin().orElse(null),
            trigger.getCountMatches(),
            trigger.getTaxYearStart(),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.HAS_PRIOR_REWARD;
    }

}
