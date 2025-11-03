package com.extole.client.rest.campaign.component;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.lang3.ArrayUtils;

import com.extole.client.rest.campaign.CampaignVersionState;
import com.extole.common.lang.ToString;
import com.extole.common.rest.time.TimeZoneParam;

public class ComponentListRequest {
    private static final String COMMA = ",";

    private static final String QUERY_PARAM_NAME = "name";
    private static final String QUERY_PARAM_OWNER = "owner";
    private static final String QUERY_PARAM_STATE = "state";
    private static final String QUERY_PARAM_VERSION_STATE = "version_state";
    private static final String QUERY_PARAM_CAMPAIGN_IDS = "campaign_ids";
    private static final String QUERY_PARAM_HAVING_ANY_TAGS = "having_any_tags";
    private static final String QUERY_PARAM_HAVING_ALL_TAGS = "having_all_tags";
    private static final String QUERY_PARAM_EXCLUDE_HAVING_ANY_TAGS = "exclude_having_any_tags";
    private static final String QUERY_PARAM_EXCLUDE_HAVING_ALL_TAGS = "exclude_having_all_tags";
    private static final String QUERY_PARAM_HAVING_ANY_TYPES = "having_any_types";
    private static final String QUERY_PARAM_HAVING_ALL_TYPES = "having_all_types";
    private static final String QUERY_PARAM_TARGET_COMPONENT_ID = "target_component_id";
    private static final String QUERY_PARAM_TARGET_SOCKET_NAME = "target_socket_name";
    private static final String QUERY_PARAM_SHOW_ALL = "show_all";
    private static final String QUERY_PARAM_LIMIT = "limit";
    private static final String QUERY_PARAM_OFFSET = "offset";

    private final Optional<String> name;
    private final Optional<String> owner;
    private final Optional<String> state;
    private final Optional<CampaignVersionState> versionState;
    private final Optional<String> campaignIds;
    private final Optional<String> havingAnyTags;
    private final Optional<String> havingAllTags;
    private final Optional<String> excludeHavingAnyTags;
    private final Optional<String> excludeHavingAllTags;
    private final Optional<String> havingAnyTypes;
    private final Optional<String> havingAllTypes;
    private final Optional<String> targetComponentId;
    private final Optional<String> targetSocketName;
    private final boolean showAll;
    private final Integer limit;
    private final Integer offset;
    private final ZoneId timeZone;

    public static Builder builder() {
        return new Builder();
    }

