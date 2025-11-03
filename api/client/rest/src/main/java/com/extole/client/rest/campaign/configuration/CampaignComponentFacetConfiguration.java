package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class CampaignComponentFacetConfiguration {
    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final String name;
    private final String value;

    @JsonCreator
    public CampaignComponentFacetConfiguration(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_VALUE) String value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static <CALLER> Builder<CALLER> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER> {

        private Builder(CALLER caller) {
            this.caller = caller;
        }

        private final CALLER caller;
        private String name;
        private String value;

        public Builder<CALLER> withName(String name) {
            this.name = name;
            return this;
        }

        public Builder<CALLER> withValue(String value) {
            this.value = value;
            return this;
        }

        public CALLER done() {
            return caller;
        }

        public CampaignComponentFacetConfiguration build() {
            return new CampaignComponentFacetConfiguration(name, value);
        }

    }

}
