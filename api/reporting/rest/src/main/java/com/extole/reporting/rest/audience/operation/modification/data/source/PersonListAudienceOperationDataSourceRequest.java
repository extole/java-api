package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceType;

public class PersonListAudienceOperationDataSourceRequest extends AudienceOperationDataSourceRequest {

    public static final String DATA_SOURCE_TYPE = "PERSON_LIST";

    private static final String AUDIENCE_MEMBERS = "audience_members";

    private final List<MemberRequest> audienceMembers;

    public PersonListAudienceOperationDataSourceRequest(
        @JsonProperty(AUDIENCE_MEMBERS) List<MemberRequest> audienceMembers) {
        super(AudienceOperationDataSourceType.PERSON_LIST);
        this.audienceMembers = audienceMembers;
    }

    @JsonProperty(AUDIENCE_MEMBERS)
    public List<MemberRequest> getAudienceMembers() {
        return audienceMembers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private List<MemberRequest> audienceMembers = new ArrayList<>();

        private Builder() {

        }

        public Builder withAudienceMembers(List<MemberRequest> audienceMembers) {
            this.audienceMembers = audienceMembers;
            return this;
        }

        public PersonListAudienceOperationDataSourceRequest build() {
            return new PersonListAudienceOperationDataSourceRequest(audienceMembers);
        }

    }

}
