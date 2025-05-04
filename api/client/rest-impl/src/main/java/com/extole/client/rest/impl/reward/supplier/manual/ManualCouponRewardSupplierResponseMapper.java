package com.extole.client.rest.impl.reward.supplier.manual;

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
import com.extole.client.rest.reward.supplier.manual.ManualCouponRewardSupplierResponse;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.manual.coupon.ManualCouponRewardSupplier;

@Component
public class ManualCouponRewardSupplierResponseMapper
    implements RewardSupplierResponseMapper<ManualCouponRewardSupplier, ManualCouponRewardSupplierResponse> {

    private final RewardSupplierRestMapper rewardSupplierRestMapper;

    @Autowired
    public ManualCouponRewardSupplierResponseMapper(RewardSupplierRestMapper rewardSupplierRestMapper) {
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
    }

    @Override
    public ManualCouponRewardSupplierResponse toResponse(Authorization authorization,
        ManualCouponRewardSupplier rewardSupplier, ZoneId timeZone) {
        return new ManualCouponRewardSupplierResponse(rewardSupplier.getId().getValue(),
            rewardSupplier.getPartnerRewardSupplierId(),
            PartnerRewardKeyType.valueOf(rewardSupplier.getPartnerRewardKeyType().name()),
            rewardSupplier.getDisplayType(),
            rewardSupplierRestMapper.toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
            rewardSupplierRestMapper.toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
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
        return RewardSupplierType.MANUAL_COUPON;
    }
}
