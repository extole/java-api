package com.extole.client.rest.auth.provider.type.extole;

import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.client.Scope;
import com.extole.common.lang.ToString;

public class ExtoleAuthProviderTypeUpdateRequest {

    private static final String NAME = "name";
    private static final String SCOPES = "scopes";
    private static final String DESCRIPTION = "description";

    private final String name;
    private final Set<Scope> scopes;
    private final String description;

    public ExtoleAuthProviderTypeUpdateRequest(@Nullable @JsonProperty(NAME) String name,
        @Nullable @JsonProperty(SCOPES) Set<Scope> scopes,
        @Nullable @JsonProperty(DESCRIPTION) String description) {
        this.name = name;
        this.scopes = scopes;
        this.description = description;
    }

    @Nullable
    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(SCOPES)
    public Set<Scope> getScopes() {
        return scopes;
    }

    @Nullable
    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private Set<Scope> scopes;
        private String description;

        private Builder() {

        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withScopes(Set<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ExtoleAuthProviderTypeUpdateRequest build() {
            return new ExtoleAuthProviderTypeUpdateRequest(name,
                scopes,
                description);
        }
    }

}
