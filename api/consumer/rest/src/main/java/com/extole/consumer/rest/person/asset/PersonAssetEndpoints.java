package com.extole.consumer.rest.person.asset;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.person.PersonRestException;
import com.extole.consumer.rest.person.asset.api.PersonAssetRestException;

@Path(PersonAssetEndpoints.ASSET_URI)
@Tag(name = PersonAssetEndpoints.ASSET_URI, description = "PersonAssets")
public interface PersonAssetEndpoints {

    String ASSET_URI = "/web/persons/{personId}/assets/";

    @GET
    @Path("/{assetId}/download")
    @Operation(summary = "Download asset by personId and assetId")
    Response downloadAssetById(@AccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.", required = true)
        @PathParam("personId") String personId,
        @Parameter(description = "The Extole unique profile identifier of this asset at Extole.", required = true)
        @PathParam("assetId") String assetId,
        @QueryParam("default_url") String defaultUrl)
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException;

    @GET
    @Path("/download")
    @Operation(summary = "Download asset by personId and name")
    Response downloadAssetByName(@AccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.", required = true)
        @PathParam("personId") String personId,
        @QueryParam("name") String name,
        @QueryParam("default_url") String defaultUrl)
        throws AuthorizationRestException, PersonRestException, PersonAssetRestException;
}
