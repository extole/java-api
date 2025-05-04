package com.extole.consumer.rest.report;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.extole.common.lang.ToString;

public class ReportResponse {
    private static final String REPORT_ID = "report_id";
    @Deprecated // TODO should delete after switch ENG-20815
    private static final String NAME = "name";
    private static final String REPORT_TYPE = "report_type";
    private static final String DISPLAY_NAME = "display_name";
    private static final String FORMATS = "formats";
    private static final String TAGS = "tags";
    private static final String COMPLETED_DATE = "completed_date";
    private static final String DOWNLOAD_URI = "download_uri";

    private final String reportId;
    private final String name;
    private final String reportType;
    private final String displayName;
    private final List<ReportFormat> formats;
    private final Set<String> tags;
    private final String completedDate;
    private final String downloadUri;

    public ReportResponse(
        @JsonProperty(REPORT_ID) String reportId,
        @Deprecated // TODO should delete after switch ENG-20815
        @JsonProperty(NAME) String name,
        @JsonProperty(REPORT_TYPE) String reportType,
        @JsonProperty(DISPLAY_NAME) String displayName,
        @JsonProperty(FORMATS) List<ReportFormat> formats,
        @JsonProperty(TAGS) Set<String> tags,
        @Nullable @JsonProperty(COMPLETED_DATE) String completedDate,
        @Nullable @JsonProperty(DOWNLOAD_URI) String downloadUri) {
        this.reportId = reportId;
        this.name = name;
        this.reportType = reportType;
        this.displayName = displayName;
        this.formats = formats;
        this.tags = tags;
        this.completedDate = completedDate;
        this.downloadUri = downloadUri;
    }

    @JsonProperty(REPORT_ID)
    public String getReportId() {
        return reportId;
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

    @JsonProperty(FORMATS)
    public List<ReportFormat> getFormats() {
        return formats;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @Nullable
    @JsonProperty(COMPLETED_DATE)
    public String getCompletedDate() {
        return completedDate;
    }

    @Nullable
    @JsonProperty(DOWNLOAD_URI)
    public String getDownloadUri() {
        return downloadUri;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String reportId;
        private String reportType;
        private String displayName;
        private List<ReportFormat> formats = Lists.newArrayList();
        private Set<String> tags = Sets.newHashSet();
        private String completedDate;
        private String downloadUri;

        private Builder() {
        }

        public Builder withReportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder withReportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withFormats(List<ReportFormat> formats) {
            this.formats = Lists.newArrayList(formats);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Sets.newHashSet(tags);
            return this;
        }

        public Builder withCompletedDate(String completedDate) {
            this.completedDate = completedDate;
            return this;
        }

        public Builder withDownloadUri(String downloadUri) {
            this.downloadUri = downloadUri;
            return this;
        }

        public ReportResponse build() {
            return new ReportResponse(reportId, reportType, reportType, displayName, formats, tags, completedDate,
                downloadUri);
        }
    }
}
