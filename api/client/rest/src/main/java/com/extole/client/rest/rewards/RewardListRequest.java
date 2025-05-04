package com.extole.client.rest.rewards;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;
import com.extole.common.rest.time.TimeZoneParam;

public class RewardListRequest {
    private static final String COMMA = ",";

    private final String states;
    private final String rewardSupplierIds;
    private final String partnerRewardSupplierIds;
    private final String personIds;
    private final String actionIds;
    private final String rootEventIds;
    private final String partnerRewardIds;
    private final String rewardTypes;
    private final Boolean successOnly;
    private final String timeInterval;
    private final Integer limit;
    private final Integer offset;
    private final ZoneId timeZone;

    public static Builder newRewardListRequest() {
        return new Builder();
    }

    public RewardListRequest(@Nullable @QueryParam("state") String states,
        @Nullable @QueryParam("reward_supplier_id") String rewardSupplierIds,
        @Nullable @QueryParam("partner_reward_supplier_id") String partnerRewardSupplierIds,
        @Nullable @QueryParam("person_id") String personIds,
        @Nullable @QueryParam("action_id") String actionIds,
        @Nullable @QueryParam("root_event_id") String rootEventIds,
        @Nullable @QueryParam("partner_reward_id") String partnerRewardIds,
        @Nullable @QueryParam("reward_type") String rewardTypes,
        @DefaultValue("true") @QueryParam("success_only") Boolean successOnly,
        @Nullable @QueryParam("time_interval") String timeInterval,
        @DefaultValue("100") @QueryParam("limit") Integer limit,
        @DefaultValue("0") @QueryParam("offset") Integer offset,
        @TimeZoneParam ZoneId timeZone) {
        this.states = states;
        this.rewardSupplierIds = rewardSupplierIds;
        this.partnerRewardSupplierIds = partnerRewardSupplierIds;
        this.personIds = personIds;
        this.actionIds = actionIds;
        this.rootEventIds = rootEventIds;
        this.partnerRewardIds = partnerRewardIds;
        this.rewardTypes = rewardTypes;
        this.successOnly = successOnly;
        this.timeInterval = timeInterval;
        this.limit = limit;
        this.offset = offset;
        this.timeZone = timeZone;
    }

    @QueryParam("state")
    public Optional<String> getStates() {
        return Optional.ofNullable(states);
    }

    @QueryParam("reward_supplier_id")
    public Optional<String> getRewardSupplierIds() {
        return Optional.ofNullable(rewardSupplierIds);
    }

    @QueryParam("partner_reward_supplier_id")
    public Optional<String> getPartnerRewardSupplierIds() {
        return Optional.ofNullable(partnerRewardSupplierIds);
    }

    @QueryParam("person_id")
    public Optional<String> getPersonIds() {
        return Optional.ofNullable(personIds);
    }

    @QueryParam("action_id")
    public Optional<String> getActionIds() {
        return Optional.ofNullable(actionIds);
    }

    @QueryParam("root_event_id")
    public Optional<String> getRootEventIds() {
        return Optional.ofNullable(rootEventIds);
    }

    @QueryParam("partner_reward_id")
    public Optional<String> getPartnerRewardIds() {
        return Optional.ofNullable(partnerRewardIds);
    }

    @QueryParam("reward_type")
    public Optional<String> getRewardTypes() {
        return Optional.ofNullable(rewardTypes);
    }

    @QueryParam("success_only")
    public Boolean getSuccessOnly() {
        return successOnly;
    }

    @QueryParam("time_interval")
    public String getTimeInterval() {
        return timeInterval;
    }

    @QueryParam("limit")
    public Integer getLimit() {
        return limit;
    }

    @QueryParam("offset")
    public Integer getOffset() {
        return offset;
    }

    @TimeZoneParam
    public ZoneId getTimeZone() {
        return timeZone;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private String states;
        private final List<String> rewardSupplierIds = new ArrayList<>();
        private final List<String> partnerRewardSupplierIds = new ArrayList<>();
        private String personIds;
        private String actionIds;
        private String rootEventIds;
        private final List<String> partnerRewardIds = new ArrayList<>();
        private String rewardTypes;
        private Boolean successOnly;
        private String timeInterval;
        private Integer limit;
        private Integer offset;
        private ZoneId timeZone;

        private Builder() {
        }

        public Builder withStates(String states) {
            this.states = states;
            return this;
        }

        public Builder withRewardSupplierIds(String... rewardSupplierIds) {
            this.rewardSupplierIds.addAll(Arrays.asList(rewardSupplierIds));
            return this;
        }

        public Builder withRewardSupplierIds(List<String> rewardSupplierIds) {
            this.rewardSupplierIds.addAll(rewardSupplierIds);
            return this;
        }

        public Builder withPartnerRewardSupplierIds(String... partnerRewardSupplierIds) {
            this.rewardSupplierIds.addAll(Arrays.asList(partnerRewardSupplierIds));
            return this;
        }

        public Builder withPartnerRewardSupplierIds(List<String> partnerRewardSupplierIds) {
            this.rewardSupplierIds.addAll(partnerRewardSupplierIds);
            return this;
        }

        public Builder withPersonIds(String personIds) {
            this.personIds = personIds;
            return this;
        }

        public Builder withActionIds(String actionIds) {
            this.actionIds = actionIds;
            return this;
        }

        public Builder withRootEventIds(String rootEventIds) {
            this.rootEventIds = rootEventIds;
            return this;
        }

        public Builder withPartnerRewardIds(String... partnerRewardIds) {
            this.partnerRewardIds.addAll(Arrays.asList(partnerRewardIds));
            return this;
        }

        public Builder withPartnerRewardIds(List<String> partnerRewardIds) {
            this.partnerRewardIds.addAll(partnerRewardIds);
            return this;
        }

        public Builder withRewardTypes(String rewardTypes) {
            this.rewardTypes = rewardTypes;
            return this;
        }

        public Builder withSuccessOnly(Boolean successOnly) {
            this.successOnly = successOnly;
            return this;
        }

        public Builder withTimeInterval(String timeInterval) {
            this.timeInterval = timeInterval;
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

        public Builder withTimeZone(ZoneId timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public RewardListRequest build() {
            return new RewardListRequest(states,
                rewardSupplierIds.stream().collect(Collectors.joining(COMMA)),
                partnerRewardSupplierIds.stream().collect(Collectors.joining(COMMA)),
                personIds,
                actionIds,
                rootEventIds,
                partnerRewardIds.stream().collect(Collectors.joining(COMMA)),
                rewardTypes,
                successOnly,
                timeInterval,
                limit,
                offset,
                timeZone);
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}
