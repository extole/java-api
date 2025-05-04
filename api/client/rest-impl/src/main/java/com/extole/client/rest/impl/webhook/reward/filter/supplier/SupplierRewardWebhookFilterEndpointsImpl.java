package com.extole.client.rest.impl.webhook.reward.filter.supplier;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.client.rest.webhook.reward.filter.supplier.SupplierRewardWebhookFilterCreateRequest;
import com.extole.client.rest.webhook.reward.filter.supplier.SupplierRewardWebhookFilterEndpoints;
import com.extole.client.rest.webhook.reward.filter.supplier.SupplierRewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.supplier.SupplierRewardWebhookFilterUpdateRequest;
import com.extole.client.rest.webhook.reward.filter.supplier.built.BuiltSupplierRewardWebhookFilterResponse;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.webhook.built.reward.filter.supplier.BuiltSupplierRewardWebhookFilter;
import com.extole.model.entity.webhook.reward.filter.supplier.SupplierRewardWebhookFilter;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookControllerActionException;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookUserSubscriptionChannelException;
import com.extole.model.service.webhook.WebhookNotFoundException;
import com.extole.model.service.webhook.built.BuildWebhookException;
import com.extole.model.service.webhook.built.RewardSupplierWebhookFilterMissingRewardSupplierIdException;
import com.extole.model.service.webhook.built.RewardSupplierWebhookFilterNotFoundException;
import com.extole.model.service.webhook.reward.RewardWebhookNotFoundException;
import com.extole.model.service.webhook.reward.RewardWebhookService;
import com.extole.model.service.webhook.reward.filter.RewardWebhookFilterBuilderType;
import com.extole.model.service.webhook.reward.filter.supplier.RewardSupplierWebhookStateFilterNotFoundException;
import com.extole.model.service.webhook.reward.filter.supplier.SupplierRewardWebhookFilterBuilder;
import com.extole.model.service.webhook.reward.filter.supplier.SupplierRewardWebhookFilterQueryBuilder;
import com.extole.model.service.webhook.reward.filter.supplier.SupplierRewardWebhookFilterService;
import com.extole.model.service.webhook.reward.filter.supplier.built.BuiltSupplierRewardWebhookFilterQueryBuilder;
import com.extole.model.service.webhook.reward.filter.supplier.built.BuiltSupplierRewardWebhookFilterService;

