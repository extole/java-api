package com.extole.webhook.dispatcher.rest.dispatch;

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
import com.extole.webhook.dispatcher.rest.dispatch.response.WebhookDispatchResponse;

@Path("/v6/webhooks/local")
@Tag(name = "/v6/webhooks/local")
public interface WebhookDispatchLocalEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{webhook_id}/dispatches/recent")
    @Operation(summary = "View recent webhook dispatches for the pod",
        description = "Retrieves webhook dispatches - most recent first - from the pod requested. Best used with"
            + " api-X.extole.io rather than api.extole.io. For general purposes please use the non-local endpoint"
            + " api.extole.io/v6/webhooks/{webhook_id}/dispatches/recent")
    <T extends WebhookDispatchResponse> List<T> getLocalWebhookDispatches(@UserAccessTokenParam String accessToken,
        @PathParam("webhook_id") String webhookId, @Nullable @QueryParam("limit") Integer limit,
        @Nullable @QueryParam("offset") Integer offset, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException;

}
