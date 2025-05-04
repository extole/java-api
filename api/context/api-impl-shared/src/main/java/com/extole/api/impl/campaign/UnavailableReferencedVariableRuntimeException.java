package com.extole.api.impl.campaign;

import java.util.List;

import com.extole.api.campaign.VariableUnavailabilityCause;

public final class UnavailableReferencedVariableRuntimeException extends RuntimeException {

    private final String variableName;
    private final List<String> attemptedVariants;
    private final List<String> availableVariants;
    private final VariableUnavailabilityCause type;

    public UnavailableReferencedVariableRuntimeException(String variableName,
        List<String> attemptedVariants,
        List<String> availableVariants,
        VariableUnavailabilityCause type) {
        super("Referenced variable " + variableName + " is not available at buildtime due " + type +
            " Referenced Variants: " + attemptedVariants + " Available variants: "
            + availableVariants);
        this.variableName = variableName;
        this.attemptedVariants = attemptedVariants;
        this.availableVariants = availableVariants;
        this.type = type;
    }

    public String getVariableName() {
        return variableName;
    }

    public List<String> getAttemptedVariants() {
        return attemptedVariants;
    }

    public List<String> getAvailableVariants() {
        return availableVariants;
    }

    public VariableUnavailabilityCause getType() {
        return type;
    }
}
