package com.extole.client.rest.impl.person.asset;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContext;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextBuilder;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService.HttpRequestBodyCapturingType;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.asset.PersonAssetEndpoints;
import com.extole.client.rest.person.asset.PersonAssetRequest;
import com.extole.client.rest.person.asset.PersonAssetResponse;
import com.extole.client.rest.person.asset.PersonAssetRestException;
import com.extole.client.rest.person.asset.PersonAssetStatus;
import com.extole.client.rest.person.asset.PersonAssetType;
import com.extole.client.rest.person.asset.PersonAssetValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventProcessorConfigurator;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.event.consumer.input.InputConsumerEvent;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.asset.AssetContentDownloadException;
import com.extole.person.service.profile.asset.AssetContentNotFoundException;
import com.extole.person.service.profile.asset.AssetContentSizeException;
import com.extole.person.service.profile.asset.AssetCreateRequest;
import com.extole.person.service.profile.asset.AssetLimitExceededException;
import com.extole.person.service.profile.asset.AssetMimeTypeInvalidException;
import com.extole.person.service.profile.asset.AssetNotFoundException;
import com.extole.person.service.profile.asset.AssetServiceRuntimeException;
import com.extole.person.service.profile.asset.PersonAsset;
import com.extole.person.service.profile.asset.PersonAssetService;

