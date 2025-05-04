package com.extole.client.rest.impl.reward.supplier;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.impl.campaign.BuildRewardSupplierExceptionMapper;
import com.extole.client.rest.impl.reward.supplier.built.BuiltRewardSupplierResponseMapperRepository;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.CustomRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.PayPalPayoutsRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreateRequest;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierEndpoints;
import com.extole.client.rest.reward.supplier.RewardSupplierResponse;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierUpdateRequest;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierCreateRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.built.BuiltRewardSupplierResponse;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.RewardSupplierType;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.service.reward.supplier.RewardSupplierIsReferencedException;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryBuilder;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryService;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalCashBackMinMaxLimitsException;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalRateLimitsException;
import com.extole.model.service.reward.supplier.custom.reward.InvalidMissingFulfillmentAutoFailDelayException;

@Provider
public class RewardSupplierEndpointsImpl implements RewardSupplierEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardSupplierService rewardSupplierService;
    private final BuiltRewardSupplierQueryService builtRewardSupplierQueryService;
    private final RewardSupplierCreateRequestMapperRepository rewardSupplierCreateRequestMapperRepository;
    private final RewardSupplierUpdateRequestMapperRepository rewardSupplierUpdateRequestMapperRepository;
    private final RewardSupplierResponseMapperRepository rewardSupplierResponseMapperRepository;
    private final BuiltRewardSupplierResponseMapperRepository builtRewardSupplierResponseMapperRepository;

    @Autowired
    public RewardSupplierEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        RewardSupplierService rewardSupplierService,
        BuiltRewardSupplierQueryService builtRewardSupplierQueryService,
        RewardSupplierCreateRequestMapperRepository rewardSupplierCreateRequestMapperRepository,
        RewardSupplierUpdateRequestMapperRepository rewardSupplierUpdateRequestMapperRepository,
        RewardSupplierResponseMapperRepository rewardSupplierResponseMapperRepository,
        BuiltRewardSupplierResponseMapperRepository builtRewardSupplierResponseMapperRepository) {
        this.authorizationProvider = authorizationProvider;
        this.rewardSupplierService = rewardSupplierService;
        this.builtRewardSupplierQueryService = builtRewardSupplierQueryService;
        this.rewardSupplierCreateRequestMapperRepository = rewardSupplierCreateRequestMapperRepository;
        this.rewardSupplierUpdateRequestMapperRepository = rewardSupplierUpdateRequestMapperRepository;
        this.rewardSupplierResponseMapperRepository = rewardSupplierResponseMapperRepository;
        this.builtRewardSupplierResponseMapperRepository = builtRewardSupplierResponseMapperRepository;
    }

    @Override
    public <T extends RewardSupplierResponse> List<T> list(String accessToken, @Nullable Boolean includeArchived,
        List<com.extole.client.rest.reward.supplier.RewardSupplierType> types, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return (List<T>) rewardSupplierService.createQueryBuilder(authorization)
                .withRewardSupplierTypes(
                    types.stream().map(type -> RewardSupplierType.valueOf(type.name())).collect(Collectors.toList()))
                .withIncludeArchived(Boolean.TRUE.equals(includeArchived))
                .list()
                .stream()
                .map(
                    rewardSupplier -> rewardSupplierResponseMapperRepository
                        .getRewardSupplierResponseMapper(com.extole.client.rest.reward.supplier.RewardSupplierType
                            .valueOf(rewardSupplier.getRewardSupplierType().name()))
                        .toResponse(authorization, rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public RewardSupplierResponse get(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RewardSupplier rewardSupplier =
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId));
            return rewardSupplierResponseMapperRepository
                .getRewardSupplierResponseMapper(com.extole.client.rest.reward.supplier.RewardSupplierType
                    .valueOf(rewardSupplier.getRewardSupplierType().name()))
                .toResponse(authorization, rewardSupplier, timeZone);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
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
    public <T extends BuiltRewardSupplierResponse> List<T> listBuilt(String accessToken,
        @Nullable Boolean includeArchived, @Nullable Boolean includeDisabled,
        List<com.extole.client.rest.reward.supplier.RewardSupplierType> types,
        List<String> displayTypes, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltRewardSupplierQueryBuilder queryBuilder =
                builtRewardSupplierQueryService.createQueryBuilder(authorization)
                    .withDisplayTypes(displayTypes)
                    .withTypes(
                        types.stream().map(type -> RewardSupplierType.valueOf(type.name()))
                            .collect(Collectors.toList()));

            if (Boolean.TRUE.equals(includeArchived)) {
                queryBuilder.includeArchived();
            }

            if (Boolean.TRUE.equals(includeDisabled)) {
                queryBuilder.includeDisabled();
            }

            List<BuiltRewardSupplier> rewardSuppliers = queryBuilder.list();

            return (List<T>) rewardSuppliers.stream()
                .map(
                    rewardSupplier -> builtRewardSupplierResponseMapperRepository
                        .getBuiltRewardSupplierResponseMapper(com.extole.client.rest.reward.supplier.RewardSupplierType
                            .valueOf(rewardSupplier.getRewardSupplierType().name()))
                        .toResponse(authorization, rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltRewardSupplierResponse getBuilt(String accessToken, String rewardSupplierId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<BuiltRewardSupplier> rewardSupplier = builtRewardSupplierQueryService
                .createQueryBuilder(authorization)
                .withId(Id.valueOf(rewardSupplierId))
                .list()
                .stream()
                .findFirst();
            if (rewardSupplier.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                    .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                    .addParameter("reward_supplier_id", rewardSupplierId)
                    .build();
            }
            BuiltRewardSupplier builtRewardSupplier = rewardSupplier.get();
            return builtRewardSupplierResponseMapperRepository
                .getBuiltRewardSupplierResponseMapper(com.extole.client.rest.reward.supplier.RewardSupplierType
                    .valueOf(builtRewardSupplier.getRewardSupplierType().name()))
                .toResponse(authorization, builtRewardSupplier, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Set<String> getDisplayTypes(String accessToken, ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<BuiltRewardSupplier> rewardSuppliers =
                builtRewardSupplierQueryService.createQueryBuilder(authorization)
                    .includeDisabled()
                    .list();

            return rewardSuppliers.stream()
                .map(supplier -> supplier.getDisplayType())
                .collect(Collectors.toSet());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public RewardSupplierResponse create(String accessToken,
        RewardSupplierCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        RewardSupplierCreationRestException, CustomRewardSupplierRestException, RewardSupplierRestException,
        CampaignComponentValidationRestException, TangoRewardSupplierCreationRestException,
        SalesforceCouponRewardSupplierCreateRestException, TangoRewardSupplierValidationRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        TangoConnectionRestException, SalesforceConnectionRestException, PayPalPayoutsRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        RewardSupplierCreateRequestMapper createRequestMapper =
            rewardSupplierCreateRequestMapperRepository.getRewardSupplierCreateRequestMapper(
                createRequest.getRewardSupplierType());
        RewardSupplier rewardSupplier = createRequestMapper.create(authorization, createRequest);
        return rewardSupplierResponseMapperRepository
            .getRewardSupplierResponseMapper(createRequest.getRewardSupplierType())
            .toResponse(authorization, rewardSupplier, timeZone);
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public RewardSupplierResponse update(String accessToken, String rewardSupplierId,
        RewardSupplierUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        CustomRewardSupplierRestException, CampaignComponentValidationRestException,
        RewardSupplierCreationRestException, TangoRewardSupplierCreationRestException,
        SalesforceCouponRewardSupplierCreateRestException, TangoRewardSupplierValidationRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        TangoConnectionRestException, SalesforceConnectionRestException, PayPalPayoutsRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        RewardSupplierUpdateRequestMapper updateRequestMapper =
            rewardSupplierUpdateRequestMapperRepository.getRewardSupplierUpdateRequestMapper(
                updateRequest.getRewardSupplierType());
        RewardSupplier rewardSupplier = updateRequestMapper.update(authorization, rewardSupplierId, updateRequest);
        return rewardSupplierResponseMapperRepository
            .getRewardSupplierResponseMapper(updateRequest.getRewardSupplierType())
            .toResponse(authorization, rewardSupplier, timeZone);
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public RewardSupplierResponse archive(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RewardSupplier rewardSupplier =
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId));
            RewardSupplier archivedSupplier = rewardSupplierService.archive(authorization, rewardSupplier);
            return rewardSupplierResponseMapperRepository
                .getRewardSupplierResponseMapper(com.extole.client.rest.reward.supplier.RewardSupplierType.valueOf(
                    rewardSupplier.getRewardSupplierType().name()))
                .toResponse(authorization, archivedSupplier, timeZone);
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (InvalidMissingFulfillmentAutoFailDelayException | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (RewardSupplierIsReferencedException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierArchiveRestException.class)
                .withErrorCode(RewardSupplierArchiveRestException.REWARD_SUPPLIER_IS_REFERENCED)
                .addParameter("references", e.getReferences())
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (RewardSupplierValidationException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_VALIDATION_FAILED)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public RewardSupplierResponse delete(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RewardSupplier rewardSupplier =
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId), true);
            RewardSupplier archivedSupplier = rewardSupplierService.delete(authorization, rewardSupplier);
            return rewardSupplierResponseMapperRepository
                .getRewardSupplierResponseMapper(com.extole.client.rest.reward.supplier.RewardSupplierType.valueOf(
                    rewardSupplier.getRewardSupplierType().name()))
                .toResponse(authorization, archivedSupplier, timeZone);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (InvalidMissingFulfillmentAutoFailDelayException | RewardSupplierIllegalCashBackMinMaxLimitsException
            | RewardSupplierIllegalRateLimitsException | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (RewardSupplierIsReferencedException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierArchiveRestException.class)
                .withErrorCode(RewardSupplierArchiveRestException.REWARD_SUPPLIER_IS_REFERENCED)
                .addParameter("references", e.getReferences())
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (RewardSupplierValidationException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_VALIDATION_FAILED)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public RewardSupplierResponse unArchive(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RewardSupplier rewardSupplier =
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId), true);
            RewardSupplier archivedSupplier = rewardSupplierService.unArchive(authorization, rewardSupplier);
            return rewardSupplierResponseMapperRepository
                .getRewardSupplierResponseMapper(com.extole.client.rest.reward.supplier.RewardSupplierType.valueOf(
                    rewardSupplier.getRewardSupplierType().name()))
                .toResponse(authorization, archivedSupplier, timeZone);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (InvalidMissingFulfillmentAutoFailDelayException | RewardSupplierIllegalCashBackMinMaxLimitsException
            | RewardSupplierIllegalRateLimitsException | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (RewardSupplierValidationException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_VALIDATION_FAILED)
                .withCause(e)
                .build();
        }
    }
}
