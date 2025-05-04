package com.extole.client.rest.campaign.summary;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/campaign-summaries")
public interface CampaignSummaryEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CampaignSummaryResponse> getCampaignSummaries(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam CampaignSummaryListQueryParams queryParams, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}")
    CampaignSummaryResponse getCampaignSummary(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException;

}
