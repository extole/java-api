package com.extole.reporting.rest.demo.data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class DemoDataRequest {

    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";
    private static final String EVENTS_PER_DAY = "events_per_day";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String FLOWS = "flows";
    private static final String EMAIL_RANDOM_PART_LENGTH = "email_random_part_length";
    private static final String FORMAT = "format";
    private static final String FILE_ASSET_NAME = "file_asset_name";

    private final ZonedDateTime startDate;
    private final ZonedDateTime endDate;
    private final int eventsPerDay;
    private final String programLabel;
    private final Integer emailRandomPartLength;
    private final List<DemoDataFlowRequest> flows;
    private final DemoDataFormat format;
    private final String fileAssetName;

    public DemoDataRequest(@JsonProperty(START_DATE) ZonedDateTime startDate,
        @JsonProperty(END_DATE) ZonedDateTime endDate,
        @JsonProperty(EVENTS_PER_DAY) int eventsPerDay,
        @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(EMAIL_RANDOM_PART_LENGTH) Integer emailRandomPartLength,
        @JsonProperty(FLOWS) List<DemoDataFlowRequest> flows,
        @Nullable @JsonProperty(FORMAT) DemoDataFormat format,
        @Nullable @JsonProperty(FILE_ASSET_NAME) String fileAssetName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventsPerDay = eventsPerDay;
        this.programLabel = programLabel;
        this.emailRandomPartLength = emailRandomPartLength;
        this.flows = flows;
        this.format = format;
        this.fileAssetName = fileAssetName;
    }

    @JsonProperty(START_DATE)
    public Optional<ZonedDateTime> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    @JsonProperty(END_DATE)
    public Optional<ZonedDateTime> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    @JsonProperty(EVENTS_PER_DAY)
    public int getEventsPerDay() {
        return eventsPerDay;
    }

    @JsonProperty(FLOWS)
    public Optional<List<DemoDataFlowRequest>> getFlows() {
        return Optional.ofNullable(flows);
    }

    @JsonProperty(PROGRAM_LABEL)
    public Optional<String> getProgramLabel() {
        return Optional.ofNullable(programLabel);
    }

    @JsonProperty(EMAIL_RANDOM_PART_LENGTH)
    public Optional<Integer> getEmailRandomPartLength() {
        return Optional.ofNullable(emailRandomPartLength);
    }

    @JsonProperty(FORMAT)
    public Optional<DemoDataFormat> getFormat() {
        return Optional.ofNullable(format);
    }

    @JsonProperty(FILE_ASSET_NAME)
    public Optional<String> getFileAssetName() {
        return Optional.ofNullable(fileAssetName);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        private int eventsPerDay;
        private Integer emailRandomPartLength;
        private String programLabel;
        private List<DemoDataFlowRequest> flows;
        private DemoDataFormat format;
        private String fileAssetName;

        private Builder() {
        }

        public Builder withStartDate(ZonedDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withEndDate(ZonedDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder withEventsPerDay(int eventsPerDay) {
            this.eventsPerDay = eventsPerDay;
            return this;
        }

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = programLabel;
            return this;
        }

        public Builder withEmaiLRandomPartLength(Integer randomPartLength) {
            this.emailRandomPartLength = randomPartLength;
            return this;
        }

        public Builder withFlows(List<DemoDataFlowRequest> flows) {
            this.flows = flows;
            return this;
        }

        public Builder withFormat(DemoDataFormat format) {
            this.format = format;
            return this;
        }

        public Builder withFileAssetName(String fileAssetName) {
            this.fileAssetName = fileAssetName;
            return this;
        }

        public DemoDataRequest build() {
            return new DemoDataRequest(startDate, endDate, eventsPerDay, programLabel, emailRandomPartLength, flows,
                format, fileAssetName);
        }
    }

    public enum DemoDataFormat {

        CSV, JSON
    }
}
