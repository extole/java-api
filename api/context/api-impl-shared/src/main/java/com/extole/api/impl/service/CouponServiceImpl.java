package com.extole.api.impl.service;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Strings;

import com.extole.api.service.CouponService;
import com.extole.api.service.StringService;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.person.service.reward.PersonRewardService;

public class CouponServiceImpl implements CouponService {
    private static final StringService STRING_SERVICE = new StringServiceImpl();

    private final Id<ClientHandle> clientId;
    private final PersonRewardService personRewardService;

    public CouponServiceImpl(Id<ClientHandle> clientId, PersonRewardService personRewardService) {
        this.clientId = clientId;
        this.personRewardService = personRewardService;
    }

    @Nullable
    @Override
    public String extoleIssued(String coupons) {
        return Strings.emptyToNull(Arrays.stream(STRING_SERVICE.split(coupons))
            .filter(couponCode -> personRewardService.findByPartnerRewardId(clientId, couponCode).isPresent())
            .collect(Collectors.joining(",")));
    }
}
