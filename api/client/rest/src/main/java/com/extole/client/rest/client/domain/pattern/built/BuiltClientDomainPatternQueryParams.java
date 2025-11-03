package com.extole.client.rest.client.domain.pattern.built;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.extole.client.rest.program.ProgramResponse;
import com.extole.id.Id;

public class BuiltClientDomainPatternQueryParams {

    private static final String CLIENT_DOMAIN_ID = "client_domain_id";
    private static final String INCLUDE_ARCHIVED = "include_archived";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final Id<ProgramResponse> clientDomainId;
    private final boolean includeArchived;
    private final Integer limit;
    private final Integer offset;

    public BuiltClientDomainPatternQueryParams(
        @Nullable @QueryParam(CLIENT_DOMAIN_ID) Id<ProgramResponse> clientDomainId,
        @QueryParam(INCLUDE_ARCHIVED) @DefaultValue("false") boolean includeArchived,
        @QueryParam(LIMIT) @DefaultValue("1000") Integer limit,
        @QueryParam(OFFSET) @DefaultValue("0") Integer offset) {
        this.clientDomainId = clientDomainId;
        this.includeArchived = includeArchived;
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam(CLIENT_DOMAIN_ID)
    public Id<ProgramResponse> getClientDomainId() {
        return clientDomainId;
    }

    @QueryParam(INCLUDE_ARCHIVED)
    public boolean getIncludeArchived() {
        return includeArchived;
    }

    @QueryParam(LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @QueryParam(OFFSET)
    public Integer getOffset() {
        return offset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Id<ProgramResponse> clientDomainId;
        private boolean includeArchived;
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withClientDomainId(Id<ProgramResponse> clientDomainId) {
            this.clientDomainId = clientDomainId;
            return this;
        }

        public Builder withIncludeArchived(boolean includeArchived) {
            this.includeArchived = includeArchived;
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

        public BuiltClientDomainPatternQueryParams build() {
            return new BuiltClientDomainPatternQueryParams(clientDomainId, includeArchived, limit, offset);
        }

    }

}