@Provider
public class PersonAssetEndpointsImpl implements PersonAssetEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(PersonAssetEndpointsImpl.class);
    private static final String ASSET_CONTENT_DISPOSITION_FORMATTER = "attachment; filename = %s";

    private static final String EVENT_NAME_FOR_ASSET_CREATE = "extole.person.asset.create";
    private static final String EVENT_NAME_FOR_ASSET_DELETE = "extole.person.asset.delete";

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final PersonAssetService personAssetService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public PersonAssetEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PersonService personService,
        PersonAssetService personAssetService,
        ConsumerEventSenderService consumerEventSenderService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.authorizationProvider = authorizationProvider;
        this.personService = personService;
        this.personAssetService = personAssetService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public List<PersonAssetResponse> listAssets(String accessToken, String personId)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personAssetService.getAssets(authorization, Id.valueOf(personId))
                .stream()
                .map(this::toPersonAssetResponse)
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId).withCause(e)
                .build();
        }
    }

    @Override
    public PersonAssetResponse readAsset(String accessToken, String personId, String assetId)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonAsset asset = personAssetService.getAsset(authorization, Id.valueOf(personId), Id.valueOf(assetId));
            return toPersonAssetResponse(asset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (com.extole.person.service.profile.asset.AssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                .withErrorCode(PersonAssetRestException.ASSET_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("criteria", "asset_id")
                .addParameter("value", assetId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Response downloadAssetById(String accessToken, String personId, String assetId)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonAsset asset = personAssetService.getAsset(authorization, Id.valueOf(personId), Id.valueOf(assetId));
            return downloadAsset(authorization, Id.valueOf(personId), asset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                .withErrorCode(PersonAssetRestException.ASSET_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("criteria", "asset_id")
                .addParameter("value", assetId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Response downloadAssetByName(String accessToken, String personId, String name)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonAsset asset = personAssetService.getAssetByName(authorization, Id.valueOf(personId), name);
            return downloadAsset(authorization, Id.valueOf(personId), asset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                .withErrorCode(PersonAssetRestException.ASSET_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("criteria", "name")
                .addParameter("value", name)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PersonAssetResponse createAsset(String accessToken, String personId, PersonAssetRequest request,
        InputStream file, FormDataBodyPart dataBodyPart)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            String fileName = dataBodyPart.getFormDataContentDisposition().getFileName();
            ClientRequestContext requestContext =
                getEventRequestContext(authorization, EVENT_NAME_FOR_ASSET_CREATE,
                    requestContextBuilder -> requestContextBuilder
                        .withHttpRequestBodyCapturing(HttpRequestBodyCapturingType.LIMITED),
                    processor -> processor
                        .addLogMessage("Creating person asset via client api /v2/persons/{personId}/assets endpoints."
                            + " Asset name: " + request.getName() + "."
                            + " Asset type: " + request.getDataType() + "."
                            + " Asset tags: " + request.getTags() + "."));

            PersonAsset asset = personAssetService.createAsset(authorization, Id.valueOf(personId),
                new AssetCreateRequest(fileName, Optional.ofNullable(request.getName()), file,
                    ImmutableList.<String>builder().addAll(request.getTags()).add("source:client-api").build(),
                    dataBodyPart.getMediaType().toString(),
                    request.getDataType().map(PersonAssetType::name).map(PersonAsset.Type::valueOf),
                    requestContext.getProcessedRawEvent().getClientDomain().toString(),
                    requestContext.getProcessedRawEvent().getClientDomain().getId().getValue()));

            sendInputEvent(authorization, Id.valueOf(personId), requestContext);

            return toPersonAssetResponse(asset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AssetContentSizeException e) {
            throw RestExceptionBuilder.newBuilder(PersonAssetValidationRestException.class)
                .withErrorCode(PersonAssetValidationRestException.ASSET_SIZE_INVALID)
                .addParameter("person_id", personId)
                .addParameter("size", Integer.valueOf(e.getSize()))
                .addParameter("max_allowed_size", Integer.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (AssetMimeTypeInvalidException e) {
            throw RestExceptionBuilder.newBuilder(PersonAssetValidationRestException.class)
                .withErrorCode(PersonAssetValidationRestException.ASSET_MIME_TYPE_INVALID)
                .addParameter("person_id", personId)
                .addParameter("mime_type", e.getMimeType())
                .withCause(e)
                .build();
        } catch (AssetLimitExceededException e) {
            throw RestExceptionBuilder.newBuilder(PersonAssetValidationRestException.class)
                .withErrorCode(PersonAssetValidationRestException.ASSET_LIMIT_EXCEEDED)
                .addParameter("person_id", personId)
                .addParameter("limit", Integer.valueOf(e.getLimit()))
                .withCause(e)
                .build();
        } catch (AssetServiceRuntimeException | EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e.getCause())
                .build();
        }
    }

    @Override
    public PersonAssetResponse deleteAsset(String accessToken, String personId, String assetId)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientRequestContext requestContext =
                getEventRequestContext(authorization, EVENT_NAME_FOR_ASSET_DELETE, requestContextBuilder -> {},
                    processor -> processor
                        .addLogMessage("Deleting person asset via client api /v2/persons/{personId}/assets endpoints."
                            + " Asset id: " + assetId + "."));

            PersonAsset deletedAsset =
                personAssetService.deleteAsset(authorization, Id.valueOf(personId), Id.valueOf(assetId),
                    requestContext.getProcessedRawEvent().getClientDomain().toString(),
                    requestContext.getProcessedRawEvent().getClientDomain().getId().getValue());

            sendInputEvent(authorization, Id.valueOf(personId), requestContext);

            return toPersonAssetResponse(deletedAsset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                .withErrorCode(PersonAssetRestException.ASSET_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("criteria", "asset_id")
                .addParameter("value", assetId)
                .withCause(e)
                .build();
        } catch (AssetServiceRuntimeException | EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    // ENG-19642 person assets cannot be created from inside person lock because the asset upload can take time
    private InputConsumerEvent sendInputEvent(ClientAuthorization authorization, Id<PersonHandle> personId,
        ClientRequestContext requestContext) throws AuthorizationException, PersonNotFoundException {
        return consumerEventSenderService
            .createInputEvent(authorization, requestContext.getProcessedRawEvent(), personId)
            .send();
    }

    private ClientRequestContext getEventRequestContext(ClientAuthorization authorization, String eventName,
        Consumer<ClientRequestContextBuilder> clientRequestContextBuilderDecorator,
        Consumer<EventProcessorConfigurator> processorConsumer) throws EventProcessorException, AuthorizationException {
        ClientRequestContextBuilder builder = clientRequestContextService.createBuilder(authorization, servletRequest)
            .withEventName(eventName)
            .withEventProcessing(processorConsumer);
        clientRequestContextBuilderDecorator.accept(builder);

        return builder.build();
    }

    private Response downloadAsset(Authorization authorization, Id<PersonHandle> personId, PersonAsset asset) {
        StreamingOutput streamer =
            outputStream -> {
                try {
                    personAssetService.downloadAsset(authorization, personId, asset.getId(), outputStream);
                } catch (AuthorizationException e) {
                    convertException(RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                        .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                        .withCause(e)
                        .build());
                } catch (PersonNotFoundException e) {
                    convertException(RestExceptionBuilder.newBuilder(PersonRestException.class)
                        .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                        .addParameter("person_id", personId)
                        .withCause(e)
                        .build());
                } catch (AssetNotFoundException e) {
                    convertException(RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                        .withErrorCode(PersonAssetRestException.ASSET_NOT_FOUND)
                        .addParameter("person_id", personId)
                        .addParameter("criteria", "asset_id")
                        .addParameter("value", asset.getId())
                        .withCause(e)
                        .build());
                } catch (AssetContentNotFoundException e) {
                    LOG.error("Asset content not found for assetId:" + asset.getId(), e);
                    convertException(RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                        .withErrorCode(PersonAssetRestException.ASSET_NOT_FOUND)
                        .addParameter("person_id", personId)
                        .addParameter("criteria", "asset_id")
                        .addParameter("value", asset.getId())
                        .withCause(e)
                        .build());
                } catch (AssetContentDownloadException e) {
                    convertException(RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                        .withErrorCode(PersonAssetRestException.ASSET_CONTENT_NOT_DOWNLOADED)
                        .addParameter("person_id", personId)
                        .addParameter("asset_id", asset.getId())
                        .withCause(e)
                        .build());
                }
            };
        Response.ResponseBuilder responseBuilder = Response.ok(streamer, asset.getMimeType());
        responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
            String.format(ASSET_CONTENT_DISPOSITION_FORMATTER, asset.getFilename().toLowerCase()));
        return responseBuilder.build();
    }

    private void convertException(RestException e) {
        RestExceptionResponse response = new RestExceptionResponseBuilder(e).build();
        throw new WebApplicationException(Response.status(response.getHttpStatusCode()).entity(response)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build());
    }

    private PersonAssetResponse toPersonAssetResponse(PersonAsset asset) {
        return PersonAssetResponse.builder()
            .withId(asset.getId().getValue())
            .withName(asset.getName())
            .withFilename(asset.getFilename())
            .withMimeType(asset.getMimeType())
            .withStatus(PersonAssetStatus.valueOf(asset.getStatus().name()))
            .withTags(asset.getTags())
            .withDataType(PersonAssetType.valueOf(asset.getType().name()))
            .build();
    }
}
