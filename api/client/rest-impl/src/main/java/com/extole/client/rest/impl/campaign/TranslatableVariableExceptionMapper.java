package com.extole.client.rest.impl.campaign;

import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.setting.InvalidVariableTranslatableValueException;
import com.extole.model.service.campaign.setting.VariableTranslatableDefaultValueNotAllowedException;
import com.extole.model.service.campaign.setting.VariableTranslatableInvalidValueEvaluatableTypeException;

public final class TranslatableVariableExceptionMapper {
    private static final TranslatableVariableExceptionMapper INSTANCE = new TranslatableVariableExceptionMapper();

    public static TranslatableVariableExceptionMapper getInstance() {
        return INSTANCE;
    }

    private TranslatableVariableExceptionMapper() {
    }

    public SettingValidationRestException map(InvalidVariableTranslatableValueException e) {
        if (e instanceof VariableTranslatableDefaultValueNotAllowedException) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DEFAULT_KEY_NOT_ALLOWED)
                .addParameter("variable_name", e.getVariableName())
                .addParameter("variable_value_key", e.getVariableValueKey())
                .addParameter("variable_value", e.getVariableValue())
                .withCause(e)
                .build();
        }
        if (e instanceof VariableTranslatableInvalidValueEvaluatableTypeException) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.INVALID_TRANSLATABLE_VALUE)
                .addParameter("variable_name", e.getVariableName())
                .addParameter("variable_value_key", e.getVariableValueKey())
                .addParameter("variable_value", e.getVariableValue())
                .withCause(e)
                .build();
        }
        throw new IllegalArgumentException("Exception " + e.getClass().getSimpleName() +
            " can't be casted to any rest exception");
    }
}
