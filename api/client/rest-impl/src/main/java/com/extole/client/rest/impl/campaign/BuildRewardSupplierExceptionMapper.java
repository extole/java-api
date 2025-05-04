package com.extole.client.rest.impl.campaign;

import java.util.Optional;

import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.built.RewardSupplierCouponPoolIdMissingException;
import com.extole.model.service.reward.supplier.built.RewardSupplierDescriptionTooLongException;
import com.extole.model.service.reward.supplier.built.RewardSupplierFaceValueMissingException;
import com.extole.model.service.reward.supplier.built.RewardSupplierFaceValueOutOfRangeException;
import com.extole.model.service.reward.supplier.built.RewardSupplierFaceValueTypeMissingException;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalCashBackMinMaxLimitsException;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalCharacterInDescriptionException;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalCharacterInNameException;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalRateLimitsException;
import com.extole.model.service.reward.supplier.built.RewardSupplierNameDuplicateException;
import com.extole.model.service.reward.supplier.built.RewardSupplierNameMissingException;
import com.extole.model.service.reward.supplier.built.RewardSupplierNameTooLongException;
import com.extole.model.service.reward.supplier.built.RewardSupplierNegativeCashBackPercentageException;
import com.extole.model.service.reward.supplier.built.RewardSupplierNegativeMaxCashBackException;
import com.extole.model.service.reward.supplier.built.RewardSupplierNegativeMinCashBackException;
import com.extole.model.service.reward.supplier.built.RewardSupplierRateLimitOutOfRangeException;

public final class BuildRewardSupplierExceptionMapper {
    private static final BuildRewardSupplierExceptionMapper INSTANCE = new BuildRewardSupplierExceptionMapper();

    public static BuildRewardSupplierExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildRewardSupplierExceptionMapper() {
    }

    public BuildRewardSupplierRestException map(BuildRewardSupplierException e) {
        return internalMapAsValidationException(e)
            .orElseGet(() -> RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_BUILD_FAILED)
                .addParameter("reward_supplier_id", e.getRewardSupplierId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable())
                .withCause(e)
                .build());
    }

    private Optional<BuildRewardSupplierRestException>
        internalMapAsValidationException(BuildRewardSupplierException e) {
        BuildRewardSupplierRestException result = null;

        if (e instanceof RewardSupplierCouponPoolIdMissingException) {
            RewardSupplierCouponPoolIdMissingException ex = (RewardSupplierCouponPoolIdMissingException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.COUPON_POOL_ID_MISSING)
                .withCause(ex)
                .build();
        }
        if (e instanceof RewardSupplierDescriptionTooLongException) {
            RewardSupplierDescriptionTooLongException ex = (RewardSupplierDescriptionTooLongException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.DESCRIPTION_TOO_LONG)
                .addParameter("description", ex.getDescription())
                .addParameter("max_description_length", Integer.valueOf(ex.getMaxDescriptionLength()))
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierFaceValueOutOfRangeException) {
            RewardSupplierFaceValueOutOfRangeException ex = (RewardSupplierFaceValueOutOfRangeException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.FACE_VALUE_OUT_OF_RANGE)
                .addParameter("face_value", ex.getFaceValue())
                .addParameter("min_value", ex.getMinValue())
                .addParameter("max_value", ex.getMaxValue())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierIllegalCashBackMinMaxLimitsException) {
            RewardSupplierIllegalCashBackMinMaxLimitsException ex =
                (RewardSupplierIllegalCashBackMinMaxLimitsException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_CASH_BACK_LIMITS)
                .addParameter("min_cash_back", ex.getMinCashBack())
                .addParameter("max_cash_back", ex.getMaxCashBack())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierIllegalCharacterInDescriptionException) {
            RewardSupplierIllegalCharacterInDescriptionException ex =
                (RewardSupplierIllegalCharacterInDescriptionException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.ILLEGAL_CHARACTER_IN_DESCRIPTION)
                .addParameter("description", ex.getDescription())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierIllegalCharacterInNameException) {
            RewardSupplierIllegalCharacterInNameException ex =
                (RewardSupplierIllegalCharacterInNameException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.ILLEGAL_CHARACTER_IN_NAME)
                .addParameter("name", ex.getName())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierIllegalRateLimitsException) {
            RewardSupplierIllegalRateLimitsException ex =
                (RewardSupplierIllegalRateLimitsException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_LIMITS)
                .addParameter("limit_per_day", ex.getLimitPerDay())
                .addParameter("limit_per_hour", ex.getLimitPerHour())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierNameDuplicateException) {
            RewardSupplierNameDuplicateException ex =
                (RewardSupplierNameDuplicateException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.DUPLICATED_NAME)
                .addParameter("name", ex.getName())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierNameTooLongException) {
            RewardSupplierNameTooLongException ex =
                (RewardSupplierNameTooLongException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.NAME_TOO_LONG)
                .addParameter("name", ex.getName())
                .addParameter("max_name_length", Integer.valueOf(ex.getMaxNameLength()))
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierNegativeCashBackPercentageException) {
            RewardSupplierNegativeCashBackPercentageException ex =
                (RewardSupplierNegativeCashBackPercentageException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.NEGATIVE_CASH_BACK_PERCENTAGE)
                .addParameter("cash_back_percentage", ex.getCashBackPercentage())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierNegativeMaxCashBackException) {
            RewardSupplierNegativeMaxCashBackException ex =
                (RewardSupplierNegativeMaxCashBackException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.NEGATIVE_MAX_CASH_BACK)
                .addParameter("max_cash_back", ex.getMaxCashBack())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierNegativeMinCashBackException) {
            RewardSupplierNegativeMinCashBackException ex =
                (RewardSupplierNegativeMinCashBackException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.NEGATIVE_MIN_CASH_BACK)
                .addParameter("min_cash_back", ex.getMinCashBack())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierRateLimitOutOfRangeException) {
            RewardSupplierRateLimitOutOfRangeException ex =
                (RewardSupplierRateLimitOutOfRangeException) e;
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.LIMIT_OUT_OF_RANGE)
                .addParameter("limit", ex.getLimit())
                .addParameter("min_value", ex.getMinLimit())
                .addParameter("max_value", ex.getMaxLimit())
                .withCause(e)
                .build();
        }
        if (e instanceof RewardSupplierFaceValueMissingException) {
            RewardSupplierFaceValueMissingException ex = (RewardSupplierFaceValueMissingException) e;
            result = RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.FACE_VALUE_MISSING)
                .withCause(ex)
                .build();
        }
        if (e instanceof RewardSupplierFaceValueTypeMissingException) {
            RewardSupplierFaceValueTypeMissingException ex = (RewardSupplierFaceValueTypeMissingException) e;
            result = RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.FACE_VALUE_TYPE_MISSING)
                .withCause(ex)
                .build();
        }
        if (e instanceof RewardSupplierNameMissingException) {
            RewardSupplierNameMissingException ex = (RewardSupplierNameMissingException) e;
            result = RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.NAME_MISSING)
                .withCause(ex)
                .build();
        }
        return Optional.ofNullable(result);
    }

}
