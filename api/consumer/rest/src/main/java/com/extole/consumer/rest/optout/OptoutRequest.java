package com.extole.consumer.rest.optout;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class OptoutRequest {

    private static final String OPTOUT = "optout";
    private static final String TYPE = "type";
    private static final String LIST_TYPE = "list_type";
    private static final String LIST_NAME = "list_name";
    private static final String SOURCE = "source";

    public enum OptoutType {
        FRIEND, ADVOCATE
    }

    private Boolean optout = Boolean.TRUE;
    private final OptoutType type;
    private final String listType;
    private final String listName;
    private final String source;

    @JsonCreator
    public OptoutRequest(@JsonProperty(OPTOUT) Boolean optout,
        @Nullable @JsonProperty(TYPE) OptoutType type,
        @Nullable @JsonProperty(LIST_TYPE) String listType,
        @Nullable @JsonProperty(LIST_NAME) String listName,
        @Nullable @JsonProperty(SOURCE) String source) {
        this.optout = optout;
        this.type = type;
        this.listType = listType;
        this.listName = listName;
        this.source = source;
    }

    @JsonProperty(LIST_TYPE)
    public String getListType() {
        return listType;
    }

    @JsonProperty(LIST_NAME)
    public String getListName() {
        return listName;
    }

    @JsonProperty(SOURCE)
    public String getSource() {
        return source;
    }

    @JsonProperty(OPTOUT)
    public Boolean getOptout() {
        return optout;
    }

    @JsonProperty(TYPE)
    @Nullable
    public OptoutType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
