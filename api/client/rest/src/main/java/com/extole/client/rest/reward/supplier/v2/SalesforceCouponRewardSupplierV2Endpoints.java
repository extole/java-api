package com.extole.client.rest.reward.supplier.v2;

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
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierCreateRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.v2.built.BuiltSalesforceCouponRewardSupplierV2Response;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/reward-suppliers/salesforce-coupons")
public interface SalesforceCouponRewardSupplierV2Endpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list-available-coupon-pools")
    @GET
    List<SalesforceCouponPoolResponse> listAvailableCouponPools(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException, SalesforceConnectionRestException, ClientSalesforceSettingsRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<SalesforceCouponRewardSupplierV2Response> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}")
    SalesforceCouponRewardSupplierV2Response get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltSalesforceCouponRewardSupplierV2Response> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, BuildRewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/built")
    BuiltSalesforceCouponRewardSupplierV2Response getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    SalesforceCouponRewardSupplierV2Response create(@UserAccessTokenParam String accessToken,
        SalesforceCouponRewardSupplierCreationV2Request creationRequest,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        RewardSupplierCreationRestException, SalesforceConnectionRestException, ClientSalesforceSettingsRestException,
        SalesforceCouponRewardSupplierValidationRestException, SalesforceCouponRewardSupplierCreateRestException,
        RewardSupplierRestException, CampaignComponentValidationRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{reward_supplier_id}")
    SalesforceCouponRewardSupplierV2Response update(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        SalesforceCouponRewardSupplierUpdateV2Request updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        SalesforceCouponRewardSupplierValidationRestException, CampaignComponentValidationRestException,
        RewardSupplierCreationRestException, SalesforceConnectionRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{reward_supplier_id}")
    SalesforceCouponRewardSupplierV2Response archive(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException,
        SalesforceConnectionRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_supplier_id}/coupons")
    SalesforceCouponRewardSupplierOperationResponse refill(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, ClientSalesforceSettingsRestException,
        SalesforceConnectionRestException, SalesforceCouponRewardSupplierRefillRestException,
        BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{reward_supplier_id}/coupons")
    SalesforceCouponRewardSupplierOperationResponse deleteCoupons(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @QueryParam("operation_id") String salesforceCouponOperationId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierOperationRestException,
        DeleteCouponsByRewardOperationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/stats")
    CouponStatsResponse getCouponStats(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId)
        throws UserAuthorizationRestException, RewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/operations")
    List<SalesforceCouponRewardSupplierOperationResponse> getOperations(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @Nullable @QueryParam("limit") String limit,
        @Nullable @QueryParam("offset") String offset,
        @TimeZoneParam ZoneId timeZone)
        throws RewardSupplierRestException, UserAuthorizationRestException, QueryLimitsRestException;
}
