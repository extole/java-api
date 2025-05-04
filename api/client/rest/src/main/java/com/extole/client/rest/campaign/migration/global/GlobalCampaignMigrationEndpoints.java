package com.extole.client.rest.campaign.migration.global;

import java.time.ZoneId;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;

import com.extole.api.campaign.Campaign;
import com.extole.client.rest.campaign.migration.GlobalCampaignMigrationRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.id.Id;

@Hidden
@Path("/v2/campaigns/global")
public interface GlobalCampaignMigrationEndpoints {

    @Hidden
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pull-client-variables")
    GlobalCampaignMigrationResponse pullClientVariables(
        @UserAccessTokenParam String accessToken,
        @QueryParam("source_campaign_id") Optional<Id<Campaign>> sourceCampaignId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, GlobalCampaignMigrationRestException;

}
