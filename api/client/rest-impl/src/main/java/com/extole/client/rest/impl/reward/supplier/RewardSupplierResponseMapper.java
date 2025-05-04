package com.extole.client.rest.impl.reward.supplier;

import java.time.ZoneId;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.reward.supplier.RewardSupplierResponse;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.model.entity.reward.supplier.RewardSupplier;

public interface RewardSupplierResponseMapper<SUPPLIER extends RewardSupplier,
    RESPONSE extends RewardSupplierResponse> {

    RESPONSE toResponse(Authorization authorization, SUPPLIER rewardSupplier, ZoneId timeZone);

    RewardSupplierType getType();

}
