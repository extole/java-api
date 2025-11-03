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

    public SettingValidationRestException map(InvalidVariableTranslatableValueException exception) {
        if (exception instanceof VariableTranslatableDefaultValueNotAllowedException) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DEFAULT_KEY_NOT_ALLOWED)
                .addParameter("variable_name", exception.getVariableName())
                .addParameter("variable_value_key", exception.getVariableValueKey())
                .addParameter("variable_value", exception.getVariableValue())
                .withCause(exception)
                .build();
        }
        if (exception instanceof VariableTranslatableInvalidValueEvaluatableTypeException) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.INVALID_TRANSLATABLE_VALUE)
                .addParameter("variable_name", exception.getVariableName())
                .addParameter("variable_value_key", exception.getVariableValueKey())
                .addParameter("variable_value", exception.getVariableValue())
                .withCause(exception)
                .build();
        }
        throw new IllegalArgumentException("Exception " + exception.getClass().getSimpleName() +
            " can't be casted to any rest exception");
    }
}
