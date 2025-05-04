package com.extole.client.rest.component.type;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

@Schema
public class ComponentTypeUpdateRequest {

    private static final String DISPLAY_NAME = "display_name";

    private final Omissible<Optional<String>> displayName;

    public ComponentTypeUpdateRequest(@JsonProperty(DISPLAY_NAME) Omissible<Optional<String>> displayName) {
        this.displayName = displayName;
    }

    @JsonProperty(DISPLAY_NAME)
    public Omissible<Optional<String>> getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<String>> displayName = Omissible.omitted();

        private Builder() {
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Omissible.of(Optional.of(displayName));
            return this;
        }

        public Builder clearDisplayName() {
            this.displayName = Omissible.of(Optional.empty());
            return this;
        }

        public ComponentTypeUpdateRequest build() {
            return new ComponentTypeUpdateRequest(displayName);
        }

    }

}
