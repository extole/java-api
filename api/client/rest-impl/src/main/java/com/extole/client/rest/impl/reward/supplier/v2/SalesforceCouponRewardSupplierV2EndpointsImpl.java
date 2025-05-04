package com.extole.client.rest.impl.reward.supplier.v2;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.BuildRewardSupplierExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierCreateRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.v2.CouponStatsResponse;
import com.extole.client.rest.reward.supplier.v2.DeleteCouponsByRewardOperationRestException;
import com.extole.client.rest.reward.supplier.v2.RewardSupplierOperationRestException;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponPoolResponse;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponPoolType;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierCreationV2Request;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierOperationResponse;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierOperationStatus;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierOperationType;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierRefillRestException;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierUpdateV2Request;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierV2Endpoints;
import com.extole.client.rest.reward.supplier.v2.SalesforceCouponRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltSalesforceCouponRewardSupplierV2Response;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.salesforce.ClientSalesforceSettings;
import com.extole.model.entity.reward.supplier.RewardSupplierType;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.entity.reward.supplier.built.salesforce.coupon.BuiltSalesforceCouponRewardSupplier;
import com.extole.model.entity.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplier;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsDisabledException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsNotFoundException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsService;
import com.extole.model.service.reward.supplier.PartnerRewardSupplierIdTooLongException;
import com.extole.model.service.reward.supplier.RewardSupplierInvalidTagException;
import com.extole.model.service.reward.supplier.RewardSupplierIsReferencedException;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierUnsupportedFaceValueAlgorithmTypeException;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryBuilder;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryService;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalCashBackMinMaxLimitsException;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalRateLimitsException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierBalanceRefillAmountMissingException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierCouponPoolIdAlreadyInUseException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierCouponPoolIdNotFoundException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierInvalidCouponPoolIdException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierInvalidInitialOffsetException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierPoolIdMissingException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierService;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponRewardSupplierSettingsIdMissingException;
import com.extole.model.service.reward.supplier.salesforce.coupon.SalesforceCouponValidationException;
import com.extole.rewards.service.coupon.CouponService;
import com.extole.rewards.service.coupon.CouponStats;
import com.extole.rewards.service.operation.salesforce.coupon.SalesforceCouponAlreadyDeletedBySpecifiedOperationException;
import com.extole.rewards.service.operation.salesforce.coupon.SalesforceCouponDeletionByInvalidOperationException;
import com.extole.rewards.service.operation.salesforce.coupon.SalesforceCouponOperation;
import com.extole.rewards.service.operation.salesforce.coupon.SalesforceCouponOperationNotFoundException;
import com.extole.rewards.service.operation.salesforce.coupon.SalesforceCouponOperationQueryBuilder;
import com.extole.rewards.service.operation.salesforce.coupon.SalesforceCouponOperationService;
import com.extole.rewards.service.salesforce.coupon.SalesforceCouponRefillAlreadyInProgressException;
import com.extole.salesforce.api.salesforce.SalesforceAuthenticationException;
import com.extole.salesforce.api.salesforce.SalesforceAuthorizationException;
import com.extole.salesforce.api.salesforce.SalesforceConnectionParameters;
import com.extole.salesforce.api.salesforce.SalesforceCouponApi;
import com.extole.salesforce.api.salesforce.SalesforceCouponPool;
import com.extole.salesforce.api.salesforce.SalesforceCouponPoolIdInvalidUrlPathSegmentException;
import com.extole.salesforce.api.salesforce.SalesforceServiceUnavailableException;

@Provider
public class SalesforceCouponRewardSupplierV2EndpointsImpl implements SalesforceCouponRewardSupplierV2Endpoints {

    private static final int DEFAULT_OPERATIONS_QUERY_LIMIT = 100;
    private static final int DEFAULT_OPERATIONS_QUERY_OFFSET = 0;

