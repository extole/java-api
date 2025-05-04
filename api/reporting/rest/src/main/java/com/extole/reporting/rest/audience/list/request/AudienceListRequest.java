package com.extole.reporting.rest.audience.list.request;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.reporting.rest.audience.list.AudienceListType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = AudienceListRequest.TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StaticAudienceListRequest.class, name = StaticAudienceListRequest.AUDIENCE_TYPE),
    @JsonSubTypes.Type(value = DynamicAudienceListRequest.class, name = DynamicAudienceListRequest.AUDIENCE_TYPE),
    @JsonSubTypes.Type(value = UploadedAudienceListRequest.class, name = UploadedAudienceListRequest.AUDIENCE_TYPE)
})
public abstract class AudienceListRequest {

    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";
    protected static final String EVENT_COLUMNS = "event_columns";
    protected static final String EVENT_DATA = "event_data";
    protected static final String TAGS = "tags";
    protected static final String TYPE = "type";

    private final Omissible<String> name;
    private final Omissible<String> description;
    private final Omissible<Set<String>> eventColumns;
    private final Omissible<Map<String, String>> eventData;
    private final Omissible<Set<String>> tags;
    private final AudienceListType type;

    public AudienceListRequest(
        @Parameter(description = "AudienceList type")
        @JsonProperty(TYPE) AudienceListType type,
        @Parameter(description = "AudienceList name")
        @JsonProperty(NAME) Omissible<String> name,
        @Parameter(description = "AudienceList description")
        @JsonProperty(DESCRIPTION) Omissible<String> description,
        @Parameter(description = "A list of columns that will be used when dispatching AudienceList")
        @JsonProperty(EVENT_COLUMNS) Omissible<Set<String>> eventColumns,
        @Parameter(description = "Data for the AudienceList")
        @JsonProperty(EVENT_DATA) Omissible<Map<String, String>> eventData,
        @Parameter(description = "A set of tags for the AudienceList")
        @JsonProperty(TAGS) Omissible<Set<String>> tags) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.eventColumns = eventColumns;
        this.eventData = eventData;
        this.type = type;
    }

    @JsonProperty(NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(DESCRIPTION)
    public Omissible<String> getDescription() {
        return description;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(EVENT_COLUMNS)
    public Omissible<Set<String>> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(EVENT_DATA)
    public Omissible<Map<String, String>> getEventData() {
        return eventData;
    }

    @JsonProperty(TYPE)
    public AudienceListType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
