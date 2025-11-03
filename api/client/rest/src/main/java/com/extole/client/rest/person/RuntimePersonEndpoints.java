package com.extole.client.rest.person;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.person.v4.PersonDataV4Response;
import com.extole.client.rest.person.v4.PersonGetV4Request;
import com.extole.client.rest.person.v4.PersonRelationshipV4Response;
import com.extole.client.rest.person.v4.PersonRelationshipV4RestException;
import com.extole.client.rest.person.v4.PersonRequestContextV4Response;
import com.extole.client.rest.person.v4.PersonRewardV4Response;
import com.extole.client.rest.person.v4.PersonShareV4Response;
import com.extole.client.rest.person.v4.PersonStepV4Response;
import com.extole.client.rest.person.v4.PersonV4Request;
import com.extole.client.rest.person.v4.PersonV4Response;
import com.extole.client.rest.person.v4.PersonValidationV4RestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/runtime-persons")
@Tag(name = "/v4/runtime-persons", description = "RuntimePerson")
public interface RuntimePersonEndpoints {

    /**
     * This endpoint will only ever return 1 profile.
     * If multiple parameters are passed, then the person will be returned for the first parameter that finds a profile.
     * The priority order is: email then partner_user_id
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list of persons",
        description = "This allows searching for a person at Extole using either the email address,\n" +
            "partner user id (partner user id is YOUR unique identifier for this person),\n" +
            "last name or person keys defined by the client (partner_conversion_id, partner_shipment_id etc.).\n" +
            "The result is currently an array as sometimes " +
            "Extole will create multiple profiles for the same person,\n " +
            "such as the instance where the same email address may be tied to multiple partner user ids,\n" +
            "or multiple persons matched the client person keys that were passed in the query.")
    List<PersonV4Response> get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam PersonGetV4Request request, @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a Person in the Extole Platform.",
        description = "This operation can be done by an authorized user")
    PersonV4Response create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @RequestBody(description = "PersonRequest object", required = true) PersonV4Request person,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonValidationV4RestException, PersonRestException;

    @PUT
    @Path("/{person_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Updates a person", description = "This operation can be done by an authorized user")
    PersonV4Response update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "Id for person to be updated") @PathParam("person_id") String personId,
        @RequestBody(description = "PersonRequest object", required = true) PersonV4Request person,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonValidationV4RestException;

    @GET
    @Path("/{person_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets Details for a Person")
    PersonV4Response getPerson(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("person_id") String personId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{first_person_id}/is-same")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Check if is the same person", description = "Verify if two IDs refer to the same person")
    IsSamePersonResponse isSamePerson(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("first_person_id") String firstPersonId,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @QueryParam("person_id") String secondPersonId)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{person_id}/relationships")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with relationships", description = "Returns relationships for a person")
    List<PersonRelationshipV4Response> getRelationships(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @Parameter(
            description = "Optional role of the other person in the relationship, friend or advocate.") @Nullable @QueryParam("role") String role,
        @Parameter(
            description = "Optional flag to exclude relationships with anonymous persons.") @Nullable @QueryParam("exclude_anonymous") Boolean excludeAnonymous,
        @Parameter(description = "Optional flag to return all relationships, not de-duplicate by identity. " +
            "Default behavior (or when flag is set to false) returns last relationship per person.") @DefaultValue("false") @QueryParam("include_duplicate_identities") boolean includeDuplicateIdentities,
        @DefaultValue("false") @QueryParam("include_self_referrals") boolean includeSelfReferrals,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonRelationshipV4RestException;

    @GET
    @Path("/{person_id}/shares/{share_id}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareV4Response getShare(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("share_id") String shareId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareRestException;

    @GET
    @Path("/{person_id}/shares")
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonShareV4Response> getShares(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @Nullable @QueryParam("partner_share_id") String partnerShareId,
        @Nullable @QueryParam("partner_id") String partnerId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/rewards")
    @Hidden
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonRewardV4Response> getRewards(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @Parameter(
            description = "Optional program label filter.") @Nullable @QueryParam("program_label") String programLabel,
        @Parameter(description = "Optional campaign id filter.") @Nullable @QueryParam("campaign_id") String campaignId,
        @Parameter(description = "Optional reward states list filter separated by comma, one of earned, fulfilled, " +
            "sent, redeemed, failed, canceled, revoked.") @Nullable @QueryParam("reward_state") String rewardStates,
        @Parameter(description = "Optional reward types list filter separated by comma, one of manual_coupon, " +
            "salesforce_coupon, tango_v2, custom_reward, paypal_payouts.") @Nullable @QueryParam("reward_type") String rewardTypes,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonQueryRestException;

    @GET
    @Path("/{person_id}/steps")
    @Hidden
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonStepV4Response> getSteps(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @Nullable @QueryParam("container") String container,
        @Nullable @QueryParam("campaign_id") String campaignId,
        @Nullable @QueryParam("program_label") String programLabel,
        @Nullable @QueryParam("stepName") String stepName,
        @Nullable @QueryParam("quality") StepQuality quality,
        @Nullable @QueryParam("partner_id") String partnerId,
        @Nullable @QueryParam("event_id") String eventId,
        @Nullable @QueryParam("cause_event_id") String causeEventId,
        @Nullable @QueryParam("root_event_id") String rootEventId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/data")
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonDataV4Response> getData(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @Nullable @QueryParam("name") String name,
        @Nullable @QueryParam("scope") PersonDataScope scope)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/partner-keys")
    @Produces(MediaType.APPLICATION_JSON)
    Set<String> getPartnerKeys(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{person_id}/request-contexts")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with request contexts", description = "Returns request contexts for a person")
    List<PersonRequestContextV4Response> getRequestContexts(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException;

}