    private final SalesforceCouponApi salesforceCouponApi;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientSalesforceSettingsService clientSalesforceSettingsService;
    private final SalesforceCouponRewardSupplierService salesforceCouponRewardSupplierService;
    private final RewardSupplierService rewardSupplierService;
    private final SalesforceCouponOperationService salesforceCouponOperationService;
    private final CouponService couponService;
    private final boolean cashBackEnabled;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final BuiltRewardSupplierQueryService builtRewardSupplierQueryService;
    private final ComponentService componentService;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public SalesforceCouponRewardSupplierV2EndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientSalesforceSettingsService clientSalesforceSettingsService, SalesforceCouponApi salesforceCouponApi,
        SalesforceCouponRewardSupplierService salesforceCouponRewardSupplierService,
        RewardSupplierService rewardSupplierService,
        SalesforceCouponOperationService salesforceCouponOperationService, CouponService couponService,
        @Value("${reward.supplier.salesforce.coupon.cash.back.enabled:true}") boolean cashBackEnabled,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        BuiltRewardSupplierQueryService builtRewardSupplierQueryService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.clientSalesforceSettingsService = clientSalesforceSettingsService;
        this.salesforceCouponApi = salesforceCouponApi;
        this.salesforceCouponRewardSupplierService = salesforceCouponRewardSupplierService;
        this.rewardSupplierService = rewardSupplierService;
        this.salesforceCouponOperationService = salesforceCouponOperationService;
        this.couponService = couponService;
        this.cashBackEnabled = cashBackEnabled;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
        this.builtRewardSupplierQueryService = builtRewardSupplierQueryService;
        this.componentService = componentService;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<SalesforceCouponPoolResponse> listAvailableCouponPools(String accessToken)
        throws UserAuthorizationRestException, SalesforceConnectionRestException,
        ClientSalesforceSettingsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<SalesforceConnectionParameters> salesforceConnectionParameters =
                clientSalesforceSettingsService.listSalesforceConnectionParameters(authorization);

            Map<SalesforceSettings, List<SalesforceCouponPool>> couponPools = new HashMap<>();

            for (SalesforceConnectionParameters connectionParameters : salesforceConnectionParameters) {
                SalesforceSettings settings =
                    new SalesforceSettings(Id.valueOf(connectionParameters.getSettingsId().getValue()),
                        connectionParameters.getSettingsName());

                couponPools.merge(settings, salesforceCouponApi.listCouponPools(connectionParameters),
                    (list1, list2) -> ListUtils.union(list1, list2));
            }

