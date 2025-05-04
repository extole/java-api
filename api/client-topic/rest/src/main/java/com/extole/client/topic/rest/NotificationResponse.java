package com.extole.client.topic.rest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class NotificationResponse {

    private static final String EVENT_ID = "event_id";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_VERSION = "client_version";
    private static final String EVENT_TIME = "event_time";
    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String MESSAGE = "message";
    private static final String DATA = "data";
    private static final String LEVEL = "level";
    private static final String SNOOZE_ID = "snooze_id";
    private static final String USER_ID = "user_id";
    private static final String CHANNELS = "channels";
    private static final String CAUSE_EVENT_ID = "cause_event_id";
    private static final String CAUSE_USER_ID = "cause_user_id";
    private static final String SUBSCRIPTION_ID = "subscription_id";
    private static final String SUBSCRIPTION_DEDUPE_DURATION_MS = "subscription_dedupe_duration_ms";

    private final String eventId;
    private final String clientId;
    private final int clientVersion;
    private final ZonedDateTime eventTime;
    private final String name;
    private final Set<String> tags;
    private final String message;
    private final Map<String, String> data;
    private final Level level;
    private final Optional<String> snoozeId;
    private final String userId;
    private final List<ChannelType> channels;
    private final String causeEventId;
    private final Optional<String> causeUserId;
    private final String subscriptionId;
    private final long subscriptionDedupeDurationMs;

    public NotificationResponse(@JsonProperty(EVENT_ID) String eventId,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(CLIENT_VERSION) int clientVersion,
        @JsonProperty(EVENT_TIME) ZonedDateTime eventTime,
        @JsonProperty(NAME) String name,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(LEVEL) Level level,
        @JsonProperty(SNOOZE_ID) Optional<String> snoozeId,
        @JsonProperty(USER_ID) String userId,
        @JsonProperty(CHANNELS) List<ChannelType> channels,
        @JsonProperty(CAUSE_EVENT_ID) String causeEventId,
        @JsonProperty(CAUSE_USER_ID) Optional<String> causeUserId,
        @JsonProperty(SUBSCRIPTION_ID) String subscriptionId,
        @JsonProperty(SUBSCRIPTION_DEDUPE_DURATION_MS) long subscriptionDedupeDurationMs) {
        this.eventId = eventId;
        this.clientId = clientId;
        this.clientVersion = clientVersion;
        this.eventTime = eventTime;
        this.name = name;
        this.tags = tags;
        this.message = message;
        this.data = data;
        this.level = level;
        this.snoozeId = snoozeId;
        this.userId = userId;
        this.channels = channels;
        this.causeEventId = causeEventId;
        this.causeUserId = causeUserId;
        this.subscriptionId = subscriptionId;
        this.subscriptionDedupeDurationMs = subscriptionDedupeDurationMs;
    }

    @JsonProperty(EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(CLIENT_VERSION)
    public int getClientVersion() {
        return clientVersion;
    }

    @JsonProperty(EVENT_TIME)
    public ZonedDateTime getEventTime() {
        return eventTime;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(SNOOZE_ID)
    public Optional<String> getSnoozeId() {
        return snoozeId;
    }

    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(LEVEL)
    public Level getLevel() {
        return level;
    }

    @JsonProperty(CHANNELS)
    public List<ChannelType> getChannels() {
        return channels;
    }

    @JsonProperty(CAUSE_EVENT_ID)
    public String getCauseEventId() {
        return causeEventId;
    }

    @JsonProperty(CAUSE_USER_ID)
    public Optional<String> getCauseUserId() {
        return causeUserId;
    }

    @JsonProperty(SUBSCRIPTION_ID)
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonProperty(SUBSCRIPTION_DEDUPE_DURATION_MS)
    public long getSubscriptionDedupeDurationMs() {
        return subscriptionDedupeDurationMs;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
