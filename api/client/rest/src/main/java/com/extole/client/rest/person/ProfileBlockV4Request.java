package com.extole.client.rest.person;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class ProfileBlockV4Request {
    private static final String ACTION = "action";
    private static final String REASON = "reason";

    private final ProfileBlockAction action;
    private final String reason;

    public ProfileBlockV4Request(@JsonProperty(ACTION) ProfileBlockAction action,
        @Nullable @JsonProperty(REASON) String reason) {
        this.reason = reason;
        this.action = action;
    }

    @Nullable
    @JsonProperty(REASON)
    public String getBlockReason() {
        return reason;
    }

    @JsonProperty(ACTION)
    public ProfileBlockAction getAction() {
        return action;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private ProfileBlockAction action;
        private String blockReason;

        private Builder() {
        }

        public Builder withAction(ProfileBlockAction action) {
            this.action = action;
            return this;
        }

        public Builder withBlockReason(String blockReason) {
            this.blockReason = blockReason;
            return this;
        }

        public ProfileBlockV4Request build() {
            return new ProfileBlockV4Request(action, blockReason);
        }
    }
}
