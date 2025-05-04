package com.extole.api.impl.service;

import com.extole.api.impl.reward.supplier.RewardSupplierImpl;
import com.extole.api.reward.RewardSupplier;
import com.extole.api.service.RewardSupplierNotFoundException;
import com.extole.api.service.RewardSupplierService;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.shared.reward.supplier.BuiltRewardSupplierCache;

public class RewardSupplierServiceImpl implements RewardSupplierService {

    private final Id<ClientHandle> clientId;
    private final BuiltRewardSupplierCache builtRewardSupplierCache;

    public RewardSupplierServiceImpl(Id<ClientHandle> clientId,
        BuiltRewardSupplierCache builtRewardSupplierCache) {
        this.clientId = clientId;
        this.builtRewardSupplierCache = builtRewardSupplierCache;
    }

    @Override
    public RewardSupplier getById(String rewardSupplierId) throws RewardSupplierNotFoundException {
        try {
            BuiltRewardSupplier rewardSupplier =
                builtRewardSupplierCache.getActiveRewardSupplier(clientId, Id.valueOf(rewardSupplierId));
            return mapRewardSupplier(rewardSupplier);
        } catch (com.extole.model.service.reward.supplier.RewardSupplierNotFoundException e) {
            throw new RewardSupplierNotFoundException("Reward supplier not found for id : " + rewardSupplierId, e);
        }
    }

    @Override
    public RewardSupplier[] findAll() {
        return builtRewardSupplierCache.getAll(clientId)
            .stream()
            .filter(rewardSupplier -> rewardSupplier.getArchivedAt().isEmpty())
            .map(rewardSupplier -> mapRewardSupplier(rewardSupplier))
            .toArray(RewardSupplier[]::new);
    }

    private static RewardSupplierImpl mapRewardSupplier(BuiltRewardSupplier rewardSupplier) {
        return new RewardSupplierImpl(
            rewardSupplier.getClientId().getValue(),
            rewardSupplier.getId().getValue(),
            rewardSupplier.getPartnerRewardSupplierId().orElse(null),
            rewardSupplier.getPartnerRewardKeyType().name(),
            rewardSupplier.getFaceValueType().name(),
            rewardSupplier.getFaceValue().toString(),
            rewardSupplier.getName(),
            rewardSupplier.getRewardSupplierType().name(),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getComponentReferences());
    }
}
