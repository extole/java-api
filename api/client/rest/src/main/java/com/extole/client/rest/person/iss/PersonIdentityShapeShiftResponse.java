package com.extole.client.rest.person.iss;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonIdentityShapeShiftResponse extends IdentityLogResponse {
    private static final String JSON_IDENTITY_SHAPE_SHIFT_TYPE = "identity_shape_shift_type";
    private static final String JSON_NEW_IDENTITY_VERSION = "identity_profile_update_version";

    private final IdentityShapeShiftType shapeShiftType;
    private final Optional<Integer> newIdentityUpdateVersion;

    @JsonCreator
    public PersonIdentityShapeShiftResponse(
        @JsonProperty(JSON_OLD_IDENTITY_KEY_NAME) Optional<String> oldIdentityKeyName,
        @JsonProperty(JSON_OLD_IDENTITY_KEY_VALUE) Optional<String> oldIdentityKeyValue,
        @JsonProperty(JSON_LOG_DATE) Optional<ZonedDateTime> logDate,
        @JsonProperty(JSON_IDENTITY_SHAPE_SHIFT_TYPE) IdentityShapeShiftType shapeShiftType,
        @JsonProperty(JSON_NEW_IDENTITY_VERSION) Optional<Integer> newIdentityUpdateVersion) {

        super(IdentityLogType.IDENTITY_SHIFT, oldIdentityKeyName, oldIdentityKeyValue, logDate);
        this.shapeShiftType = shapeShiftType;
        this.newIdentityUpdateVersion = newIdentityUpdateVersion;
    }

    @JsonProperty(JSON_IDENTITY_SHAPE_SHIFT_TYPE)
    public IdentityShapeShiftType getShapeShiftType() {
        return shapeShiftType;
    }

    @JsonProperty(JSON_NEW_IDENTITY_VERSION)
    public Optional<Integer> getNewIdentityUpdateVersion() {
        return newIdentityUpdateVersion;
    }

}
