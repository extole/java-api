package com.extole.client.rest.campaign.component.setting;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;

public final class ComponentVariablesDownloadRequest {
    private static final String COMMA = ",";

    private static final String QUERY_PARAM_EXCLUDE_DISABLED_CREATIVES = "exclude_disabled_creatives";
    private static final String QUERY_PARAM_HAVING_ANY_TAGS = "having_any_tags";
    private static final String QUERY_PARAM_HAVING_ALL_TAGS = "having_all_tags";
    private static final String QUERY_PARAM_EXCLUDE_HAVING_ANY_TAGS = "exclude_having_any_tags";
    private static final String QUERY_PARAM_EXCLUDE_HAVING_ALL_TAGS = "exclude_having_all_tags";
    private static final String QUERY_ENABLED_VARIANTS_ONLY = "enabled_variants_only";
    private static final String QUERY_EXCLUDE_INHERITING = "exclude_inheriting";

    private final boolean excludeDisabledCreatives;
    private final String havingAnyTags;
    private final String havingAllTags;
    private final String excludeHavingAnyTags;
    private final String excludeHavingAllTags;
    private final boolean enabledVariantsOnly;
    private final boolean excludeInheriting;

    public static ComponentVariablesDownloadRequest.Builder builder() {
        return new ComponentVariablesDownloadRequest.Builder();
    }

    public ComponentVariablesDownloadRequest(
        @DefaultValue("false") @QueryParam(QUERY_PARAM_EXCLUDE_DISABLED_CREATIVES) boolean excludeDisabledCreatives,
        @Nullable @QueryParam(QUERY_PARAM_HAVING_ANY_TAGS) String havingAnyTags,
        @Nullable @QueryParam(QUERY_PARAM_HAVING_ALL_TAGS) String havingAllTags,
        @Nullable @QueryParam(QUERY_PARAM_EXCLUDE_HAVING_ANY_TAGS) String excludeHavingAnyTags,
        @Nullable @QueryParam(QUERY_PARAM_EXCLUDE_HAVING_ALL_TAGS) String excludeHavingAllTags,
        @DefaultValue("false") @QueryParam(QUERY_ENABLED_VARIANTS_ONLY) boolean enabledVariantsOnly,
        @DefaultValue("false") @QueryParam(QUERY_EXCLUDE_INHERITING) boolean excludeInheriting) {
        this.excludeDisabledCreatives = excludeDisabledCreatives;
        this.havingAnyTags = havingAnyTags;
        this.havingAllTags = havingAllTags;
        this.excludeHavingAnyTags = excludeHavingAnyTags;
        this.excludeHavingAllTags = excludeHavingAllTags;
        this.enabledVariantsOnly = enabledVariantsOnly;
        this.excludeInheriting = excludeInheriting;
    }

    @QueryParam(QUERY_PARAM_EXCLUDE_DISABLED_CREATIVES)
    public boolean getExcludeDisabledCreatives() {
        return excludeDisabledCreatives;
    }

    @QueryParam(QUERY_PARAM_HAVING_ANY_TAGS)
    @Nullable
    public String getHavingAnyTags() {
        return havingAnyTags;
    }

    @QueryParam(QUERY_PARAM_HAVING_ALL_TAGS)
    @Nullable
    public String getHavingAllTags() {
        return havingAllTags;
    }

    @QueryParam(QUERY_PARAM_EXCLUDE_HAVING_ANY_TAGS)
    @Nullable
    public String getExcludeHavingAnyTags() {
        return excludeHavingAnyTags;
    }

    @QueryParam(QUERY_PARAM_EXCLUDE_HAVING_ALL_TAGS)
    @Nullable
    public String getExcludeHavingAllTags() {
        return excludeHavingAllTags;
    }

    @QueryParam(QUERY_ENABLED_VARIANTS_ONLY)
    public boolean getEnabledVariants() {
        return enabledVariantsOnly;
    }

    @QueryParam(QUERY_EXCLUDE_INHERITING)
    public boolean getExcludeInheriting() {
        return excludeInheriting;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {

        private boolean excludeDisabledCreatives = false;
        private String[] havingAnyTags = new String[] {};
        private String[] havingAllTags = new String[] {};
        private String[] excludeHavingAnyTags = new String[] {};
        private String[] excludeHavingAllTags = new String[] {};
        private boolean enabledVariantsOnly = false;
        private boolean excludeInheriting = false;

        private Builder() {
        }

        public ComponentVariablesDownloadRequest.Builder
            withExcludeDisabledCreatives(boolean excludeDisabledCreatives) {
            this.excludeDisabledCreatives = excludeDisabledCreatives;
            return this;
        }

        public ComponentVariablesDownloadRequest.Builder withHavingAnyTags(String... havingAnyTags) {
            this.havingAnyTags = havingAnyTags;
            return this;
        }

        public ComponentVariablesDownloadRequest.Builder withHavingAllTags(String... havingAllTags) {
            this.havingAllTags = havingAllTags;
            return this;
        }

        public ComponentVariablesDownloadRequest.Builder withExcludeHavingAnyTags(String... excludeHavingAnyTags) {
            this.excludeHavingAnyTags = excludeHavingAnyTags;
            return this;
        }

        public ComponentVariablesDownloadRequest.Builder withExcludeHavingAllTags(String... excludeHavingAllTags) {
            this.excludeHavingAllTags = excludeHavingAllTags;
            return this;
        }

        public ComponentVariablesDownloadRequest.Builder withEnabledVariants(boolean enabledVariantsOnly) {
            this.enabledVariantsOnly = enabledVariantsOnly;
            return this;
        }

        public ComponentVariablesDownloadRequest.Builder withExcludeInheriting(boolean excludeInheriting) {
            this.excludeInheriting = excludeInheriting;
            return this;
        }

        public ComponentVariablesDownloadRequest build() {
            return new ComponentVariablesDownloadRequest(
                excludeDisabledCreatives,
                String.join(COMMA, havingAnyTags),
                String.join(COMMA, havingAllTags),
                String.join(COMMA, excludeHavingAnyTags),
                String.join(COMMA, excludeHavingAllTags),
                enabledVariantsOnly,
                excludeInheriting);
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}
