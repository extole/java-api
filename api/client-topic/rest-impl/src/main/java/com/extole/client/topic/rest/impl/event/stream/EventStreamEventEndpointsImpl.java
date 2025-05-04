package com.extole.client.topic.rest.impl.event.stream;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.event.stream.EventStreamEventFilterQueryParams;
import com.extole.client.rest.event.stream.EventStreamEventResponse;
import com.extole.client.rest.event.stream.EventStreamRestException;
import com.extole.client.topic.rest.event.stream.EventStreamEventEndpoints;
import com.extole.client.topic.rest.event.stream.EventStreamLocalEndpoints;
import com.extole.common.rest.exception.ExtoleRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.event.stream.EventStreamNotFoundException;
import com.extole.model.service.event.stream.built.BuiltEventStreamService;

@Provider
public class EventStreamEventEndpointsImpl implements EventStreamEventEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(EventStreamEventEndpointsImpl.class);

    private final ClientAuthorizationProvider authorizationProvider;
    private final BuiltEventStreamService builtEventStreamService;
    private final EventStreamLocalEndpointsProvider endpointsProvider;

    @Autowired
    public EventStreamEventEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        BuiltEventStreamService builtEventStreamService,
        EventStreamLocalEndpointsProvider endpointsProvider) {
        this.authorizationProvider = authorizationProvider;
        this.builtEventStreamService = builtEventStreamService;
        this.endpointsProvider = endpointsProvider;
    }

    @Override
    public List<EventStreamEventResponse> events(String accessToken,
        String eventStreamId,
        EventStreamEventFilterQueryParams filterRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            builtEventStreamService.getById(authorization, Id.valueOf(eventStreamId));
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }

        List<EventStreamEventResponse> events = new ArrayList<>();
        for (EventStreamLocalEndpoints endpoints : endpointsProvider.getLocalEndpoints()) {
            try {
                List<EventStreamEventResponse> localDispatches =
                    endpoints.getEvents(accessToken, eventStreamId,
                        new EventStreamEventFilterQueryParams(filterRequest.getLimit(), filterRequest.getOffset(),
                            filterRequest.getStartDate().orElse(null),
                            filterRequest.getEndDate().orElse(null),
                            UriUtils.encodePath(filterRequest.getJsonPathFilters(), Charset.defaultCharset())),
                        timeZone);
                events.addAll(localDispatches);
            } catch (ExtoleRestRuntimeException e) {
                LOG.error("Unexpected error while retrieving events for local cluster " +
                    "for client_id: {}, event_stream_id: {}, error_code: {}, error_parameters: {}",
                    authorization.getClientId(), eventStreamId, e.getErrorCode(), e.getParameters(), e);
            }
        }

        Set<String> eventIds = new HashSet<>();
        events.removeIf(event -> !eventIds.add(event.getEventId().getValue()));

        events.sort(Collections.reverseOrder(
            EventStreamEventComparator.COMPARATOR_INSTANCE));

        if (filterRequest.getLimit() != null && events.size() > filterRequest.getLimit().intValue()) {
            events = events.subList(0, filterRequest.getLimit().intValue());
        }

        return events.stream()
            .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    private static final class EventStreamEventComparator
        implements Comparator<EventStreamEventResponse> {

        private static final EventStreamEventComparator COMPARATOR_INSTANCE =
            new EventStreamEventComparator();

        private EventStreamEventComparator() {
        }

        @Override
        public int compare(EventStreamEventResponse firstEvent, EventStreamEventResponse secondEvent) {
            ZonedDateTime firstEventTime = firstEvent.getEventTime();
            ZonedDateTime secondEventTime = secondEvent.getEventTime();

            return firstEventTime.compareTo(secondEventTime);
        }
    }

}
