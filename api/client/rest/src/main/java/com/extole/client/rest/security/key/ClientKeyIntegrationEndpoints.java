package com.extole.client.rest.security.key;

import java.time.ZoneId;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.security.key.integration.IntegrationClientKeyRestException;
import com.extole.client.rest.security.key.integration.IntegrationCreateRequest;
import com.extole.client.rest.security.key.integration.IntegrationType;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/integration")
public interface ClientKeyIntegrationEndpoints {

    @POST
    @Path("/{integrationType}/key")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    <T extends ClientKeyResponse> T create(@UserAccessTokenParam String accessToken,
        @PathParam("integrationType") IntegrationType integrationType, IntegrationCreateRequest createRequest,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException,
        IntegrationClientKeyRestException, BuildClientKeyRestException;

    @DELETE
    @Path("/{integrationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    <T extends ClientKeyResponse> T delete(@UserAccessTokenParam String accessToken,
        @PathParam("integrationType") IntegrationType integrationType,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException,
        IntegrationClientKeyRestException;
}
