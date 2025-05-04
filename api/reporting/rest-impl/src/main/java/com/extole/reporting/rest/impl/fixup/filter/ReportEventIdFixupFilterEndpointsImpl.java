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
import com.extole.reporting.rest.fixup.filter.FixupFilterRestException;
import com.extole.reporting.rest.fixup.filter.FixupFilterUpdateRestException;
import com.extole.reporting.rest.fixup.filter.FixupFilterValidationRestException;
import com.extole.reporting.rest.fixup.filter.ReportEventIdFixupFilterEndpoints;
import com.extole.reporting.rest.fixup.filter.ReportEventIdFixupFilterRequest;
import com.extole.reporting.rest.fixup.filter.ReportEventIdFixupFilterResponse;
import com.extole.reporting.rest.fixup.filter.ReportIdFixupFilterValidationRestException;
import com.extole.reporting.service.fixup.FixupNotFoundException;
import com.extole.reporting.service.fixup.FixupRuntimeException;
import com.extole.reporting.service.fixup.filter.FixupFilterAlreadyExistsException;
import com.extole.reporting.service.fixup.filter.FixupFilterNotEditableException;
import com.extole.reporting.service.fixup.filter.FixupFilterNotFoundException;
import com.extole.reporting.service.fixup.filter.FixupFilterValidationException;
import com.extole.reporting.service.fixup.filter.ReportIdFixupFilterBuilder;
import com.extole.reporting.service.fixup.filter.ReportIdFixupFilterService;

@Provider
public class ReportEventIdFixupFilterEndpointsImpl implements ReportEventIdFixupFilterEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportIdFixupFilterService reportIdFixupFilterService;
    private final ReportEventIdFixupFilterRestMapper restMapper;

    @Autowired
    public ReportEventIdFixupFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ReportIdFixupFilterService reportIdFixupFilterService,
        ReportEventIdFixupFilterRestMapper restMapper) {
        this.authorizationProvider = authorizationProvider;
        this.reportIdFixupFilterService = reportIdFixupFilterService;
        this.restMapper = restMapper;
    }

    @Override
    public ReportEventIdFixupFilterResponse getFilter(String accessToken, String fixupId, String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper
                .toResponse(reportIdFixupFilterService.get(authorization, Id.valueOf(fixupId), Id.valueOf(filterId)));
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
    public ReportEventIdFixupFilterResponse createFilter(String accessToken, String fixupId,
        ReportEventIdFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterValidationRestException,
        ReportIdFixupFilterValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportIdFixupFilterBuilder filterBuilder =
                reportIdFixupFilterService.create(authorization, Id.valueOf(fixupId));
            filterBuilder.withReportId(Id.valueOf(request.getReportEventId()));
            if (request.getEventIdAttributeName() == null) {
                filterBuilder.withEventIdAttributeName(request.getReportEventId());
            } else {
                filterBuilder.withEventIdAttributeName(request.getEventIdAttributeName());
            }
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
        } catch (FixupFilterValidationException e) {
            throw RestExceptionBuilder.newBuilder(ReportIdFixupFilterValidationRestException.class)
                .withErrorCode(ReportIdFixupFilterValidationRestException.FILTER_REPORT_EVENT_ID_INVALID)
                .withCause(e).build();
        }
    }

    @Override
    public ReportEventIdFixupFilterResponse updateFilter(String accessToken, String fixupId, String filterId,
        ReportEventIdFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        ReportIdFixupFilterValidationRestException, FixupFilterUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportIdFixupFilterBuilder filterBuilder =
                reportIdFixupFilterService.update(authorization, Id.valueOf(fixupId), Id.valueOf(filterId));
            if (request.getReportEventId() != null) {
                filterBuilder.withReportId(Id.valueOf(request.getReportEventId()));
            }
            if (request.getEventIdAttributeName() != null) {
                filterBuilder.withEventIdAttributeName(request.getEventIdAttributeName());
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
        } catch (FixupFilterValidationException e) {
            throw RestExceptionBuilder.newBuilder(ReportIdFixupFilterValidationRestException.class)
                .withErrorCode(ReportIdFixupFilterValidationRestException.FILTER_REPORT_EVENT_ID_INVALID)
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
        }
    }

    @Override
    public ReportEventIdFixupFilterResponse deleteFilter(String accessToken, String fixupId, String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        FixupFilterUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(
                reportIdFixupFilterService.delete(authorization, Id.valueOf(fixupId), Id.valueOf(filterId)));
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
