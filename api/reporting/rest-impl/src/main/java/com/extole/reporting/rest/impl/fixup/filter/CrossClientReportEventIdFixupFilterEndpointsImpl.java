package com.extole.reporting.rest.impl.fixup.filter;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.rest.fixup.FixupRestException;
import com.extole.reporting.rest.fixup.filter.CrossClientReportEventIdFixupFilterCreateRequest;
import com.extole.reporting.rest.fixup.filter.CrossClientReportEventIdFixupFilterEndpoints;
import com.extole.reporting.rest.fixup.filter.CrossClientReportEventIdFixupFilterResponse;
import com.extole.reporting.rest.fixup.filter.CrossClientReportEventIdFixupFilterUpdateRequest;
import com.extole.reporting.rest.fixup.filter.CrossClientReportIdFixupFilterValidationRestException;
import com.extole.reporting.rest.fixup.filter.FixupFilterRestException;
import com.extole.reporting.rest.fixup.filter.FixupFilterUpdateRestException;
import com.extole.reporting.rest.fixup.filter.FixupFilterValidationRestException;
import com.extole.reporting.service.fixup.FixupNotFoundException;
import com.extole.reporting.service.fixup.FixupRuntimeException;
import com.extole.reporting.service.fixup.filter.CrossClientReportIdFixupFilterBuilder;
import com.extole.reporting.service.fixup.filter.CrossClientReportIdFixupFilterClientIdAttributeNameMissingException;
import com.extole.reporting.service.fixup.filter.CrossClientReportIdFixupFilterClientIdAttributeNameTooLongException;
import com.extole.reporting.service.fixup.filter.CrossClientReportIdFixupFilterEventIdAttributeNameMissingException;
import com.extole.reporting.service.fixup.filter.CrossClientReportIdFixupFilterEventIdAttributeNameTooLongException;
import com.extole.reporting.service.fixup.filter.CrossClientReportIdFixupFilterReportDoesNotExistException;
import com.extole.reporting.service.fixup.filter.CrossClientReportIdFixupFilterReportIdMissingException;
import com.extole.reporting.service.fixup.filter.CrossClientReportIdFixupFilterService;
import com.extole.reporting.service.fixup.filter.FixupFilterAlreadyExistsException;
import com.extole.reporting.service.fixup.filter.FixupFilterNotEditableException;
import com.extole.reporting.service.fixup.filter.FixupFilterNotFoundException;
import com.extole.reporting.service.fixup.filter.FixupFilterValidationException;

