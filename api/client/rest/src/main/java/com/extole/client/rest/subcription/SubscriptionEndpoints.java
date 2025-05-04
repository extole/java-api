package com.extole.client.rest.subcription;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v6/subscriptions")
@Tag(name = "/v6/subscriptions", description = "Subscription")
public interface SubscriptionEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets subscriptions for a client")
    List<SubscriptionResponse> listSubscriptions(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @QueryParam("having_any_tags") String havingAnyTags,
        @Nullable @QueryParam("having_all_tags") String havingAllTags) throws UserAuthorizationRestException;

}
