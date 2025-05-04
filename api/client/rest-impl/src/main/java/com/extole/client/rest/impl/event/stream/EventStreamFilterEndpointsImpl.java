package com.extole.client.rest.impl.event.stream;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamFilterCreateRequest;
import com.extole.client.rest.event.stream.EventStreamFilterEndpoints;
import com.extole.client.rest.event.stream.EventStreamFilterResponse;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.client.rest.event.stream.EventStreamFilterUpdateRequest;
import com.extole.client.rest.event.stream.EventStreamRestException;
import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.client.rest.impl.campaign.BuildEventStreamExceptionMapper;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamFilterBuilder;
import com.extole.model.service.event.stream.EventStreamFilterNotFoundException;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;
import com.extole.model.service.event.stream.EventStreamNotFoundException;
import com.extole.model.service.event.stream.EventStreamService;
import com.extole.model.service.event.stream.built.BuildEventStreamException;

@Provider
public class EventStreamFilterEndpointsImpl implements EventStreamFilterEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final EventStreamService eventStreamService;
    private final ComponentService componentService;
    private final Map<EventFilterType, EventStreamFilterCreateRequestMapper> filterTypeCreateRequestMappers;
    private final Map<EventFilterType, EventStreamFilterUpdateRequestMapper> filterTypeUpdateRequestMappers;
    private final Map<EventStreamFilter.Type, EventStreamFilterResponseMapper> filterTypeResponseMappers;

    @Autowired
    public EventStreamFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        EventStreamService eventStreamService,
        ComponentService componentService,
        List<EventStreamFilterCreateRequestMapper> eventStreamFilterCreateRequestMappers,
        List<EventStreamFilterUpdateRequestMapper> eventStreamFilterUpdateRequestMappers,
        List<EventStreamFilterResponseMapper> eventStreamFilterResponseMappers) {
        this.authorizationProvider = authorizationProvider;
        this.eventStreamService = eventStreamService;
        this.componentService = componentService;
        this.filterTypeCreateRequestMappers = eventStreamFilterCreateRequestMappers.stream()
            .collect(Collectors.toMap(mapper -> mapper.getType(), Function.identity()));
        this.filterTypeResponseMappers = eventStreamFilterResponseMappers.stream()
            .collect(Collectors.toMap(mapper -> mapper.getType(), Function.identity()));
        this.filterTypeUpdateRequestMappers = eventStreamFilterUpdateRequestMappers.stream()
            .collect(Collectors.toMap(mapper -> mapper.getType(), Function.identity()));
    }

    @Override
    public EventStreamFilterResponse create(String accessToken,
        Id<?> eventStreamId,
        EventStreamFilterCreateRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException, EventStreamFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStreamBuilder builder = eventStreamService.update(authorization, Id.valueOf(eventStreamId.getValue()));
            EventStreamFilter eventStreamFilter = filterTypeCreateRequestMappers.get(request.getType())
                .create(builder, request);
            builder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
            return filterTypeResponseMappers.get(eventStreamFilter.getType()).map(eventStreamFilter);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildEventStreamException e) {
            throw BuildEventStreamExceptionMapper.getInstance().map(e);
        } catch (EventStreamFiltersValidationException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamValidationRestException.class)
                .withErrorCode(EventStreamValidationRestException.EVENT_STREAM_FILTER_VALIDATION_FAILED)
                .addParameter("event_stream_id", e.getEventStreamId())
                .addParameter("filter_id", e.getEventStreamFilterId())
                .addParameter("message", e.getMessage())
                .withCause(e)
                .build();
        }
    }

    @Override
    public EventStreamFilterResponse update(String accessToken,
        Id<?> eventStreamId,
        Id<?> eventStreamFilterId,
        EventStreamFilterUpdateRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException, EventStreamFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStreamBuilder builder = eventStreamService.update(authorization, Id.valueOf(eventStreamId.getValue()));
            EventStreamFilter eventStreamFilter = filterTypeUpdateRequestMappers.get(request.getType())
                .update(builder, Id.valueOf(eventStreamFilterId.getValue()), request);
            builder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
            return filterTypeResponseMappers.get(eventStreamFilter.getType()).map(eventStreamFilter);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildEventStreamException e) {
            throw BuildEventStreamExceptionMapper.getInstance().map(e);
        } catch (EventStreamFiltersValidationException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamValidationRestException.class)
                .withErrorCode(EventStreamValidationRestException.EVENT_STREAM_FILTER_VALIDATION_FAILED)
                .addParameter("event_stream_id", e.getEventStreamId())
                .addParameter("filter_id", e.getEventStreamFilterId())
                .addParameter("message", e.getMessage())
                .withCause(e)
                .build();
        } catch (EventStreamFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamFilterRestException.class)
                .withErrorCode(EventStreamFilterRestException.EVENT_STREAM_FILTER_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .addParameter("filter_id", e.getEventStreamId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public EventStreamFilterResponse get(String accessToken,
        Id<?> eventStreamId,
        Id<?> eventStreamFilterId,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException, EventStreamFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStreamFilter filter =
                eventStreamService.getFilterById(authorization, Id.valueOf(eventStreamId.getValue()),
                    Id.valueOf(eventStreamFilterId.getValue()));

            return filterTypeResponseMappers.get(filter.getType()).map(filter);
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
        } catch (EventStreamFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamFilterRestException.class)
                .withErrorCode(EventStreamFilterRestException.EVENT_STREAM_FILTER_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .addParameter("filter_id", e.getEventStreamId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<EventStreamFilterResponse> list(String accessToken,
        Id<?> eventStreamId,
        ZoneId timeZone) throws UserAuthorizationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return eventStreamService.listFilters(authorization, Id.valueOf(eventStreamId.getValue()))
                .stream()
                .map(filter -> filterTypeResponseMappers.get(filter.getType()).map(filter))
                .collect(Collectors.toList());
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
    }

    @Override
    public EventStreamFilterResponse archive(String accessToken, Id<?> eventStreamId,
        Id<?> eventStreamFilterId, ZoneId timeZone) throws UserAuthorizationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStreamFilter filter =
                eventStreamService.getFilterById(authorization, Id.valueOf(eventStreamId.getValue()),
                    Id.valueOf(eventStreamFilterId.getValue()));
            EventStreamBuilder builder = eventStreamService.update(authorization, Id.valueOf(eventStreamId.getValue()));
            EventStreamFilterBuilder eventStreamFilter =
                builder.updateFilter(Id.valueOf(eventStreamFilterId.getValue()));
            eventStreamFilter.withArchived();
            builder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
            return filterTypeResponseMappers.get(filter.getType()).map(filter);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException | EventStreamFiltersValidationException
            | EventStreamFilterNotFoundException | InvalidComponentReferenceException | BuildEventStreamException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
