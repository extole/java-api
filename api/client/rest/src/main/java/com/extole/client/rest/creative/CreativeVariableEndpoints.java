package com.extole.client.rest.creative;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns")
public interface CreativeVariableEndpoints {

    String VARIABLES_PATH = "/{campaignId}{version:(/version/.+)?}";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(VARIABLES_PATH + "/variables")
    List<CreativeVariableResponse> getCampaignVariables(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @QueryParam("zone_state") Optional<String> zoneState)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(VARIABLES_PATH + "/variables/{variableName}")
    CreativeVariableResponse getCampaignVariable(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("variableName") String variableName)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(VARIABLES_PATH + "/variables/{variableName}")
    CreativeVariableResponse editCampaignVariable(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("variableName") String variableName,
        CreativeVariableRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    @Path(VARIABLES_PATH + "/variables/{variableName}")
    CreativeVariableResponse replaceCampaignImagePost(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("variableName") String variableName,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition contentDisposition,
        @PathParam("version") String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    @Path(VARIABLES_PATH + "/variables/{variableName}")
    CreativeVariableResponse replaceCampaignImagePut(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("variableName") String variableName,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition contentDisposition)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(VARIABLES_PATH + "/creatives/{frontendControllerActionId}/variables")
    List<CreativeVariableResponse> getCreativeVariables(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("frontendControllerActionId") String frontendControllerActionId)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(VARIABLES_PATH + "/creatives/{frontendControllerActionId}/variables/{variableName}")
    CreativeVariableResponse getCreativeVariable(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("frontendControllerActionId") String frontendControllerActionId,
        @PathParam("variableName") String variableName) throws UserAuthorizationRestException, CampaignRestException,
        CreativeVariableRestException, BuildCampaignRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(VARIABLES_PATH + "/creatives/{frontendControllerActionId}/variables/{variableName}")
    CreativeVariableResponse editCreativeVariable(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("frontendControllerActionId") String frontendControllerActionId,
        @PathParam("variableName") String variableName,
        CreativeVariableRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    @Path(VARIABLES_PATH + "/creatives/{frontendControllerActionId}/variables/{variableName}")
    CreativeVariableResponse replaceImagePost(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("frontendControllerActionId") String frontendControllerActionId,
        @PathParam("variableName") String variableName,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition contentDisposition,
        @PathParam("version") String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    @Path(VARIABLES_PATH + "/creatives/{frontendControllerActionId}/variables/{variableName}")
    CreativeVariableResponse replaceImagePut(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("frontendControllerActionId") String frontendControllerActionId,
        @PathParam("variableName") String variableName,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition contentDisposition)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    @Path(VARIABLES_PATH + "/variables/{variableName}/{locale}")
    CreativeVariableResponse replaceCampaignLocaleImagePost(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("variableName") String variableName,
        @PathParam("locale") String locale,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition contentDisposition,
        @PathParam("version") String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    @Path(VARIABLES_PATH + "/variables/{variableName}/{locale}")
    CreativeVariableResponse replaceCampaignLocaleImagePut(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("variableName") String variableName,
        @PathParam("locale") String locale,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition contentDisposition)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    @Path(VARIABLES_PATH + "/creatives/{frontendControllerActionId}/variables/{variableName}/{locale}")
    CreativeVariableResponse replaceLocaleImagePost(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("frontendControllerActionId") String frontendControllerActionId,
        @PathParam("variableName") String variableName,
        @PathParam("locale") String locale,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition contentDisposition,
        @PathParam("version") String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    @Path(VARIABLES_PATH + "/creatives/{frontendControllerActionId}/variables/{variableName}/{locale}")
    CreativeVariableResponse replaceLocaleImagePut(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("frontendControllerActionId") String frontendControllerActionId,
        @PathParam("variableName") String variableName,
        @PathParam("locale") String locale,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition contentDisposition)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException;
}
