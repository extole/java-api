package com.extole.client.rest.impl.reward.supplier;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.reward.supplier.CustomRewardType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.v2.CustomRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.PayPalPayoutsRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.RewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.TangoRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltCustomRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltManualCouponRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltPayPalPayoutsRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltSalesforceCouponRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltTangoRewardSupplierV2Response;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.FaceValueAlgorithmType;
import com.extole.model.entity.reward.supplier.FaceValueType;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.entity.reward.supplier.built.custom.reward.BuiltCustomRewardSupplier;
import com.extole.model.entity.reward.supplier.built.manual.coupon.BuiltManualCouponRewardSupplier;
import com.extole.model.entity.reward.supplier.built.paypal.payouts.BuiltPayPalPayoutsRewardSupplier;
import com.extole.model.entity.reward.supplier.built.salesforce.coupon.BuiltSalesforceCouponRewardSupplier;
import com.extole.model.entity.reward.supplier.built.tango.BuiltTangoRewardSupplier;
import com.extole.model.entity.reward.supplier.custom.reward.CustomRewardSupplier;
import com.extole.model.entity.reward.supplier.manual.coupon.ManualCouponRewardSupplier;
import com.extole.model.entity.reward.supplier.paypal.payouts.PayPalPayoutsRewardSupplier;
import com.extole.model.entity.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplier;
import com.extole.model.entity.reward.supplier.tango.TangoRewardSupplier;

@Component
public class RewardSupplierRestMapper {

    private final ObjectMapper objectMapper;

    public RewardSupplierRestMapper(@Qualifier("clientApiObjectMapper") Provider<ObjectMapper> objectMapperProvider) {
        this.objectMapper = objectMapperProvider.get();
    }

