package com.extole.client.rest.reward.supplier;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;

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
import com.extole.client.rest.reward.supplier.built.BuiltRewardSupplierResponse;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/reward-suppliers")
public interface RewardSupplierEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    <T extends RewardSupplierResponse> List<T> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived,
        @QueryParam("types") List<RewardSupplierType> types, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}")
    <T extends RewardSupplierResponse> T get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    <T extends BuiltRewardSupplierResponse> List<T> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived,
        @QueryParam("include_disabled") Boolean includeDisabled,
        @QueryParam("types") List<RewardSupplierType> types,
        @QueryParam("display_types") List<String> displayTypes,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_supplier_id}/built")
    <T extends BuiltRewardSupplierResponse> T getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/display-types")
    Set<String> getDisplayTypes(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    <T extends RewardSupplierResponse> T create(@UserAccessTokenParam String accessToken,
        RewardSupplierCreateRequest creationRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        RewardSupplierCreationRestException, CustomRewardSupplierRestException, RewardSupplierRestException,
        CampaignComponentValidationRestException, TangoRewardSupplierCreationRestException,
        SalesforceCouponRewardSupplierCreateRestException, TangoRewardSupplierValidationRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        TangoConnectionRestException, SalesforceConnectionRestException, PayPalPayoutsRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{reward_supplier_id}")
    <T extends RewardSupplierResponse> T update(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        RewardSupplierUpdateRequest updateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        CustomRewardSupplierRestException, CampaignComponentValidationRestException,
        RewardSupplierCreationRestException, TangoRewardSupplierCreationRestException,
        SalesforceCouponRewardSupplierCreateRestException, TangoRewardSupplierValidationRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        TangoConnectionRestException, SalesforceConnectionRestException, PayPalPayoutsRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{reward_supplier_id}")
    <T extends RewardSupplierResponse> T archive(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{reward_supplier_id}/delete")
    <T extends RewardSupplierResponse> T delete(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_supplier_id}/unarchive")
    <T extends RewardSupplierResponse> T unArchive(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException;

}
