package com.extole.client.rest.impl.reward.supplier.paypal;

import static com.extole.model.service.reward.supplier.RewardSupplierService.XOOM_CLIENT_ID;
import static com.extole.model.service.reward.supplier.RewardSupplierService.XOOM_SANDBOX_CLIENT_ID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.campaign.BuildRewardSupplierExceptionMapper;
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
import com.extole.client.rest.reward.supplier.paypal.PayPalPayoutsRewardSupplierCreateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.paypal.payouts.PayPalPayoutsRewardSupplier;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.reward.supplier.RewardSupplierBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.built.paypal.payouts.RewardSupplierUnsupportedFaceValueException;
import com.extole.model.service.reward.supplier.built.paypal.payouts.UnsupportedDecimalFaceValueTypeException;
import com.extole.model.service.reward.supplier.built.paypal.payouts.UnsupportedFaceValueTypeException;
import com.extole.model.service.reward.supplier.paypal.payouts.PayPalPayoutsRewardSupplierBuilder;

@Component
public class PayPalPayoutsRewardSupplierCreateRequestMapper extends BaseRewardSupplierCreateRequestMapper<
    PayPalPayoutsRewardSupplierCreateRequest, PayPalPayoutsRewardSupplier, PayPalPayoutsRewardSupplierBuilder> {

    private final RewardSupplierService rewardSupplierService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;

    public PayPalPayoutsRewardSupplierCreateRequestMapper(ComponentService componentService,
        RewardSupplierService rewardSupplierService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(componentService, rewardSupplierRestMapper, componentReferenceRequestMapper);
        this.rewardSupplierService = rewardSupplierService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
    }

    @Override
    public PayPalPayoutsRewardSupplierBuilder initialize(Authorization authorization,
        PayPalPayoutsRewardSupplierCreateRequest createRequest)
        throws RewardSupplierCreationRestException, AuthorizationException, BuildRewardSupplierRestException {
        checkCreationAccessRights(authorization);

        if (StringUtils.isBlank(createRequest.getMerchantToken())) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.MERCHANT_TOKEN_MISSING)
                .build();
        }
        if (!createRequest.getName().isPresent()) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.NAME_MISSING)
                .build();
        }
        if (!createRequest.getFaceValue().isPresent()) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.FACE_VALUE_MISSING)
                .build();
        }

        PayPalPayoutsRewardSupplierBuilder builder = rewardSupplierService.create(authorization,
            com.extole.model.entity.reward.supplier.RewardSupplierType.valueOf(getType().name()),
            rewardSupplierRestMapper.toFaceValueType(createRequest.getFaceValueType()));
        builder.withMerchantToken(createRequest.getMerchantToken());
        return builder;
    }

    @Override
    protected PayPalPayoutsRewardSupplier complete(Authorization authorization,
        RewardSupplierBuilder<PayPalPayoutsRewardSupplier, ?> supplierBuilder,
        PayPalPayoutsRewardSupplierCreateRequest createRequest, ComponentReferenceContext componentReferenceContext)
        throws BuildRewardSupplierException, CustomRewardSupplierRestException, AuthorizationException,
        InvalidComponentReferenceException, RewardSupplierValidationException, BuildRewardSupplierRestException,
        UserAuthorizationRestException, SalesforceCouponRewardSupplierCreateRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        SalesforceConnectionRestException, TangoRewardSupplierValidationRestException,
        PayPalPayoutsRewardSupplierRestException, RewardSupplierCreationRestException,
        MoreThanOneComponentReferenceException {

        createRequest.getFaceValue()
            .ifPresent(faceValue -> supplierBuilder.withFaceValue(faceValue));

        try {
            return super.complete(authorization, supplierBuilder, createRequest, componentReferenceContext);
        } catch (UnsupportedFaceValueTypeException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.UNSUPPORTED_FACE_VALUE_TYPE)
                .addParameter("face_value_type", e.getFaceValueType())
                .addParameter("supported_face_value_types", e.getSupportedFaceValueTypes())
                .build();
        } catch (RewardSupplierUnsupportedFaceValueException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.ZERO_FACE_VALUE)
                .withCause(e)
                .build();
        } catch (UnsupportedDecimalFaceValueTypeException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.UNSUPPORTED_DECIMAL_FACE_VALUE_TYPE)
                .addParameter("face_value_type", e.getFaceValueType())
                .addParameter("supported_decimal_face_value_types", e.getSupportedDecimalFaceValueTypes())
                .build();
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        }
    }

    private void checkCreationAccessRights(Authorization authorization) throws AuthorizationException {
        if (!XOOM_CLIENT_ID.equals(authorization.getClientId()) &&
            !XOOM_SANDBOX_CLIENT_ID.equals(authorization.getClientId())) {
            if (!authorization.isAuthorized(authorization.getClientId(), Authorization.Scope.CLIENT_SUPERUSER)) {
                throw new AuthorizationException("Access denied");
            }
        }
    }

    @Override
    public RewardSupplierType getType() {
        return RewardSupplierType.PAYPAL_PAYOUTS;
    }
}
