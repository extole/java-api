package com.extole.client.rest.campaign.migration;

import java.time.ZoneId;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;

import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Hidden
@Path("/v2/campaigns")
public interface CampaignMigrationEndpoints {

    @Hidden
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}/migrate-creative-v7-to-v8")
    MigrationResponse migrateCreativesV7ToV8(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @QueryParam("root_component_source") Optional<VariableSource> rootComponentSource,
        Optional<MigrationRequest> request,
        @QueryParam("dry_run") boolean dryRun,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignMigrationRestException, CampaignRestException;

}