    public ComponentListRequest(
        @QueryParam(QUERY_PARAM_NAME) @Parameter(
            description = "The name of the component to filter by.") Optional<String> name,
        @QueryParam(QUERY_PARAM_OWNER) @Parameter(
            description = "The owner of the component to filter by. (CLIENT, EXTOLE, EXTOLE_BETA)") Optional<
                String> owner,
        @QueryParam(QUERY_PARAM_STATE) @Parameter(
            description = "The state of the components campaign to filter by. (LIVE, PAUSED, ENDED,...)") Optional<
                String> state,
        @QueryParam(QUERY_PARAM_VERSION_STATE) @Parameter(
            description = "The version state PUBLISHED or LATEST(default)") Optional<CampaignVersionState> versionState,
        @QueryParam(QUERY_PARAM_CAMPAIGN_IDS) @Parameter(
            description = "A comma-separated list of components campaign IDs to filter by.") Optional<
                String> campaignIds,
        @QueryParam(QUERY_PARAM_HAVING_ANY_TAGS) @Parameter(
            description = "A comma-separated list of tags; the component must have at least one of these tags.") Optional<
                String> havingAnyTags,
        @QueryParam(QUERY_PARAM_HAVING_ALL_TAGS) @Parameter(
            description = "A comma-separated list of tags; the component must have all of these tags.") Optional<
                String> havingAllTags,
        @QueryParam(QUERY_PARAM_EXCLUDE_HAVING_ANY_TAGS) @Parameter(
            description = "A comma-separated list of tags; the component having any of these tags is excluded.") Optional<
                String> excludeHavingAnyTags,
        @QueryParam(QUERY_PARAM_EXCLUDE_HAVING_ALL_TAGS) @Parameter(
            description = "A comma-separated list of tags; the component having all of these tags is excluded.") Optional<
                String> excludeHavingAllTags,
        @QueryParam(QUERY_PARAM_HAVING_ANY_TYPES) @Parameter(
            description = "A comma-separated list of component types; the component must have at least one of these types.") Optional<
                String> havingAnyTypes,
        @QueryParam(QUERY_PARAM_HAVING_ALL_TYPES) @Parameter(
            description = "A comma-separated list of component types; the component must have all of these types.") Optional<
                String> havingAllTypes,
        @QueryParam(QUERY_PARAM_TARGET_COMPONENT_ID) @Parameter(
            description = "The ID of the target component to filter by." +
                " Only compatible components will be returned.") Optional<String> targetComponentId,
        @QueryParam(QUERY_PARAM_TARGET_SOCKET_NAME) @Parameter(
            description = "The name of the target socket to filter by." +
                " Only compatible components will be returned.") Optional<String> targetSocketName,
        @QueryParam(QUERY_PARAM_SHOW_ALL) @Parameter(
            description = "showAll applies only for components from EXTOLE & EXTOLE_BETA owners" +
                "false (default) shows components from LIVE, `integration` and `campaign-components` program-types." +
                "true returns all components regardless of their program-type or state of the campaign.") boolean showAll,
        @DefaultValue("1000") @QueryParam(QUERY_PARAM_LIMIT) Integer limit,
        @DefaultValue("0") @QueryParam(QUERY_PARAM_OFFSET) Integer offset,
        @Parameter(description = "The timezone of the returned dates.") @Nullable @TimeZoneParam ZoneId timeZone) {
        this.name = name;
        this.owner = owner;
        this.state = state;
        this.versionState = versionState;
        this.campaignIds = campaignIds;
        this.havingAnyTags = havingAnyTags;
        this.havingAllTags = havingAllTags;
        this.excludeHavingAnyTags = excludeHavingAnyTags;
        this.excludeHavingAllTags = excludeHavingAllTags;
        this.havingAnyTypes = havingAnyTypes;
        this.havingAllTypes = havingAllTypes;
        this.targetComponentId = targetComponentId;
        this.targetSocketName = targetSocketName;
        this.showAll = showAll;
        this.limit = limit;
        this.offset = offset;
        this.timeZone = timeZone;
    }

    @QueryParam(QUERY_PARAM_NAME)
    public Optional<String> getName() {
        return name;
    }

    @Deprecated // TODO remove in ENG-25673
    @QueryParam(QUERY_PARAM_OWNER)
    public Optional<String> getOwner() {
        return owner;
    }

    @QueryParam(QUERY_PARAM_STATE)
    public Optional<String> getState() {
        return state;
    }

    @QueryParam(QUERY_PARAM_VERSION_STATE)
    public Optional<CampaignVersionState> getVersionState() {
        return versionState;
    }

    @QueryParam(QUERY_PARAM_CAMPAIGN_IDS)
    public Optional<String> getCampaignIds() {
        return campaignIds;
    }

    @QueryParam(QUERY_PARAM_HAVING_ANY_TAGS)
    public Optional<String> getHavingAnyTags() {
        return havingAnyTags;
    }

    @QueryParam(QUERY_PARAM_HAVING_ALL_TAGS)
    public Optional<String> getHavingAllTags() {
        return havingAllTags;
    }

    @QueryParam(QUERY_PARAM_EXCLUDE_HAVING_ANY_TAGS)
    public Optional<String> getExcludeHavingAnyTags() {
        return excludeHavingAnyTags;
    }

    @QueryParam(QUERY_PARAM_EXCLUDE_HAVING_ALL_TAGS)
    public Optional<String> getExcludeHavingAllTags() {
        return excludeHavingAllTags;
    }

    @QueryParam(QUERY_PARAM_HAVING_ANY_TYPES)
    public Optional<String> getHavingAnyTypes() {
        return havingAnyTypes;
    }

    @QueryParam(QUERY_PARAM_HAVING_ALL_TYPES)
    public Optional<String> getHavingAllTypes() {
        return havingAllTypes;
    }

