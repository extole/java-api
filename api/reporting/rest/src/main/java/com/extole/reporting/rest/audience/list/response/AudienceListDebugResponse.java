package com.extole.reporting.rest.audience.list.response;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListType;

public class AudienceListDebugResponse {

    private static final String TYPE = "type";
    private static final String STATE = "state";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DEBUG_MESSAGE = "debug_message";
    private static final String ERROR_CODE = "error_code";
    private static final String ERROR_MESSAGE = "error_message";
    private final AudienceListType type;

    private final String id;
    private final String name;
    private final AudienceListState audienceListState;
    private final Optional<String> debugMessage;
    private final Optional<String> errorCode;
    private final Optional<String> errorMessage;

    public AudienceListDebugResponse(@JsonProperty(TYPE) AudienceListType type,
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(STATE) AudienceListState audienceListState,
        @JsonProperty(DEBUG_MESSAGE) Optional<String> debugMessage,
        @JsonProperty(ERROR_CODE) Optional<String> errorCode,
        @JsonProperty(ERROR_MESSAGE) Optional<String> errorMessage) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.debugMessage = debugMessage;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.audienceListState = audienceListState;

    }

    @JsonProperty(TYPE)
    public AudienceListType getType() {
        return type;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DEBUG_MESSAGE)
    public Optional<String> getDebugMessage() {
        return debugMessage;
    }

    @JsonProperty(ERROR_CODE)
    public Optional<String> getErrorCode() {
        return errorCode;
    }

    @JsonProperty(ERROR_MESSAGE)
    public Optional<String> getErrorMessage() {
        return errorMessage;
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
