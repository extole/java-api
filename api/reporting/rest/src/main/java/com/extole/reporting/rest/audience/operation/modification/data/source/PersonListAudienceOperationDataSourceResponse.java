package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceType;

public class PersonListAudienceOperationDataSourceResponse extends AudienceOperationDataSourceResponse {

    public static final String DATA_SOURCE_TYPE = "PERSON_LIST";

    private static final String AUDIENCE_MEMBERS = "audience_members";

    private final List<MemberResponse> audienceMembers;

    public PersonListAudienceOperationDataSourceResponse(
        @JsonProperty(AUDIENCE_MEMBERS) List<MemberResponse> audienceMembers) {
        super(AudienceOperationDataSourceType.PERSON_LIST);
        this.audienceMembers = ImmutableList.copyOf(audienceMembers);
    }

    @JsonProperty(AUDIENCE_MEMBERS)
    public List<MemberResponse> getAudienceMembers() {
        return audienceMembers;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
