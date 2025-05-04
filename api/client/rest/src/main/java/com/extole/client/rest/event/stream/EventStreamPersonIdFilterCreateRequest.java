package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;

public class EventStreamPersonIdFilterCreateRequest
    extends EventStreamFilterCreateRequest {
    public static final String TYPE_PERSON_ID = "PERSON_ID";

    private static final String PERSON_IDS = "person_ids";

    private final Omissible<List<Id<?>>> personIds;

    public EventStreamPersonIdFilterCreateRequest(
        @JsonProperty(PERSON_IDS) Omissible<List<Id<?>>> personIds) {
        super(EventFilterType.PERSON_ID);
        this.personIds = personIds;
    }

    @JsonProperty(PERSON_IDS)
    public Omissible<List<Id<?>>> getPersonIds() {
        return personIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<List<Id<?>>> personIds = Omissible.omitted();

        private Builder() {
        }

        public Builder withPersonIds(Omissible<List<Id<?>>> personIds) {
            this.personIds = personIds;
            return this;
        }

        public EventStreamPersonIdFilterCreateRequest build() {
            return new EventStreamPersonIdFilterCreateRequest(personIds);
        }
    }
}
