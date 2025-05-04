package com.extole.client.rest.person.asset;

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

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path(PersonAssetEndpoints.ASSET_URI)
public interface PersonAssetEndpoints {

    String ASSET_URI = "/v2/persons/{personId}/assets/";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonAssetResponse> listAssets(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("personId") String personId) throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonAssetResponse readAsset(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("personId") String personId,
        @PathParam("assetId") String assetId)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetRestException;

    @GET
    @Path("/{assetId}/download")
    Response downloadAssetById(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("personId") String personId,
        @PathParam("assetId") String assetId)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetRestException;

    @GET
    @Path("/download")
    Response downloadAssetByName(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("personId") String personId,
        @QueryParam("name") String name)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetRestException;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    PersonAssetResponse createAsset(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("personId") String personId,
        @FormDataParam("asset") PersonAssetRequest request,
        @FormDataParam("file") InputStream file,
        @FormDataParam("file") FormDataBodyPart dataBodyPart)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetValidationRestException;

    @DELETE
    @Path("/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonAssetResponse deleteAsset(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("personId") String personId,
        @PathParam("assetId") String assetId)
        throws UserAuthorizationRestException, PersonRestException, PersonAssetRestException;
}
