package com.extole.client.rest.impl.event.stream;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.event.stream.BuiltEventStreamResponse;
import com.extole.client.rest.event.stream.EventStreamCreateRequest;
import com.extole.client.rest.event.stream.EventStreamEndpoints;
import com.extole.client.rest.event.stream.EventStreamQueryParams;
import com.extole.client.rest.event.stream.EventStreamResponse;
import com.extole.client.rest.event.stream.EventStreamRestException;
import com.extole.client.rest.event.stream.EventStreamUpdateRequest;
import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.client.rest.impl.campaign.BuildEventStreamExceptionMapper;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.event.stream.EventStream;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.entity.event.stream.built.BuiltEventStream;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;
import com.extole.model.service.event.stream.EventStreamNotFoundException;
import com.extole.model.service.event.stream.EventStreamService;
import com.extole.model.service.event.stream.built.BuildEventStreamException;
import com.extole.model.service.event.stream.built.BuiltEventStreamQueryBuilder;
import com.extole.model.service.event.stream.built.BuiltEventStreamService;
import com.extole.model.service.event.stream.built.EventStreamQueryBuilder;

@Provider
public class EventStreamEndpointsImpl implements EventStreamEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final EventStreamService eventStreamService;
    private final BuiltEventStreamService builtEventStreamService;
    private final ComponentService componentService;
    private final Map<EventStreamFilter.Type, EventStreamFilterResponseMapper> filterTypeResponseMappers;

    @Autowired
    public EventStreamEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        EventStreamService eventStreamService,
        BuiltEventStreamService builtEventStreamService,
        ComponentService componentService,
        List<EventStreamFilterResponseMapper> eventStreamFilterResponseMappers) {
        this.authorizationProvider = authorizationProvider;
        this.eventStreamService = eventStreamService;
        this.builtEventStreamService = builtEventStreamService;
        this.componentService = componentService;
        this.filterTypeResponseMappers = eventStreamFilterResponseMappers.stream()
            .collect(Collectors.toMap(mapper -> mapper.getType(), Function.identity()));
    }

    @Override
    public EventStreamResponse create(String accessToken,
        EventStreamCreateRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStreamBuilder eventStreamBuilder = eventStreamService.create(authorization);

            request.getName().ifDefined(name -> eventStreamBuilder.withName(name));
            request.getDescription().ifPresent(description -> eventStreamBuilder.withDescription(description));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(eventStreamBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                handleComponentReferences(eventStreamBuilder, componentReferences);
            });
            request.getTags().ifPresent(tags -> eventStreamBuilder.withTags(tags));
            request.getStopAt().ifPresent(stopAt -> eventStreamBuilder.withStopAt(stopAt));
            EventStream eventStream =
                eventStreamBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));

            return toResponse(eventStream);

        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
    public EventStreamResponse update(String accessToken, Id<?> eventStreamId, EventStreamUpdateRequest request,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStreamBuilder eventStreamBuilder =
                eventStreamService.update(authorization, Id.valueOf(eventStreamId.getValue()));

            request.getName().ifPresent(name -> eventStreamBuilder.withName(name));
            request.getDescription().ifPresent(description -> eventStreamBuilder.withDescription(description));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(eventStreamBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                handleComponentReferences(eventStreamBuilder, componentReferences);
            });
            request.getTags().ifPresent(tags -> eventStreamBuilder.withTags(tags));
            request.getStopAt().ifPresent(stopAt -> eventStreamBuilder.withStopAt(stopAt));
            EventStream eventStream =
                eventStreamBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));

            return toResponse(eventStream);

        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
                .withCause(e).build();
        } catch (BuildEventStreamException e) {
            throw BuildEventStreamExceptionMapper.getInstance().map(e);
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
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
    public EventStreamResponse get(String accessToken, Id<?> eventStreamId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStream entity = eventStreamService.get(authorization, Id.valueOf(eventStreamId.getValue()));
            return toResponse(entity);
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
    public List<EventStreamResponse> list(String accessToken,
        EventStreamQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            EventStreamQueryBuilder queryBuilder = eventStreamService.list(authorization);

            if (queryParams.getIncludeArchived()) {
                queryBuilder.includeArchived();
            }

            queryBuilder.withLimit(queryParams.getLimit());
            queryBuilder.withOffset(queryParams.getOffset());
            return queryBuilder
                .list()
                .stream()
                .map(EventStream -> toResponse(EventStream))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<BuiltEventStreamResponse> listBuilt(String accessToken,
        @BeanParam EventStreamQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltEventStreamQueryBuilder queryBuilder = builtEventStreamService.list(authorization);

            if (queryParams.getIncludeArchived()) {
                queryBuilder.includeArchived();
            }

            queryBuilder.withLimit(queryParams.getLimit());
            queryBuilder.withOffset(queryParams.getOffset());

            return queryBuilder.list().stream()
                .map(EventStream -> toBuiltResponse(EventStream))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltEventStreamResponse getBuilt(String accessToken, Id<?> eventStreamId, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toBuiltResponse(
                builtEventStreamService.getById(authorization, Id.valueOf(eventStreamId.getValue())));
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
        }
    }

    @Override
    public EventStreamResponse archive(String accessToken, Id<?> eventStreamId, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStream archivedEntity =
                eventStreamService.archive(authorization, Id.valueOf(eventStreamId.getValue()));
            return toResponse(archivedEntity);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
                .withCause(e).build();
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
        } catch (BuildEventStreamException e) {
            throw BuildEventStreamExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public EventStreamResponse delete(String accessToken, Id<?> eventStreamId, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStream deletedEntity =
                eventStreamService.delete(authorization, Id.valueOf(eventStreamId.getValue()));
            return toResponse(deletedEntity);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
                .withCause(e).build();
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
        } catch (BuildEventStreamException e) {
            throw BuildEventStreamExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public EventStreamResponse unArchive(String accessToken, Id<?> eventStreamId, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EventStream unArchivedEntity =
                eventStreamService.unArchive(authorization, Id.valueOf(eventStreamId.getValue()));
            return toResponse(unArchivedEntity);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
                .withCause(e).build();
        } catch (EventStreamNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamRestException.class)
                .withErrorCode(EventStreamRestException.EVENT_STREAM_NOT_FOUND)
                .addParameter("event_stream_id", e.getEventStreamId())
                .withCause(e)
                .build();
        } catch (BuildEventStreamException e) {
            throw BuildEventStreamExceptionMapper.getInstance().map(e);
        }
    }

    private EventStreamResponse toResponse(EventStream eventStream) {
        return new EventStreamResponse(eventStream.getId(),
            eventStream.getName(),
            eventStream.getDescription(),
            eventStream.getFilters().stream()
                .map(filter -> filterTypeResponseMappers.get(filter.getType()).map(filter))
                .collect(Collectors.toList()),
            eventStream.getTags(),
            eventStream.getComponentReferences().stream()
                .map(component -> component.getComponentId()).collect(Collectors.toList()),
            eventStream.getStopAt(),
            eventStream.getCreatedDate(),
            eventStream.getUpdatedDate());
    }

    private BuiltEventStreamResponse toBuiltResponse(BuiltEventStream eventStream) {
        return new BuiltEventStreamResponse(eventStream.getId(),
            eventStream.getName(),
            eventStream.getDescription(),
            eventStream.getFilters().stream()
                .map(filter -> filterTypeResponseMappers.get(filter.getType()).map(filter))
                .collect(Collectors.toList()),
            eventStream.getTags(),
            eventStream.getComponentReferences().stream()
                .map(component -> component.getComponentId()).collect(Collectors.toList()),
            eventStream.getStopAt(),
            eventStream.getCreatedDate(),
            eventStream.getUpdatedDate());
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (Id<?> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private void handleComponentReferences(ComponentElementBuilder elementBuilder,
        List<ComponentReferenceRequest> componentReferences) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (ComponentReferenceRequest reference : componentReferences) {
            if (reference.getComponentId() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                elementBuilder.addComponentReference(Id.valueOf(reference.getComponentId().getValue()));
            reference.getSocketNames().ifPresent(referenceBuilder::withSocketNames);
        }
    }

}
