package com.extole.reporting.rest.impl.audience.operation.modification.data.source;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.entity.report.audience.operation.modification.data.source.PersonListAudienceOperationDataSource;
import com.extole.reporting.rest.audience.operation.modification.data.source.MemberResponse;
import com.extole.reporting.rest.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceResponse;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationDataSourceResponseMapper;

@Component
public class PersonListAudienceOperationDataSourceResponseMapper implements AudienceOperationDataSourceResponseMapper<
    PersonListAudienceOperationDataSource, PersonListAudienceOperationDataSourceResponse> {

    @Override
    public PersonListAudienceOperationDataSourceResponse toResponse(PersonListAudienceOperationDataSource dataSource) {
        return new PersonListAudienceOperationDataSourceResponse(dataSource.getAudienceMembers()
            .stream()
            .map(member -> new MemberResponse(
                member.getIdentityKey().getName(),
                member.getIdentityKeyValue(),
                member.getData()))
            .collect(Collectors.toList()));
    }

    @Override
    public AudienceOperationDataSourceType getType() {
        return AudienceOperationDataSourceType.PERSON_LIST;
    }

}
