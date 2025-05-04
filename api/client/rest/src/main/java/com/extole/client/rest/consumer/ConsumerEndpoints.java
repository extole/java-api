package com.extole.client.rest.consumer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path(ConsumerEndpoints.CONSUMER_ENDPOINTS_PATH)
public interface ConsumerEndpoints {

    String CONSUMER_ENDPOINTS_PATH = "/v2/consumers";

    String CONSUMER_UPGRADE_AUTHORIZATION_PATH = "/{accessToken}/authorize";
    String CONSUMER_CREATE_AUTHORIZATION_PATH = "/{personId}/token";

    // TODO remove these endpoints as a latter part of ENG-21217
    @POST
    @Path(CONSUMER_UPGRADE_AUTHORIZATION_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    ConsumerTokenResponse upgradeAuthorization(@UserAccessTokenParam String accessToken,
        @PathParam("accessToken") String consumerAccessToken)
        throws UserAuthorizationRestException, UpgradeAuthorizationRestException;

    @POST
    @Path(CONSUMER_CREATE_AUTHORIZATION_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    ConsumerTokenResponse createAuthorization(@UserAccessTokenParam String accessToken,
        @PathParam("personId") Long personId)
        throws UserAuthorizationRestException, PersonRestException;

}
