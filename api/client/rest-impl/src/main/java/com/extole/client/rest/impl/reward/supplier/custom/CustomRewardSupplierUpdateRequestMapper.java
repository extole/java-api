package com.extole.client.rest.impl.reward.supplier.custom;

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
import com.extole.client.rest.reward.supplier.custom.CustomRewardSupplierUpdateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.custom.reward.CustomRewardSupplier;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.reward.supplier.RewardSupplierBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.custom.reward.CustomRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.custom.reward.NegativeMissingFulfillmentAlertDelayException;
import com.extole.model.service.reward.supplier.custom.reward.NegativeMissingFulfillmentAutoFailDelayException;

@Component
public class CustomRewardSupplierUpdateRequestMapper extends BaseRewardSupplierUpdateRequestMapper<
    CustomRewardSupplierUpdateRequest, CustomRewardSupplier, CustomRewardSupplierBuilder> {

    private final RewardSupplierService rewardSupplierService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;

    public CustomRewardSupplierUpdateRequestMapper(ComponentService componentService,
        RewardSupplierService rewardSupplierService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(componentService, rewardSupplierRestMapper, componentReferenceRequestMapper);
        this.rewardSupplierService = rewardSupplierService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
    }

    @Override
    public CustomRewardSupplierBuilder initialize(Authorization authorization, String rewardSupplierId,
        CustomRewardSupplierUpdateRequest updateRequest)
        throws CustomRewardSupplierRestException, AuthorizationException, RewardSupplierNotFoundException {
        RewardSupplier rewardSupplier =
            rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId));
        return rewardSupplierService.update(authorization, rewardSupplier);
    }

    @Override
    protected CustomRewardSupplier complete(Authorization authorization,
        RewardSupplierBuilder<CustomRewardSupplier, ?> supplierBuilder, CustomRewardSupplierUpdateRequest updateRequest,
        ComponentReferenceContext componentReferenceContext)
        throws InvalidComponentReferenceException, CustomRewardSupplierRestException, BuildRewardSupplierException,
        AuthorizationException, RewardSupplierValidationException, BuildRewardSupplierRestException,
        UserAuthorizationRestException, SalesforceCouponRewardSupplierCreateRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        SalesforceConnectionRestException, TangoRewardSupplierValidationRestException,
        PayPalPayoutsRewardSupplierRestException, RewardSupplierCreationRestException,
        MoreThanOneComponentReferenceException {
        CustomRewardSupplierBuilder builder = (CustomRewardSupplierBuilder) supplierBuilder;
        try {
            updateRequest.getFaceValue()
                .ifPresent(faceValue -> builder.withFaceValue(faceValue));
            updateRequest.getFaceValueType()
                .ifPresent(faceValueType -> builder
                    .withFaceValueType(rewardSupplierRestMapper.toFaceValueType(faceValueType)));
            updateRequest.getType()
                .ifPresent(type -> builder.withType(
                    com.extole.model.entity.reward.supplier.custom.reward.CustomRewardType.valueOf(type.name())));
            updateRequest.isAutoSendRewardEmailEnabled()
                .ifPresent(isAutoSendRewardEmailEnabled -> builder.withRewardEmailAutoSendEnabled(
                    isAutoSendRewardEmailEnabled.booleanValue()));
            updateRequest.isAutoFulfillmentEnabled()
                .ifPresent(isAutoFulfillmentEnabled -> builder
                    .withAutoFulfillmentEnabled(isAutoFulfillmentEnabled.booleanValue()));
            updateRequest.isMissingFulfillmentAlertEnabled()
                .ifPresent(isMissingFulfillmentAlertEnabled -> builder.withMissingFulfillmentAlertEnabled(
                    isMissingFulfillmentAlertEnabled.booleanValue()));
            updateRequest.getMissingFulfillmentAlertDelayMs()
                .ifPresent(missingFulfillmentAlertDelayMs -> builder.withMissingFulfillmentAlertDelayMs(
                    missingFulfillmentAlertDelayMs));
            updateRequest.isMissingFulfillmentAutoFailEnabled()
                .ifPresent(isMissingFulfillmentAutoFailEnabled -> builder.withMissingFulfillmentAutoFailEnabled(
                    isMissingFulfillmentAutoFailEnabled.booleanValue()));
            updateRequest.getMissingFulfillmentAutoFailDelayMs()
                .ifPresent(FulfillmentAutoFailDelayMs -> builder.withMissingFulfillmentAutoFailDelayMs(
                    FulfillmentAutoFailDelayMs));
            updateRequest.getStateTransitions()
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

        return super.complete(authorization, builder, updateRequest, componentReferenceContext);
    }

    private static java.util.Map<com.extole.model.entity.reward.supplier.RewardState,
        java.util.List<com.extole.model.entity.reward.supplier.RewardState>>
        mapStateTransitions(java.util.Map<com.extole.client.rest.reward.supplier.RewardState,
            java.util.List<com.extole.client.rest.reward.supplier.RewardState>> stateTransitions) {
        return stateTransitions
            .entrySet()
            .stream()
            .collect(
                java.util.stream.Collectors
                    .toMap(entry -> com.extole.model.entity.reward.supplier.RewardState.valueOf(entry.getKey()
                        .name()), entry -> entry.getValue()
                            .stream()
                            .map(item -> com.extole.model.entity.reward.supplier.RewardState.valueOf(item.name()))
                            .collect(java.util.stream.Collectors.toList())));
    }

    @Override
    public RewardSupplierType getType() {
        return RewardSupplierType.CUSTOM_REWARD;
    }
}