    @QueryParam(QUERY_PARAM_TARGET_COMPONENT_ID)
    public Optional<String> getTargetComponentId() {
        return targetComponentId;
    }

    @QueryParam(QUERY_PARAM_TARGET_SOCKET_NAME)
    public Optional<String> getTargetSocketName() {
        return targetSocketName;
    }

    @Deprecated // TODO remove in ENG-25673
    @QueryParam(QUERY_PARAM_SHOW_ALL)
    public boolean getShowAll() {
        return showAll;
    }

    @QueryParam(QUERY_PARAM_LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @QueryParam(QUERY_PARAM_OFFSET)
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

        private Optional<String> name = Optional.empty();
        private Optional<String> owner = Optional.empty();
        private String[] campaignIds = new String[] {};
        private String[] states = new String[] {};
        private Optional<CampaignVersionState> versionState = Optional.empty();
        private String[] havingAnyTags = new String[] {};
        private String[] havingAllTags = new String[] {};
        private String[] excludeHavingAnyTags = new String[] {};
        private String[] excludeHavingAllTags = new String[] {};
        private String[] havingAnyTypes = new String[] {};
        private String[] havingAllTypes = new String[] {};
        private Optional<String> targetComponentId;
        private Optional<String> targetSocketName;
        private boolean showAll;
        private Integer limit;
        private Integer offset;
        private ZoneId timeZone = ZoneOffset.UTC;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = Optional.ofNullable(name);
            return this;
        }

        public Builder withOwner(String owner) {
            this.owner = Optional.ofNullable(owner);
            return this;
        }

        public Builder withState(String... states) {
            this.states = states;
            return this;
        }

        public Builder withVersionState(CampaignVersionState versionState) {
            this.versionState = Optional.ofNullable(versionState);
            return this;
        }

        public Builder withCampaignIds(String... campaignIds) {
            this.campaignIds = campaignIds;
            return this;
        }

        public Builder withHavingAnyTags(String... havingAnyTags) {
            this.havingAnyTags = havingAnyTags;
            return this;
        }

        public Builder withHavingAllTags(String... havingAllTags) {
            this.havingAllTags = havingAllTags;
            return this;
        }

        public Builder withExcludeHavingAnyTags(String... excludeHavingAnyTags) {
            this.excludeHavingAnyTags = excludeHavingAnyTags;
            return this;
        }

        public Builder withExcludeHavingAllTags(String... excludeHavingAllTags) {
            this.excludeHavingAllTags = excludeHavingAllTags;
            return this;
        }

        public Builder withHavingAnyTypes(String... havingAnyTypes) {
            this.havingAnyTypes = havingAnyTypes;
            return this;
        }

        public Builder withHavingAllTypes(String... havingAllTypes) {
            this.havingAllTypes = havingAllTypes;
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

        public Builder withTargetComponentId(String targetComponentId) {
            this.targetComponentId = Optional.ofNullable(targetComponentId);
            return this;
        }

        public Builder withTargetSocketName(String targetSocketName) {
            this.targetSocketName = Optional.ofNullable(targetSocketName);
            return this;
        }

        public Builder withShowAll(boolean showAll) {
            this.showAll = showAll;
            return this;
        }

        public ComponentListRequest build() {
            return new ComponentListRequest(name,
                owner,
                getArrayAsCommaJoinedString(states),
                versionState,
                getArrayAsCommaJoinedString(campaignIds),
                getArrayAsCommaJoinedString(havingAnyTags),
                getArrayAsCommaJoinedString(havingAllTags),
                getArrayAsCommaJoinedString(excludeHavingAnyTags),
                getArrayAsCommaJoinedString(excludeHavingAllTags),
                getArrayAsCommaJoinedString(havingAnyTypes),
                getArrayAsCommaJoinedString(havingAllTypes),
                targetComponentId,
                targetSocketName,
                showAll,
                limit,
                offset,
                timeZone);
        }

        private Optional<String> getArrayAsCommaJoinedString(String[] array) {
            if (ArrayUtils.isEmpty(array)) {
                return Optional.empty();
            }
            return Optional.of(String.join(COMMA, array));
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}
