package com.extole.client.rest.person.v2;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
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

import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.PersonShareRestException;
import com.extole.client.rest.person.RuntimePersonEndpoints;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

/**
 * @deprecated Use {@link RuntimePersonEndpoints} instead
 */
@Deprecated // TODO remove in ENG-13035
@Path("/v2/persons")
@Tag(name = "/v2/persons", description = "PersonsV2")
public interface PersonV2Endpoints {

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
    List<PersonV2Response> get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam PersonGetV2Request request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a Person in the Extole Platform.",
        description = "This operation can be done by an authorized user")
    PersonV2Response create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @RequestBody(description = "PersonRequest object", required = true) PersonV2Request person,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonValidationV2RestException, PersonRestException;

    @PUT
    @Path("/{person_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Updates a person", description = "This operation can be done by an authorized user")
    PersonV2Response update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "Id for person to be updated") @PathParam("person_id") String personId,
        @RequestBody(description = "PersonRequest object", required = true) PersonV2Request person,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonValidationV2RestException;

    @GET
    @Path("/{person_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets Details for a Person")
    PersonV2Response getPerson(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("person_id") String personId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{first_person_id}/is-same")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Check if is the same person", description = "Verify if two IDs refer to the same person")
    IsSamePersonV2Response isSamePerson(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("first_person_id") String firstPersonId,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @QueryParam("person_id") String secondPersonId)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{person_id}/advocates")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with advocates", description = "Returns advocates for a person")
    List<RelationshipV2Response> getAssociatedAdvocates(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/friends")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with friends", description = "Returns friends of a person")
    List<RelationshipV2Response> getAssociatedFriends(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/referrals-to-person")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with referrals", description = "Returns a list with referrals to a person")
    List<RelationshipV2Response> getReferralsToPerson(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "personId to get referrals") @PathParam("person_id") String personId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/referrals-from-person")
    @Produces(MediaType.APPLICATION_JSON)
    List<RelationshipV2Response> getReferralsFromPerson(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/shares/{share_id}")
    @Produces(MediaType.APPLICATION_JSON)
    ShareV2Response getShare(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("share_id") String shareId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareRestException;

    @GET
    @Path("/{person_id}/shares")
    @Produces(MediaType.APPLICATION_JSON)
    List<ShareV2Response> getShares(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @Nullable @QueryParam("partner_share_id") String partnerShareId,
        @Parameter(
            description = "A partner id using this format: <name>:<value>") @Nullable @QueryParam("partner_id") String partnerId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{person_id}/rewards")
    @Hidden
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonRewardV2Response> getRewards(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @POST
    @Path("/{person_id}/merge-device/{identity_id}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonV2Response mergeDeviceToIdentity(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId, @PathParam("identity_id") String identityId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException;

    @POST
    @Path("/{person_id}/merge-identity/{identity_id}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonV2Response mergeIdentityToIdentity(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId, @PathParam("identity_id") String identityId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException;
}
