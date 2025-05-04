package com.extole.client.rest.reward.supplier.v2;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
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
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.v2.built.BuiltManualCouponRewardSupplierV2Response;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/reward-suppliers/manual-coupons")
public interface ManualCouponRewardSupplierV2Endpoints {

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<ManualCouponRewardSupplierV2Response> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}")
    ManualCouponRewardSupplierV2Response get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, RewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltManualCouponRewardSupplierV2Response> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/built")
    BuiltManualCouponRewardSupplierV2Response getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    ManualCouponRewardSupplierV2Response create(@UserAccessTokenParam String accessToken,
        ManualCouponRewardSupplierCreationV2Request createRequest,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        RewardSupplierCreationRestException, RewardSupplierRestException, CampaignComponentValidationRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{reward_supplier_id}")
    ManualCouponRewardSupplierV2Response update(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        ManualCouponRewardSupplierUpdateV2Request updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        CampaignComponentValidationRestException, RewardSupplierCreationRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{reward_supplier_id}")
    ManualCouponRewardSupplierV2Response archive(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_supplier_id}/coupons")
    ManualCouponRewardSupplierOperationResponse uploadCoupons(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        ManualCouponRewardSupplierUploadCouponsRequest couponRequest,
        @BeanParam UploadCouponParams uploadCouponParams,
        @TimeZoneParam ZoneId timeZone)

        throws UserAuthorizationRestException, ManualCouponRewardSupplierUploadCouponsRestException,
        RewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    @Path("/{reward_supplier_id}/coupons")
    ManualCouponRewardSupplierOperationResponse uploadCoupons(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDisposition,
        @BeanParam UploadCouponParams uploadCouponParams,
        @QueryParam("default_expiry_date") Optional<ZonedDateTime> defaultExpiryDate,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ManualCouponRewardSupplierUploadCouponsRestException,
        RewardSupplierRestException;

    @GET
    @Path("/{reward_supplier_id}/coupons{extension :\\.csv|\\.txt}")
    Response downloadCoupons(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @PathParam("extension") String extension,
        @Nullable @QueryParam("operation_id") String operationId,
        @Nullable @QueryParam("issued") Boolean issued,
        @Nullable @QueryParam("include_expired") Boolean includeExpired,
        @Nullable @QueryParam("limit") String limit,
        @Nullable @QueryParam("offset") String offset,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierOperationRestException,
        QueryLimitsRestException, BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/coupons")
    ManualCouponRewardSupplierDownloadCouponsResponse downloadCoupons(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @Nullable @QueryParam("operation_id") String operationId,
        @Nullable @QueryParam("issued") Boolean issued,
        @Nullable @QueryParam("include_expired") Boolean includeExpired,
        @Nullable @QueryParam("limit") String limit,
        @Nullable @QueryParam("offset") String offset,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierOperationRestException,
        QueryLimitsRestException;

    @DELETE
    @Path("/{reward_supplier_id}/coupons")
    @Produces(MediaType.APPLICATION_JSON)
    ManualCouponRewardSupplierOperationResponse deleteCoupons(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @QueryParam("operation_id") String operationId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, RewardSupplierRestException,
        RewardSupplierOperationRestException, DeleteCouponsByRewardOperationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/stats")
    CouponStatsResponse getCouponStats(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId)
        throws UserAuthorizationRestException, RewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/operations")
    List<ManualCouponRewardSupplierOperationResponse> getOperations(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @Nullable @QueryParam("filename") String filename,
        @Nullable @QueryParam("limit") String limit,
        @Nullable @QueryParam("offset") String offset,
        @TimeZoneParam ZoneId timeZone)
        throws RewardSupplierRestException, UserAuthorizationRestException, QueryLimitsRestException;

}
