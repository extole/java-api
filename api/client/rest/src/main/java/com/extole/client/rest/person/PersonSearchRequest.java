package com.extole.client.rest.person;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonSearchRequest {

    public static final int DEFAULT_LIMIT = 100;
    public static final int DEFAULT_OFFSET = 0;
    private static final String JSON_IDENTITY_KEY_VALUE = "identity_key_value";
    private static final String JSON_PERSON_KEYS = "person_keys";
    private static final String JSON_LIMIT = "limit";
    private static final String JSON_OFFSET = "offset";

    private final Optional<String> identityKeyValue;
    private final List<String> personKeys;
    private final int limit;
    private final int offset;

    public PersonSearchRequest(
        @Parameter(
            description = "The identity key value of the person.") @QueryParam(JSON_IDENTITY_KEY_VALUE) Optional<
                String> identityKeyValue,
        @Parameter(
            description = "The person keys using this format: <name>:<value>") @QueryParam(JSON_PERSON_KEYS) List<
                String> personKeys,
        @DefaultValue("" + DEFAULT_LIMIT) @QueryParam(JSON_LIMIT) Optional<Integer> limit,
        @DefaultValue("" + DEFAULT_OFFSET) @QueryParam(JSON_OFFSET) Optional<Integer> offset) {
        this.identityKeyValue = identityKeyValue;
        this.personKeys = personKeys != null ? ImmutableList.copyOf(personKeys) : ImmutableList.of();
        this.limit = limit.orElse(Integer.valueOf(DEFAULT_LIMIT)).intValue();
        this.offset = offset.orElse(Integer.valueOf(DEFAULT_OFFSET)).intValue();
    }

    @QueryParam(JSON_IDENTITY_KEY_VALUE)
    public Optional<String> getIdentityKeyValue() {
        return identityKeyValue;
    }

    @QueryParam(JSON_PERSON_KEYS)
    public List<String> getPersonKeys() {
        return personKeys;
    }

    @QueryParam(JSON_LIMIT)
    public int getLimit() {
        return limit;
    }

    @QueryParam(JSON_OFFSET)
    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Optional<String> identityKeyValue = Optional.empty();
        private List<String> personKeys = Lists.newArrayList();
        private Optional<Integer> limit = Optional.empty();
        private Optional<Integer> offset = Optional.empty();

        private Builder() {
        }

        public Builder withIdentityKeyValue(String identityKeyValue) {
            this.identityKeyValue = Optional.ofNullable(identityKeyValue);
            return this;
        }

        public Builder withPersonKeys(List<String> personKeys) {
            this.personKeys = Collections.unmodifiableList(personKeys);
            return this;
        }

        public Builder addPersonKey(String keyName, String keyValue) {
            this.personKeys.add(keyName + ":" + keyValue);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public PersonSearchRequest build() {
            return new PersonSearchRequest(identityKeyValue, personKeys, limit, offset);
        }

    }

}
