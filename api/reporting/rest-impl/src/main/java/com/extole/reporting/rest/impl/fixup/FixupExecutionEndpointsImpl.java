package com.extole.reporting.rest.impl.fixup;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.entity.fixup.FixupExecution;
import com.extole.reporting.rest.fixup.FixupExecutedRestException;
import com.extole.reporting.rest.fixup.FixupExecutionEndpoints;
import com.extole.reporting.rest.fixup.FixupExecutionInProgressRestException;
import com.extole.reporting.rest.fixup.FixupExecutionResponse;
import com.extole.reporting.rest.fixup.FixupExecutionRestException;
import com.extole.reporting.rest.fixup.FixupRestException;
import com.extole.reporting.service.fixup.FixupExecutedException;
import com.extole.reporting.service.fixup.FixupExecutionInProgressException;
import com.extole.reporting.service.fixup.FixupExecutionNotFoundException;
import com.extole.reporting.service.fixup.FixupExecutionService;
import com.extole.reporting.service.fixup.FixupNotFoundException;

@Provider
public class FixupExecutionEndpointsImpl implements FixupExecutionEndpoints {
    private static final Comparator<FixupExecution> FIXUP_EXECUTION_STARTED_DATE_COMPARATOR =
        Comparator.comparing(FixupExecution::getStartDate).reversed();

    private final ClientAuthorizationProvider authorizationProvider;
    private final FixupExecutionService fixupExecutionService;

    @Autowired
    public FixupExecutionEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        FixupExecutionService fixupExecutionService) {
        this.authorizationProvider = authorizationProvider;
        this.fixupExecutionService = fixupExecutionService;
    }

    @Override
    public List<FixupExecutionResponse> listExecutions(String accessToken, String fixupId, ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            List<FixupExecution> executions =
                Lists.newArrayList(fixupExecutionService.getAll(authorization, Id.valueOf(fixupId)));
            executions.sort(FIXUP_EXECUTION_STARTED_DATE_COMPARATOR);

            return executions.stream()
                .map(execution -> toFixupExecutionResponse(execution, timeZone))
                .collect(Collectors.toList());
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
    public FixupExecutionResponse getExecution(String accessToken, String fixupId, String fixupExecutionId,
        ZoneId timeZone) throws UserAuthorizationRestException, FixupRestException, FixupExecutionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            FixupExecution fixupExecution =
                fixupExecutionService.get(authorization, Id.valueOf(fixupId), Id.valueOf(fixupExecutionId));
            return toFixupExecutionResponse(fixupExecution, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId)
                .build();
        } catch (FixupExecutionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupExecutionRestException.class)
                .withErrorCode(FixupExecutionRestException.FIXUP_EXECUTION_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId)
                .addParameter("fixup_execution_id", e.getFixupExecutionId())
                .build();
        }
    }

    @Override
    public FixupExecutionResponse start(String accessToken, String fixupId, ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException,
        FixupExecutionInProgressRestException, FixupExecutedRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            FixupExecution fixup = fixupExecutionService.create(authorization, Id.valueOf(fixupId)).execute();
            return toFixupExecutionResponse(fixup, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", fixupId)
                .build();
        } catch (FixupExecutionInProgressException e) {
            throw RestExceptionBuilder.newBuilder(FixupExecutionInProgressRestException.class)
                .withErrorCode(FixupExecutionInProgressRestException.FIXUP_EXECUTION_IN_PROGRESS)
                .withCause(e)
                .build();
        } catch (FixupExecutedException e) {
            throw RestExceptionBuilder.newBuilder(FixupExecutedRestException.class)
                .withErrorCode(FixupExecutedRestException.FIXUP_EXECUTED)
                .withCause(e)
                .build();
        }
    }

    private FixupExecutionResponse toFixupExecutionResponse(FixupExecution fixupExecution, ZoneId timeZone) {
        FixupExecutionResponse.Builder builder = FixupExecutionResponse.builder()
            .withId(fixupExecution.getId().getValue())
            .withUserId(fixupExecution.getUserId().getValue())
            .withStartDate(ZonedDateTime.ofInstant(fixupExecution.getStartDate(), timeZone))
            .withStatus(fixupExecution.getStatus().name());

        fixupExecution.getEndDate()
            .ifPresent(endDate -> builder.withEndDate(ZonedDateTime.ofInstant(endDate, timeZone)));
        fixupExecution.getErrorCode().ifPresent(errorCode -> builder.withErrorCode(errorCode.name()));
        fixupExecution.getErrorMessage().ifPresent(builder::withErrorMessage);
        fixupExecution.getEventCount().ifPresent(builder::withEventCount);
        return builder.build();
    }
}
