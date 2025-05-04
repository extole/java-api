package com.extole.client.rest.sftp;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;

public class SftpDestinationListQuery {

    static final String QUERY_PARAM_TYPE = "type";
    static final String QUERY_PARAM_NAME = "name";
    static final String QUERY_PARAM_USERNAME = "username";
    static final String QUERY_PARAM_HOST = "host";
    static final String QUERY_PARAM_LIMIT = "limit";
    static final String QUERY_PARAM_OFFSET = "offset";

    private final Optional<SftpDestinationType> type;
    private final Optional<String> name;
    private final Optional<String> username;
    private final Optional<String> host;
    private final Integer limit;
    private final Integer offset;

    SftpDestinationListQuery(@Nullable @QueryParam(QUERY_PARAM_TYPE) SftpDestinationType type,
        @Nullable @QueryParam(QUERY_PARAM_NAME) String name,
        @Nullable @QueryParam(QUERY_PARAM_USERNAME) String username,
        @Nullable @QueryParam(QUERY_PARAM_HOST) String host,
        @DefaultValue("100") @QueryParam(QUERY_PARAM_LIMIT) Integer limit,
        @DefaultValue("0") @QueryParam(QUERY_PARAM_OFFSET) Integer offset) {
        this.name = Optional.ofNullable(name);
        this.type = Optional.ofNullable(type);
        this.username = Optional.ofNullable(username);
        this.host = Optional.ofNullable(host);
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam(QUERY_PARAM_TYPE)
    public Optional<SftpDestinationType> getType() {
        return type;
    }

    @QueryParam(QUERY_PARAM_NAME)
    public Optional<String> getName() {
        return name;
    }

    @QueryParam(QUERY_PARAM_USERNAME)
    public Optional<String> getUsername() {
        return username;
    }

    @QueryParam(QUERY_PARAM_HOST)
    public Optional<String> getHost() {
        return host;
    }

    @QueryParam(QUERY_PARAM_LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @QueryParam(QUERY_PARAM_OFFSET)
    public Integer getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private SftpDestinationType type;
        private String name;
        private String username;
        private String host;
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withType(SftpDestinationType type) {
            this.type = type;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withHost(String host) {
            this.host = host;
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public SftpDestinationListQuery build() {
            return new SftpDestinationListQuery(type, name, username, host, limit, offset);
        }
    }
}
