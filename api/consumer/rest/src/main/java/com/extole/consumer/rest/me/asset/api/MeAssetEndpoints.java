package com.extole.consumer.rest.me.asset.api;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path(MeAssetEndpoints.ASSET_URI)
@Tag(name = MeAssetEndpoints.ASSET_URI, description = "MeAssets")
public interface MeAssetEndpoints {

    String ASSET_URI = "/v4/me/assets";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<AssetResponse> listAssets(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    AssetResponse createAsset(@AccessTokenParam(readCookie = false) String accessToken,
        @FormDataParam("asset") AssetRequest request,
        @FormDataParam("file") InputStream file,
        @FormDataParam("file") FormDataBodyPart dataBodyPart)
        throws AuthorizationRestException, AssetValidationRestException;

    @GET
    @Path("/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    AssetResponse readAsset(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("assetId") String assetId)
        throws AuthorizationRestException, AssetRestException;

    @DELETE
    @Path("/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    AssetResponse deleteAsset(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("assetId") String assetId)
        throws AuthorizationRestException, AssetRestException;

    @GET
    @Path("/{assetId}/download")
    Response downloadAssetById(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("assetId") String assetId)
        throws AuthorizationRestException, AssetRestException;

    @GET
    @Path("/download")
    Response downloadAssetByName(@AccessTokenParam(readCookie = false) String accessToken,
        @QueryParam("name") String name)
        throws AuthorizationRestException, AssetRestException;
}
