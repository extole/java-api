package com.extole.reporting.rest.file.assets;

import java.util.Optional;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class FileAssetsQueryParams {
    private static final String NAME = "name";
    private static final String USER_ID = "user_id";
    private static final String STATUSES = "statuses";
    private static final String TAGS = "tags";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final String name;
    private final String userId;
    private final Set<String> tags;
    private final Set<FileAssetStatus> statuses;
    private final Integer limit;
    private final Integer offset;

    public FileAssetsQueryParams(
        @Parameter(description = "Optional name filter")
        @QueryParam(NAME) String name,
        @Parameter(description = "Optional userId filter")
        @QueryParam(USER_ID) String userId,
        @Parameter(description = "Optional statuses filter")
        @QueryParam(STATUSES) Set<FileAssetStatus> statuses,
        @Parameter(description = "Optional tags filter")
        @QueryParam(TAGS) Set<String> tags,
        @Parameter(description = "Optional limit filter, defaults to 100")
        @QueryParam(LIMIT) @DefaultValue("100") Integer limit,
        @Parameter(description = "Optional offset filter, defaults to 0")
        @QueryParam(OFFSET) @DefaultValue("0") Integer offset) {
        this.name = name;
        this.userId = userId;
        this.tags = tags;
        this.statuses = statuses;
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam(NAME)
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @QueryParam(USER_ID)
    public Optional<String> getUserId() {
        return Optional.ofNullable(userId);
    }

    @QueryParam(STATUSES)
    public Optional<Set<FileAssetStatus>> getStatuses() {
        return Optional.ofNullable(statuses);
    }

    @QueryParam(TAGS)
    public Optional<Set<String>> getTags() {
        return Optional.ofNullable(tags);
    }

    @QueryParam(LIMIT)
    public Optional<Integer> getLimit() {
        return Optional.ofNullable(limit);
    }

    @QueryParam(OFFSET)
    public Optional<Integer> getOffset() {
        return Optional.ofNullable(offset);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String userId;
        private Set<String> tags;
        private Set<FileAssetStatus> statuses;
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withStatuses(Set<FileAssetStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
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

        public FileAssetsQueryParams build() {
            return new FileAssetsQueryParams(name, userId, statuses, tags, limit, offset);
        }
    }
}
