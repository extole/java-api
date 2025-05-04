package com.extole.client.rest.campaign;

import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

public class CampaignListQueryParams {

    private static final String QUERY_PARAM_VERSION = "version";
    private static final String QUERY_PARAM_SUPPLIER = "reward_supplier_id";
    private static final String QUERY_PARAM_TAGS = "tags";
    private static final String QUERY_PARAM_INCLUDE_ARCHIVED = "include_archived";

    private final String version;
    private final String rewardSupplierId;
    private final Set<String> tags;
    private final Boolean includeArchived;

    public CampaignListQueryParams(
        @Nullable @QueryParam(QUERY_PARAM_VERSION) String version,
        @Nullable @QueryParam(QUERY_PARAM_SUPPLIER) String rewardSupplierId,
        @Nullable @QueryParam(QUERY_PARAM_TAGS) Set<String> tags,
        @Nullable @QueryParam(QUERY_PARAM_INCLUDE_ARCHIVED) Boolean includeArchived) {
        this.version = version;
        this.rewardSupplierId = rewardSupplierId;
        this.tags = tags;
        this.includeArchived = includeArchived;
    }

    @QueryParam(QUERY_PARAM_VERSION)
    public String getVersion() {
        return version;
    }

    @QueryParam(QUERY_PARAM_SUPPLIER)
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @QueryParam(QUERY_PARAM_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @QueryParam(QUERY_PARAM_INCLUDE_ARCHIVED)
    public Boolean getIncludeArchived() {
        return includeArchived;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String version;
        private String rewardSupplierId;
        private Set<String> tags;
        private Boolean includeArchived;

        private Builder() {
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withRewardSupplierId(String rewardSupplierId) {
            this.rewardSupplierId = rewardSupplierId;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withIncludeArchived(Boolean includeArchived) {
            this.includeArchived = includeArchived;
            return this;
        }

        public CampaignListQueryParams build() {
            return new CampaignListQueryParams(version, rewardSupplierId, tags, includeArchived);
        }
    }
}
