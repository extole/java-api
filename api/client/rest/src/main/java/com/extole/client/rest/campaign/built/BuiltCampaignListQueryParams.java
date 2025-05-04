package com.extole.client.rest.campaign.built;

import java.util.Optional;

import javax.ws.rs.QueryParam;

import com.extole.api.reward.RewardSupplier;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltCampaignListQueryParams {

    private static final String QUERY_PARAM_VERSION = "version";
    private static final String QUERY_PARAM_SUPPLIER = "reward_supplier_id";
    private static final String QUERY_PROGRAM_LABEL = "program_label";

    private final Optional<String> version;
    private final Optional<Id<RewardSupplier>> rewardSupplierId;
    private final Optional<String> programLabel;

    public BuiltCampaignListQueryParams(
        @QueryParam(QUERY_PARAM_VERSION) Optional<String> version,
        @QueryParam(QUERY_PARAM_SUPPLIER) Optional<Id<RewardSupplier>> rewardSupplierId,
        @QueryParam(QUERY_PROGRAM_LABEL) Optional<String> programLabel) {
        this.version = version;
        this.rewardSupplierId = rewardSupplierId;
        this.programLabel = programLabel;
    }

    @QueryParam(QUERY_PARAM_VERSION)
    public Optional<String> getVersion() {
        return version;
    }

    @QueryParam(QUERY_PARAM_SUPPLIER)
    public Optional<Id<RewardSupplier>> getRewardSupplierId() {
        return rewardSupplierId;
    }

    @QueryParam(QUERY_PROGRAM_LABEL)
    public Optional<String> getProgramLabel() {
        return programLabel;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Optional<String> version = Optional.empty();
        private Optional<Id<RewardSupplier>> rewardSupplierId = Optional.empty();
        private Optional<String> programLabel = Optional.empty();

        private Builder() {
        }

        public Builder withVersion(String version) {
            this.version = Optional.ofNullable(version);
            return this;
        }

        public Builder withRewardSupplierId(Id<RewardSupplier> rewardSupplierId) {
            this.rewardSupplierId = Optional.ofNullable(rewardSupplierId);
            return this;
        }

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = Optional.ofNullable(programLabel);
            return this;
        }

        public BuiltCampaignListQueryParams build() {
            return new BuiltCampaignListQueryParams(version, rewardSupplierId, programLabel);
        }
    }
}
