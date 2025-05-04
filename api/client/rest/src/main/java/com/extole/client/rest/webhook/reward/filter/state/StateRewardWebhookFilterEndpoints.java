package com.extole.client.rest.webhook.reward.filter.state;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/webhooks/reward/{webhook_id}/filters/state")
public interface StateRewardWebhookFilterEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    List<StateRewardWebhookFilterResponse> listStateRewardWebhookFilters(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{filter_id}")
    StateRewardWebhookFilterResponse getStateRewardWebhookFilter(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, StateRewardWebhookFilterRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    StateRewardWebhookFilterResponse createStateRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, StateRewardWebhookFilterCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, StateRewardWebhookFilterRestException, RewardWebhookRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @PUT
    @Path("/{filter_id}")
    StateRewardWebhookFilterResponse updateStateRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        StateRewardWebhookFilterUpdateRequest updateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, StateRewardWebhookFilterRestException, RewardWebhookRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @DELETE
    @Path("/{filter_id}")
    StateRewardWebhookFilterResponse archiveStateRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, StateRewardWebhookFilterRestException, RewardWebhookRestException,
        ExternalElementRestException;
}
