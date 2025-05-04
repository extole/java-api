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
import com.extole.reporting.rest.fixup.filter.ReportEventIdTimeFixupFilterEndpoints;
import com.extole.reporting.rest.fixup.filter.ReportEventIdTimeFixupFilterRequest;
import com.extole.reporting.rest.fixup.filter.ReportEventIdTimeFixupFilterResponse;
import com.extole.reporting.rest.fixup.filter.ReportEventIdTimeFixupFilterValidationRestException;
import com.extole.reporting.service.ReportNotFoundException;
import com.extole.reporting.service.fixup.FixupNotFoundException;
import com.extole.reporting.service.fixup.FixupRuntimeException;
import com.extole.reporting.service.fixup.filter.FixupFilterAlreadyExistsException;
import com.extole.reporting.service.fixup.filter.FixupFilterNotEditableException;
import com.extole.reporting.service.fixup.filter.FixupFilterNotFoundException;
import com.extole.reporting.service.fixup.filter.FixupFilterValidationException;
import com.extole.reporting.service.fixup.filter.ReportEventIdTimeFixupFilterBuilder;
import com.extole.reporting.service.fixup.filter.ReportEventIdTimeFixupFilterService;

@Provider
public class ReportEventIdTimeFixupFilterEndpointsImpl implements ReportEventIdTimeFixupFilterEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportEventIdTimeFixupFilterService reportEventIdTimeFixupFilterService;
    private final ReportEventIdTimeFixupFilterRestMapper restMapper;

    @Autowired
    public ReportEventIdTimeFixupFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ReportEventIdTimeFixupFilterService reportEventIdTimeFixupFilterService,
        ReportEventIdTimeFixupFilterRestMapper restMapper) {
        this.authorizationProvider = authorizationProvider;
        this.reportEventIdTimeFixupFilterService = reportEventIdTimeFixupFilterService;
        this.restMapper = restMapper;
    }

    @Override
    public ReportEventIdTimeFixupFilterResponse getFilter(String accessToken, String fixupId, String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper
                .toResponse(reportEventIdTimeFixupFilterService.get(authorization, Id.valueOf(fixupId),
                    Id.valueOf(filterId)));
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
    public ReportEventIdTimeFixupFilterResponse createFilter(String accessToken, String fixupId,
        ReportEventIdTimeFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterValidationRestException,
        ReportEventIdTimeFixupFilterValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportEventIdTimeFixupFilterBuilder filterBuilder =
                reportEventIdTimeFixupFilterService.create(authorization, Id.valueOf(fixupId));
            filterBuilder.withReportId(Id.valueOf(request.getReportId()))
                .withEventIdAttributeName(request.getEventIdAttributeName())
                .withEventTimeAttributeName(request.getEventTimeAttributeName());
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
            throw RestExceptionBuilder.newBuilder(ReportEventIdTimeFixupFilterValidationRestException.class)
                .withErrorCode(ReportEventIdTimeFixupFilterValidationRestException.FILTER_INVALID)
                .withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportEventIdTimeFixupFilterValidationRestException.class)
                .withErrorCode(ReportEventIdTimeFixupFilterValidationRestException.REPORT_NOT_FOUND)
                .withCause(e).build();
        }
    }

    @Override
    public ReportEventIdTimeFixupFilterResponse updateFilter(String accessToken, String fixupId, String filterId,
        ReportEventIdTimeFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        ReportEventIdTimeFixupFilterValidationRestException, FixupFilterUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportEventIdTimeFixupFilterBuilder filterBuilder =
                reportEventIdTimeFixupFilterService.update(authorization, Id.valueOf(fixupId), Id.valueOf(filterId));
            if (request.getReportId() != null) {
                filterBuilder.withReportId(Id.valueOf(request.getReportId()));
            }
            if (request.getEventIdAttributeName() != null) {
                filterBuilder.withEventIdAttributeName(request.getEventIdAttributeName());
            }
            if (request.getEventTimeAttributeName() != null) {
                filterBuilder.withEventTimeAttributeName(request.getEventTimeAttributeName());
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
            throw RestExceptionBuilder.newBuilder(ReportEventIdTimeFixupFilterValidationRestException.class)
                .withErrorCode(ReportEventIdTimeFixupFilterValidationRestException.FILTER_INVALID)
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
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportEventIdTimeFixupFilterValidationRestException.class)
                .withErrorCode(ReportEventIdTimeFixupFilterValidationRestException.REPORT_NOT_FOUND)
                .withCause(e).build();
        }
    }

    @Override
    public ReportEventIdTimeFixupFilterResponse deleteFilter(String accessToken, String fixupId, String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        FixupFilterUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(
                reportEventIdTimeFixupFilterService.delete(authorization, Id.valueOf(fixupId),
                    Id.valueOf(filterId)));
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
