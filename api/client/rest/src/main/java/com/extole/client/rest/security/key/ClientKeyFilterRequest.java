package com.extole.client.rest.security.key;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableSet;

public class ClientKeyFilterRequest {

    private final Set<String> tags;

    public ClientKeyFilterRequest(@Nullable @QueryParam("tags") Set<String> tags) {
        this.tags = tags;
    }

    @QueryParam("tags")
    public Set<String> getTags() {
        return tags == null ? ImmutableSet.of()
            : ImmutableSet.copyOf(tags.stream().filter(tag -> Objects.nonNull(tag)).collect(
                Collectors.toSet()));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Set<String> tags;

        private Builder() {
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public ClientKeyFilterRequest build() {
            return new ClientKeyFilterRequest(tags);
        }
    }
}
