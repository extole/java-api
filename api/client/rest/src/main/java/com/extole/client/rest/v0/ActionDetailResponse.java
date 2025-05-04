package com.extole.client.rest.v0;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class ActionDetailResponse {

    private static final String JSON_ACTION = "action";
    private static final String JSON_REVIEW_UPDATES = "review_updates";
    private static final String JSON_CONVERSIONS = "conversions";
    private static final String JSON_REGISTRATIONS = "registrations";
    private static final String JSON_SHARE_ACTION = "share_action";

    private final ActionResponse action;
    private final List<ReviewStatusUpdateResponse> reviewUpdates;
    private final List<ActionResponse> conversions;
    private final List<ActionResponse> registrations;
    private final ActionResponse shareAction;

    @JsonCreator
    public ActionDetailResponse(
        @JsonProperty(JSON_ACTION) ActionResponse action,
        @JsonProperty(JSON_REVIEW_UPDATES) List<ReviewStatusUpdateResponse> reviewUpdates,
        @JsonProperty(JSON_CONVERSIONS) List<ActionResponse> conversions,
        @JsonProperty(JSON_REGISTRATIONS) List<ActionResponse> registrations,
        @JsonProperty(JSON_SHARE_ACTION) ActionResponse shareAction) {
        this.action = action;
        this.reviewUpdates = reviewUpdates;
        this.conversions = conversions;
        this.registrations = registrations;
        this.shareAction = shareAction;
    }

    @JsonProperty(JSON_ACTION)
    public ActionResponse getAction() {
        return action;
    }

    @JsonProperty(JSON_REVIEW_UPDATES)
    public List<ReviewStatusUpdateResponse> getReviewUpdates() {
        return reviewUpdates;
    }

    @JsonProperty(JSON_CONVERSIONS)
    public List<ActionResponse> getConversions() {
        return conversions;
    }

    @JsonProperty(JSON_REGISTRATIONS)
    public List<ActionResponse> getRegistrations() {
        return registrations;
    }

    @JsonProperty(JSON_SHARE_ACTION)
    public ActionResponse getShareAction() {
        return shareAction;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
