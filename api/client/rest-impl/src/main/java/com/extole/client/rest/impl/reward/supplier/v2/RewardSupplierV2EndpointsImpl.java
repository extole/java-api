package com.extole.client.rest.impl.reward.supplier.v2;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.v2.RewardSupplierV2Endpoints;
import com.extole.client.rest.reward.supplier.v2.RewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltRewardSupplierV2Response;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.service.reward.supplier.RewardSupplierQueryBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryBuilder;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryService;

@Provider
public class RewardSupplierV2EndpointsImpl implements RewardSupplierV2Endpoints {
    private final RewardSupplierService rewardSupplierService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final BuiltRewardSupplierQueryService builtRewardSupplierQueryService;

    @Inject
    public RewardSupplierV2EndpointsImpl(RewardSupplierService rewardSupplierService,
        ClientAuthorizationProvider authorizationProvider,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        BuiltRewardSupplierQueryService builtRewardSupplierQueryService) {
        this.authorizationProvider = authorizationProvider;
        this.rewardSupplierService = rewardSupplierService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
        this.builtRewardSupplierQueryService = builtRewardSupplierQueryService;
    }

    @Override
    public List<RewardSupplierV2Response> list(String accessToken, @Nullable String partnerRewardSupplierId,
        @Nullable Boolean includeArchived, ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RewardSupplierQueryBuilder queryBuilder = rewardSupplierService.createQueryBuilder(userAuthorization);
            queryBuilder.withIncludeArchived(Boolean.TRUE.equals(includeArchived));

            if (partnerRewardSupplierId != null) {
                queryBuilder.withPartnerRewardSupplierId(partnerRewardSupplierId);
            }

            return queryBuilder.list().stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toRewardSupplierResponse(rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<BuiltRewardSupplierV2Response> listBuilt(String accessToken, @Nullable String partnerRewardSupplierId,
        @Nullable Boolean includeArchived, @Nullable Boolean includeDisabled,
        @Nullable Integer limit, @Nullable Integer offset,
        @Nullable String rewardSupplierType, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltRewardSupplierQueryBuilder queryBuilder =
                builtRewardSupplierQueryService.createQueryBuilder(userAuthorization);
            if (Boolean.TRUE.equals(includeArchived)) {
                queryBuilder.includeArchived();
            }
            if (Boolean.TRUE.equals(includeDisabled)) {
                queryBuilder.includeDisabled();
            }
            if (limit != null) {
                queryBuilder.withLimit(limit);
            }
            if (offset != null) {
                queryBuilder.withOffset(offset);
            }
            if (rewardSupplierType != null) {
                queryBuilder.withType(
                    com.extole.model.entity.reward.supplier.RewardSupplierType.valueOf(rewardSupplierType));
            }
            if (partnerRewardSupplierId != null) {
                queryBuilder.withPartnerRewardSupplierId(partnerRewardSupplierId);
            }

            return queryBuilder.list().stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toBuiltRewardSupplierResponse(rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }
}
