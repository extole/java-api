package com.extole.client.rest.impl.reward.supplier.paypal;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierResponseMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.paypal.PayPalPayoutsRewardSupplierResponse;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.paypal.payouts.PayPalPayoutsRewardSupplier;

@Component
public class PayPalPayoutsRewardSupplierResponseMapper
    implements RewardSupplierResponseMapper<PayPalPayoutsRewardSupplier, PayPalPayoutsRewardSupplierResponse> {

    private final RewardSupplierRestMapper rewardSupplierRestMapper;

    @Autowired
    public PayPalPayoutsRewardSupplierResponseMapper(RewardSupplierRestMapper rewardSupplierRestMapper) {
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
    }

    @Override
    public PayPalPayoutsRewardSupplierResponse toResponse(Authorization authorization,
        PayPalPayoutsRewardSupplier rewardSupplier, ZoneId timeZone) {
        return new PayPalPayoutsRewardSupplierResponse(rewardSupplier.getId().getValue(),
            rewardSupplier.getMerchantToken(),
            rewardSupplier.getPartnerRewardSupplierId(),
            PartnerRewardKeyType.valueOf(rewardSupplier.getPartnerRewardKeyType().name()),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            rewardSupplierRestMapper.toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplierRestMapper.toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
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
            rewardSupplier.getEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    private static Map<RewardState, List<RewardState>> mapStateTransitions(
        Map<com.extole.model.entity.reward.supplier.RewardState,
            List<com.extole.model.entity.reward.supplier.RewardState>> stateTransitions) {
        return stateTransitions
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
