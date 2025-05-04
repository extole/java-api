package com.extole.client.rest.share;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.client.rest.person.v4.PersonShareV4Response;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/shares")
public interface ClientShareEndpoints {

    @GET
    @Path("/{share_id}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareV4Response getShare(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("share_id") String shareId,
        @Parameter(description = "Time zone to be used when representing dates.")
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientShareRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonShareV4Response> getShares(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @QueryParam("partner_share_id") String partnerShareId,
        @Parameter(description = "A partner id using this format: <name>:<value>")
        @Nullable @QueryParam("partner_id") String partnerId,
        @Parameter(description = "Time zone to be used when representing dates.")
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientShareUnconstrainedRestException;

}
