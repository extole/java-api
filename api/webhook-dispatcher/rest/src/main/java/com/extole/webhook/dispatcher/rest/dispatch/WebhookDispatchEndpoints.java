package com.extole.webhook.dispatcher.rest.dispatch;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.webhook.dispatcher.rest.WebhookRestException;
import com.extole.webhook.dispatcher.rest.dispatch.request.WebhookEventRequest;
import com.extole.webhook.dispatcher.rest.dispatch.response.WebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultResponse;

@Path("/v6/webhooks")
@Tag(name = "/v6/webhooks")
public interface WebhookDispatchEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{webhook_id}/dispatches/recent")
    @Operation(summary = "View recent webhook dispatches",
        description = "Retrieves webhook dispatches - most recent first")
    List<WebhookDispatchResponse> getWebhookDispatches(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @Nullable @QueryParam("limit") Integer limit,
        @Nullable @QueryParam("offset") Integer offset, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    @Path("/events/send")
    @Operation(summary = "Send webhook event",
        description = "It is expected this will be used to recover events lost by a 3rd party. "
            + "Events would be by provided by a webhook events report, and filtered prior to submitting.")
    WebhookDispatchResponse sendWebhookEvent(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        WebhookEventRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, WebhookDispatchRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    @Path("/events/sync/send")
    @Operation(summary = "Send webhook event synchronically")
    WebhookDispatchResultResponse sendSyncWebhookEvent(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        WebhookEventRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, WebhookDispatchRestException;

}
