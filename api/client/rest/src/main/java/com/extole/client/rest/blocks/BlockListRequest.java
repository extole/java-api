package com.extole.client.rest.blocks;

import java.time.ZoneId;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;
import com.extole.common.rest.time.TimeZoneParam;

public class BlockListRequest {

    public static final String DEFAULT_LIMIT = "100";
    public static final String DEFAULT_OFFSET = "0";
    private final Integer limit;
    private final Integer offset;
    private final ZoneId timeZone;

    public static Builder newblockListRequest() {
        return new Builder();
    }

    public BlockListRequest(
        @DefaultValue(DEFAULT_LIMIT) @QueryParam("limit") Integer limit,
        @DefaultValue(DEFAULT_OFFSET) @QueryParam("offset") Integer offset,
        @TimeZoneParam ZoneId timeZone) {
        this.limit = limit;
        this.offset = offset;
        this.timeZone = timeZone;
    }

    @QueryParam("limit")
    public Integer getLimit() {
        return limit;
    }

    @QueryParam("offset")
    public Integer getOffset() {
        return offset;
    }

    @TimeZoneParam
    public ZoneId getTimeZone() {
        return timeZone;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private Integer limit;
        private Integer offset;
        private ZoneId timeZone;

        private Builder() {
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder withTimeZone(ZoneId timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public BlockListRequest build() {
            return new BlockListRequest(
                limit,
                offset,
                timeZone);
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}
