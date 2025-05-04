package com.extole.client.rest.person.v4;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonQueryRestException;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.PersonStepsListRestException;
import com.extole.client.rest.shareable.ShareableRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/persons")
@Tag(name = "/v4/persons", description = "Person")
public interface PersonV4Endpoints {

    @Deprecated // TODO Use /v5/persons instead. ENG-23822
    @GET
    @Path("/{person_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets details for a person",
        description = "Deprecated - use /v5/{person_id} instead.")
    PersonV4Response getPerson(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("person_id") String personId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @Deprecated // TODO Use /v5/persons/X/steps instead. ENG-23822
    @GET
    @Path("/{person_id}/steps")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with steps",
        description = "Deprecated - use /v5/{person_id}/steps instead. " +
            "Returns steps for a person, sorted by event date in descending order.")
    List<PersonStepV4Response> getSteps(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Nullable @BeanParam PersonStepsV4ListRequest personStepsListRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonStepsListRestException, PersonRestException;

    @GET
    @Path("/{person_id}/shares")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with shares",
        description = "Returns shares for a person, sorted by share date in descending order.")
    List<PersonShareV4Response> getShares(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Optional campaign id filter.")
        @Nullable @QueryParam("campaign_id") String campaignId,
        @Parameter(description = "Optional partner share id filter.")
        @Nullable @QueryParam("partner_share_id") String partnerShareId,
        @Parameter(description = "Optional partner id filter, using this format: <name>:<value>")
        @Nullable @QueryParam("partner_id") String partnerId,
        @Parameter(description = "Optional offset filter, defaults to 0.")
        @Nullable @QueryParam("offset") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @Nullable @QueryParam("limit") Integer limit,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/audience-memberships")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with audience memberships",
        description = "Returns audience memberships for a person, sorted by updated date in descending order.")
    List<PersonAudienceMembershipV4Response> getAudienceMemberships(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Optional name filter.")
        @QueryParam("name") Optional<String> name,
        @Parameter(description = "Optional offset filter, defaults to 0.")
        @QueryParam("offset") Optional<Integer> offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @QueryParam("limit") Optional<Integer> limit,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/shareables")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with shareables",
        description = "Returns shareables for a person, sorted by updated date in descending order.")
    List<PersonShareableV4Response> getShareables(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Optional offset filter, defaults to 0.")
        @Nullable @QueryParam("offset") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @Nullable @QueryParam("limit") Integer limit)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/shareables/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a shareable",
        description = "Returns a shareable by a given code")
    PersonShareableV4Response getShareableByCode(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Shareable code")
        @PathParam("code") String code)
        throws UserAuthorizationRestException, PersonRestException, ShareableRestException;

    // TODO Rename type to journey_name - ENG-18547
    @GET
    @Path("/{person_id}/journeys")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with journeys",
        description = "Returns journeys for a person, sorted by updated date in descending order.")
    List<PersonJourneyV4Response> getJourneys(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Optional campaign id filter.")
        @Nullable @QueryParam("campaign_id") String campaignId,
        @Parameter(description = "Optional program label filter.")
        @Nullable @QueryParam("program_label") String programLabel,
        @Parameter(description = "Optional container filter, defaults to production container. " +
            "Pass \"*\" to include steps for all containers")
            @Nullable @QueryParam("container") String container,
        @Parameter(description = "Optional journey type filter, one of friend or advocate.")
        @Nullable @QueryParam("type") String journeyName,
        @Parameter(description = "Optional offset filter, defaults to 0.")
        @Nullable @QueryParam("offset") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @Nullable @QueryParam("limit") Integer limit,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/rewards")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with rewards",
        description = "Returns rewards for a person, sorted by created date in descending order.")
    List<PersonRewardV4Response> getRewards(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Optional program label filter.")
        @Nullable @QueryParam("program_label") String programLabel,
        @Parameter(description = "Optional campaign id filter.")
        @Nullable @QueryParam("campaign_id") String campaignId,
        @Parameter(description = "Optional reward states list filter separated by comma, one of earned, fulfilled, " +
            "sent, redeemed, failed, canceled, revoked.")
        @Nullable @QueryParam("reward_state") String rewardStates,
        @Parameter(description = "Optional reward types list filter separated by comma, one of manual_coupon, " +
            "salesforce_coupon, tango_v2, custom_reward, paypal_payouts.")
        @Nullable @QueryParam("reward_type") String rewardTypes,
        @Parameter(description = "Optional offset filter, defaults to 0.")
        @Nullable @QueryParam("offset") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @Nullable @QueryParam("limit") Integer limit,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonQueryRestException;

    @GET
    @Path("/{person_id}/relationships")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with relationships",
        description = "Returns relationships for a person, sorted by updated date in descending order.")
    List<PersonRelationshipV4Response> getRelationships(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Nullable @BeanParam PersonRelationshipsV4ListRequest relationshipsListRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonRelationshipV4RestException;

    @GET
    @Path("/{person_id}/data")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with profile data",
        description = "Returns data for a person, sorted by updated date in descending order.")
    List<PersonDataV4Response> getData(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Optional filter for data name")
        @Nullable @QueryParam("name") String name,
        @Parameter(description = "Optional filter for data scope, one of PUBLIC, PRIVATE")
        @Nullable @QueryParam("scope") PersonDataScope scope,
        @Parameter(description = "Optional offset filter, defaults to 0.")
        @Nullable @QueryParam("offset") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @Nullable @QueryParam("limit") Integer limit)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/request-contexts")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with request contexts", description = "Returns request contexts for a person")
    List<PersonRequestContextV4Response> getRequestContexts(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Nullable @QueryParam("offset") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @Nullable @QueryParam("limit") Integer limit,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException;

}
