package com.extole.client.rest.impl.reward.supplier.built;

import java.time.ZoneId;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.built.BuiltRewardSupplierResponse;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;

public interface BuiltRewardSupplierResponseMapper<SUPPLIER extends BuiltRewardSupplier, RESPONSE extends BuiltRewardSupplierResponse> {

    RESPONSE toResponse(Authorization authorization, SUPPLIER rewardSupplier, ZoneId timeZone);

    RewardSupplierType getType();

}
