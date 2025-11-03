package com.extole.client.rest.impl.campaign;

import java.util.Optional;

import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.built.RewardSupplierCouponPoolIdMissingException;
import com.extole.model.service.reward.supplier.built.RewardSupplierDescriptionTooLongException;
import com.extole.model.service.reward.supplier.built.RewardSupplierDisplayNameTooLongException;
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

    public BuildRewardSupplierRestException map(BuildRewardSupplierException exception) {
        return internalMapAsValidationException(exception)
            .orElseGet(() -> RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_BUILD_FAILED)
                .addParameter("reward_supplier_id", exception.getRewardSupplierId())
                .addParameter("evaluatable_name", exception.getEvaluatableName())
                .addParameter("evaluatable", exception.getEvaluatable())
                .withCause(exception)
                .build());
    }

    private Optional<BuildRewardSupplierRestException>
        internalMapAsValidationException(BuildRewardSupplierException exception) {
        BuildRewardSupplierRestException result = null;

        if (exception instanceof RewardSupplierCouponPoolIdMissingException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.COUPON_POOL_ID_MISSING)
                .withCause(castedException)
                .build();
        }
        if (exception instanceof RewardSupplierDescriptionTooLongException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.DESCRIPTION_TOO_LONG)
                .addParameter("description", castedException.getDescription())
                .addParameter("max_description_length", Integer.valueOf(castedException.getMaxDescriptionLength()))
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierFaceValueOutOfRangeException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.FACE_VALUE_OUT_OF_RANGE)
                .addParameter("face_value", castedException.getFaceValue())
                .addParameter("min_value", castedException.getMinValue())
                .addParameter("max_value", castedException.getMaxValue())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierIllegalCashBackMinMaxLimitsException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_CASH_BACK_LIMITS)
                .addParameter("min_cash_back", castedException.getMinCashBack())
                .addParameter("max_cash_back", castedException.getMaxCashBack())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierIllegalCharacterInDescriptionException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.ILLEGAL_CHARACTER_IN_DESCRIPTION)
                .addParameter("description", castedException.getDescription())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierIllegalCharacterInNameException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.ILLEGAL_CHARACTER_IN_NAME)
                .addParameter("name", castedException.getName())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierIllegalRateLimitsException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_LIMITS)
                .addParameter("limit_per_day", castedException.getLimitPerDay())
                .addParameter("limit_per_hour", castedException.getLimitPerHour())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierNameDuplicateException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.DUPLICATED_NAME)
                .addParameter("name", castedException.getName())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierDisplayNameTooLongException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.DISPLAY_NAME_TOO_LONG)
                .addParameter("display_name", castedException.getDisplayName())
                .addParameter("max_length", Integer.valueOf(castedException.getMaxLength()))
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierNameTooLongException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.NAME_TOO_LONG)
                .addParameter("name", castedException.getName())
                .addParameter("max_name_length", Integer.valueOf(castedException.getMaxNameLength()))
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierNegativeCashBackPercentageException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.NEGATIVE_CASH_BACK_PERCENTAGE)
                .addParameter("cash_back_percentage", castedException.getCashBackPercentage())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierNegativeMaxCashBackException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.NEGATIVE_MAX_CASH_BACK)
                .addParameter("max_cash_back", castedException.getMaxCashBack())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierNegativeMinCashBackException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.NEGATIVE_MIN_CASH_BACK)
                .addParameter("min_cash_back", castedException.getMinCashBack())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierRateLimitOutOfRangeException castedException) {
            result = RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.LIMIT_OUT_OF_RANGE)
                .addParameter("limit", castedException.getLimit())
                .addParameter("min_value", castedException.getMinLimit())
                .addParameter("max_value", castedException.getMaxLimit())
                .withCause(exception)
                .build();
        }
        if (exception instanceof RewardSupplierFaceValueMissingException castedException) {
            result = RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.FACE_VALUE_MISSING)
                .withCause(castedException)
                .build();
        }
        if (exception instanceof RewardSupplierFaceValueTypeMissingException castedException) {
            result = RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.FACE_VALUE_TYPE_MISSING)
                .withCause(castedException)
                .build();
        }
        if (exception instanceof RewardSupplierNameMissingException castedException) {
            result = RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.NAME_MISSING)
                .withCause(castedException)
                .build();
        }
        return Optional.ofNullable(result);
    }

}
