package com.extole.client.rest.webhook.reward.filter.supplier;

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

import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.client.rest.webhook.reward.filter.supplier.built.BuiltSupplierRewardWebhookFilterResponse;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/webhooks/reward/{webhook_id}/filters/supplier")
public interface SupplierRewardWebhookFilterEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<SupplierRewardWebhookFilterResponse> listSupplierRewardWebhookFilters(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltSupplierRewardWebhookFilterResponse> listBuiltSupplierRewardWebhookFilters(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{filter_id}")
    SupplierRewardWebhookFilterResponse getSupplierRewardWebhookFilter(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, BuildWebhookRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{filter_id}/built")
    BuiltSupplierRewardWebhookFilterResponse getBuiltSupplierRewardWebhookFilter(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, BuildWebhookRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    SupplierRewardWebhookFilterResponse createSupplierRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, SupplierRewardWebhookFilterCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardWebhookRestException,
        BuildWebhookRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{filter_id}")
    SupplierRewardWebhookFilterResponse updateSupplierRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        SupplierRewardWebhookFilterUpdateRequest updateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardWebhookRestException,
        BuildWebhookRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{filter_id}")
    SupplierRewardWebhookFilterResponse archiveSupplierRewardWebhookFilter(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @PathParam("filter_id") String filterId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, BuildWebhookRestException;
}
