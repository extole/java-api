package com.extole.client.rest.media;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/media-assets")
public interface MediaAssetEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<MediaAssetResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    MediaAssetResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("assetId") String assetId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, MediaAssetRestException;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    MediaAssetResponse create(@UserAccessTokenParam String accessToken,
        @FormDataParam("asset") MediaAssetRequest request, @FormDataParam("file") InputStream inputStream,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, MediaAssetValidationRestException;

    @PUT
    @Path("/{assetId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    MediaAssetResponse update(@UserAccessTokenParam String accessToken, @PathParam("assetId") String assetId,
        @Nullable @FormDataParam("asset") MediaAssetRequest request,
        @Nullable @FormDataParam("file") InputStream inputStream, @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, MediaAssetRestException, MediaAssetValidationRestException;

    @DELETE
    @Path("/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    MediaAssetResponse delete(@UserAccessTokenParam String accessToken, @PathParam("assetId") String assetId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, MediaAssetRestException;

    @GET
    @Path("/{assetId}/download")
    Response download(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("assetId") String assetId)
        throws UserAuthorizationRestException, MediaAssetRestException;
}
