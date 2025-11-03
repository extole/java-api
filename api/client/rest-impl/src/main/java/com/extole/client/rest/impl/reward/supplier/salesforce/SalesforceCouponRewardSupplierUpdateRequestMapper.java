package com.extole.client.rest.impl.reward.supplier.salesforce;

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
import com.extole.client.rest.reward.supplier.salesforce.SalesforceCouponRewardSupplierUpdateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplier;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsDisabledException;
import com.extole.model.service.reward.supplier.RewardSupplierBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponValidationException;
import com.extole.salesforce.api.salesforce.SalesforceAuthenticationException;
import com.extole.salesforce.api.salesforce.SalesforceAuthorizationException;
import com.extole.salesforce.api.salesforce.SalesforceServiceUnavailableException;

@Component
public class SalesforceCouponRewardSupplierUpdateRequestMapper
    extends
    BaseRewardSupplierUpdateRequestMapper<SalesforceCouponRewardSupplierUpdateRequest, SalesforceCouponRewardSupplier,
        SalesforceCouponRewardSupplierBuilder> {

    private final RewardSupplierService rewardSupplierService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final boolean cashBackEnabled;

    public SalesforceCouponRewardSupplierUpdateRequestMapper(ComponentService componentService,
        RewardSupplierService rewardSupplierService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper,
        @Value("${reward.supplier.salesforce.coupon.cash.back.enabled:true}") boolean cashBackEnabled) {
        super(componentService, rewardSupplierRestMapper, componentReferenceRequestMapper);
        this.rewardSupplierService = rewardSupplierService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
        this.cashBackEnabled = cashBackEnabled;
    }

    @Override
    public SalesforceCouponRewardSupplierBuilder initialize(Authorization authorization, String rewardSupplierId,
        SalesforceCouponRewardSupplierUpdateRequest updateRequest)
        throws AuthorizationException, RewardSupplierNotFoundException {
        RewardSupplier rewardSupplier =
            rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId));
        SalesforceCouponRewardSupplierBuilder builder = rewardSupplierService.update(authorization, rewardSupplier);
        builder.withCashBackEnabled(cashBackEnabled);
        return builder;
    }

    @Override
    protected SalesforceCouponRewardSupplier complete(Authorization authorization,
        RewardSupplierBuilder<SalesforceCouponRewardSupplier, ?> supplierBuilder,
        SalesforceCouponRewardSupplierUpdateRequest updateRequest, ComponentReferenceContext componentReferenceContext)
        throws BuildRewardSupplierException, CustomRewardSupplierRestException, AuthorizationException,
        InvalidComponentReferenceException, RewardSupplierValidationException, BuildRewardSupplierRestException,
        SalesforceCouponRewardSupplierValidationRestException, SalesforceCouponRewardSupplierCreateRestException,
        UserAuthorizationRestException, ClientSalesforceSettingsRestException, SalesforceConnectionRestException,
        TangoRewardSupplierValidationRestException, PayPalPayoutsRewardSupplierRestException,
        RewardSupplierCreationRestException, MoreThanOneComponentReferenceException {
        SalesforceCouponRewardSupplierBuilder builder = (SalesforceCouponRewardSupplierBuilder) supplierBuilder;

        try {
            updateRequest.getFaceValue()
                .ifPresent(faceValue -> builder.withFaceValue(faceValue));
            updateRequest.getFaceValueType()
                .ifPresent(faceValueType -> builder
                    .withFaceValueType(rewardSupplierRestMapper.toFaceValueType(faceValueType)));
            updateRequest.getBalanceRefillAmount()
                .ifPresent(balanceRefillAmount -> builder.withBalanceRefillAmount(balanceRefillAmount));

        } catch (SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierValidationRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierValidationRestException.INVALID_BALANCE_REFILL_AMOUNT)
                .addParameter("balance_refill_amount", updateRequest.getBalanceRefillAmount())
                .withCause(e).build();
        }

        try {
            return super.complete(authorization, builder, updateRequest, componentReferenceContext);
        } catch (ClientSalesforceSettingsDisabledException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SETTINGS_DISABLED)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceCouponValidationException e) {
            if (e.getCause() != null) {
                if (e.getCause() instanceof SalesforceServiceUnavailableException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e.getCause()).build();
                }

                if (e.getCause() instanceof SalesforceAuthenticationException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e.getCause()).build();
                }

                if (e.getCause() instanceof SalesforceAuthorizationException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e)
                        .build();
                }
            }
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public RewardSupplierType getType() {
        return RewardSupplierType.SALESFORCE_COUPON;
    }
}
