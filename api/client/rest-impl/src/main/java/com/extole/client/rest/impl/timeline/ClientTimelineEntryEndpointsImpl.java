package com.extole.client.rest.impl.timeline;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.timeline.ClientTimelineEntryEndpoints;
import com.extole.client.rest.timeline.ClientTimelineEntryRequest;
import com.extole.client.rest.timeline.ClientTimelineEntryResponse;
import com.extole.client.rest.timeline.UpdateClientTimelineEntryRequest;
import com.extole.client.rest.timeline.exception.ClientTimelineEntryNotFoundRestException;
import com.extole.client.rest.timeline.exception.ClientTimelineEntryNotModifiableRestException;
import com.extole.client.rest.timeline.exception.ClientTimelineEntryRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.timeline.ClientTimelineEntry;
import com.extole.model.service.timeline.ClientTimelineEntryBuilder;
import com.extole.model.service.timeline.ClientTimelineEntryService;
import com.extole.model.service.timeline.exception.ClientTimelineEntryInvalidDescriptionException;
import com.extole.model.service.timeline.exception.ClientTimelineEntryInvalidNameException;
import com.extole.model.service.timeline.exception.ClientTimelineEntryMissingDateException;
import com.extole.model.service.timeline.exception.ClientTimelineEntryMissingNameException;
import com.extole.model.service.timeline.exception.ClientTimelineEntryNameExistsException;
import com.extole.model.service.timeline.exception.ClientTimelineEntryNotFoundException;
import com.extole.model.service.timeline.exception.ClientTimelineEntryNotModifiableException;

@Provider
public class ClientTimelineEntryEndpointsImpl implements ClientTimelineEntryEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientTimelineEntryService clientTimelineEntryService;

    @Autowired
    public ClientTimelineEntryEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientTimelineEntryService clientTimelineEntryService) {
        this.authorizationProvider = authorizationProvider;
        this.clientTimelineEntryService = clientTimelineEntryService;
    }

    @Override
    public ClientTimelineEntryResponse create(String accessToken, ClientTimelineEntryRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientTimelineEntryRestException,
        ClientTimelineEntryNotModifiableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (request.getDate() == null) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.DATE_MISSING)
                .build();
        }

        try {
            ClientTimelineEntryBuilder builder =
                clientTimelineEntryService.create(authorization, request.getName(), request.getDate().toInstant());
            if (request.getDescription().isPresent()) {
                builder = builder.withDescription(request.getDescription().get());
            }
            builder.withTags(request.getTags());
            ClientTimelineEntry clientTimelineEntry = builder.save();
            return toClientTimelineEntryResponse(clientTimelineEntry, timeZone);
        } catch (ClientTimelineEntryInvalidDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.DESCRIPTION_INVALID)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.NAME_INVALID)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryNameExistsException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.NAME_EXISTS)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryMissingDateException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.DATE_MISSING)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryNotModifiableException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryNotModifiableRestException.class)
                .withErrorCode(ClientTimelineEntryNotModifiableRestException.NOT_MODIFIABLE)
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
    public ClientTimelineEntryResponse update(String accessToken, String name,
        UpdateClientTimelineEntryRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientTimelineEntryNotFoundRestException,
        ClientTimelineEntryRestException, ClientTimelineEntryNotModifiableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ClientTimelineEntryBuilder builder = clientTimelineEntryService.update(authorization, name);
            if (request.getName().isPresent()) {
                builder = builder.withName(request.getName().get());
            }
            if (request.getDate().isPresent()) {
                builder = builder.withDate(request.getDate().get().toInstant());
            }
            if (request.getDescription().isPresent()) {
                builder = builder.withDescription(request.getDescription().get());
            }
            if (request.getTags().isPresent()) {
                builder.withTags(request.getTags().get());
            }
            ClientTimelineEntry clientTimelineEntry = builder.save();
            return toClientTimelineEntryResponse(clientTimelineEntry, timeZone);
        } catch (ClientTimelineEntryInvalidDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.DESCRIPTION_INVALID)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.NAME_INVALID)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryNameExistsException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.NAME_EXISTS)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryMissingDateException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryRestException.class)
                .withErrorCode(ClientTimelineEntryRestException.DATE_MISSING)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryNotModifiableException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryNotModifiableRestException.class)
                .withErrorCode(ClientTimelineEntryNotModifiableRestException.NOT_MODIFIABLE)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryNotFoundRestException.class)
                .withErrorCode(ClientTimelineEntryNotFoundRestException.ENTRY_NOT_FOUND)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientTimelineEntryResponse get(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientTimelineEntryNotFoundRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return toClientTimelineEntryResponse(clientTimelineEntryService.get(authorization, name), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryNotFoundRestException.class)
                .withErrorCode(ClientTimelineEntryNotFoundRestException.ENTRY_NOT_FOUND)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientTimelineEntryResponse delete(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientTimelineEntryNotFoundRestException,
        ClientTimelineEntryNotModifiableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return toClientTimelineEntryResponse(clientTimelineEntryService.delete(authorization, name), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryNotFoundRestException.class)
                .withErrorCode(ClientTimelineEntryNotFoundRestException.ENTRY_NOT_FOUND)
                .withCause(e)
                .build();
        } catch (ClientTimelineEntryNotModifiableException e) {
            throw RestExceptionBuilder.newBuilder(ClientTimelineEntryNotModifiableRestException.class)
                .withErrorCode(ClientTimelineEntryNotModifiableRestException.NOT_MODIFIABLE)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<ClientTimelineEntryResponse> list(String accessToken,
        @Nullable Set<String> filterTags, ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return clientTimelineEntryService
                .list(authorization, filterTags == null ? Collections.emptySet() : filterTags)
                .stream()
                .map(entry -> toClientTimelineEntryResponse(entry, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private ClientTimelineEntryResponse toClientTimelineEntryResponse(ClientTimelineEntry clientTimelineEntry,
        ZoneId timeZone) {
        return ClientTimelineEntryResponse.builder()
            .withUserId(clientTimelineEntry.getUserId().getValue())
            .withName(clientTimelineEntry.getName())
            .withDescription(clientTimelineEntry.getDescription().orElse(null))
            .withDate(clientTimelineEntry.getDate().atZone(timeZone))
            .withTags(clientTimelineEntry.getTags())
            .build();
    }
}
