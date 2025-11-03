package com.extole.reporting.rest.report.runner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.ReportFormat;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportRunnerViewResponse.JSON_TYPE)
@JsonSubTypes({
    @Type(value = ScheduledReportRunnerViewResponse.class, name = "SCHEDULED"),
    @Type(value = RefreshingReportRunnerViewResponse.class, name = "REFRESHING")
})
public abstract class ReportRunnerViewResponse {
    protected static final String JSON_TYPE = "type";
    protected static final String JSON_ID = "id";
    protected static final String JSON_NAME = "name";
    protected static final String JSON_REPORT_TYPE_NAME = "report_type_name";
    protected static final String JSON_FORMATS = "formats";
    protected static final String JSON_CREATED_DATE = "created_date";
    protected static final String JSON_UPDATED_DATE = "updated_date";
    protected static final String JSON_PARAMETERS = "parameters";
    protected static final String JSON_SCOPES = "scopes";
    protected static final String JSON_TAGS = "tags";
    protected static final String JSON_USER_ID = "user_id";
    protected static final String SFTP_SERVER_ID = "sftp_server_id";
    protected static final String JSON_PAUSED = "paused";
    protected static final String JSON_MERGING_CONFIGURATION = "merging_configuration";
    protected static final String JSON_NEXT_EXECUTION_DATE = "next_execution_date";

    private final String type;
    private final String id;
    private final String name;
    private final String reportTypeName;
    private final List<ReportFormat> formats;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final Map<String, ReportParameterResponse> parameters;
    private final Set<ReportTypeScope> scopes;
    private final Set<String> tags;
    private final String userId;
    private final String sftpServerId;
    private final Optional<PauseInfoResponse> paused;
    private final Optional<MergingConfigurationResponse> mergingConfiguration;
    private final ZonedDateTime nextExecutionDate;

    public ReportRunnerViewResponse(
        @JsonProperty(JSON_TYPE) String type,
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_REPORT_TYPE_NAME) String reportTypeName,
        @JsonProperty(JSON_FORMATS) List<ReportFormat> formats,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_PARAMETERS) Map<String, ReportParameterResponse> parameters,
        @JsonProperty(JSON_SCOPES) Set<ReportTypeScope> scopes,
        @JsonProperty(JSON_TAGS) Set<String> tags,
        @JsonProperty(JSON_USER_ID) String userId,
        @Nullable @JsonProperty(SFTP_SERVER_ID) String sftpServerId,
        @JsonProperty(JSON_PAUSED) Optional<PauseInfoResponse> paused,
        @JsonProperty(JSON_MERGING_CONFIGURATION) Optional<MergingConfigurationResponse> mergingConfiguration,
        @JsonProperty(JSON_NEXT_EXECUTION_DATE) ZonedDateTime nextExecutionDate) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.reportTypeName = reportTypeName;
        this.formats = formats;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.parameters = parameters;
        this.scopes = scopes != null ? ImmutableSet.copyOf(scopes) : ImmutableSet.of();
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.of();
        this.userId = userId;
        this.sftpServerId = sftpServerId;
        this.paused = paused;
        this.mergingConfiguration = mergingConfiguration;
        this.nextExecutionDate = nextExecutionDate;
    }

    @JsonProperty(JSON_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_REPORT_TYPE_NAME)
    public String getReportTypeName() {
        return reportTypeName;
    }

    @JsonProperty(JSON_FORMATS)
    public List<ReportFormat> getFormats() {
        return formats;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public Optional<ZonedDateTime> getUpdatedDate() {
        return Optional.ofNullable(updatedDate);
    }

    @JsonProperty(JSON_PARAMETERS)
    public Map<String, ReportParameterResponse> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_SCOPES)
    public Set<ReportTypeScope> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_USER_ID)
    public String getUserId() {
        return userId;
    }

    @Nullable
    @JsonProperty(SFTP_SERVER_ID)
    public String getSftpServerId() {
        return sftpServerId;
    }

    @JsonProperty(JSON_PAUSED)
    public Optional<PauseInfoResponse> getPaused() {
        return paused;
    }

    @JsonProperty(JSON_MERGING_CONFIGURATION)
    public Optional<MergingConfigurationResponse> getMergingConfiguration() {
        return mergingConfiguration;
    }

    @JsonProperty(JSON_NEXT_EXECUTION_DATE)
    public ZonedDateTime getNextExecutionDate() {
        return nextExecutionDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public abstract static class Builder {
        protected String id;
        protected String name;
        protected String reportTypeName;
        protected List<ReportFormat> formats = Lists.newArrayList();
        protected ZonedDateTime createdDate;
        protected ZonedDateTime updatedDate;
        protected Map<String, ReportParameterResponse> parameters = Maps.newHashMap();
        protected Set<ReportTypeScope> scopes;
        protected Set<String> tags;
        protected String userId;
        protected String sftpServerId;
        protected Optional<PauseInfoResponse> paused = Optional.empty();
        protected Optional<MergingConfigurationResponse> mergingConfiguration = Optional.empty();
        protected ZonedDateTime nextExecutionDate;

        protected Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withReportTypeName(String reportTypeName) {
            this.reportTypeName = reportTypeName;
            return this;
        }

        public Builder withFormats(List<ReportFormat> formats) {
            this.formats = Lists.newArrayList(formats);
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withUpdatedDate(ZonedDateTime updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public Builder withParameters(Map<String, ReportParameterResponse> parameters) {
            this.parameters = Maps.newHashMap(parameters);
            return this;
        }

        public Builder withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withSftpServerId(String sftpServerId) {
            this.sftpServerId = sftpServerId;
            return this;
        }

        public Builder withPaused(PauseInfoResponse paused) {
            this.paused = Optional.ofNullable(paused);
            return this;
        }

        public Builder withMergingConfiguration(MergingConfigurationResponse mergingConfiguration) {
            this.mergingConfiguration = Optional.ofNullable(mergingConfiguration);
            return this;
        }

        public Builder withNextExecutionDate(ZonedDateTime nextExecutionDate) {
            this.nextExecutionDate = nextExecutionDate;
            return this;
        }
    }
}