@Provider
public class SupplierRewardWebhookFilterEndpointsImpl implements SupplierRewardWebhookFilterEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardWebhookService rewardWebhookService;
    private final ComponentService componentService;
    private final SupplierRewardWebhookFilterService supplierRewardWebhookFilterService;
    private final BuiltSupplierRewardWebhookFilterService builtSupplierRewardWebhookFilterService;
    private final SupplierRewardWebhookFilterRestMapper supplierRewardWebhookFilterRestMapper;

    @Autowired
    public SupplierRewardWebhookFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        RewardWebhookService rewardWebhookService,
        ComponentService componentService,
        SupplierRewardWebhookFilterService supplierRewardWebhookFilterService,
        BuiltSupplierRewardWebhookFilterService builtSupplierRewardWebhookFilterService,
        SupplierRewardWebhookFilterRestMapper supplierRewardWebhookFilterRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.rewardWebhookService = rewardWebhookService;
        this.componentService = componentService;
        this.supplierRewardWebhookFilterService = supplierRewardWebhookFilterService;
        this.builtSupplierRewardWebhookFilterService = builtSupplierRewardWebhookFilterService;
        this.supplierRewardWebhookFilterRestMapper = supplierRewardWebhookFilterRestMapper;
    }

    @Override
    public List<SupplierRewardWebhookFilterResponse> listSupplierRewardWebhookFilters(String accessToken,
        String webhookId, ZoneId timeZone) throws UserAuthorizationRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            SupplierRewardWebhookFilterQueryBuilder supplierRewardWebhookFilterQueryBuilder =
                supplierRewardWebhookFilterService.createRewardWebhookSupplierFilterQueryBuilder(authorization,
                    Id.valueOf(webhookId));

            return supplierRewardWebhookFilterQueryBuilder.list().stream()
                .map(rewardWebhookFilter -> supplierRewardWebhookFilterRestMapper
                    .toRewardWebhookSupplierFilterResponse(rewardWebhookFilter,
                        timeZone))
                .collect(Collectors.toList());
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<BuiltSupplierRewardWebhookFilterResponse> listBuiltSupplierRewardWebhookFilters(String accessToken,
        String webhookId, ZoneId timeZone) throws UserAuthorizationRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            BuiltSupplierRewardWebhookFilterQueryBuilder supplierRewardWebhookFilterQueryBuilder =
                builtSupplierRewardWebhookFilterService
                    .createBuiltRewardWebhookSupplierFilterQueryBuilder(authorization, Id.valueOf(webhookId));

            return supplierRewardWebhookFilterQueryBuilder.list().stream()
                .map(rewardWebhookFilter -> supplierRewardWebhookFilterRestMapper
                    .toBuiltRewardWebhookSupplierFilterResponse(rewardWebhookFilter,
                        timeZone))
                .collect(Collectors.toList());
        } catch (WebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SupplierRewardWebhookFilterResponse getSupplierRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException,
        BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            SupplierRewardWebhookFilter supplierRewardWebhookFilter =
                supplierRewardWebhookFilterService.getRewardWebhookSupplierFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));
            return supplierRewardWebhookFilterRestMapper.toRewardWebhookSupplierFilterResponse(
                supplierRewardWebhookFilter,
                timeZone);

        } catch (RewardSupplierWebhookStateFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltSupplierRewardWebhookFilterResponse getBuiltSupplierRewardWebhookFilter(String accessToken,
        String webhookId,
        String filterId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            BuiltSupplierRewardWebhookFilter supplierRewardWebhookFilter =
                builtSupplierRewardWebhookFilterService.getBuiltRewardWebhookSupplierFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));
            return supplierRewardWebhookFilterRestMapper.toBuiltRewardWebhookSupplierFilterResponse(
                supplierRewardWebhookFilter,
                timeZone);

        } catch (RewardSupplierWebhookStateFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
        } catch (WebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SupplierRewardWebhookFilterResponse createSupplierRewardWebhookFilter(String accessToken, String webhookId,
        SupplierRewardWebhookFilterCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, RewardSupplierRestException,
        BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            SupplierRewardWebhookFilter savedFilter =
                rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                    .addFilter(RewardWebhookFilterBuilderType.SUPPLIER)
                    .withRewardSupplierId(createRequest.getRewardSupplierId())
                    .save();

            return supplierRewardWebhookFilterRestMapper.toRewardWebhookSupplierFilterResponse(
                savedFilter, timeZone);

        } catch (RewardSupplierWebhookFilterMissingRewardSupplierIdException e) {
            throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_IS_MISSING)
                .addParameter("webhook_id", webhookId)
                .build();
        } catch (RewardSupplierWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", createRequest.getRewardSupplierId())
                .withCause(e)
                .build();
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_BUILD_FAILED)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        }
    }

    @Override
    public SupplierRewardWebhookFilterResponse updateSupplierRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, SupplierRewardWebhookFilterUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, RewardSupplierRestException,
        BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {

            SupplierRewardWebhookFilter webhookSupplierFilter =
                supplierRewardWebhookFilterService.getRewardWebhookSupplierFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));

            SupplierRewardWebhookFilterBuilder<SupplierRewardWebhookFilter> updateBuilder =
                rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                    .updateFilter(webhookSupplierFilter);

            if (updateRequest.getRewardSupplierId() != null) {
                updateBuilder.withRewardSupplierId(updateRequest.getRewardSupplierId());
            }

            return supplierRewardWebhookFilterRestMapper.toRewardWebhookSupplierFilterResponse(
                updateBuilder.save(), timeZone);

        } catch (RewardSupplierWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", updateRequest.getRewardSupplierId())
                .withCause(e)
                .build();
        } catch (RewardSupplierWebhookStateFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.WEBHOOK_BUILD_FAILED)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        }
    }

    @Override
    public SupplierRewardWebhookFilterResponse archiveSupplierRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, BuildWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            SupplierRewardWebhookFilter webhookSupplierFilter =
                supplierRewardWebhookFilterService.getRewardWebhookSupplierFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));

            rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                .removeFilter(webhookSupplierFilter)
                .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));

            return supplierRewardWebhookFilterRestMapper
                .toRewardWebhookSupplierFilterResponse(webhookSupplierFilter, timeZone);
        } catch (RewardSupplierWebhookStateFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BuildWebhookRestException.class)
                .withErrorCode(BuildWebhookRestException.REWARD_SUPPLIER_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException | BuildWebhookException
            | WebhookAssociatedWithWebhookControllerActionException
            | WebhookAssociatedWithWebhookUserSubscriptionChannelException | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
