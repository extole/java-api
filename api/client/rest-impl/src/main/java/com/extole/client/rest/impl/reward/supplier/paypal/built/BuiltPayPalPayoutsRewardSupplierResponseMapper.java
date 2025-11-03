package com.extole.client.rest.impl.reward.supplier.paypal.built;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.reward.supplier.built.BuiltRewardSupplierResponseMapper;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.paypal.built.BuiltPayPalPayoutsRewardSupplierResponse;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.built.paypal.payouts.BuiltPayPalPayoutsRewardSupplier;

@Component
public class BuiltPayPalPayoutsRewardSupplierResponseMapper
    implements
    BuiltRewardSupplierResponseMapper<BuiltPayPalPayoutsRewardSupplier, BuiltPayPalPayoutsRewardSupplierResponse> {

    @Override
    public BuiltPayPalPayoutsRewardSupplierResponse toResponse(Authorization authorization,
        BuiltPayPalPayoutsRewardSupplier rewardSupplier, ZoneId timeZone) {
        return new BuiltPayPalPayoutsRewardSupplierResponse(rewardSupplier.getId().getValue(),
            rewardSupplier.getMerchantToken(),
            rewardSupplier.getPartnerRewardSupplierId(),
            PartnerRewardKeyType.valueOf(rewardSupplier.getPartnerRewardKeyType().name()),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType
                .valueOf(rewardSupplier.getFaceValueAlgorithmType().name()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            com.extole.client.rest.reward.supplier.FaceValueType
                .valueOf(rewardSupplier.getFaceValueType().name()),
            rewardSupplier.getDescription(),
            rewardSupplier.getCreatedAt().atZone(timeZone),
            rewardSupplier.getUpdatedAt().atZone(timeZone),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.isEnabled(),
            mapStateTransitions(rewardSupplier));
    }

    private static Map<RewardState, List<RewardState>>
        mapStateTransitions(BuiltPayPalPayoutsRewardSupplier rewardSupplier) {
        return rewardSupplier.getStateTransitions()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> RewardState.valueOf(entry.getKey()
                .name()), entry -> entry.getValue()
                    .stream()
                    .map(item -> RewardState.valueOf(item.name()))
                    .collect(Collectors.toList())));
    }

    @Override
    public RewardSupplierType getType() {
        return RewardSupplierType.PAYPAL_PAYOUTS;
    }
}
