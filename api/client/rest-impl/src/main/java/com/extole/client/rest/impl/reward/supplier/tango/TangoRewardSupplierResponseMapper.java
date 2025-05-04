package com.extole.client.rest.impl.reward.supplier.tango;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierMetaData;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierResponseMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.tango.TangoRewardSupplierResponse;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.tango.TangoRewardSupplier;
import com.extole.model.service.reward.supplier.tango.TangoBrand;
import com.extole.model.service.reward.supplier.tango.TangoBrandItem;
import com.extole.model.service.tango.TangoServiceUnavailableException;
import com.extole.model.shared.reward.supplier.tango.TangoCatalogCache;

@Component
public class TangoRewardSupplierResponseMapper
    implements RewardSupplierResponseMapper<TangoRewardSupplier, TangoRewardSupplierResponse> {

    private final TangoCatalogCache tangoCatalogCache;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;

    @Autowired
    public TangoRewardSupplierResponseMapper(TangoCatalogCache tangoCatalogCache,
        RewardSupplierRestMapper rewardSupplierRestMapper) {
        this.tangoCatalogCache = tangoCatalogCache;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
    }

    @Override
    public TangoRewardSupplierResponse toResponse(Authorization authorization,
        TangoRewardSupplier rewardSupplier, ZoneId timeZone) {
        RewardSupplierMetaData metaData = getMetaData(authorization, rewardSupplier.getUtid());
        return new TangoRewardSupplierResponse(
            rewardSupplier.getId().toString(),
            rewardSupplier.getPartnerRewardSupplierId(),
            PartnerRewardKeyType.valueOf(rewardSupplier.getPartnerRewardKeyType().name()),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getUtid(),
            rewardSupplier.getAccountId().toString(),
            rewardSupplierRestMapper.toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplierRestMapper.toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
            rewardSupplier.getName(),
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
