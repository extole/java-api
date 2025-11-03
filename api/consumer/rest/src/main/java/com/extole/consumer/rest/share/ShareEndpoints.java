package com.extole.consumer.rest.share;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Deprecated // TODO use /v6/email/share instead ENG-14783
@Path("/v4/shares")
public interface ShareEndpoints {

    @Path("/{share_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    PublicShareResponse getShare(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("share_id") String shareId)
        throws AuthorizationRestException, ShareRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PublicShareResponse> getShares(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @QueryParam("partner_share_id") String partnerShareId,
        @Parameter(
            description = "A partner id using this format: <name>:<value>") @Nullable @QueryParam("partner_id") String partnerId)
        throws AuthorizationRestException, ShareUnconstrainedRestException;

}
