package com.extole.reporting.rest.impl.audience.operation.modification.data.source;

import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.rest.audience.operation.modification.data.source.MemberRequest;
import com.extole.reporting.rest.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationDataSourceRequestUploader;
import com.extole.reporting.service.audience.operation.AudienceOperationBuilder;
import com.extole.reporting.service.audience.operation.AudienceOperationDataSourceBuildException;
import com.extole.reporting.service.audience.operation.AudienceOperationParameterUpdateNotAllowedException;
import com.extole.reporting.service.audience.operation.modification.data.source.MemberInvalidIdentityKeyValueException;
import com.extole.reporting.service.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceBuilder;

@Component
public class PersonListAudienceOperationDataSourceRequestUploader
    implements AudienceOperationDataSourceRequestUploader<PersonListAudienceOperationDataSourceRequest> {

    @Override
    public void upload(AudienceOperationBuilder builder, PersonListAudienceOperationDataSourceRequest request)
        throws PersonListAudienceOperationDataSourceValidationRestException {
        try {
            PersonListAudienceOperationDataSourceBuilder sourceBuilder =
                builder.withDataSource(AudienceOperationDataSourceType.PERSON_LIST);

            for (MemberRequest memberRequest : request.getAudienceMembers()) {
                sourceBuilder.addAudienceMember()
                    .withIdentityKeyValue(memberRequest.getIdentityKeyValue())
                    .withData(memberRequest.getData());
            }

            sourceBuilder.done();
        } catch (MemberInvalidIdentityKeyValueException e) {
            throw RestExceptionBuilder.newBuilder(PersonListAudienceOperationDataSourceValidationRestException.class)
                .withErrorCode(PersonListAudienceOperationDataSourceValidationRestException.INVALID_IDENTITY_KEY_VALUE)
                .withCause(e)
                .addParameter("identity_key_value", e.getIdentityKeyValue())
                .build();
        } catch (AudienceOperationDataSourceBuildException | AudienceOperationParameterUpdateNotAllowedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationDataSourceType getType() {
        return AudienceOperationDataSourceType.PERSON_LIST;
    }

}
