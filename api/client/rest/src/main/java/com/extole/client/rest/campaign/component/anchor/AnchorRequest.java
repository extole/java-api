package com.extole.client.rest.campaign.component.anchor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class AnchorRequest {

    private static final String JSON_SOURCE_ELEMENT_ID = "source_element_id";
    private static final String JSON_TARGET_ELEMENT_ID = "target_element_id";

    private final Id<?> sourceElementId;
    private final Id<?> targetElementId;

    @JsonCreator
    public AnchorRequest(@JsonProperty(JSON_SOURCE_ELEMENT_ID) Id<?> sourceElementId,
        @JsonProperty(JSON_TARGET_ELEMENT_ID) Id<?> targetElementId) {
        this.sourceElementId = sourceElementId;
        this.targetElementId = targetElementId;
    }

    @JsonProperty(JSON_SOURCE_ELEMENT_ID)
    public Id<?> getSourceElementId() {
        return sourceElementId;
    }

    @JsonProperty(JSON_TARGET_ELEMENT_ID)
    public Id<?> getTargetElementId() {
        return targetElementId;
    }

    public static AnchorRequest.Builder builder() {
        return new AnchorRequest.Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {

        private Id<?> sourceElementId;
        private Id<?> targetElementId;

        private Builder() {

        }

        public AnchorRequest.Builder withSourceElementId(Id<?> sourceElementId) {
            this.sourceElementId = sourceElementId;
            return this;
        }

        public AnchorRequest.Builder withTargetElementId(Id<?> targetElementId) {
            this.targetElementId = targetElementId;
            return this;
        }

        public AnchorRequest.Builder withSourceElementId(String sourceElementId) {
            this.sourceElementId = Id.valueOf(sourceElementId);
            return this;
        }

        public AnchorRequest.Builder withTargetElementId(String targetElementId) {
            this.targetElementId = Id.valueOf(targetElementId);
            return this;
        }

        public AnchorRequest build() {
            return new AnchorRequest(sourceElementId,
                targetElementId);
        }
    }
}
