package com.extole.client.rest.webhook.reward.filter;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/webhooks/reward/{webhook_id}/filters")
public interface RewardWebhookFilterEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    List<RewardWebhookFilterResponse> listRewardWebhookFilters(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @QueryParam("type") Optional<String> type,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, RewardWebhookFilterRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{filter_id}")
    RewardWebhookFilterResponse getRewardWebhookFilter(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, RewardWebhookFilterRestException;
}
