package com.extole.client.rest.shareable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class UpdateClientShareableOwnerRequest {

    private static final String NEW_PERSON_ID = "new_person_id";

    private final String newPersonId;

    @JsonCreator
    public UpdateClientShareableOwnerRequest(@JsonProperty(NEW_PERSON_ID) String newPersonId) {
        this.newPersonId = newPersonId;
    }

    @JsonProperty(NEW_PERSON_ID)
    public String getNewPersonId() {
        return newPersonId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
