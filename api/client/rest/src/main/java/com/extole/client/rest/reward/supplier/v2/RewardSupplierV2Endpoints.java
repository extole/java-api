package com.extole.client.rest.reward.supplier.v2;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.v2.built.BuiltRewardSupplierV2Response;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/reward-suppliers")
public interface RewardSupplierV2Endpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<RewardSupplierV2Response> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("partner_reward_supplier_id") String partnerRewardSupplierId,
        @QueryParam("include_archived") Boolean includeArchived, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltRewardSupplierV2Response> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("partner_reward_supplier_id") String partnerRewardSupplierId,
        @QueryParam("include_archived") Boolean includeArchived,
        @QueryParam("include_disabled") Boolean includeDisabled,
        @QueryParam("limit") @Nullable Integer limit,
        @QueryParam("offset") @Nullable Integer offset,
        @QueryParam("reward_supplier_type") @Nullable String rewardSupplierType,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException;

}
