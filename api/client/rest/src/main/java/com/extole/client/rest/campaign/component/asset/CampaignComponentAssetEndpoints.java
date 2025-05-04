package com.extole.client.rest.campaign.component.asset;

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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.BuiltCampaignComponentAssetResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/campaigns/{campaign_id}{version:(/version/.+)?}/components/{component_id}/assets")
public interface CampaignComponentAssetEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<CampaignComponentAssetResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{asset_id}")
    CampaignComponentAssetResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @PathParam("asset_id") String assetId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException;

    @GET
    @Path("/{asset_id}/content")
    Response getContent(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @PathParam("asset_id") String assetId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException;

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    CampaignComponentAssetResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("component_id") String componentId,
        @FormDataParam("asset") CampaignComponentAssetCreateRequest request,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition fileMetadata,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        OmissibleRestException;

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{asset_id}")
    CampaignComponentAssetResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("component_id") String componentId,
        @PathParam("asset_id") String assetId,
        @FormDataParam("asset") CampaignComponentAssetUpdateRequest request,
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataContentDisposition fileMetadata,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException, CampaignComponentAssetValidationRestException, BuildCampaignRestException,
        CampaignUpdateRestException, OmissibleRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{asset_id}")
    CampaignComponentAssetResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("component_id") String componentId,
        @PathParam("asset_id") String assetId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException, BuildCampaignRestException, CampaignUpdateRestException,
        OmissibleRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltCampaignComponentAssetResponse> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        BuildCampaignRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{asset_id}/built")
    BuiltCampaignComponentAssetResponse getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @PathParam("asset_id") String assetId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentAssetRestException, BuildCampaignRestException;

}
