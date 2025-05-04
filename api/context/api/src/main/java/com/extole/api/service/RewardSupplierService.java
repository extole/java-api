package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.reward.RewardSupplier;

@Schema
public interface RewardSupplierService {

    RewardSupplier getById(String rewardSupplierId) throws RewardSupplierNotFoundException;

    RewardSupplier[] findAll();
}
