package com.extole.consumer.rest.impl.me.asset;

import java.net.URI;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.common.SitePatternsUrlValidator;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.asset.MeAssetEndpoints;
import com.extole.consumer.rest.me.asset.api.AssetRestException;
import com.extole.id.Id;
import com.extole.person.service.profile.asset.AssetContentDownloadException;
import com.extole.person.service.profile.asset.AssetContentNotFoundException;
import com.extole.person.service.profile.asset.AssetNotFoundException;
import com.extole.person.service.profile.asset.AssetServiceRuntimeException;
import com.extole.person.service.profile.asset.PersonAsset;
import com.extole.person.service.profile.asset.PersonAssetService;

@Provider
public class MeAssetEndpointsImpl implements MeAssetEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(MeAssetEndpointsImpl.class);
    private static final String ASSET_CONTENT_DISPOSITION_FORMATTER = "attachment; filename = %s";

    private final ContainerRequestContext requestContext;
    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonAssetService personAssetService;
    private final SitePatternsUrlValidator sitePatternsUrlValidator;

    @Inject
    public MeAssetEndpointsImpl(
        @Context ContainerRequestContext requestContext,
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
    public Response downloadAssetById(String accessToken, String assetId, String defaultUrl)
        throws AuthorizationRestException, AssetRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        try {

            // TODO serve binary asset from origin server ENG-12277
            PersonAsset asset = personAssetService.getAsset(authorization, Id.valueOf(assetId));
            return downloadAsset(authorization, asset);
        } catch (AssetNotFoundException e) {
            if (!Strings.isNullOrEmpty(defaultUrl)
                && sitePatternsUrlValidator.isSupported(authorization.getClientId(),
                    requestContext.getUriInfo().getBaseUri().getHost(), defaultUrl)) {
                return Response.temporaryRedirect(URI.create(defaultUrl)).build();
            } else {
                throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                    .withErrorCode(AssetRestException.ASSET_NOT_FOUND)
                    .addParameter("criteria", "asset_id")
                    .addParameter("value", assetId)
                    .withCause(e)
                    .build();
            }
        }
    }

    @Override
    public Response downloadAssetByName(String accessToken, String name, String defaultUrl)
        throws AuthorizationRestException, AssetRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        try {
            // TODO serve binary asset from origin server ENG-12277
            PersonAsset asset = personAssetService.getAssetByName(authorization, name);
            return downloadAsset(authorization, asset);
        } catch (AssetNotFoundException e) {
            if (!Strings.isNullOrEmpty(defaultUrl)
                && sitePatternsUrlValidator.isSupported(authorization.getClientId(),
                    requestContext.getUriInfo().getBaseUri().getHost(), defaultUrl)) {
                return Response.temporaryRedirect(URI.create(defaultUrl)).build();
            } else {
                throw RestExceptionBuilder.newBuilder(AssetRestException.class)
                    .withErrorCode(AssetRestException.ASSET_NOT_FOUND)
                    .addParameter("criteria", "name")
                    .addParameter("value", name)
                    .withCause(e)
                    .build();
            }
        }
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
                LOG.error("Asset content not found for assetId:" + asset.getId(), cause);
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
}
