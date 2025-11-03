package com.extole.consumer.rest.impl.person.asset;

import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.common.SitePatternsUrlValidator;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.person.PersonRestException;
import com.extole.consumer.rest.person.asset.PersonAssetEndpoints;
import com.extole.consumer.rest.person.asset.api.PersonAssetRestException;
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

    private final ContainerRequestContext requestContext;
    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonAssetService personAssetService;
    private final SitePatternsUrlValidator sitePatternsUrlValidator;

    @Inject
    public PersonAssetEndpointsImpl(ContainerRequestContext requestContext,
        @Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        PersonAssetService personAssetService,
        SitePatternsUrlValidator sitePatternsUrlValidator) {
        this.requestContext = requestContext;
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.personAssetService = personAssetService;
        this.sitePatternsUrlValidator = sitePatternsUrlValidator;
    }

    @Override
    public Response downloadAssetById(String accessToken, String personId, String assetId, String defaultUrl)
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
                .addParameter("client_id", authorization.getClientId())
                .withCause(e)
                .build();
        } catch (AssetNotFoundException e) {
            if (!Strings.isNullOrEmpty(defaultUrl)
                && sitePatternsUrlValidator.isSupported(authorization.getClientId(),
                    requestContext.getUriInfo().getBaseUri().getHost(), defaultUrl)) {
                return Response.temporaryRedirect(URI.create(defaultUrl)).build();
            } else {
                throw RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                    .withErrorCode(PersonAssetRestException.ASSET_NOT_FOUND)
                    .addParameter("person_id", personId)
                    .addParameter("criteria", "asset_id")
                    .addParameter("value", assetId)
                    .withCause(e)
                    .build();
            }
        }
    }

    @Override
    public Response downloadAssetByName(String accessToken, String personId, Optional<String> name,
        Optional<String> defaultUrl)
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException {
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            String assetName = name.orElseThrow(() -> new AssetNotFoundException("Asset not found for name: " + name));
            PersonAsset asset;
            if (isAuthorized(authorization, personId)) {
                asset = personAssetService.getAssetByName(authorization, Id.valueOf(personId), assetName);
            } else {
                asset =
                    personAssetService.getPublicAssetByName(authorization.getClientId(), Id.valueOf(personId),
                        assetName);
            }
            return downloadAsset(authorization, Id.valueOf(personId), asset);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("client_id", authorization.getClientId())
                .withCause(e)
                .build();
        } catch (AssetNotFoundException e) {
            if (defaultUrl.isPresent()
                && sitePatternsUrlValidator.isSupported(authorization.getClientId(),
                    requestContext.getUriInfo().getBaseUri().getHost(), defaultUrl.get())) {
                return Response.temporaryRedirect(URI.create(defaultUrl.get())).build();
            }
            throw RestExceptionBuilder.newBuilder(PersonAssetRestException.class)
                .withErrorCode(PersonAssetRestException.ASSET_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("criteria", "name")
                .addParameter("value", name.orElse(null))
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
                        .addParameter("client_id", authorization.getClientId())
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
