package com.extole.consumer.rest.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressComponent {
    private static final String JSON_COMPONENT_NAME = "component_name";
    private static final String JSON_COMPONENT_TYPE = "component_type";
    private static final String JSON_CONFIRMATION_LEVEL = "confirmation_level";
    private static final String JSON_INFERRED = "inferred";
    private static final String JSON_REPLACED = "replaced";
    private static final String JSON_SPELL_CORRECTED = "spell_corrected";

    private final ComponentName componentName;
    private final String componentType;
    private final String confirmationLevel;
    private final Boolean inferred;
    private final Boolean replaced;
    private final Boolean spellCorrected;

    public AddressComponent(@JsonProperty(JSON_COMPONENT_NAME) ComponentName componentName,
        @JsonProperty(JSON_COMPONENT_TYPE) String componentType,
        @JsonProperty(JSON_CONFIRMATION_LEVEL) String confirmationLevel,
        @JsonProperty(JSON_INFERRED) Boolean inferred,
        @JsonProperty(JSON_REPLACED) Boolean replaced,
        @JsonProperty(JSON_SPELL_CORRECTED) Boolean spellCorrected) {
        this.componentName = componentName;
        this.componentType = componentType;
        this.confirmationLevel = confirmationLevel;
        this.inferred = inferred;
        this.replaced = replaced;
        this.spellCorrected = spellCorrected;
    }

    @JsonProperty(JSON_COMPONENT_NAME)
    public ComponentName getComponentName() {
        return componentName;
    }

    @JsonProperty(JSON_COMPONENT_TYPE)
    public String getComponentType() {
        return componentType;
    }

    @JsonProperty(JSON_CONFIRMATION_LEVEL)
    public String getConfirmationLevel() {
        return confirmationLevel;
    }

    @JsonProperty(JSON_INFERRED)
    public Boolean getInferred() {
        return inferred;
    }

    @JsonProperty(JSON_REPLACED)
    public Boolean getReplaced() {
        return replaced;
    }

    @JsonProperty(JSON_SPELL_CORRECTED)
    public Boolean getSpellCorrected() {
        return spellCorrected;
    }
}
