package com.extole.reporting.rest.impl.fixup;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.entity.fixup.Fixup;
import com.extole.reporting.entity.fixup.FixupDataSource;
import com.extole.reporting.entity.fixup.FixupExecution;
import com.extole.reporting.rest.fixup.FixupEndpoints;
import com.extole.reporting.rest.fixup.FixupRequest;
import com.extole.reporting.rest.fixup.FixupResponse;
import com.extole.reporting.rest.fixup.FixupRestException;
import com.extole.reporting.rest.fixup.FixupUpdateRestException;
import com.extole.reporting.rest.fixup.FixupValidationRestException;
import com.extole.reporting.service.fixup.FixupBuilder;
import com.extole.reporting.service.fixup.FixupExecutionService;
import com.extole.reporting.service.fixup.FixupMissingDataSourceException;
import com.extole.reporting.service.fixup.FixupMissingNameException;
import com.extole.reporting.service.fixup.FixupNotEditableException;
import com.extole.reporting.service.fixup.FixupNotFoundException;
import com.extole.reporting.service.fixup.FixupRuntimeException;
import com.extole.reporting.service.fixup.FixupService;

@Provider
public class FixupEndpointsImpl implements FixupEndpoints {
    private static final Comparator<Fixup> FIXUP_CREATED_DATE_COMPARATOR =
        Comparator.comparing(Fixup::getCreatedDate).reversed();
    private static final Comparator<FixupExecution> FIXUP_EXECUTION_STARTED_DATE_COMPARATOR =
        Comparator.comparing(FixupExecution::getStartDate).reversed();

    private final ClientAuthorizationProvider authorizationProvider;
    private final FixupService fixupService;
    private final FixupRestMapper restMapper;
    private final FixupExecutionService fixupExecutionService;

    @Autowired
    public FixupEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        FixupService fixupService,
        FixupRestMapper restMapper,
        FixupExecutionService fixupExecutionService) {
        this.authorizationProvider = authorizationProvider;
        this.fixupService = fixupService;
        this.restMapper = restMapper;
        this.fixupExecutionService = fixupExecutionService;
    }

    @Override
    public List<FixupResponse> listFixups(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            List<Fixup> fixups = Lists.newArrayList(fixupService.getAll(authorization));
            fixups.sort(FIXUP_CREATED_DATE_COMPARATOR);

            List<FixupResponse> fixupResponses = Lists.newArrayList();
            for (Fixup fixup : fixups) {
                List<FixupExecution> executions =
                    Lists.newArrayList(fixupExecutionService.getAll(authorization, fixup.getId()));
                executions.sort(FIXUP_EXECUTION_STARTED_DATE_COMPARATOR);
                fixupResponses.add(restMapper.toFixupResponse(fixup, executions, timeZone));
            }
            return fixupResponses;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public FixupResponse getFixup(String accessToken, String fixupId, ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Fixup fixup = fixupService.get(authorization, Id.valueOf(fixupId));
            return restMapper.toFixupResponse(fixup, fixupExecutionService.getAll(authorization, fixup.getId()),
                timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId).build();
        }
    }

    @Override
    public FixupResponse createFixup(String accessToken, FixupRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, FixupValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            FixupBuilder builder = fixupService.create(authorization);
            if (!Strings.isNullOrEmpty(request.getName())) {
                builder.withName(request.getName());
            }
            if (request.getDataSource() != null) {
                builder.withDataSource(FixupDataSource.valueOf(request.getDataSource().name()));
            }
            if (!Strings.isNullOrEmpty(request.getDescription())) {
                builder.withDescription(request.getDescription());
            }
            Fixup fixup = builder.save();
            return restMapper.toFixupResponse(fixup, fixupExecutionService.getAll(authorization, fixup.getId()),
                timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(FixupValidationRestException.class)
                .withErrorCode(FixupValidationRestException.MISSING_NAME).withCause(e).build();
        } catch (FixupMissingDataSourceException e) {
            throw RestExceptionBuilder.newBuilder(FixupValidationRestException.class)
                .withErrorCode(FixupValidationRestException.MISSING_DATA_SOURCE).withCause(e).build();
        } catch (FixupNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public FixupResponse updateFixup(String accessToken, String fixupId, FixupRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException, FixupUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            FixupBuilder builder = fixupService.update(authorization, Id.valueOf(fixupId));
            if (!Strings.isNullOrEmpty(request.getName())) {
                builder.withName(request.getName());
            }
            if (request.getDataSource() != null) {
                builder.withDataSource(
                    com.extole.reporting.entity.fixup.FixupDataSource.valueOf(request.getDataSource().name()));
            }
            if (!Strings.isNullOrEmpty(request.getDescription())) {
                builder.withDescription(request.getDescription());
            }
            Fixup fixup = builder.save();
            return restMapper.toFixupResponse(fixup, fixupExecutionService.getAll(authorization, fixup.getId()),
                timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .addParameter("fixup_id", fixupId)
                .withCause(e).build();
        } catch (FixupMissingNameException | FixupMissingDataSourceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (FixupNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupUpdateRestException.class)
                .withErrorCode(FixupUpdateRestException.NOT_EDITABLE)
                .withCause(e).build();
        }
    }

    @Override
    public FixupResponse deleteFixup(String accessToken, String fixupId, ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException, FixupUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            List<FixupExecution> executions = fixupExecutionService.getAll(authorization, Id.valueOf(fixupId));
            Fixup fixup = fixupService.delete(authorization, Id.valueOf(fixupId));
            return restMapper.toFixupResponse(fixup, executions, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .addParameter("fixup_id", fixupId)
                .withCause(e).build();
        } catch (FixupRuntimeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (FixupNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupUpdateRestException.class)
                .withErrorCode(FixupUpdateRestException.NOT_EDITABLE)
                .withCause(e).build();
        }
    }
}
