package com.extole.client.rest.subcription;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.subcription.channel.SlackUserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.ThirdPartyEmailUserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.WebhookUserSubscriptionChannelValidationRestException;
import com.extole.client.rest.user.UserRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.expression.EvaluatableRestException;
import com.extole.common.rest.model.SuccessResponse;

@Path("/v6/users/{user_id}/subscriptions")
@Tag(name = "/v6/users/{user_id}/subscriptions", description = "UserSubscription")
public interface UserSubscriptionEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets subscriptions for a user")
    List<UserSubscriptionResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The unique identifier of this user at Extole.",
            required = true) @PathParam("user_id") String userId)
        throws UserAuthorizationRestException, UserRestException;

    @GET
    @Path("/{subscription_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a particular subscription for a user")
    UserSubscriptionResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The unique identifier of this user at Extole.",
            required = true) @PathParam("user_id") String userId,
        @Parameter(required = true) @PathParam("subscription_id") String subscriptionId)
        throws UserAuthorizationRestException, UserRestException, UserSubscriptionRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "create a subscription for a user")
    UserSubscriptionResponse create(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The unique identifier of this user at Extole.",
            required = true) @PathParam("user_id") String userId,
        UserSubscriptionRequest request)
        throws UserAuthorizationRestException, UserRestException, UserSubscriptionValidationRestException,
        UserSubscriptionCreationRestException, WebhookUserSubscriptionChannelValidationRestException,
        SlackUserSubscriptionChannelValidationRestException, EvaluatableRestException,
        ThirdPartyEmailUserSubscriptionChannelValidationRestException;

    @PUT
    @Path("/{subscription_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "create a subscription for a user")
    UserSubscriptionResponse update(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The unique identifier of this user at Extole.",
            required = true) @PathParam("user_id") String userId,
        @Parameter(required = true) @PathParam("subscription_id") String subscriptionId,
        UserSubscriptionUpdateRequest request)
        throws UserAuthorizationRestException, UserRestException, UserSubscriptionCreationRestException,
        UserSubscriptionValidationRestException, UserSubscriptionRestException,
        WebhookUserSubscriptionChannelValidationRestException, SlackUserSubscriptionChannelValidationRestException,
        EvaluatableRestException, ThirdPartyEmailUserSubscriptionChannelValidationRestException;

    @Path("/{subscription_id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "deletes a particular subscription for a user")
    SuccessResponse delete(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The unique identifier of this user at Extole.",
            required = true) @PathParam("user_id") String userId,
        @Parameter(required = true) @PathParam("subscription_id") String subscriptionId)
        throws UserAuthorizationRestException, UserRestException, UserSubscriptionRestException;
}