@Provider
public class CrossClientReportEventIdFixupFilterEndpointsImpl implements CrossClientReportEventIdFixupFilterEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final CrossClientReportIdFixupFilterService fixupFilterService;
    private final CrossClientReportEventIdFixupFilterRestMapper restMapper;

    @Autowired
    public CrossClientReportEventIdFixupFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CrossClientReportIdFixupFilterService fixupFilterService,
        CrossClientReportEventIdFixupFilterRestMapper restMapper) {
        this.authorizationProvider = authorizationProvider;
        this.fixupFilterService = fixupFilterService;
        this.restMapper = restMapper;
    }

    @Override
    public CrossClientReportEventIdFixupFilterResponse getFilter(String accessToken, String fixupId, String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper
                .toResponse(fixupFilterService.get(authorization, Id.valueOf(fixupId), Id.valueOf(filterId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .addParameter("fixup_id", e.getFixupId())
                .withCause(e).build();
        } catch (FixupFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterRestException.class)
                .withErrorCode(FixupFilterRestException.FILTER_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .addParameter("fixup_filter_id", e.getFilterId()).build();
        }
    }

    @Override
    public CrossClientReportEventIdFixupFilterResponse createFilter(String accessToken, String fixupId,
        CrossClientReportEventIdFixupFilterCreateRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterValidationRestException,
        CrossClientReportIdFixupFilterValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            CrossClientReportIdFixupFilterBuilder filterBuilder =
                fixupFilterService.create(authorization, Id.valueOf(fixupId))
                    .withReportId(request.getReportId() != null ? Id.valueOf(request.getReportId()) : null)
                    .withEventIdAttributeName(request.getEventIdAttributeName())
                    .withClientIdAttributeName(request.getClientIdAttributeName());
            return restMapper.toResponse(filterBuilder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId).build();
        } catch (FixupFilterAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterValidationRestException.class)
                .withErrorCode(FixupFilterValidationRestException.FILTER_ALREADY_EXISTS)
                .addParameter("fixup_id", fixupId)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterReportIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.REPORT_ID_MISSING)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterReportDoesNotExistException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.REPORT_ID_INVALID)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterClientIdAttributeNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.CLIENT_ID_FIELD_MISSING)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterClientIdAttributeNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.CLIENT_ID_FIELD_LENGTH)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterEventIdAttributeNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.EVENT_ID_FIELD_MISSING)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterEventIdAttributeNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.EVENT_ID_FIELD_LENGTH)
                .withCause(e).build();
        } catch (FixupFilterValidationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public CrossClientReportEventIdFixupFilterResponse updateFilter(String accessToken, String fixupId, String filterId,
        CrossClientReportEventIdFixupFilterUpdateRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        CrossClientReportIdFixupFilterValidationRestException, FixupFilterUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            CrossClientReportIdFixupFilterBuilder filterBuilder =
                fixupFilterService.update(authorization, Id.valueOf(fixupId), Id.valueOf(filterId));
            if (!request.getReportId().isOmitted()) {
                filterBuilder.withReportId(Id.valueOf(request.getReportId().getValue()));
            }
            if (!request.getEventIdAttributeName().isOmitted()) {
                filterBuilder.withEventIdAttributeName(request.getEventIdAttributeName().getValue());
            }
            if (!request.getClientIdAttributeName().isOmitted()) {
                filterBuilder.withClientIdAttributeName(request.getClientIdAttributeName().getValue());
            }
            return restMapper.toResponse(filterBuilder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterRestException.class)
                .withErrorCode(FixupFilterRestException.FILTER_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .addParameter("fixup_filter_id", e.getFilterId())
                .build();
        } catch (CrossClientReportIdFixupFilterReportIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.REPORT_ID_MISSING)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterReportDoesNotExistException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.REPORT_ID_INVALID)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterClientIdAttributeNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.CLIENT_ID_FIELD_MISSING)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterClientIdAttributeNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.CLIENT_ID_FIELD_LENGTH)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterEventIdAttributeNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.EVENT_ID_FIELD_MISSING)
                .withCause(e).build();
        } catch (CrossClientReportIdFixupFilterEventIdAttributeNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CrossClientReportIdFixupFilterValidationRestException.class)
                .withErrorCode(CrossClientReportIdFixupFilterValidationRestException.EVENT_ID_FIELD_LENGTH)
                .withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .build();
        } catch (FixupFilterNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterUpdateRestException.class)
                .withErrorCode(FixupFilterUpdateRestException.NOT_EDITABLE)
                .withCause(e).build();
        } catch (FixupFilterValidationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public CrossClientReportEventIdFixupFilterResponse deleteFilter(String accessToken, String fixupId, String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        FixupFilterUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(
                fixupFilterService.delete(authorization, Id.valueOf(fixupId), Id.valueOf(filterId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId).build();
        } catch (FixupFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterRestException.class)
                .withErrorCode(FixupFilterRestException.FILTER_NOT_FOUND)
                .addParameter("fixup_id", fixupId)
                .addParameter("fixup_filter_id", filterId)
                .withCause(e).build();
        } catch (FixupRuntimeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (FixupFilterNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupFilterUpdateRestException.class)
                .withErrorCode(FixupFilterUpdateRestException.NOT_EDITABLE)
                .withCause(e).build();
        }
    }
}
