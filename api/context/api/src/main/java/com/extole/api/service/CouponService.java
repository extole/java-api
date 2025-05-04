package com.extole.api.service;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface CouponService {

    @Nullable
    String extoleIssued(String coupons);

}
