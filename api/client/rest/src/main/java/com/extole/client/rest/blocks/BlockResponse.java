package com.extole.client.rest.blocks;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BlockResponse {

    private static final String BLOCK_ID = "block_id";
    private static final String FILTER_TYPE = "filter_type";
    private static final String LIST_TYPE = "list_type";
    private static final String SOURCE = "source";
    private static final String CLIENT_ID = "client_id";
    private static final String CREATED_DATE = "created_date";
    private static final String VALUE = "value";
    private static final String USER_ID = "user_id";

    private final String blockId;
    private final FilterType filterType;
    private final ListType listType;
    private final String source;
    private final String clientId;
    private final String value;
    private final ZonedDateTime createdDate;
    private final String userId;

    @JsonCreator
    public BlockResponse(
        @JsonProperty(BLOCK_ID) String blockId,
        @JsonProperty(FILTER_TYPE) FilterType filterType,
        @JsonProperty(LIST_TYPE) ListType listType,
        @JsonProperty(SOURCE) String source,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(VALUE) String value,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @Nullable @JsonProperty(USER_ID) String userId) {
        this.blockId = blockId;
        this.filterType = filterType;
        this.listType = listType;
        this.source = source;
        this.clientId = clientId;
        this.value = value;
        this.createdDate = createdDate;
        this.userId = userId;
    }

    @JsonProperty(BLOCK_ID)
    public String getBlockId() {
        return blockId;
    }

    @JsonProperty(FILTER_TYPE)
    public FilterType getFilterType() {
        return filterType;
    }

    @JsonProperty(LIST_TYPE)
    public ListType getListType() {
        return listType;
    }

    @JsonProperty(SOURCE)
    public String getSource() {
        return source;
    }

    @Nullable
    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return this.clientId;
    }

    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @Nullable
    @JsonProperty(USER_ID)
    public String getUserId() {
        return this.userId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
