package com.extole.client.rest.person.v2;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonGetV2Request {

    private final String email;
    private final String partnerUserId;
    private final String lastName;
    private final Integer limit;
    private final Integer offset;
    private final List<String> partnerIds;

    public PersonGetV2Request(
        @Parameter(
            description = "The email address of the person. Make sure to URL encode.") @QueryParam("email") @Nullable String email,
        @Parameter(
            description = "Search for a person based on the partner user id of the person") @QueryParam("partner_user_id") @Nullable String partnerUserId,
        @Parameter(description = "The last name of the user") @QueryParam("last_name") @Nullable String lastName,
        @DefaultValue("100") @QueryParam("limit") Integer limit,
        @DefaultValue("0") @QueryParam("offset") Integer offset,
        @Parameter(
            description = "The partner ids using this format: <name>:<value>") @QueryParam("partner_id") @Nullable List<
                String> partnerIds) {
        this.email = email;
        this.partnerUserId = partnerUserId;
        this.lastName = lastName;
        this.limit = limit;
        this.offset = offset;
        this.partnerIds = partnerIds != null ? Collections.unmodifiableList(partnerIds) : null;
    }

    @Nullable
    @QueryParam("email")
    public String getEmail() {
        return email;
    }

    @Nullable
    @QueryParam("partner_user_id")
    public String getPartnerUserId() {
        return partnerUserId;
    }

    @Nullable
    @QueryParam("last_name")
    public String getLastName() {
        return lastName;
    }

    @QueryParam("limit")
    public Integer getLimit() {
        return limit;
    }

    @QueryParam("offset")
    public Integer getOffset() {
        return offset;
    }

    @Nullable
    @QueryParam("partner_id")
    public List<String> getPartnerIds() {
        return partnerIds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String email;
        private String partnerUserId;
        private String lastName;
        private Integer limit;
        private Integer offset;
        private List<String> partnerIds;

        private Builder() {
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPartnerUserId(String partnerUserId) {
            this.partnerUserId = partnerUserId;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder withPartnerIds(List<String> partnerIds) {
            this.partnerIds = Collections.unmodifiableList(partnerIds);
            return this;
        }

        public PersonGetV2Request build() {
            return new PersonGetV2Request(email, partnerUserId, lastName, limit, offset, partnerIds);
        }

    }

}
