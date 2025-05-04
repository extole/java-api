package com.extole.client.topic.rest.impl.event.stream;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.event.stream.EventStreamEventFilterQueryParams;
import com.extole.client.rest.event.stream.EventStreamEventResponse;
import com.extole.client.rest.event.stream.EventStreamRestException;
import com.extole.client.topic.rest.event.stream.EventStreamLocalEndpoints;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.event.stream.EventStreamEvent;
import com.extole.id.Id;
import com.extole.model.entity.event.stream.built.BuiltEventStream;
import com.extole.model.service.event.stream.EventStreamEventService;
import com.extole.model.service.event.stream.EventStreamEventService.EventStreamRecentEventQueryBuilder;
import com.extole.model.service.event.stream.EventStreamNotFoundException;
import com.extole.model.service.event.stream.built.BuiltEventStreamService;

@Provider
public class EventStreamLocalEndpointsImpl implements EventStreamLocalEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final BuiltEventStreamService builtEventStreamService;
    private final EventStreamEventService eventStreamEventService;
    private final EventStreamRestQueryFilterMapper eventStreamRestQueryFilterMapper;

    @Inject
    public EventStreamLocalEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        BuiltEventStreamService builtEventStreamService,
        EventStreamEventService eventStreamEventService,
        EventStreamRestQueryFilterMapper eventStreamRestQueryFilterMapper) {
        this.authorizationProvider = authorizationProvider;
        this.builtEventStreamService = builtEventStreamService;
        this.eventStreamEventService = eventStreamEventService;
        this.eventStreamRestQueryFilterMapper = eventStreamRestQueryFilterMapper;
    }

    @Override
    public List<EventStreamEventResponse> getEvents(String accessToken,
        String eventStreamId,
        EventStreamEventFilterQueryParams filterRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltEventStream eventStream =
                builtEventStreamService.getById(authorization, Id.valueOf(eventStreamId));

            EventStreamRecentEventQueryBuilder eventQueryBuilder =
                eventStreamEventService.createRecentEventQuery(authorization.getClientId(),
                    authorization.getIdentityId(), eventStream.getId());
            if (filterRequest != null) {
                if (filterRequest.getLimit() != null) {
                    eventQueryBuilder.withLimit(filterRequest.getLimit());
                }
                if (filterRequest.getOffset() != null) {
                    eventQueryBuilder.withOffset(filterRequest.getOffset());
                }
                if (filterRequest.getStartDate().isPresent() || filterRequest.getEndDate().isPresent()) {
                    eventQueryBuilder
                        .addFilter(filter -> isWithinStartAndEndDates(filterRequest, filter));
                }
                eventQueryBuilder
                    .addFilter(eventStreamRestQueryFilterMapper.parse(filterRequest.getJsonPathFilters()));
            }
            return eventQueryBuilder
                .query()
                .stream()
                .map(event -> new EventStreamEventResponse(event.getEventId(), event.getEventTime().atZone(timeZone),
                    event.getEventStreamId(), event.getEvent()))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
        }
    }

    private static boolean isWithinStartAndEndDates(EventStreamEventFilterQueryParams filterRequest,
        EventStreamEvent filter) {
        return (filterRequest.getStartDate().isEmpty()
            || filter.getEventTime().isAfter(filterRequest.getStartDate().get().toInstant())) &&
            (filterRequest.getEndDate().isEmpty()
                || filter.getEventTime().isBefore(filterRequest.getEndDate().get().toInstant()));
    }
}
