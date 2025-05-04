package com.extole.webhook.dispatcher.rest.dispatch.result;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.webhook.dispatcher.rest.WebhookRestException;

@Path("/v6/webhooks")
@Tag(name = "/v6/webhooks", description = "WebhookDispatchResult")
public interface WebhookDispatchResultEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{webhook_id}/dispatch-results/recent")
    @Operation(summary = "View recent webhook dispatch results",
        description = "Retrieves webhook dispatch results - most recent first")
    List<WebhookDispatchResultResponse> getWebhookDispatchResults(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @Nullable @QueryParam("limit") Integer limit,
        @Nullable @QueryParam("offset") Integer offset, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException;

}
