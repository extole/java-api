package com.extole.consumer.rest.person.asset.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.person.PersonRestException;

@Path(PersonAssetEndpoints.ASSET_URI)
public interface PersonAssetEndpoints {

    String ASSET_URI = "/v4/persons/{personId}/assets/";

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Operation(summary = "Get a list of assets")
    List<PersonAssetResponse> listAssets(@AccessTokenParam(readCookie = false) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("personId") String personId)
        throws AuthorizationRestException, PersonRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{assetId}")
    @Operation(summary = "Gets Details for an asset")
    PersonAssetResponse readAsset(@AccessTokenParam(readCookie = false) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("personId") String personId,
        @Parameter(description = "The Extole unique profile identifier of this asset at Extole.",
            required = true) @PathParam("assetId") String assetId)
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException;

    @GET
    @Path("/{assetId}/download")
    @Operation(summary = "Download content for an asset")
    Response downloadAssetById(@AccessTokenParam(readCookie = false) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("personId") String personId,
        @Parameter(description = "The Extole unique profile identifier of this asset at Extole.",
            required = true) @PathParam("assetId") String assetId)
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException;

    @GET
    @Path("/download")
    @Operation(summary = "Download content for an asset")
    Response downloadAssetByName(@AccessTokenParam(readCookie = false) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("personId") String personId,
        @QueryParam("name") String name)
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException;
}
