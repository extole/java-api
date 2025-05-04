package com.extole.reporting.rest.audience.list.response;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = AudienceListResponse.TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StaticAudienceListResponse.class, name = StaticAudienceListResponse.AUDIENCE_TYPE),
    @JsonSubTypes.Type(value = DynamicAudienceListResponse.class, name = DynamicAudienceListResponse.AUDIENCE_TYPE),
    @JsonSubTypes.Type(value = UploadedAudienceListResponse.class, name = UploadedAudienceListResponse.AUDIENCE_TYPE)
})
public abstract class AudienceListResponse {

    protected static final String STATE = "state";
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";
    protected static final String EVENT_COLUMNS = "event_columns";
    protected static final String EVENT_DATA = "event_data";
    protected static final String TAGS = "tags";
    protected static final String TYPE = "type";
    protected static final String MEMBER_COUNT = "member_count";
    protected static final String LAST_UPDATE = "last_update";
    protected static final String ERROR_CODE = "error_code";
    protected static final String ERROR_MESSAGE = "error_message";

    private final String id;
    private final String name;
    private final AudienceListState audienceListState;
    private final Set<String> tags;
    private final AudienceListType type;
    private final Optional<String> description;
    private final Set<String> eventColumns;
    private final Map<String, String> eventData;
    private final Optional<Long> memberCount;
    private final Optional<ZonedDateTime> lastUpdatedDate;
    private final Optional<String> errorCode;
    private final Optional<String> errorMessage;

    public AudienceListResponse(@JsonProperty(TYPE) AudienceListType type,
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(STATE) AudienceListState audienceListState,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(EVENT_COLUMNS) Set<String> eventColumns,
        @JsonProperty(EVENT_DATA) Map<String, String> eventData,
        @JsonProperty(MEMBER_COUNT) Optional<Long> memberCount,
        @JsonProperty(LAST_UPDATE) Optional<ZonedDateTime> lastUpdatedDate,
        @JsonProperty(ERROR_CODE) Optional<String> errorCode,
        @JsonProperty(ERROR_MESSAGE) Optional<String> errorMessage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.eventColumns = eventColumns;
        this.eventData = eventData;
        this.memberCount = memberCount;
        this.lastUpdatedDate = lastUpdatedDate;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.audienceListState = audienceListState;
        this.type = type;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(EVENT_COLUMNS)
    public Set<String> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(EVENT_DATA)
    public Map<String, String> getEventData() {
        return eventData;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(ERROR_CODE)
    public Optional<String> getErrorCode() {
        return errorCode;
    }

    @JsonProperty(ERROR_MESSAGE)
    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    @JsonProperty(TYPE)
    public AudienceListType getType() {
        return type;
    }

    @JsonProperty(MEMBER_COUNT)
    public Optional<Long> getMemberCount() {
        return memberCount;
    }

    @JsonProperty(LAST_UPDATE)
    public Optional<ZonedDateTime> getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    @JsonProperty(STATE)
    public AudienceListState getAudienceListState() {
        return audienceListState;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
