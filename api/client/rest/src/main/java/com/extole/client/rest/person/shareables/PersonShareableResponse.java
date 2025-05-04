package com.extole.client.rest.person.shareables;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.extole.common.lang.ToString;

public class PersonShareableResponse {

    private static final String CODE = "code";
    private static final String KEY = "key";
    private static final String LINK = "link";
    private static final String CONTENT = "content";
    private static final String LABEL = "label";
    private static final String DATA = "data";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private final String code;
    private final String key;
    private final Optional<String> label;
    private final String link;
    private final Optional<PersonShareableContentResponse> content;
    private final Map<String, PersonShareableDataResponse> data;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public PersonShareableResponse(
        @JsonProperty(CODE) String code,
        @JsonProperty(KEY) String key,
        @JsonProperty(LABEL) Optional<String> label,
        @JsonProperty(LINK) String link,
        @JsonProperty(CONTENT) Optional<PersonShareableContentResponse> content,
        @JsonProperty(DATA) Map<String, PersonShareableDataResponse> data,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.code = code;
        this.key = key;
        this.label = label;
        this.link = link;
        this.content = content;
        this.data = data == null ? ImmutableMap.of() : ImmutableMap.copyOf(data);
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(KEY)
    public String getKey() {
        return key;
    }

    @JsonProperty(LABEL)
    public Optional<String> getLabel() {
        return label;
    }

    @JsonProperty(LINK)
    public String getLink() {
        return link;
    }

    @JsonProperty(CONTENT)
    public Optional<PersonShareableContentResponse> getContent() {
        return content;
    }

    @JsonProperty(DATA)
    public Map<String, PersonShareableDataResponse> getData() {
        return data;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new PersonShareableResponse.Builder();
    }

    public static final class Builder {
        private String code;
        private String key;
        private Optional<String> label = Optional.empty();
        private String link;
        private Optional<PersonShareableContentResponse> content = Optional.empty();

        private Map<String, PersonShareableDataResponse> data = Maps.newHashMap();
        private ZonedDateTime createdDate;
        private ZonedDateTime updatedDate;

        private Builder() {
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withKey(String key) {
            this.key = key;
            return this;
        }

        public Builder withLabel(String label) {
            this.label = Optional.ofNullable(label);
            return this;
        }

        public Builder withLink(String link) {
            this.link = link;
            return this;
        }

        public Builder withContent(PersonShareableContentResponse content) {
            this.content = Optional.ofNullable(content);
            return this;
        }

        public Builder withData(Map<String, PersonShareableDataResponse> data) {
            this.data = data;
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withUpdatedDate(ZonedDateTime updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public PersonShareableResponse build() {
            return new PersonShareableResponse(code, key, label, link, content, data, createdDate, updatedDate);
        }
    }
}