            return couponPools.entrySet().stream()
                .map(entry -> salesforceCouponPoolToResponses(entry.getKey(), entry.getValue()))
                .flatMap(item -> item.stream())
                .collect(Collectors.toList());
        } catch (ClientSalesforceSettingsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsRestException.class)
                .withErrorCode(ClientSalesforceSettingsRestException.SALESFORCE_SETTINGS_NOT_FOUND)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e)
                .build();
        } catch (SalesforceAuthenticationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e)
                .build();
        } catch (SalesforceServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceAuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e)
                .build();
        } catch (ClientSalesforceSettingsDisabledException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SETTINGS_DISABLED)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public List<SalesforceCouponRewardSupplierV2Response> list(String accessToken,
        Boolean includeArchived, ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return salesforceCouponRewardSupplierService.createQueryBuilder(userAuthorization)
                .withIncludeArchived(Boolean.TRUE.equals(includeArchived))
                .list()
                .stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toSalesforceCouponRewardSupplierResponse(
                    rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public SalesforceCouponRewardSupplierV2Response get(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SalesforceCouponRewardSupplier rewardSupplier =
                salesforceCouponRewardSupplierService.get(userAuthorization, Id.valueOf(rewardSupplierId));
            return rewardSupplierRestMapper.toSalesforceCouponRewardSupplierResponse(rewardSupplier, timeZone);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public List<BuiltSalesforceCouponRewardSupplierV2Response> listBuilt(String accessToken,
        Boolean includeArchived, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltRewardSupplierQueryBuilder queryBuilder =
                builtRewardSupplierQueryService.createQueryBuilder(userAuthorization)
                    .withType(RewardSupplierType.SALESFORCE_COUPON);

            if (Boolean.TRUE.equals(includeArchived)) {
                queryBuilder.includeArchived();
            }

            List<BuiltSalesforceCouponRewardSupplier> rewardSuppliers = queryBuilder.list();
            return rewardSuppliers.stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toBuiltSalesforceCouponRewardSupplierResponse(
                    rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public BuiltSalesforceCouponRewardSupplierV2Response getBuilt(String accessToken, String rewardSupplierId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<BuiltRewardSupplier> rewardSupplier = builtRewardSupplierQueryService
                .createQueryBuilder(userAuthorization)
                .withId(Id.valueOf(rewardSupplierId))
                .withType(RewardSupplierType.SALESFORCE_COUPON)
                .list()
                .stream()
                .findFirst();
            if (rewardSupplier.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                    .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                    .addParameter("reward_supplier_id", rewardSupplierId)
                    .build();
            }
            BuiltSalesforceCouponRewardSupplier builtRewardSupplier =
                (BuiltSalesforceCouponRewardSupplier) rewardSupplier.get();
            return rewardSupplierRestMapper.toBuiltSalesforceCouponRewardSupplierResponse(builtRewardSupplier,
                timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public SalesforceCouponRewardSupplierV2Response create(String accessToken,
        SalesforceCouponRewardSupplierCreationV2Request creationRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        RewardSupplierCreationRestException, SalesforceConnectionRestException, ClientSalesforceSettingsRestException,
        SalesforceCouponRewardSupplierValidationRestException, SalesforceCouponRewardSupplierCreateRestException,
        RewardSupplierRestException, CampaignComponentValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return createRewardSupplier(creationRequest, timeZone, authorization);
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", creationRequest.getPartnerRewardSupplierId())
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierValidationRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierValidationRestException.INVALID_BALANCE_REFILL_AMOUNT)
                .addParameter("balance_refill_amount", creationRequest.getBalanceRefillAmount())
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierInvalidInitialOffsetException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.INVALID_INITIAL_OFFSET)
                .addParameter("initial_offset", creationRequest.getInitialOffset())
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierCouponPoolIdAlreadyInUseException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.COUPON_POOL_ID_ALREADY_IN_USE)
                .addParameter("coupon_pool_id", creationRequest.getCouponPoolId())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsRestException.class)
                .withErrorCode(ClientSalesforceSettingsRestException.SALESFORCE_SETTINGS_NOT_FOUND)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e).build();
        } catch (SalesforceAuthenticationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceAuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceCouponPoolIdInvalidUrlPathSegmentException
            | SalesforceCouponRewardSupplierInvalidCouponPoolIdException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.INVALID_COUPON_POOL_ID)
                .addParameter("coupon_pool_id", creationRequest.getCouponPoolId())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsDisabledException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SETTINGS_DISABLED)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierBalanceRefillAmountMissingException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.BALANCE_REFILL_AMOUNT_MISSING)
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierPoolIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.COUPON_POOL_ID_MISSING)
                .withCause(e).build();
        } catch (SalesforceCouponRewardSupplierCouponPoolIdNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.COUPON_POOL_ID_NOT_FOUND)
                .addParameter("coupon_pool_id", creationRequest.getCouponPoolId()).build();
        } catch (SalesforceCouponRewardSupplierSettingsIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.SETTINGS_ID_MISSING).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (RewardSupplierInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e).build();
        } catch (RewardSupplierUnsupportedFaceValueAlgorithmTypeException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE)
                .addParameter("face_value_algorithm_type", e.getFaceValueAlgorithmType())
                .build();
        } catch (SalesforceCouponValidationException e) {
            handleSalesforceCouponValidationException(e, creationRequest, authorization);
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (RewardSupplierValidationException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_VALIDATION_FAILED)
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public SalesforceCouponRewardSupplierV2Response update(String accessToken, String rewardSupplierId,
        SalesforceCouponRewardSupplierUpdateV2Request updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        SalesforceCouponRewardSupplierValidationRestException, CampaignComponentValidationRestException,
        RewardSupplierCreationRestException, SalesforceConnectionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return updateRewardSupplier(rewardSupplierId, updateRequest, timeZone, authorization);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", updateRequest.getPartnerRewardSupplierId())
                .withCause(e)
                .build();
        } catch (SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierValidationRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierValidationRestException.INVALID_BALANCE_REFILL_AMOUNT)
                .addParameter("balance_refill_amount", updateRequest.getBalanceRefillAmount())
                .withCause(e)
                .build();
        } catch (SalesforceCouponRewardSupplierBalanceRefillAmountMissingException
            | SalesforceCouponRewardSupplierPoolIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (RewardSupplierUnsupportedFaceValueAlgorithmTypeException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE)
                .addParameter("face_value_algorithm_type", e.getFaceValueAlgorithmType())
                .build();
        } catch (RewardSupplierInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e).build();
        } catch (SalesforceCouponValidationException e) {
            if (e.getCause() != null) {
                if (e.getCause() instanceof SalesforceServiceUnavailableException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e.getCause()).build();
                }

                if (e.getCause() instanceof SalesforceAuthenticationException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e.getCause()).build();
                }

                if (e.getCause() instanceof SalesforceAuthorizationException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e)
                        .build();
                }
            }
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (SalesforceAuthenticationException | SalesforceServiceUnavailableException
            | SalesforceAuthorizationException | SalesforceCouponRewardSupplierInvalidCouponPoolIdException
            | SalesforceCouponRewardSupplierCouponPoolIdNotFoundException | ClientSalesforceSettingsNotFoundException
            | SalesforceCouponRewardSupplierCouponPoolIdAlreadyInUseException
            | SalesforceCouponRewardSupplierSettingsIdMissingException | ClientSalesforceSettingsDisabledException
            | SalesforceCouponPoolIdInvalidUrlPathSegmentException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (RewardSupplierValidationException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_VALIDATION_FAILED)
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public SalesforceCouponRewardSupplierV2Response archive(String accessToken,
        String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException,
        SalesforceConnectionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SalesforceCouponRewardSupplier rewardSupplier = rewardSupplierService.archive(authorization,
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId)));
            return rewardSupplierRestMapper.toSalesforceCouponRewardSupplierResponse(rewardSupplier, timeZone);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (SalesforceCouponRewardSupplierBalanceRefillAmountMissingException
            | SalesforceCouponRewardSupplierPoolIdMissingException | RewardSupplierIllegalCashBackMinMaxLimitsException
            | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (RewardSupplierUnsupportedFaceValueAlgorithmTypeException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE)
                .addParameter("face_value_algorithm_type", e.getFaceValueAlgorithmType())
                .build();
        } catch (RewardSupplierIsReferencedException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierArchiveRestException.class)
                .withErrorCode(RewardSupplierArchiveRestException.REWARD_SUPPLIER_IS_REFERENCED)
                .addParameter("references", e.getReferences())
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (SalesforceCouponValidationException e) {
            if (e.getCause() != null) {
                if (e.getCause() instanceof SalesforceServiceUnavailableException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e.getCause()).build();
                }

                if (e.getCause() instanceof SalesforceAuthenticationException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e.getCause()).build();
                }

                if (e.getCause() instanceof SalesforceAuthorizationException) {
                    throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                        .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                        .addParameter("client_id", authorization.getClientId().getValue())
                        .withCause(e)
                        .build();
                }
            }
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (SalesforceCouponRewardSupplierInvalidCouponPoolIdException
            | SalesforceCouponRewardSupplierCouponPoolIdNotFoundException | ClientSalesforceSettingsNotFoundException
            | SalesforceCouponRewardSupplierCouponPoolIdAlreadyInUseException
            | SalesforceCouponRewardSupplierSettingsIdMissingException | ClientSalesforceSettingsDisabledException
            | RewardSupplierIllegalRateLimitsException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (RewardSupplierValidationException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_VALIDATION_FAILED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SalesforceCouponRewardSupplierOperationResponse refill(String accessToken, String rewardSupplierId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, ClientSalesforceSettingsRestException,
        SalesforceConnectionRestException, SalesforceCouponRewardSupplierRefillRestException,
        BuildRewardSupplierRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SalesforceCouponOperation salesforceCouponOperation =
                salesforceCouponOperationService.refillCoupons(authorization, Id.valueOf(rewardSupplierId));

            return salesforceCouponOperationToResponse(salesforceCouponOperation, timeZone);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (ClientSalesforceSettingsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsRestException.class)
                .withErrorCode(ClientSalesforceSettingsRestException.SALESFORCE_SETTINGS_NOT_FOUND)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e)
                .build();
        } catch (ClientSalesforceSettingsDisabledException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SETTINGS_DISABLED)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e)
                .build();
        } catch (SalesforceCouponRefillAlreadyInProgressException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierRefillRestException.class)
                .withErrorCode(SalesforceCouponRewardSupplierRefillRestException.REFILL_ALREADY_IN_PROGRESS)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public SalesforceCouponRewardSupplierOperationResponse deleteCoupons(String accessToken, String rewardSupplierId,
        String salesforceCouponOperationId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierOperationRestException,
        DeleteCouponsByRewardOperationRestException {

        if (StringUtils.isBlank(salesforceCouponOperationId)) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierOperationRestException.class)
                .withErrorCode(RewardSupplierOperationRestException.OPERATION_ID_MISSING)
                .build();
        }

        ClientAuthorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SalesforceCouponOperation salesforceCouponOperation =
                salesforceCouponOperationService.deleteCouponsByOperationId(userAuthorization,
                    Id.valueOf(rewardSupplierId), Id.valueOf(salesforceCouponOperationId));

            return salesforceCouponOperationToResponse(salesforceCouponOperation, timeZone);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (SalesforceCouponOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierOperationRestException.class)
                .withErrorCode(RewardSupplierOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", salesforceCouponOperationId)
                .withCause(e)
                .build();
        } catch (SalesforceCouponDeletionByInvalidOperationException e) {
            throw RestExceptionBuilder.newBuilder(DeleteCouponsByRewardOperationRestException.class)
                .withErrorCode(
                    DeleteCouponsByRewardOperationRestException.DELETE_COUPONS_BY_UNSUPPORTED_OPERATION)
                .addParameter("operation_id", salesforceCouponOperationId)
                .withCause(e)
                .build();
        } catch (SalesforceCouponAlreadyDeletedBySpecifiedOperationException e) {
            throw RestExceptionBuilder.newBuilder(DeleteCouponsByRewardOperationRestException.class)
                .withErrorCode(
                    DeleteCouponsByRewardOperationRestException.COUPONS_ALREADY_DELETED_BY_OPERATION)
                .addParameter("operation_id", salesforceCouponOperationId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public CouponStatsResponse getCouponStats(String accessToken, String rewardSupplierId)
        throws UserAuthorizationRestException, RewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CouponStats couponStats =
                couponService.getCouponStats(userAuthorization, Id.valueOf(rewardSupplierId));
            return new CouponStatsResponse(couponStats.getAvailableCouponsCount(),
                couponStats.getIssuedCouponsCount());
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public List<SalesforceCouponRewardSupplierOperationResponse> getOperations(String accessToken,
        String rewardSupplierId, String limit, String offset, ZoneId timeZone)
        throws RewardSupplierRestException, UserAuthorizationRestException, QueryLimitsRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SalesforceCouponOperationQueryBuilder operationQueryBuilder = salesforceCouponOperationService
                .createOperationQueryBuilder(userAuthorization, Id.valueOf(rewardSupplierId));

            return operationQueryBuilder
                .withLimit(parseLimit(limit, DEFAULT_OPERATIONS_QUERY_LIMIT))
                .withOffset(parseOffset(offset, DEFAULT_OPERATIONS_QUERY_OFFSET))
                .list()
                .stream()
                .map(salesforceCouponOperation -> salesforceCouponOperationToResponse(salesforceCouponOperation,
                    timeZone))
                .collect(Collectors.toList());

        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    private List<SalesforceCouponPoolResponse> salesforceCouponPoolToResponses(SalesforceSettings settings,
        List<SalesforceCouponPool> salesforceCouponPool) {
        return salesforceCouponPool.stream()
            .map(item -> new SalesforceCouponPoolResponse(settings.getSettingsId().getValue(),
                settings.getSettingsName(),
                item.getSalesforceCouponPoolId(),
                item.getCouponCount(),
                SalesforceCouponPoolType.valueOf(item.getType().name()),
                item.getEnabled()))
            .collect(Collectors.toList());
    }

    private SalesforceCouponRewardSupplierV2Response createRewardSupplier(
        SalesforceCouponRewardSupplierCreationV2Request creationRequest, ZoneId timeZone, Authorization authorization)
        throws AuthorizationException, RewardSupplierValidationException, BuildRewardSupplierException,
        PartnerRewardSupplierIdTooLongException, SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException,
        SalesforceAuthenticationException, SalesforceAuthorizationException, SalesforceServiceUnavailableException,
        SalesforceCouponPoolIdInvalidUrlPathSegmentException,
        SalesforceCouponRewardSupplierInvalidInitialOffsetException, CampaignComponentValidationRestException,
        InvalidComponentReferenceException, RewardSupplierInvalidTagException,
        MoreThanOneComponentReferenceException {

        SalesforceCouponRewardSupplierBuilder rewardSupplierBuilder =
            rewardSupplierService.create(authorization, RewardSupplierType.SALESFORCE_COUPON,
                rewardSupplierRestMapper.toFaceValueType(creationRequest.getFaceValueType()));
        rewardSupplierBuilder.withCashBackEnabled(cashBackEnabled);

        if (creationRequest.getCouponPoolId().isPresent()) {
            rewardSupplierBuilder.withCouponPoolId(creationRequest.getCouponPoolId().getValue());
        }
        creationRequest.getSettingsId().ifPresent(settingId -> {
            if (!StringUtils.isBlank(settingId)) {
                rewardSupplierBuilder.withSettingsId(Id.valueOf(settingId));
            }
        });
        creationRequest.getFaceValue()
            .ifPresent(faceValue -> rewardSupplierBuilder.withFaceValue(faceValue));
        creationRequest.getPartnerRewardSupplierId()
            .ifPresent(partnerRewardSupplierId -> rewardSupplierBuilder
                .withPartnerRewardSupplierId(partnerRewardSupplierId));
        creationRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        creationRequest.getName()
            .ifPresent(name -> rewardSupplierBuilder.withName(name));
        creationRequest.getBalanceRefillAmount()
            .ifPresent(balanceRefillAmount -> rewardSupplierBuilder.withBalanceRefillAmount(balanceRefillAmount));
        creationRequest.getInitialOffset()
            .ifPresent(initialOffset -> rewardSupplierBuilder.withInitialOffset(initialOffset));
        creationRequest.getFaceValueAlgorithmType()
            .ifPresent(faceValueAlgorithmType -> rewardSupplierBuilder
                .withFaceValueAlgorithmType(rewardSupplierRestMapper.toFaceValueAlgorithmType(faceValueAlgorithmType)));
        creationRequest.getCashBackPercentage()
            .ifPresent(cashBackPercentage -> rewardSupplierBuilder.withCashBackPercentage(cashBackPercentage));
        creationRequest.getMinCashBack()
            .ifPresent(minCashBack -> rewardSupplierBuilder.withMinCashBack(minCashBack));
        creationRequest.getMaxCashBack()
            .ifPresent(maxCashBack -> rewardSupplierBuilder.withMaxCashBack(maxCashBack));
        creationRequest.getLimitPerDay()
            .ifPresent(limitPerDay -> rewardSupplierBuilder.withLimitPerDay(limitPerDay));
        creationRequest.getLimitPerHour()
            .ifPresent(limitPerHour -> rewardSupplierBuilder.withLimitPerHour(limitPerHour));

        creationRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(rewardSupplierBuilder, componentIds);
        });

        creationRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(rewardSupplierBuilder, componentReferences);
        });

        creationRequest.getTags().ifPresent(tags -> rewardSupplierBuilder.withTags(tags));
        creationRequest.getData().ifPresent(data -> rewardSupplierBuilder.withData(data));
        creationRequest.getEnabled().ifPresent(enabled -> rewardSupplierBuilder.withEnabled(enabled));

        SalesforceCouponRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toSalesforceCouponRewardSupplierResponse(rewardSupplier, timeZone);
    }

    private SalesforceCouponRewardSupplierV2Response updateRewardSupplier(String rewardSupplierId,
        SalesforceCouponRewardSupplierUpdateV2Request updateRequest, ZoneId timeZone, Authorization authorization)
        throws RewardSupplierNotFoundException, AuthorizationException, BuildRewardSupplierException,
        PartnerRewardSupplierIdTooLongException, SalesforceCouponRewardSupplierInvalidBalanceRefillAmountException,
        RewardSupplierValidationException, SalesforceAuthenticationException,
        SalesforceCouponPoolIdInvalidUrlPathSegmentException, SalesforceServiceUnavailableException,
        SalesforceAuthorizationException, InvalidComponentReferenceException, CampaignComponentValidationRestException,
        RewardSupplierInvalidTagException, MoreThanOneComponentReferenceException {

        SalesforceCouponRewardSupplierBuilder rewardSupplierBuilder = rewardSupplierService.update(authorization,
            rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId)));
        rewardSupplierBuilder.withCashBackEnabled(cashBackEnabled);

        updateRequest.getFaceValueType()
            .ifPresent(faceValueType -> rewardSupplierBuilder
                .withFaceValueType(rewardSupplierRestMapper.toFaceValueType(faceValueType)));
        updateRequest.getFaceValue()
            .ifPresent(faceValue -> rewardSupplierBuilder.withFaceValue(faceValue));
        updateRequest.getPartnerRewardSupplierId()
            .ifPresent(partnerRewardSupplierId -> rewardSupplierBuilder
                .withPartnerRewardSupplierId(partnerRewardSupplierId));
        updateRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        updateRequest.getName()
            .ifPresent(name -> rewardSupplierBuilder.withName(name));
        updateRequest.getBalanceRefillAmount()
            .ifPresent(balanceRefillAmount -> rewardSupplierBuilder.withBalanceRefillAmount(balanceRefillAmount));
        updateRequest.getFaceValueAlgorithmType()
            .ifPresent(faceValueAlgorithmType -> rewardSupplierBuilder
                .withFaceValueAlgorithmType(rewardSupplierRestMapper.toFaceValueAlgorithmType(faceValueAlgorithmType)));
        updateRequest.getCashBackPercentage()
            .ifPresent(cashBackPercentage -> rewardSupplierBuilder.withCashBackPercentage(cashBackPercentage));
        updateRequest.getMinCashBack()
            .ifPresent(minCashBack -> rewardSupplierBuilder.withMinCashBack(minCashBack));
        updateRequest.getMaxCashBack()
            .ifPresent(maxCashBack -> rewardSupplierBuilder.withMaxCashBack(maxCashBack));
        updateRequest.getLimitPerDay()
            .ifPresent(limitPerDay -> rewardSupplierBuilder.withLimitPerDay(limitPerDay));
        updateRequest.getLimitPerHour()
            .ifPresent(limitPerHour -> rewardSupplierBuilder.withLimitPerHour(limitPerHour));

        updateRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(rewardSupplierBuilder, componentIds);
        });

        updateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(rewardSupplierBuilder, componentReferences);
        });

        updateRequest.getTags().ifPresent(tags -> rewardSupplierBuilder.withTags(tags));
        updateRequest.getData().ifPresent(data -> rewardSupplierBuilder.withData(data));
        updateRequest.getEnabled().ifPresent(enabled -> rewardSupplierBuilder.withEnabled(enabled));

        SalesforceCouponRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toSalesforceCouponRewardSupplierResponse(rewardSupplier, timeZone);
    }

    private SalesforceCouponRewardSupplierOperationResponse
        salesforceCouponOperationToResponse(SalesforceCouponOperation salesforceCouponOperation, ZoneId timezone) {
        return new SalesforceCouponRewardSupplierOperationResponse(salesforceCouponOperation.getId().getValue(),
            salesforceCouponOperation.getRewardSupplierId().getValue(),
            SalesforceCouponRewardSupplierOperationStatus.valueOf(salesforceCouponOperation.getStatus().name()),
            salesforceCouponOperation.getRequestMessage(), salesforceCouponOperation.getResultMessage(),
            SalesforceCouponRewardSupplierOperationType.valueOf(salesforceCouponOperation.getOperationType().name()),
            salesforceCouponOperation.getOffset(), salesforceCouponOperation.getNumberOfRequestedCoupons(),
            salesforceCouponOperation.getNumberOfReceivedCoupons(),
            salesforceCouponOperation.getCreatedAt().atZone(timezone));
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private void handleSalesforceCouponValidationException(SalesforceCouponValidationException e,
        SalesforceCouponRewardSupplierCreationV2Request creationRequest, Authorization authorization)
        throws SalesforceCouponRewardSupplierCreateRestException, SalesforceConnectionRestException {
        if (e.getCause() != null) {
            if (e.getCause() instanceof SalesforceCouponPoolIdInvalidUrlPathSegmentException ||
                e.getCause() instanceof SalesforceCouponRewardSupplierInvalidCouponPoolIdException) {
                throw RestExceptionBuilder.newBuilder(SalesforceCouponRewardSupplierCreateRestException.class)
                    .withErrorCode(SalesforceCouponRewardSupplierCreateRestException.INVALID_COUPON_POOL_ID)
                    .addParameter("coupon_pool_id", creationRequest.getCouponPoolId())
                    .withCause(e.getCause()).build();
            }

            if (e.getCause() instanceof SalesforceServiceUnavailableException) {
                throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                    .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                    .addParameter("client_id", authorization.getClientId().getValue())
                    .withCause(e.getCause()).build();
            }

            if (e.getCause() instanceof SalesforceAuthenticationException) {
                throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                    .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                    .addParameter("client_id", authorization.getClientId().getValue())
                    .withCause(e.getCause()).build();
            }

            if (e.getCause() instanceof SalesforceAuthorizationException) {
                throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                    .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                    .addParameter("client_id", authorization.getClientId().getValue())
                    .withCause(e)
                    .build();
            }
        }
    }

    private static final class SalesforceSettings {
        private final Id<ClientSalesforceSettings> settingsId;
        private final String settingsName;

        private SalesforceSettings(Id<ClientSalesforceSettings> settingsId, String settingsName) {
            this.settingsId = settingsId;
            this.settingsName = settingsName;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            SalesforceSettings that = (SalesforceSettings) other;
            return Objects.equals(settingsId, that.settingsId) && Objects.equals(settingsName, that.settingsName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(settingsId, settingsName);
        }

        private Id<ClientSalesforceSettings> getSettingsId() {
            return settingsId;
        }

        private String getSettingsName() {
            return settingsName;
        }
    }

}
