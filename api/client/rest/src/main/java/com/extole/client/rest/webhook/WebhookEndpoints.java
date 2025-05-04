package com.extole.client.rest.webhook;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.webhook.built.BuiltWebhookResponse;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/webhooks")
@Tag(name = "/v6/webhooks", description = "Webhook")
public interface WebhookEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    @Operation(summary = "List webhooks")
    <T extends WebhookResponse> List<T> listWebhooks(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @QueryParam("enabled") Boolean enabled,
        @Nullable @QueryParam("type") WebhookType type,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildWebhookRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{webhook_id}")
    @Operation(summary = "Get webhook")
    <T extends WebhookResponse> T getWebhook(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/built")
    @Operation(summary = "List webhooks")
    List<BuiltWebhookResponse> listBuiltWebhooks(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @QueryParam("enabled") Boolean enabled,
        @Nullable @QueryParam("type") WebhookType type,
        @Nullable @QueryParam("name") String name,
        @Nullable @QueryParam("include_archived") Boolean includeArchived,
        @Nullable @QueryParam("limit") Integer limit,
        @Nullable @QueryParam("offset") Integer offset,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{webhook_id}/built")
    @Operation(summary = "Get webhook")
    BuiltWebhookResponse getBuiltWebhook(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("webhook_id") String webhookId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, BuildWebhookRestException;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Create webhook")
    <T extends WebhookResponse> T createWebhook(
        @UserAccessTokenParam String accessToken,
        @RequestBody(description = "WebhookCreateRequest object",
            required = true) WebhookCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException, BuildWebhookRestException;

    @PUT
    @Path("/{webhook_id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Update webhook")
    <T extends WebhookResponse> T updateWebhook(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The id of the webhook to be updated.",
            required = true) @PathParam("webhook_id") String webhookId,
        @RequestBody(description = "WebhookUpdateRequest object",
            required = true) WebhookUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, OmissibleRestException,
        CampaignComponentValidationRestException, BuildWebhookRestException;

    @DELETE
    @Path("/{webhook_id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Delete webhook")
    <T extends WebhookResponse> T archiveWebhook(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The id of the webhook to be deleted.",
            required = true) @PathParam("webhook_id") String webhookId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, WebhookArchiveRestException;

}
