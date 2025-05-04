package com.extole.consumer.rest.impl.me.asset.api;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.asset.api.AssetRequest;
import com.extole.consumer.rest.me.asset.api.AssetResponse;
import com.extole.consumer.rest.me.asset.api.AssetRestException;
import com.extole.consumer.rest.me.asset.api.AssetStatus;
import com.extole.consumer.rest.me.asset.api.AssetValidationRestException;
import com.extole.consumer.rest.me.asset.api.MeAssetEndpoints;
import com.extole.consumer.rest.me.asset.api.MeAssetType;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.event.consumer.input.InputConsumerEvent;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;
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
public class MeAssetEndpointsImpl implements MeAssetEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(MeAssetEndpointsImpl.class);
    private static final String ASSET_CONTENT_DISPOSITION_FORMATTER = "attachment; filename = %s";

    private static final String EVENT_NAME_FOR_ASSET_CREATE = "extole.person.asset.create";
    private static final String EVENT_NAME_FOR_ASSET_DELETE = "extole.person.asset.delete";

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonAssetService personAssetService;
    private final ConsumerEventSenderService consumerEventSenderService;

    @Inject
    public MeAssetEndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        PersonAssetService personAssetService,
        ConsumerEventSenderService consumerEventSenderService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.personAssetService = personAssetService;
        this.consumerEventSenderService = consumerEventSenderService;
    }

    @Override
    public List<AssetResponse> listAssets(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        return personAssetService.getAssets(authorization)
            .stream()
            .map(this::toAssetResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AssetResponse createAsset(String accessToken, AssetRequest request, InputStream file,
        FormDataBodyPart dataBodyPart) throws AuthorizationRestException, AssetValidationRestException {
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .withHttpRequestBodyCapturing(ConsumerRequestContextService.HttpRequestBodyCapturingType.LIMITED)
            .build()
            .getAuthorization();
        try {
            String filename = dataBodyPart.getFormDataContentDisposition().getFileName();
            ConsumerRequestContext requestContext = getConsumerRequestContext(authorization,
                requestContextBuilder -> requestContextBuilder
                    .withEventName(EVENT_NAME_FOR_ASSET_CREATE)
                    .withEventProcessing(processor -> processor
                        .addLogMessage("Creating person asset via consumer api /v4/me/assets endpoints."
                            + " Asset name: " + request.getName() + "."
                            + " Asset type: " + request.getDataType() + "."
                            + " Asset tags: " + request.getTags() + "."))
                    .withHttpRequestBodyCapturing(ConsumerRequestContextService.HttpRequestBodyCapturingType.LIMITED));

            PersonAsset asset = personAssetService.createAsset(authorization,
                new AssetCreateRequest(filename, Optional.ofNullable(request.getName()), file,
                    ImmutableList.<String>builder().addAll(request.getTags()).add("source:consumer-api").build(),
                    dataBodyPart.getMediaType().toString(),
                    request.getDataType().map(MeAssetType::name).map(PersonAsset.Type::valueOf),
                    requestContext.getProcessedRawEvent().getClientDomain().toString(),
                    requestContext.getProcessedRawEvent().getClientDomain().getId().getValue()));

            sendInputEvent(authorization, requestContext);

            return toAssetResponse(asset);
        } catch (PersonNotFoundException | AssetServiceRuntimeException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (AssetContentSizeException e) {
            throw RestExceptionBuilder.newBuilder(AssetValidationRestException.class)
                .withErrorCode(AssetValidationRestException.ASSET_SIZE_INVALID)
                .addParameter("size", Integer.valueOf(e.getSize()))
                .addParameter("max_allowed_size", Integer.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (AssetMimeTypeInvalidException e) {
            throw RestExceptionBuilder.newBuilder(AssetValidationRestException.class)
                .withErrorCode(AssetValidationRestException.ASSET_MIME_TYPE_INVALID)
                .addParameter("mime_type", e.getMimeType())
                .withCause(e)
                .build();
        } catch (AssetLimitExceededException e) {
            throw RestExceptionBuilder.newBuilder(AssetValidationRestException.class)
                .withErrorCode(AssetValidationRestException.ASSET_LIMIT_EXCEEDED)
                .addParameter("limit", Integer.valueOf(e.getLimit()))
                .withCause(e)
                .build();
        }
    }

    @Override
    public AssetResponse readAsset(String accessToken, String assetId)
        throws AuthorizationRestException, AssetRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            return toAssetResponse(personAssetService.getAsset(authorization, Id.valueOf(assetId)));
        } catch (AssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                .withErrorCode(AssetRestException.ASSET_NOT_FOUND)
                .addParameter("criteria", "asset_id")
                .addParameter("value", assetId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AssetResponse deleteAsset(String accessToken, String assetId)
        throws AuthorizationRestException, AssetRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            ConsumerRequestContext requestContext = getConsumerRequestContext(authorization,
                requestContextBuilder -> requestContextBuilder
                    .withEventName(EVENT_NAME_FOR_ASSET_DELETE)
                    .withEventProcessing(processor -> processor.addLogMessage(
                        "Deleting person asset via consumer api /v4/me/assets endpoints.Asset id: " + assetId + ".")));

            PersonAsset deletedAsset = personAssetService.deleteAsset(authorization, Id.valueOf(assetId),
                requestContext.getProcessedRawEvent().getClientDomain().toString(),
                requestContext.getProcessedRawEvent().getClientDomain().getId().getValue());

            sendInputEvent(authorization, requestContext);

            return toAssetResponse(deletedAsset);
        } catch (AssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                .withErrorCode(AssetRestException.ASSET_NOT_FOUND)
                .addParameter("criteria", "asset_id")
                .addParameter("value", assetId)
                .withCause(e)
                .build();
        } catch (AssetServiceRuntimeException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e.getCause())
                .build();
        }
    }

    @Override
    public Response downloadAssetById(String accessToken, String assetId)
        throws AuthorizationRestException, AssetRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            // TODO serve binary asset from origin server ENG-12277
            PersonAsset asset = personAssetService.getAsset(authorization, Id.valueOf(assetId));
            return downloadAsset(authorization, asset);
        } catch (AssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                .withErrorCode(AssetRestException.ASSET_NOT_FOUND)
                .addParameter("criteria", "asset_id")
                .addParameter("value", assetId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Response downloadAssetByName(String accessToken, String name)
        throws AuthorizationRestException, AssetRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            // TODO serve binary asset from origin server ENG-12277
            PersonAsset asset = personAssetService.getAssetByName(authorization, name);
            return downloadAsset(authorization, asset);
        } catch (AssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                .withErrorCode(AssetRestException.ASSET_NOT_FOUND)
                .addParameter("criteria", "name")
                .addParameter("value", name)
                .withCause(e)
                .build();
        }
    }

    // ENG-19642 person assets cannot be created from inside person lock because the asset upload can take time
    private InputConsumerEvent sendInputEvent(PersonAuthorization authorization,
        ConsumerRequestContext requestContext) throws AuthorizationException {
        return consumerEventSenderService
            .createInputEvent(authorization, requestContext.getProcessedRawEvent(), authorization.getIdentity())
            .send();
    }

    private ConsumerRequestContext getConsumerRequestContext(Authorization authorization,
        Consumer<ConsumerRequestContextService.ConsumerRequestContextBuilder> requestContextBuilderConsumerDecorator)
        throws AuthorizationRestException {
        ConsumerRequestContextService.ConsumerRequestContextBuilder requestContextBuilder =
            consumerRequestContextService.createBuilder(servletRequest)
                .withAccessToken(authorization.getAccessToken());
        requestContextBuilderConsumerDecorator.accept(requestContextBuilder);
        return requestContextBuilder.build();
    }

    private Response downloadAsset(PersonAuthorization authorization, PersonAsset asset) throws AssetRestException {
        try {
            StreamingOutput streamer =
                outputStream -> {
                    try {
                        personAssetService.downloadAsset(authorization, asset.getId(), outputStream);
                    } catch (AssetNotFoundException | AssetContentNotFoundException | AssetContentDownloadException e) {
                        throw new AssetServiceRuntimeException("Unable to download asset", e);
                    }
                };
            Response.ResponseBuilder responseBuilder = Response.ok(streamer, asset.getMimeType());
            responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
                String.format(ASSET_CONTENT_DISPOSITION_FORMATTER, asset.getFilename().toLowerCase()));
            return responseBuilder.build();
        } catch (AssetServiceRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssetNotFoundException) {
                throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                    .withErrorCode(AssetRestException.ASSET_NOT_FOUND)
                    .addParameter("criteria", "asset_id")
                    .addParameter("value", asset.getId())
                    .withCause(cause)
                    .build();
            } else if (cause instanceof AssetContentNotFoundException) {
                throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                    .withErrorCode(AssetRestException.ASSET_NOT_FOUND)
                    .addParameter("criteria", "asset_id")
                    .addParameter("value", asset.getId())
                    .withCause(cause)
                    .build();
            } else if (cause instanceof AssetContentDownloadException) {
                throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                    .withErrorCode(AssetRestException.ASSET_CONTENT_NOT_DOWNLOADABLE)
                    .addParameter("asset_id", asset.getId())
                    .withCause(cause)
                    .build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(cause)
                    .build();
            }
        }
    }

    private PersonAuthorization getAuthorizationFromRequest(String accessToken) throws AuthorizationRestException {
        return consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
    }

    private AssetResponse toAssetResponse(PersonAsset asset) {
        return AssetResponse.builder()
            .withId(asset.getId().getValue())
            .withName(asset.getName())
            .withFilename(asset.getFilename())
            .withMimeType(asset.getMimeType())
            .withStatus(AssetStatus.valueOf(asset.getStatus().name()))
            .withTags(asset.getTags())
            .withDataType(MeAssetType.valueOf(asset.getType().name()))
            .build();
    }
}
