package com.extole.client.rest.webhook.reward.filter.tags;

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
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/webhooks/reward/{webhook_id}/filters/tags")
public interface TagsRewardWebhookFilterEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<TagsRewardWebhookFilterResponse> listTagsRewardWebhookFilters(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{filter_id}")
    TagsRewardWebhookFilterResponse getTagsRewardWebhookFilter(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, TagsRewardWebhookFilterRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    TagsRewardWebhookFilterResponse createTagsRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, TagsRewardWebhookFilterCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException,
        RewardWebhookRestException, TagsRewardWebhookFilterRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{filter_id}")
    TagsRewardWebhookFilterResponse updateTagsRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        TagsRewardWebhookFilterUpdateRequest updateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, TagsRewardWebhookFilterRestException,
        RewardWebhookRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{filter_id}")
    TagsRewardWebhookFilterResponse archiveTagsRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, TagsRewardWebhookFilterRestException, RewardWebhookRestException;
}
