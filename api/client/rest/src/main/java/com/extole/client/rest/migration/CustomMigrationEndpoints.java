package com.extole.client.rest.migration;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v1/custom-migrations")
public interface CustomMigrationEndpoints {

    @POST
    @Path("/{migration_name}/campaign")
    String migrateCampaign(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("migration_name") String migrationName,
        @QueryParam("campaign_id") String campaignId)
        throws UserAuthorizationRestException, CustomMigrationRestException;
}
