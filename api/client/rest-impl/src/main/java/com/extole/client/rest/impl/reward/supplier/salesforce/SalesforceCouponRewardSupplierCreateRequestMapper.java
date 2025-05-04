package com.extole.client.rest.impl.reward.supplier.salesforce;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.reward.supplier.BaseRewardSupplierCreateRequestMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.CustomRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.PayPalPayoutsRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierCreateRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.salesforce.SalesforceCouponRewardSupplierCreateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplier;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsDisabledException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierBalanceRefillAmountMissingException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierCouponPoolIdAlreadyInUseException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierCouponPoolIdNotFoundException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierInvalidCouponPoolIdException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierInvalidInitialOffsetException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierSettingsIdMissingException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponValidationException;
import com.extole.salesforce.api.salesforce.SalesforceAuthenticationException;
import com.extole.salesforce.api.salesforce.SalesforceAuthorizationException;
import com.extole.salesforce.api.salesforce.SalesforceCouponPoolIdInvalidUrlPathSegmentException;
import com.extole.salesforce.api.salesforce.SalesforceServiceUnavailableException;

@Component
public class SalesforceCouponRewardSupplierCreateRequestMapper
    extends BaseRewardSupplierCreateRequestMapper<SalesforceCouponRewardSupplierCreateRequest,
        SalesforceCouponRewardSupplier, SalesforceCouponRewardSupplierBuilder> {

    private final RewardSupplierService rewardSupplierService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final boolean cashBackEnabled;

    public SalesforceCouponRewardSupplierCreateRequestMapper(ComponentService componentService,
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
    public SalesforceCouponRewardSupplierBuilder initialize(Authorization authorization,
        SalesforceCouponRewardSupplierCreateRequest createRequest)
        throws RewardSupplierCreationRestException, AuthorizationException, BuildRewardSupplierRestException {

        SalesforceCouponRewardSupplierBuilder builder = rewardSupplierService.create(authorization,
            com.extole.model.entity.reward.supplier.RewardSupplierType.valueOf(getType().name()),
            rewardSupplierRestMapper.toFaceValueType(createRequest.getFaceValueType()));
        builder.withCashBackEnabled(cashBackEnabled);
        return builder;
    }

    @Override
    protected SalesforceCouponRewardSupplier complete(Authorization authorization,
        RewardSupplierBuilder<SalesforceCouponRewardSupplier, ?> supplierBuilder,
        SalesforceCouponRewardSupplierCreateRequest createRequest, ComponentReferenceContext componentReferenceContext)
        throws BuildRewardSupplierException, CustomRewardSupplierRestException, AuthorizationException,
        InvalidComponentReferenceException, RewardSupplierValidationException, BuildRewardSupplierRestException,
        SalesforceCouponRewardSupplierValidationRestException, SalesforceCouponRewardSupplierCreateRestException,
        UserAuthorizationRestException, ClientSalesforceSettingsRestException, SalesforceConnectionRestException,
        TangoRewardSupplierValidationRestException, PayPalPayoutsRewardSupplierRestException,
        RewardSupplierCreationRestException, MoreThanOneComponentReferenceException {
        SalesforceCouponRewardSupplierBuilder builder = (SalesforceCouponRewardSupplierBuilder) supplierBuilder;

        try {
            createRequest.getFaceValue()
                .ifPresent(faceValue -> builder.withFaceValue(faceValue));
            if (createRequest.getCouponPoolId().isPresent()) {
                builder.withCouponPoolId(createRequest.getCouponPoolId().getValue());
            }
            createRequest.getSettingsId().ifPresent(settingId -> {
                if (!StringUtils.isBlank(settingId)) {
                    builder.withSettingsId(Id.valueOf(settingId));
                }
            });
            createRequest.getBalanceRefillAmount()
                .ifPresent(balanceRefillAmount -> builder.withBalanceRefillAmount(balanceRefillAmount));
            createRequest.getInitialOffset()
                .ifPresent(initialOffset -> builder.withInitialOffset(initialOffset));
        } catch (SalesforceCouponPoolIdInvalidUrlPathSegmentException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.INVALID_COUPON_POOL_ID)
                .addParameter("coupon_pool_id", createRequest.getCouponPoolId())
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierValidationRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierValidationRestException.INVALID_BALANCE_REFILL_AMOUNT)
                .addParameter("balance_refill_amount", createRequest.getBalanceRefillAmount())
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierInvalidInitialOffsetException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.INVALID_INITIAL_OFFSET)
                .addParameter("initial_offset", createRequest.getInitialOffset())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsRestException.class)
                .withErrorCode(ClientSalesforceSettingsRestException.SALESFORCE_SETTINGS_NOT_FOUND)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e).build();
        } catch (SalesforceAuthenticationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceAuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }

        try {
            return super.complete(authorization, builder, createRequest, componentReferenceContext);
        } catch (SalesforceCouponRewardSupplierCouponPoolIdAlreadyInUseException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.COUPON_POOL_ID_ALREADY_IN_USE)
                .addParameter("coupon_pool_id", createRequest.getCouponPoolId())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsDisabledException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SETTINGS_DISABLED)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierBalanceRefillAmountMissingException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.BALANCE_REFILL_AMOUNT_MISSING)
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierSettingsIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.SETTINGS_ID_MISSING).build();
        } catch (SalesforceCouponRewardSupplierInvalidCouponPoolIdException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.INVALID_COUPON_POOL_ID)
                .addParameter("coupon_pool_id", createRequest.getCouponPoolId())
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierCouponPoolIdNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.COUPON_POOL_ID_NOT_FOUND)
                .addParameter("coupon_pool_id", createRequest.getCouponPoolId()).build();
        } catch (SalesforceCouponValidationException e) {
            if (e.getCause() != null) {
                if (e.getCause() instanceof SalesforceCouponPoolIdInvalidUrlPathSegmentException ||
                    e.getCause() instanceof SalesforceCouponRewardSupplierInvalidCouponPoolIdException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                        .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.INVALID_COUPON_POOL_ID)
                        .addParameter("coupon_pool_id", createRequest.getCouponPoolId())
                        .withCause(e.getCause()).build();
                }

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
