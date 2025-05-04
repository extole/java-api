package com.extole.consumer.rest.impl.person.asset.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.person.PersonRestException;
import com.extole.consumer.rest.person.asset.api.DataType;
import com.extole.consumer.rest.person.asset.api.PersonAssetEndpoints;
import com.extole.consumer.rest.person.asset.api.PersonAssetResponse;
import com.extole.consumer.rest.person.asset.api.PersonAssetRestException;
import com.extole.consumer.rest.person.asset.api.PersonAssetStatus;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.asset.AssetContentDownloadException;
import com.extole.person.service.profile.asset.AssetContentNotFoundException;
import com.extole.person.service.profile.asset.AssetNotFoundException;
import com.extole.person.service.profile.asset.PersonAsset;
import com.extole.person.service.profile.asset.PersonAssetService;

@Provider
public class PersonAssetEndpointsImpl implements PersonAssetEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(PersonAssetEndpointsImpl.class);
    private static final String ASSET_CONTENT_DISPOSITION_FORMATTER = "attachment; filename = %s";

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonAssetService personAssetService;

    @Inject
    public PersonAssetEndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        PersonAssetService personAssetService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.personAssetService = personAssetService;
    }

    @Override
    public List<PersonAssetResponse> listAssets(String accessToken, String personId)
        throws AuthorizationRestException, PersonRestException {
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            List<PersonAsset> assets;

            if (isAuthorized(authorization, personId)) {
                assets = personAssetService.getAssets(authorization, Id.valueOf(personId));
            } else {
                assets = personAssetService.getPublicAssets(authorization.getClientId(), Id.valueOf(personId));
            }

            return assets.stream()
                .map(this::toAssetResponse)
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId).withCause(e)
                .build();
        }
    }

    @Override
    public PersonAssetResponse readAsset(String accessToken, String personId, String assetId)
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException {
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            PersonAsset asset;

            if (isAuthorized(authorization, personId)) {
                asset = personAssetService.getAsset(authorization, Id.valueOf(personId), Id.valueOf(assetId));
            } else {
                asset = personAssetService.getPublicAsset(authorization.getClientId(), Id.valueOf(personId),
                    Id.valueOf(assetId));
            }

            return toAssetResponse(asset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED).withCause(e).build();
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
    public Response downloadAssetById(String accessToken, String personId, String assetId)
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException {
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            PersonAsset asset;

            if (isAuthorized(authorization, personId)) {
                asset = personAssetService.getAsset(authorization, Id.valueOf(personId), Id.valueOf(assetId));
            } else {
                asset = personAssetService.getPublicAsset(authorization.getClientId(), Id.valueOf(personId),
                    Id.valueOf(assetId));
            }

            return downloadAsset(authorization, Id.valueOf(personId), asset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED).withCause(e).build();
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
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException {
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            PersonAsset asset;
            if (isAuthorized(authorization, personId)) {
                asset = personAssetService.getAssetByName(authorization, Id.valueOf(personId), name);
            } else {
                asset =
                    personAssetService.getPublicAssetByName(authorization.getClientId(), Id.valueOf(personId), name);
            }
            return downloadAsset(authorization, Id.valueOf(personId), asset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED).withCause(e).build();
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

    private Authorization getAuthorizationFromRequest(String accessToken) throws AuthorizationRestException {
        return consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
    }

    private Response downloadAsset(Authorization authorization, Id<PersonHandle> personId, PersonAsset asset) {
        StreamingOutput streamer =
            outputStream -> {
                try {
                    if (isAuthorized(authorization, personId.getValue())) {
                        personAssetService.downloadAsset(authorization, personId, asset.getId(),
                            outputStream);
                    } else {
                        personAssetService.downloadPublicAsset(authorization, personId, asset.getId(),
                            outputStream);
                    }
                } catch (AuthorizationException e) {
                    convertException(RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                        .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
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
                        .withErrorCode(PersonAssetRestException.ASSET_CONTENT_NOT_DOWNLOADABLE)
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

    private PersonAssetResponse toAssetResponse(PersonAsset asset) {
        return PersonAssetResponse.builder()
            .withId(asset.getId().getValue())
            .withName(asset.getName())
            .withFilename(asset.getFilename())
            .withMimeType(asset.getMimeType())
            .withStatus(PersonAssetStatus.valueOf(asset.getStatus().name()))
            .withTags(asset.getTags())
            .withPersonDataType(DataType.valueOf(asset.getType().name()))
            .build();
    }

    private void convertException(RestException e) {
        RestExceptionResponse response = new RestExceptionResponseBuilder(e).build();
        throw new WebApplicationException(Response.status(response.getHttpStatusCode()).entity(response)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build());
    }

    private boolean isAuthorized(Authorization authorization, String personId) {
        return authorization.isAuthorized(authorization.getClientId(), Authorization.Scope.CLIENT_ADMIN)
            || authorization.getIdentityId().getValue().equals(personId);
    }
}
