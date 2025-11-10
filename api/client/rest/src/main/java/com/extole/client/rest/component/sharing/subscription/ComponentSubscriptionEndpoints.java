package com.extole.client.rest.component.sharing.subscription;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.component.sharing.subscription.built.BuiltComponentSubscriptionResponse;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/component-subscriptions")
public interface ComponentSubscriptionEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ComponentSubscriptionResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/built")
    @Produces(MediaType.APPLICATION_JSON)
    List<BuiltComponentSubscriptionResponse> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ComponentSubscriptionResponse subscribe(@UserAccessTokenParam String accessToken,
        @RequestBody ComponentSubscriptionCreateRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentSubscriptionRestException,
        CampaignComponentValidationRestException;

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{subscriptionId}")
    ComponentSubscriptionResponse unsubscribe(@UserAccessTokenParam String accessToken,
        @PathParam("subscriptionId") String subscriptionId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentSubscriptionRestException,
        CampaignComponentValidationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/subscribers")
    List<ComponentSubscriberResponse> listSubscribers(@UserAccessTokenParam String accessToken,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

}
