package com.extole.client.rest.impl.schedule;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.activity.service.schedule.ScheduledTask;
import com.extole.activity.service.schedule.ScheduledTaskIllegalStateException;
import com.extole.activity.service.schedule.ScheduledTaskNotFoundException;
import com.extole.activity.service.schedule.ScheduledTaskPersistenceException;
import com.extole.activity.service.schedule.ScheduledTaskQueryBuilder;
import com.extole.activity.service.schedule.ScheduledTaskService;
import com.extole.activity.service.schedule.ScheduledTaskStatus;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.schedule.ScheduleCancelRestException;
import com.extole.client.rest.schedule.ScheduleEndpoints;
import com.extole.client.rest.schedule.ScheduleListRequest;
import com.extole.client.rest.schedule.ScheduleResponse;
import com.extole.client.rest.schedule.ScheduleRestException;
import com.extole.client.rest.schedule.ScheduleStatus;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;

@Provider
public class ScheduleEndpointsImpl implements ScheduleEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ScheduledTaskService scheduledTaskService;

    @Autowired
    public ScheduleEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ScheduledTaskService scheduledTaskService) {
        this.authorizationProvider = authorizationProvider;
        this.scheduledTaskService = scheduledTaskService;
    }

    @Override
    public List<ScheduleResponse> listSchedules(String accessToken, ScheduleListRequest request)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ScheduledTaskQueryBuilder builder = scheduledTaskService.query(authorization);
            request.getStatus().ifPresent(value -> builder.withStatus(ScheduledTaskStatus.valueOf(value.name())));
            request.getScheduleName().ifPresent(value -> builder.withScheduleName(value));
            request.getPersonId().ifPresent(value -> builder.withPersonId(Id.valueOf(value)));
            request.getPersonIdentityId().ifPresent(value -> builder.withPersonIdentityId(Id.valueOf(value)));
            request.getScheduledExecutionDateFrom()
                .ifPresent(value -> builder.withScheduledExecutionDateFrom(value.toInstant()));
            request.getScheduledExecutionDateTo()
                .ifPresent(value -> builder.withScheduledExecutionDateTo(value.toInstant()));
            request.getLimit().ifPresent(value -> builder.withLimit(value.intValue()));
            List<ScheduledTask> tasks = builder.list();
            return tasks.stream()
                .map(task -> toScheduleResponse(task, request.getTimeZone()))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ScheduleResponse getSchedule(String accessToken, String scheduleId, ZoneId timeZone)
        throws UserAuthorizationRestException, ScheduleRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ScheduledTask task = scheduledTaskService.getScheduledTaskById(authorization, Id.valueOf(scheduleId));
            return toScheduleResponse(task, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ScheduledTaskNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ScheduleRestException.class)
                .withErrorCode(ScheduleRestException.SCHEDULE_NOT_FOUND)
                .addParameter("schedule_id", scheduleId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ScheduleResponse cancelSchedule(String accessToken, String scheduleId, ZoneId timeZone)
        throws UserAuthorizationRestException, ScheduleRestException, ScheduleCancelRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ScheduledTask task = scheduledTaskService.cancelScheduledTask(authorization, Id.valueOf(scheduleId));
            return toScheduleResponse(task, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ScheduledTaskNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ScheduleRestException.class)
                .withErrorCode(ScheduleRestException.SCHEDULE_NOT_FOUND)
                .addParameter("schedule_id", scheduleId)
                .withCause(e)
                .build();
        } catch (ScheduledTaskIllegalStateException | ScheduledTaskPersistenceException e) {
            throw RestExceptionBuilder.newBuilder(ScheduleCancelRestException.class)
                .withErrorCode(ScheduleCancelRestException.ILLEGAL_STATE_CHANGE)
                .withCause(e)
                .build();
        }
    }

    private ScheduleResponse toScheduleResponse(ScheduledTask task, ZoneId timeZone) {
        return new ScheduleResponse(task.getId().getValue(), task.getCampaignId().getValue(),
            task.getControllerActionId().getValue(), task.getPersonId().getValue(), task.getProgramId().getValue(),
            task.getScheduleName(), task.getContainer().getName(), ScheduleStatus.valueOf(task.getStatus().name()),
            task.getCreatedDate().atZone(timeZone), task.getScheduleStartDate().atZone(timeZone),
            task.getScheduledExecutionDate().atZone(timeZone), task.getData());
    }
}
