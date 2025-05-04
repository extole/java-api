package com.extole.client.rest.impl.reward.supplier.manual;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.reward.supplier.BaseRewardSupplierUpdateRequestMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.CustomRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.PayPalPayoutsRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierCreateRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.manual.ManualCouponRewardSupplierUpdateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.manual.coupon.ManualCouponRewardSupplier;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.reward.supplier.RewardSupplierBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.coupon.CouponRewardSupplierIllegalValueInWarningAmountException;
import com.extole.model.service.reward.supplier.coupon.CouponRewardSupplierIllegalValueOfMinimumCouponLifetimeException;
import com.extole.model.service.reward.supplier.manual.coupon.ManualCouponRewardSupplierBuilder;

@Component
public class ManualCouponRewardSupplierUpdateRequestMapper extends BaseRewardSupplierUpdateRequestMapper<
    ManualCouponRewardSupplierUpdateRequest, ManualCouponRewardSupplier, ManualCouponRewardSupplierBuilder> {

    private final RewardSupplierService rewardSupplierService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final boolean cashBackEnabled;

    public ManualCouponRewardSupplierUpdateRequestMapper(ComponentService componentService,
        RewardSupplierService rewardSupplierService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper,
        @Value("${reward.supplier.manual.coupon.cash.back.enabled:true}") boolean cashBackEnabled) {
        super(componentService, rewardSupplierRestMapper, componentReferenceRequestMapper);
        this.rewardSupplierService = rewardSupplierService;
        this.cashBackEnabled = cashBackEnabled;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
    }

    @Override
    public ManualCouponRewardSupplierBuilder initialize(Authorization authorization, String rewardSupplierId,
        ManualCouponRewardSupplierUpdateRequest updateRequest)
        throws AuthorizationException, RewardSupplierNotFoundException {
        RewardSupplier rewardSupplier =
            rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId));
        ManualCouponRewardSupplierBuilder builder = rewardSupplierService.update(authorization, rewardSupplier);
        builder.withCashBackEnabled(cashBackEnabled);
        return builder;
    }

    @Override
    protected ManualCouponRewardSupplier complete(Authorization authorization,
        RewardSupplierBuilder<ManualCouponRewardSupplier, ?> supplierBuilder,
        ManualCouponRewardSupplierUpdateRequest updateRequest, ComponentReferenceContext componentReferenceContext)
        throws BuildRewardSupplierException, CustomRewardSupplierRestException, AuthorizationException,
        InvalidComponentReferenceException, RewardSupplierValidationException, BuildRewardSupplierRestException,
        UserAuthorizationRestException, SalesforceCouponRewardSupplierCreateRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        SalesforceConnectionRestException, TangoRewardSupplierValidationRestException,
        PayPalPayoutsRewardSupplierRestException, RewardSupplierCreationRestException,
        MoreThanOneComponentReferenceException {
        ManualCouponRewardSupplierBuilder builder = (ManualCouponRewardSupplierBuilder) supplierBuilder;

        try {
            updateRequest.getFaceValue()
                .ifPresent(faceValue -> builder.withFaceValue(faceValue));
            updateRequest.getFaceValueType()
                .ifPresent(faceValueType -> builder
                    .withFaceValueType(rewardSupplierRestMapper.toFaceValueType(faceValueType)));
            updateRequest.getCouponCountWarnLimit()
                .ifPresent(limit -> builder.withCouponCountWarnLimit(limit));
            updateRequest.getMinimumCouponLifetime()
                .ifPresent(minimumCouponLifetime -> builder.withMinimumCouponLifetime(minimumCouponLifetime));
            updateRequest.getDefaultCouponExpiryDate()
                .ifPresent(defaultCouponExpiryTime -> builder
                    .withDefaultCouponExpiryDate(defaultCouponExpiryTime.toInstant()));
        } catch (CouponRewardSupplierIllegalValueOfMinimumCouponLifetimeException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.ILLEGAL_VALUE_OF_MINIMUM_COUPON_LIFETIME)
                .addParameter("minimum_coupon_lifetime", updateRequest.getMinimumCouponLifetime())
                .withCause(e)
                .build();
        } catch (CouponRewardSupplierIllegalValueInWarningAmountException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_COUPON_COUNT_WARN_LIMIT)
                .addParameter("coupon_count_warn_limit", updateRequest.getCouponCountWarnLimit())
                .withCause(e)
                .build();
        }

        return super.complete(authorization, builder, updateRequest, componentReferenceContext);
    }

    @Override
    public RewardSupplierType getType() {
        return RewardSupplierType.MANUAL_COUPON;
    }
}
