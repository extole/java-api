package com.extole.client.rest.impl.reward.supplier.custom;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.extole.client.rest.reward.supplier.custom.CustomRewardSupplierCreateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.RewardState;
import com.extole.model.entity.reward.supplier.custom.reward.CustomRewardSupplier;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.reward.supplier.RewardSupplierBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.custom.reward.CustomRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.custom.reward.NegativeMissingFulfillmentAlertDelayException;
import com.extole.model.service.reward.supplier.custom.reward.NegativeMissingFulfillmentAutoFailDelayException;

@Component
public class CustomRewardSupplierCreateRequestMapper extends
    BaseRewardSupplierCreateRequestMapper<CustomRewardSupplierCreateRequest, CustomRewardSupplier,
        CustomRewardSupplierBuilder> {

    private final RewardSupplierService rewardSupplierService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;

    public CustomRewardSupplierCreateRequestMapper(ComponentService componentService,
        RewardSupplierService rewardSupplierService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(componentService, rewardSupplierRestMapper, componentReferenceRequestMapper);
        this.rewardSupplierService = rewardSupplierService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
    }

    @Override
    public CustomRewardSupplierBuilder initialize(Authorization authorization,
        CustomRewardSupplierCreateRequest createRequest)
        throws CustomRewardSupplierRestException, AuthorizationException {
        if (createRequest.getType() == null) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.CUSTOM_REWARD_TYPE_MISSING)
                .build();
        }
        CustomRewardSupplierBuilder builder = rewardSupplierService.create(authorization,
            com.extole.model.entity.reward.supplier.RewardSupplierType.valueOf(getType().name()),
            rewardSupplierRestMapper.toFaceValueType(createRequest.getFaceValueType()));
        builder.withType(com.extole.model.entity.reward.supplier.custom.reward.CustomRewardType.valueOf(
            createRequest.getType().name()));
        return builder;
    }

    @Override
    protected CustomRewardSupplier complete(Authorization authorization,
        RewardSupplierBuilder<CustomRewardSupplier, ?> supplierBuilder,
        CustomRewardSupplierCreateRequest createRequest, ComponentReferenceContext componentReferenceContext)
        throws InvalidComponentReferenceException, CustomRewardSupplierRestException, BuildRewardSupplierException,
        AuthorizationException, RewardSupplierValidationException, BuildRewardSupplierRestException,
        UserAuthorizationRestException, SalesforceCouponRewardSupplierCreateRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        SalesforceConnectionRestException, TangoRewardSupplierValidationRestException,
        PayPalPayoutsRewardSupplierRestException, RewardSupplierCreationRestException,
        MoreThanOneComponentReferenceException {
        CustomRewardSupplierBuilder builder = (CustomRewardSupplierBuilder) supplierBuilder;
        try {
            createRequest.getFaceValue()
                .ifPresent(faceValue -> builder.withFaceValue(faceValue));
            createRequest.isAutoSendRewardEmailEnabled()
                .ifPresent(isAutoSendRewardEmailEnabled -> builder.withRewardEmailAutoSendEnabled(
                    isAutoSendRewardEmailEnabled.booleanValue()));
            createRequest.isAutoFulfillmentEnabled()
                .ifPresent(isAutoFulfillmentEnabled -> builder
                    .withAutoFulfillmentEnabled(isAutoFulfillmentEnabled.booleanValue()));
            createRequest.isMissingFulfillmentAlertEnabled()
                .ifPresent(isMissingFulfillmentAlertEnabled -> builder.withMissingFulfillmentAlertEnabled(
                    isMissingFulfillmentAlertEnabled.booleanValue()));
            createRequest.getMissingFulfillmentAlertDelayMs()
                .ifPresent(missingFulfillmentAlertDelayMs -> builder.withMissingFulfillmentAlertDelayMs(
                    missingFulfillmentAlertDelayMs));
            createRequest.isMissingFulfillmentAutoFailEnabled()
                .ifPresent(isMissingFulfillmentAutoFailEnabled -> builder.withMissingFulfillmentAutoFailEnabled(
                    isMissingFulfillmentAutoFailEnabled.booleanValue()));
            createRequest.getMissingFulfillmentAutoFailDelayMs()
                .ifPresent(FulfillmentAutoFailDelayMs -> builder.withMissingFulfillmentAutoFailDelayMs(
                    FulfillmentAutoFailDelayMs));

            createRequest.getStateTransitions()
                .ifPresent(stateTransitions -> builder
                    .withStateTransitions(mapStateTransitions(stateTransitions)));
        } catch (NegativeMissingFulfillmentAlertDelayException e) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.NEGATIVE_ALERT_DELAY)
                .addParameter("missing_fulfillment_alert_delay_ms", e.getMissingFulfillmentAlertDelayMs())
                .withCause(e).build();
        } catch (NegativeMissingFulfillmentAutoFailDelayException e) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.NEGATIVE_AUTO_FAIL_DELAY)
                .addParameter("missing_fulfillment_auto_fail_delay_ms", e.getMissingFulfillmentAutoFailDelayMs())
                .withCause(e).build();
        }

        return super.complete(authorization, builder, createRequest, componentReferenceContext);
    }

    private static Map<RewardState, List<RewardState>>
        mapStateTransitions(
            Map<com.extole.client.rest.reward.supplier.RewardState,
                List<com.extole.client.rest.reward.supplier.RewardState>> stateTransitions) {
        return stateTransitions
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(entry -> com.extole.model.entity.reward.supplier.RewardState.valueOf(entry.getKey()
                    .name()), entry -> entry.getValue()
                        .stream()
                        .map(item -> com.extole.model.entity.reward.supplier.RewardState.valueOf(item.name()))
                        .collect(Collectors.toList())));
    }

    @Override
    public RewardSupplierType getType() {
        return RewardSupplierType.CUSTOM_REWARD;
    }
}
