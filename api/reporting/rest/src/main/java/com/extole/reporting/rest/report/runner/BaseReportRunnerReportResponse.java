package com.extole.reporting.rest.report.runner;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportParameterResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BaseReportRunnerReportResponse.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = NotExecutedReportRunnerReportResponse.class,
        name = NotExecutedReportRunnerReportResponse.TYPE),
    @JsonSubTypes.Type(value = ExecutedReportRunnerReportResponse.class,
        name = ExecutedReportRunnerReportResponse.TYPE),
    @JsonSubTypes.Type(value = RollingReportRunnerReportResponse.class, name = RollingReportRunnerReportResponse.TYPE)
})
public abstract class BaseReportRunnerReportResponse {
    protected static final String JSON_TYPE = "type";
    @Deprecated // TODO should delete after switch ENG-20815
    protected static final String NAME = "name";
    protected static final String REPORT_TYPE = "report_type";
    protected static final String DISPLAY_NAME = "display_name";
    protected static final String PARAMETERS = "parameters";
    protected static final String TAGS = "tags";

    private final String type;
    @Deprecated // TODO should delete after switch ENG-20815
    private final String name;
    protected final String reportType;
    private final String displayName;
    private final Map<String, ReportParameterResponse> parameters;
    private final Set<String> tags;

    public BaseReportRunnerReportResponse(
        @JsonProperty(JSON_TYPE) String type,
        @Deprecated // TODO should delete after switch ENG-20815
        @JsonProperty(NAME) String name,
        @JsonProperty(REPORT_TYPE) String reportType,
        @JsonProperty(DISPLAY_NAME) String displayName,
        @JsonProperty(PARAMETERS) Map<String, ReportParameterResponse> parameters,
        @JsonProperty(TAGS) Set<String> tags) {
        this.type = type;
        this.name = name;
        this.reportType = reportType;
        this.displayName = displayName;
        this.parameters = parameters;
        this.tags = tags;
    }

    @JsonProperty(JSON_TYPE)
    public String getType() {
        return type;
    }

    @Deprecated // TODO should delete after switch ENG-20815
    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(REPORT_TYPE)
    public String getReportType() {
        return reportType;
    }

    @JsonProperty(DISPLAY_NAME)
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty(PARAMETERS)
    public Map<String, ReportParameterResponse> getParameters() {
        return parameters;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static class Builder {
        protected String reportType;
        protected String displayName;
        protected Map<String, ReportParameterResponse> parameters = Maps.newHashMap();
        protected Set<String> tags = Sets.newHashSet();

        protected Builder() {
        }

        public Builder withReportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withParameters(Map<String, ReportParameterResponse> parameters) {
            this.parameters = Maps.newHashMap(parameters);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Sets.newHashSet(tags);
            return this;
        }
    }
}
