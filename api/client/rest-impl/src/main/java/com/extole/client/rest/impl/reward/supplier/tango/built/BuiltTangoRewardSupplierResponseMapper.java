package com.extole.client.rest.impl.reward.supplier.tango.built;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierMetaData;
import com.extole.client.rest.impl.reward.supplier.built.BuiltRewardSupplierResponseMapper;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.tango.built.BuiltTangoRewardSupplierResponse;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.built.tango.BuiltTangoRewardSupplier;
import com.extole.model.service.reward.supplier.tango.TangoBrand;
import com.extole.model.service.reward.supplier.tango.TangoBrandItem;
import com.extole.model.service.tango.TangoServiceUnavailableException;
import com.extole.model.shared.reward.supplier.tango.TangoCatalogCache;

@Component
public class BuiltTangoRewardSupplierResponseMapper
    implements BuiltRewardSupplierResponseMapper<BuiltTangoRewardSupplier, BuiltTangoRewardSupplierResponse> {

    private final TangoCatalogCache tangoCatalogCache;

    public BuiltTangoRewardSupplierResponseMapper(TangoCatalogCache tangoCatalogCache) {
        this.tangoCatalogCache = tangoCatalogCache;
    }

    @Override
    public BuiltTangoRewardSupplierResponse toResponse(Authorization authorization,
        BuiltTangoRewardSupplier rewardSupplier, ZoneId timeZone) {
        RewardSupplierMetaData metaData = getMetaData(authorization, rewardSupplier.getUtid());
        return new BuiltTangoRewardSupplierResponse(
            rewardSupplier.getId().toString(),
            rewardSupplier.getPartnerRewardSupplierId(),
            PartnerRewardKeyType.valueOf(rewardSupplier.getPartnerRewardKeyType().name()),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getUtid(),
            rewardSupplier.getAccountId().toString(),
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
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            metaData.getBrandName(),
            metaData.getBrandDescription(),
            metaData.getBrandDisclaimer(),
            metaData.getBrandImageUrl(),
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
        mapStateTransitions(BuiltTangoRewardSupplier rewardSupplier) {
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
        return RewardSupplierType.TANGO_V2;
    }

    private RewardSupplierMetaData getMetaData(Authorization authorization,
        String utid) {
        try {
            for (TangoBrand brand : tangoCatalogCache.getCatalog(authorization)) {
                for (TangoBrandItem item : brand.getItems()) {
                    if (item.getUtid().equals(utid)) {
                        return RewardSupplierMetaData.create(brand, item);
                    }
                }
            }
        } catch (TangoServiceUnavailableException | AuthorizationException e) {
            return RewardSupplierMetaData.empty();
        }
        return RewardSupplierMetaData.empty();
    }

}