    public CustomRewardSupplierV2Response toCustomRewardSupplierResponse(
        CustomRewardSupplier rewardSupplier, ZoneId timezone) {
        return new CustomRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            CustomRewardType.valueOf(rewardSupplier.getType().name()),
            rewardSupplier.isRewardEmailAutoSendEnabled(),
            rewardSupplier.isAutoFulfillmentEnabled(),
            rewardSupplier.isMissingFulfillmentAlertEnabled(),
            rewardSupplier.getMissingFulfillmentAlertDelay().toMillis(),
            rewardSupplier.isMissingFulfillmentAutoFailEnabled(),
            rewardSupplier.getMissingFulfillmentAutoFailDelay().toMillis(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.getEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public BuiltCustomRewardSupplierV2Response toBuiltCustomRewardSupplierResponse(
        BuiltCustomRewardSupplier rewardSupplier, ZoneId timezone) {
        return new BuiltCustomRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            rewardSupplier.getName(),
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType
                .valueOf(rewardSupplier.getFaceValueAlgorithmType().name()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            com.extole.client.rest.reward.supplier.FaceValueType
                .valueOf(rewardSupplier.getFaceValueType().name()),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            CustomRewardType.valueOf(rewardSupplier.getType().name()),
            rewardSupplier.isRewardEmailAutoSendEnabled(),
            rewardSupplier.isAutoFulfillmentEnabled(),
            rewardSupplier.isMissingFulfillmentAlertEnabled(),
            rewardSupplier.getMissingFulfillmentAlertDelay().toMillis(),
            rewardSupplier.isMissingFulfillmentAutoFailEnabled(),
            rewardSupplier.getMissingFulfillmentAutoFailDelay().toMillis(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.isEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public ManualCouponRewardSupplierV2Response toManualCouponRewardSupplierResponse(
        ManualCouponRewardSupplier rewardSupplier, ZoneId timezone) {
        return new ManualCouponRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
            toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            rewardSupplier.getCouponCountWarnLimit(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getMinimumCouponLifetime(),
            rewardSupplier.getDefaultCouponExpiryDate().map(expiryDate -> expiryDate.atZone(timezone)).orElse(null),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.getEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public BuiltManualCouponRewardSupplierV2Response toBuiltManualCouponRewardSupplierResponse(
        BuiltManualCouponRewardSupplier rewardSupplier, ZoneId timezone) {
        return new BuiltManualCouponRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            com.extole.client.rest.reward.supplier.FaceValueType
                .valueOf(rewardSupplier.getFaceValueType().name()),
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType
                .valueOf(rewardSupplier.getFaceValueAlgorithmType().name()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplier.getName(),
            rewardSupplier.getCouponCountWarnLimit(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getMinimumCouponLifetime(),
            rewardSupplier.getDefaultCouponExpiryDate().map(expiryDate -> expiryDate.atZone(timezone)).orElse(null),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.isEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public PayPalPayoutsRewardSupplierV2Response toPayPalPayoutsRewardSupplierResponse(
        PayPalPayoutsRewardSupplier rewardSupplier, ZoneId timezone) {
        return new PayPalPayoutsRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            rewardSupplier.getMerchantToken(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
            rewardSupplier.getDescription(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.getEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public BuiltPayPalPayoutsRewardSupplierV2Response toBuiltPayPalPayoutsRewardSupplierResponse(
        BuiltPayPalPayoutsRewardSupplier rewardSupplier, ZoneId timezone) {
        return new BuiltPayPalPayoutsRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            rewardSupplier.getMerchantToken(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getName(),
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType
                .valueOf(rewardSupplier.getFaceValueAlgorithmType().name()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            com.extole.client.rest.reward.supplier.FaceValueType
                .valueOf(rewardSupplier.getFaceValueType().name()),
            rewardSupplier.getDescription(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.isEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public SalesforceCouponRewardSupplierV2Response toSalesforceCouponRewardSupplierResponse(
        SalesforceCouponRewardSupplier rewardSupplier, ZoneId timezone) {
        return new SalesforceCouponRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
            toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            rewardSupplier.getCouponPoolId(),
            rewardSupplier.getBalanceRefillAmount(),
            rewardSupplier.getInitialOffset(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getSettingsId().getValue(),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.getEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public BuiltSalesforceCouponRewardSupplierV2Response toBuiltSalesforceCouponRewardSupplierResponse(
        BuiltSalesforceCouponRewardSupplier rewardSupplier, ZoneId timezone) {
        return new BuiltSalesforceCouponRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            com.extole.client.rest.reward.supplier.FaceValueType
                .valueOf(rewardSupplier.getFaceValueType().name()),
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType
                .valueOf(rewardSupplier.getFaceValueAlgorithmType().name()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplier.getName(),
            rewardSupplier.getCouponPoolId(),
            rewardSupplier.getBalanceRefillAmount(),
            rewardSupplier.getInitialOffset(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getSettingsId().getValue(),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.isEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public TangoRewardSupplierV2Response toTangoRewardSupplierResponse(
        TangoRewardSupplier rewardSupplier, ZoneId timeZone, RewardSupplierMetaData metaData) {
        return new TangoRewardSupplierV2Response(
            rewardSupplier.getId().toString(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getUtid(),
            rewardSupplier.getAccountId().toString(),
            toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            metaData.getBrandName(),
            metaData.getBrandDescription(),
            metaData.getBrandDisclaimer(),
            metaData.getBrandImageUrl(),
            rewardSupplier.getDescription(),
            rewardSupplier.getCreatedAt().atZone(timeZone),
            rewardSupplier.getUpdatedAt().atZone(timeZone),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.getEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public BuiltTangoRewardSupplierV2Response toBuiltTangoRewardSupplierResponse(
        BuiltTangoRewardSupplier rewardSupplier, ZoneId timeZone,
        RewardSupplierMetaData metaData) {
        return new BuiltTangoRewardSupplierV2Response(
            rewardSupplier.getId().toString(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getUtid(),
            rewardSupplier.getAccountId().toString(),
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType
                .valueOf(rewardSupplier.getFaceValueAlgorithmType().name()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            com.extole.client.rest.reward.supplier.FaceValueType
                .valueOf(rewardSupplier.getFaceValueType().name()),
            rewardSupplier.getName(),
            metaData.getBrandName(),
            metaData.getBrandDescription(),
            metaData.getBrandDisclaimer(),
            metaData.getBrandImageUrl(),
            rewardSupplier.getDescription(),
            rewardSupplier.getCreatedAt().atZone(timeZone),
            rewardSupplier.getUpdatedAt().atZone(timeZone),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardSupplier.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.isEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    public RewardSupplierV2Response toRewardSupplierResponse(RewardSupplier rewardSupplier, ZoneId timezone) {
        return new RewardSupplierV2Response(rewardSupplier.getId().getValue(),
            RewardSupplierType.valueOf(rewardSupplier.getRewardSupplierType().name()),
            toFaceValueTypeResponse(rewardSupplier.getFaceValueType()),
            toFaceValueAlgorithmTypeResponse(rewardSupplier.getFaceValueAlgorithmType()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getName(),
            rewardSupplier.getDisplayName(),
            rewardSupplier.getDescription(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.getEnabled(),
            mapStateTransitions(rewardSupplier.getStateTransitions()));
    }

    private static Map<RewardState, List<RewardState>> mapStateTransitions(
        Map<com.extole.model.entity.reward.supplier.RewardState,
            List<com.extole.model.entity.reward.supplier.RewardState>> stateTransitions) {
        return stateTransitions
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> RewardState.valueOf(entry.getKey()
                .name()), entry -> entry.getValue()
                    .stream()
                    .map(item -> RewardState.valueOf(item.name()))
                    .collect(Collectors.toList())));
    }

    public BuiltRewardSupplierV2Response toBuiltRewardSupplierResponse(BuiltRewardSupplier rewardSupplier,
        ZoneId timezone) {
        return new BuiltRewardSupplierV2Response(rewardSupplier.getId().getValue(),
            RewardSupplierType.valueOf(rewardSupplier.getRewardSupplierType().name()),
            com.extole.client.rest.reward.supplier.FaceValueType
                .valueOf(rewardSupplier.getFaceValueType().name()),
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType
                .valueOf(rewardSupplier.getFaceValueAlgorithmType().name()),
            rewardSupplier.getFaceValue(),
            rewardSupplier.getCashBackPercentage(),
            rewardSupplier.getMinCashBack(),
            rewardSupplier.getMaxCashBack(),
            rewardSupplier.getLimitPerDay(),
            rewardSupplier.getLimitPerHour(),
            rewardSupplier.getPartnerRewardSupplierId(),
            rewardSupplier.getDisplayType(),
            rewardSupplier.getName(),
            rewardSupplier.getDescription(),
            rewardSupplier.getCreatedAt().atZone(timezone),
            rewardSupplier.getUpdatedAt().atZone(timezone),
            rewardSupplier.getTags(),
            rewardSupplier.getData(),
            rewardSupplier.isEnabled());
    }

    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> toFaceValueType(
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            com.extole.client.rest.reward.supplier.FaceValueType> evaluatable) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(evaluatable), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> toFaceValueAlgorithmType(
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType> evaluatable) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(evaluatable), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, com.extole.client.rest.reward.supplier.FaceValueType>
        toFaceValueTypeResponse(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> evaluatable) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(evaluatable), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            com.extole.client.rest.reward.supplier.FaceValueAlgorithmType>
        toFaceValueAlgorithmTypeResponse(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> evaluatable) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(evaluatable), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
