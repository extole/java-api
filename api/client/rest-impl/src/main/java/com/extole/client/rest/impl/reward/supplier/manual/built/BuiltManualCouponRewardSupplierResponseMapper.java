package com.extole.client.rest.impl.reward.supplier.manual.built;

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
import com.extole.client.rest.reward.supplier.manual.built.BuiltManualCouponRewardSupplierResponse;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.built.manual.coupon.BuiltManualCouponRewardSupplier;

@Component
public class BuiltManualCouponRewardSupplierResponseMapper
    implements
    BuiltRewardSupplierResponseMapper<BuiltManualCouponRewardSupplier, BuiltManualCouponRewardSupplierResponse> {

    @Override
    public BuiltManualCouponRewardSupplierResponse toResponse(Authorization authorization,
        BuiltManualCouponRewardSupplier rewardSupplier, ZoneId timeZone) {
        return new BuiltManualCouponRewardSupplierResponse(rewardSupplier.getId().getValue(),
            rewardSupplier.getPartnerRewardSupplierId(),
            PartnerRewardKeyType.valueOf(rewardSupplier.getPartnerRewardKeyType().name()),
            rewardSupplier.getDisplayType(),
            com.extole.client.rest.reward.supplier.FaceValueType
                .valueOf(rewardSupplier.getFaceValueType().name()),
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType
                .valueOf(rewardSupplier.getFaceValueAlgorithmType().name()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplier.getName(),
            rewardSupplier.getCouponCountWarnLimit(),
            rewardSupplier.getCreatedAt().atZone(timeZone),
            rewardSupplier.getUpdatedAt().atZone(timeZone),
            rewardSupplier.getMinimumCouponLifetime(),
            rewardSupplier.getDefaultCouponExpiryDate().map(expiryDate -> expiryDate.atZone(timeZone)).orElse(null),
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
        mapStateTransitions(BuiltManualCouponRewardSupplier rewardSupplier) {
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
        return RewardSupplierType.MANUAL_COUPON;
    }
}
