package com.extole.client.topic.rest;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;

public class NotificationGetRequest {

    private final Optional<Integer> limit;
    private final Optional<Integer> offset;
    private final Optional<Boolean> wasSnoozed;
    private final Optional<Set<String>> havingAllTags;
    private final Optional<String> eventId;
    private final Optional<String> subscriptionId;

    public NotificationGetRequest(@QueryParam("limit") Optional<Integer> limit,
        @QueryParam("offset") Optional<Integer> offset,
        @QueryParam("was_snoozed") Optional<Boolean> wasSnoozed,
        @Nullable @QueryParam("having_all_tags") Set<String> havingAllTags,
        @QueryParam("event_id") Optional<String> eventId,
        @QueryParam("subscription_id") Optional<String> subscriptionId) {
        this.limit = limit;
        this.offset = offset;
        this.wasSnoozed = wasSnoozed;
        this.havingAllTags = Optional.ofNullable(havingAllTags);
        this.eventId = eventId;
        this.subscriptionId = subscriptionId;

    }

    @QueryParam("limit")
    public Optional<Integer> getLimit() {
        return limit;
    }

    @QueryParam("offset")
    public Optional<Integer> getOffset() {
        return offset;
    }

    @QueryParam("was_snoozed")
    public Optional<Boolean> wasSnoozed() {
        return wasSnoozed;
    }

    @QueryParam("having_all_tags")
    public Optional<Set<String>> getHavingAllTags() {
        return havingAllTags;
    }

    @QueryParam("event_id")
    public Optional<String> getEventId() {
        return eventId;
    }

    @QueryParam("subscription_id")
    public Optional<String> getSubscriptionId() {
        return subscriptionId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private Integer limit;
        private Integer offset;
        private Boolean wasSnoozed;
        private Set<String> havingAllTags;
        private String eventId;
        private String subscriptionId;

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

        public Builder withWasSnoozed(Boolean wasSnoozed) {
            this.wasSnoozed = wasSnoozed;
            return this;
        }

        public Builder withHavingAllTags(Set<String> havingAllTags) {
            this.havingAllTags = havingAllTags;
            return this;
        }

        public Builder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder withSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
            return this;
        }

        public NotificationGetRequest build() {
            return new NotificationGetRequest(Optional.ofNullable(limit), Optional.ofNullable(offset),
                Optional.ofNullable(wasSnoozed), havingAllTags, Optional.ofNullable(eventId),
                Optional.ofNullable(subscriptionId));
        }
    }
}
