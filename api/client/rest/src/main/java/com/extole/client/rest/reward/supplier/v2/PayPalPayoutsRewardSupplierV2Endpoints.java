package com.extole.client.rest.reward.supplier.v2;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.PayPalPayoutsRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.v2.built.BuiltPayPalPayoutsRewardSupplierV2Response;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
@Path("/v2/reward-suppliers/paypal-payouts")
public interface PayPalPayoutsRewardSupplierV2Endpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<PayPalPayoutsRewardSupplierV2Response> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}")
    PayPalPayoutsRewardSupplierV2Response get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltPayPalPayoutsRewardSupplierV2Response> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/built")
    BuiltPayPalPayoutsRewardSupplierV2Response getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    PayPalPayoutsRewardSupplierV2Response create(@UserAccessTokenParam String accessToken,
        PayPalPayoutsRewardSupplierCreationV2Request creationRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        RewardSupplierCreationRestException, PayPalPayoutsRewardSupplierRestException, RewardSupplierRestException,
        CampaignComponentValidationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{reward_supplier_id}")
    PayPalPayoutsRewardSupplierV2Response update(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        PayPalPayoutsRewardSupplierUpdateV2Request updateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        PayPalPayoutsRewardSupplierRestException, CampaignComponentValidationRestException,
        RewardSupplierCreationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{reward_supplier_id}")
    PayPalPayoutsRewardSupplierV2Response archive(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException;

}
