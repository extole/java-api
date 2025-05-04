package com.extole.consumer.rest.validation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Address {
    private static final String JSON_FORMATTED_ADDRESS = "formatted_address";
    private static final String JSON_POSTAL_CODE = "postal_address";
    private static final String JSON_ADDRESS_COMPONENTS = "address_components";
    private static final String JSON_ADDRESS_MISSING_COMPONENT_TYPES = "missing_component_types";
    private static final String JSON_ADDRESS_UNCONFIRMED_COMPONENT_TYPES = "unconfirmed_component_types";

    private final String formattedAddress;
    private final PostalAddress postalAddress;
    private final List<AddressComponent> addressComponents;
    private final List<String> missingComponentTypes;
    private final List<String> unconfirmedComponentTypes;

    public Address(
        @JsonProperty(JSON_FORMATTED_ADDRESS) String formattedAddress,
        @JsonProperty(JSON_POSTAL_CODE) PostalAddress postalAddress,
        @JsonProperty(JSON_ADDRESS_COMPONENTS) List<AddressComponent> addressComponents,
        @JsonProperty(JSON_ADDRESS_MISSING_COMPONENT_TYPES) List<String> missingComponentTypes,
        @JsonProperty(JSON_ADDRESS_UNCONFIRMED_COMPONENT_TYPES) List<String> unconfirmedComponentTypes) {
        this.formattedAddress = formattedAddress;
        this.postalAddress = postalAddress;
        this.addressComponents = addressComponents;
        this.missingComponentTypes = missingComponentTypes != null ? missingComponentTypes : List.of();
        this.unconfirmedComponentTypes = unconfirmedComponentTypes != null ? unconfirmedComponentTypes : List.of();
    }

    @JsonProperty(JSON_FORMATTED_ADDRESS)
    public String getFormattedAddress() {
        return formattedAddress;
    }

    @JsonProperty(JSON_POSTAL_CODE)
    public PostalAddress getPostalAddress() {
        return postalAddress;
    }

    @JsonProperty(JSON_ADDRESS_COMPONENTS)
    public List<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    @JsonProperty(JSON_ADDRESS_MISSING_COMPONENT_TYPES)
    public List<String> getMissingComponentTypes() {
        return missingComponentTypes;
    }

    @JsonProperty(JSON_ADDRESS_UNCONFIRMED_COMPONENT_TYPES)
    public List<String> getUnconfirmedComponentTypes() {
        return unconfirmedComponentTypes;
    }
}
