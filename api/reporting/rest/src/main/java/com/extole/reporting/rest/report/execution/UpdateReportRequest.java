package com.extole.reporting.rest.report.execution;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;
import com.extole.reporting.rest.report.ReportTypeScope;

public class UpdateReportRequest {
    private static final String VISIBLE = "visible";
    private static final String DISPLAY_NAME = "display_name";
    private static final String SCOPES = "scopes";
    private static final String TAGS = "tags";

    @Deprecated // TODO should be removed ENG-8856
    private final Omissible<Boolean> visible;
    private final Omissible<String> displayName;
    private final Omissible<Set<ReportTypeScope>> scopes;
    private final Omissible<Set<String>> tags;

    public UpdateReportRequest(
        @JsonProperty(VISIBLE) Omissible<Boolean> visible,
        @JsonProperty(DISPLAY_NAME) Omissible<String> displayName,
        @JsonProperty(SCOPES) Omissible<Set<ReportTypeScope>> scopes,
        @JsonProperty(TAGS) Omissible<Set<String>> tags) {
        this.visible = visible;
        this.displayName = displayName;
        this.scopes = scopes;
        this.tags = tags;
    }

    @Deprecated // TODO should be removed ENG-8856
    @JsonProperty(VISIBLE)
    public Omissible<Boolean> isVisible() {
        return visible;
    }

    @JsonProperty(DISPLAY_NAME)
    public Omissible<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(SCOPES)
    public Omissible<Set<ReportTypeScope>> getScopes() {
        return scopes;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<Boolean> visible = Omissible.omitted();
        private Omissible<String> displayName = Omissible.omitted();
        private Omissible<Set<ReportTypeScope>> scopes = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withVisible(Boolean visible) {
            this.visible = Omissible.of(visible);
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Omissible.of(displayName);
            return this;
        }

        public Builder withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = Omissible.of(scopes);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public UpdateReportRequest build() {
            return new UpdateReportRequest(visible, displayName, scopes, tags);
        }
    }
}
