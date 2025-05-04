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
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.v2.built.BuiltTangoRewardSupplierV2Response;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/reward-suppliers/tango")
public interface TangoRewardSupplierV2Endpoints {

    @GET
    @Path("/catalog")
    @Produces(MediaType.APPLICATION_JSON)
    List<TangoBrandResponse> getCatalog(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException, TangoConnectionRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<TangoRewardSupplierV2Response> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TangoRewardSupplierV2Response create(@UserAccessTokenParam String accessToken,
        TangoRewardSupplierCreationV2Request creationRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, TangoConnectionRestException, TangoRewardSupplierCreationRestException,
        BuildRewardSupplierRestException, RewardSupplierRestException, TangoRewardSupplierValidationRestException,
        RewardSupplierCreationRestException, CampaignComponentValidationRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @GET
    @Path("/{reward_supplier_id}")
    @Produces(MediaType.APPLICATION_JSON)
    TangoRewardSupplierV2Response get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @GET
    @Path("/built")
    @Produces(MediaType.APPLICATION_JSON)
    List<BuiltTangoRewardSupplierV2Response> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("include_archived") Boolean includeArchived, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @GET
    @Path("/{reward_supplier_id}/built")
    @Produces(MediaType.APPLICATION_JSON)
    BuiltTangoRewardSupplierV2Response getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @PUT
    @Path("/{reward_supplier_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    TangoRewardSupplierV2Response update(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId,
        TangoRewardSupplierUpdateV2Request updateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        TangoConnectionRestException, TangoRewardSupplierCreationRestException,
        TangoRewardSupplierValidationRestException, CampaignComponentValidationRestException,
        RewardSupplierCreationRestException;

    @Deprecated // TODO use /v6/reward-suppliers instead ENG-20707
    @DELETE
    @Path("/{reward_supplier_id}")
    @Produces(MediaType.APPLICATION_JSON)
    TangoRewardSupplierV2Response archive(@UserAccessTokenParam String accessToken,
        @PathParam("reward_supplier_id") String rewardSupplierId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        BuildRewardSupplierRestException;
}
