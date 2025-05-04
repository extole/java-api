package com.extole.consumer.rest.me.asset;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.me.asset.api.AssetRestException;

@Path(MeAssetEndpoints.ASSET_URI)
public interface MeAssetEndpoints {

    String ASSET_URI = "/web/me/assets";

    @GET
    @Path("/{assetId}/download")
    Response downloadAssetById(@AccessTokenParam String accessToken,
        @PathParam("assetId") String assetId,
        @QueryParam("default_url") String defaultUrl)
        throws AuthorizationRestException, AssetRestException;

    @GET
    @Path("/download")
    Response downloadAssetByName(@AccessTokenParam String accessToken,
        @QueryParam("name") String name,
        @QueryParam("default_url") String defaultUrl)
        throws AuthorizationRestException, AssetRestException;
}
