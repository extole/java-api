package com.extole.consumer.rest.me;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.person.PersonProfileUpdateRequest;
import com.extole.consumer.rest.person.PersonRestException;
import com.extole.consumer.rest.share.PublicShareResponse;

@Path("/v4/me")
@Tag(name = "/v4/me", description = "Me")
public interface MeEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Will return profile information based on the access token used.")
    MyProfileResponse getMyProfile(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    SuccessResponse updateMyProfile(@AccessTokenParam(readCookie = false) String accessToken,
        PersonProfileUpdateRequest request)
        throws PersonRestException, AuthorizationRestException;

    @GET
    @Path("/capabilities")
    @Produces(MediaType.APPLICATION_JSON)
    MeCapabilityResponse getMyCapabilities(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @Deprecated // TODO remove and support generalized authentication leveraging scopes/access token ENG-9724
    @POST
    @Path("/send-verification-email")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @DefaultApplicationJSON
    VerificationEmailResponse sendVerificationEmail(@AccessTokenParam(readCookie = false) String accessToken,
        Optional<VerificationEmailRequest> request) throws AuthorizationRestException;

    @Deprecated // TODO remove and support generalized authentication leveraging scopes/access token ENG-9724
    @GET
    @Path("/send-verification-email/status/{pollingId}")
    @Produces(MediaType.APPLICATION_JSON)
    VerificationEmailResponse verificationEmailStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("pollingId") String pollingId) throws AuthorizationRestException;

    @POST
    @Path("/friends/{friendEmail}/remind")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    FriendReminderEmailResponse sendFriendReminderEmail(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("friendEmail") String friendEmail) throws AuthorizationRestException;

    @GET
    @Path("/friends/remind/status/{pollingId}")
    @Produces(MediaType.APPLICATION_JSON)
    FriendReminderEmailResponse friendReminderEmailStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("pollingId") String pollingId) throws AuthorizationRestException;

    @GET
    @Path("/shares")
    @Produces(MediaType.APPLICATION_JSON)
    List<ShareResponse> getShares(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @QueryParam("partner_share_id") String partnerShareId,
        @Parameter(
            description = "A partner id using this format: <name>:<value>") @Nullable @QueryParam("partner_id") String partnerId)
        throws AuthorizationRestException;

    @GET
    @Path("/shares/{shareId}")
    @Produces(MediaType.APPLICATION_JSON)
    ShareResponse getShare(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("shareId") String shareId)
        throws AuthorizationRestException, ShareRestException;

    @GET
    @Path("/audience-memberships")
    @Produces(MediaType.APPLICATION_JSON)
    List<AudienceMembershipResponse> getAudienceMemberships(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws AuthorizationRestException;

    @Deprecated // TODO decide on name for relationships ENG-10590
    @GET
    @Path("/friends")
    @Produces(MediaType.APPLICATION_JSON)
    List<FriendProfileResponse> getFriends(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @Deprecated // TODO decide on name for relationships ENG-10590
    @GET
    @Path("/advocates")
    @Produces(MediaType.APPLICATION_JSON)
    List<RelationshipResponse> getAdvocateRelationships(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @GET
    @Path("/associated-advocates")
    @Produces(MediaType.APPLICATION_JSON)
    List<RelationshipResponse> getAssociatedAdvocates(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @GET
    @Path("/associated-friends")
    @Produces(MediaType.APPLICATION_JSON)
    List<RelationshipResponse> getAssociatedFriends(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @GET
    @Path("/referrals-to-me")
    @Produces(MediaType.APPLICATION_JSON)
    List<RelationshipResponse> getReferralsToMe(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @GET
    @Path("/referrals-from-me")
    @Produces(MediaType.APPLICATION_JSON)
    List<RelationshipResponse> getReferralsFromMe(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/steps")
    List<StepResponse> getSteps(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @QueryParam("campaign_id") String campaignId,
        @Nullable @QueryParam("program_label") String programLabel,
        @Nullable @QueryParam("step_name") String stepName,
        @Nullable @QueryParam("quality") StepQuality quality,
        @Nullable @BeanParam PartnerEventIdRequest partnerEventId)
        throws AuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/public-steps")
    List<PublicPersonStepResponse> getPublicPersonSteps(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @QueryParam("campaign_id") String campaignId,
        @Nullable @QueryParam("program_label") String programLabel,
        @Nullable @QueryParam("step_name") String stepName)
        throws AuthorizationRestException;

    @GET
    @Path("/shares-all")
    @Produces(MediaType.APPLICATION_JSON)
    List<PublicShareResponse> getAllShares(@AccessTokenParam String accessToken) throws AuthorizationRestException;

}
