package com.extole.client.rest.impl.reward.supplier.v2;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.BuildRewardSupplierExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierMetaData;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.v2.TangoBrandItemResponse;
import com.extole.client.rest.reward.supplier.v2.TangoBrandItemValueType;
import com.extole.client.rest.reward.supplier.v2.TangoBrandResponse;
import com.extole.client.rest.reward.supplier.v2.TangoRewardSupplierCreationV2Request;
import com.extole.client.rest.reward.supplier.v2.TangoRewardSupplierUpdateV2Request;
import com.extole.client.rest.reward.supplier.v2.TangoRewardSupplierV2Endpoints;
import com.extole.client.rest.reward.supplier.v2.TangoRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltTangoRewardSupplierV2Response;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.tango.ClientTangoSettings;
import com.extole.model.entity.client.tango.TangoAccount;
import com.extole.model.entity.reward.supplier.RewardSupplierType;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.entity.reward.supplier.built.tango.BuiltTangoRewardSupplier;
import com.extole.model.entity.reward.supplier.tango.TangoRewardSupplier;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.tango.ClientTangoSettingsNotDefinedException;
import com.extole.model.service.client.tango.ClientTangoSettingsService;
import com.extole.model.service.reward.supplier.PartnerRewardSupplierIdTooLongException;
import com.extole.model.service.reward.supplier.RewardSupplierInvalidTagException;
import com.extole.model.service.reward.supplier.RewardSupplierIsReferencedException;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.TangoBrandItemInvalidValueException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryBuilder;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryService;
import com.extole.model.service.reward.supplier.tango.TangoAccountNotFoundException;
import com.extole.model.service.reward.supplier.tango.TangoBrand;
import com.extole.model.service.reward.supplier.tango.TangoBrandItem;
import com.extole.model.service.reward.supplier.tango.TangoBrandItemNotFoundException;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierCashBackLimitsOutOfBoundsException;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierFaceValueOutOfBoundsException;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierService;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierUnsupportedFaceValueAlgorithmTypeException;
import com.extole.model.service.tango.TangoServiceUnavailableException;
import com.extole.model.shared.reward.supplier.tango.TangoCatalogCache;

@Provider
public class TangoRewardSupplierV2EndpointsImpl implements TangoRewardSupplierV2Endpoints {

    private static final Logger LOG = LoggerFactory.getLogger(TangoRewardSupplierV2EndpointsImpl.class);

    private final ClientAuthorizationProvider authorizationProvider;
    private final TangoRewardSupplierService tangoRewardSupplierService;
    private final ClientTangoSettingsService clientTangoSettingsService;
    private final RewardSupplierService rewardSupplierService;
    private final TangoCatalogCache tangoCatalogCache;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final BuiltRewardSupplierQueryService builtRewardSupplierQueryService;
    private final ComponentService componentService;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public TangoRewardSupplierV2EndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        TangoRewardSupplierService tangoRewardSupplierService,
        ClientTangoSettingsService clientTangoSettingsService,
        RewardSupplierService rewardSupplierService,
        TangoCatalogCache rewardSupplierTangoCache,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        BuiltRewardSupplierQueryService builtRewardSupplierQueryService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.tangoRewardSupplierService = tangoRewardSupplierService;
        this.clientTangoSettingsService = clientTangoSettingsService;
        this.rewardSupplierService = rewardSupplierService;
        this.tangoCatalogCache = rewardSupplierTangoCache;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
        this.builtRewardSupplierQueryService = builtRewardSupplierQueryService;
        this.componentService = componentService;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<TangoBrandResponse> getCatalog(String accessToken)
        throws UserAuthorizationRestException, TangoConnectionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (!authorization.isAuthorized(authorization.getClientId(), Authorization.Scope.USER_SUPPORT)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
        try {
            return tangoCatalogCache.getCatalog(authorization).stream().map(brand -> {
                List<TangoBrandItemResponse> items = brand.getItems().stream().map(brandItem -> {
                    TangoBrandItemValueType valueType =
                        TangoBrandItemValueType.valueOf(brandItem.getValueType().toString());
                    BigDecimal faceValue =
                        brandItem.getFaceValue().isPresent() ? brandItem.getFaceValue().get() : null;
                    BigDecimal minValue =
                        brandItem.getMinValue().isPresent() ? brandItem.getMinValue().get() : null;
                    BigDecimal maxValue =
                        brandItem.getMaxValue().isPresent() ? brandItem.getMaxValue().get() : null;
                    FaceValueType currencyCode = FaceValueType.valueOf(brandItem.getCurrencyCode().toString());
                    return new TangoBrandItemResponse(brandItem.getUtid(), brandItem.getRewardName(), valueType,
                        faceValue, minValue, maxValue, currencyCode, brandItem.getCountries());
                }).collect(Collectors.toList());
                return new TangoBrandResponse(brand.getBrandName(), brand.getDisclaimer(), brand.getDescription(),
                    brand.getImageUrl().orElse(null), items);
            }).collect(Collectors.toList());
        } catch (TangoServiceUnavailableException e) {
            LOG.warn("Tango connection error for client: {}", authorization.getClientId(), e);

            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public List<TangoRewardSupplierV2Response> list(String accessToken, Boolean includeArchived, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return tangoRewardSupplierService.createQueryBuilder(authorization)
                .withIncludeArchived(Boolean.TRUE.equals(includeArchived))
                .list()
                .stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toTangoRewardSupplierResponse(
                    rewardSupplier, timeZone, getMetaData(authorization, rewardSupplier.getUtid())))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public TangoRewardSupplierV2Response create(String accessToken, TangoRewardSupplierCreationV2Request request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, TangoConnectionRestException, TangoRewardSupplierCreationRestException,
        BuildRewardSupplierRestException, RewardSupplierRestException, TangoRewardSupplierValidationRestException,
        RewardSupplierCreationRestException, CampaignComponentValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return createRewardSupplier(request, timeZone, authorization);
        } catch (ClientTangoSettingsNotDefinedException | TangoAccountNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.ACCOUNT_NOT_FOUND)
                .addParameter("account_id", request.getAccountId())
                .withCause(e).build();
        } catch (TangoBrandItemNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.CATALOG_ITEM_NOT_FOUND)
                .addParameter("utid", request.getUtid())
                .withCause(e).build();
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e).build();
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", request.getPartnerRewardSupplierId())
                .withCause(e).build();
        } catch (TangoBrandItemInvalidValueException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (TangoRewardSupplierFaceValueOutOfBoundsException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierValidationRestException.class)
                .withErrorCode(TangoRewardSupplierValidationRestException.FACE_VALUE_OUT_OF_BOUNDS)
                .addParameter("utid", e.getUtid())
                .addParameter("face_value", e.getFaceValue().doubleValue())
                .addParameter("min_brand_item_value", e.getMinFaceValue().doubleValue())
                .addParameter("max_brand_item_value", e.getMaxFaceValue().doubleValue())
                .withCause(e).build();
        } catch (TangoRewardSupplierCashBackLimitsOutOfBoundsException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierValidationRestException.class)
                .withErrorCode(TangoRewardSupplierValidationRestException.CASH_BACK_LIMITS_OUT_OF_BOUNDS)
                .addParameter("utid", e.getUtid())
                .addParameter("min_cash_back", e.getMinCashBack().doubleValue())
                .addParameter("max_cash_back", e.getMaxCashBack().doubleValue())
                .addParameter("min_brand_item_value", e.getMinBrandValue().doubleValue())
                .addParameter("max_brand_item_value", e.getMaxBrandValue().doubleValue())
                .withCause(e).build();
        } catch (TangoRewardSupplierUnsupportedFaceValueAlgorithmTypeException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierValidationRestException.class)
                .withErrorCode(TangoRewardSupplierValidationRestException.UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE)
                .addParameter("utid", e.getUtid())
                .addParameter("face_value_algorithm_type", e.getFaceValueAlgorithmType())
                .withCause(e).build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e).build();
        } catch (RewardSupplierInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e).build();
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
    public TangoRewardSupplierV2Response get(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            TangoRewardSupplier rewardSupplier =
                tangoRewardSupplierService.get(authorization, Id.valueOf(rewardSupplierId));
            return rewardSupplierRestMapper.toTangoRewardSupplierResponse(
                rewardSupplier, timeZone, getMetaData(authorization, rewardSupplier.getUtid()));
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
    public List<BuiltTangoRewardSupplierV2Response> listBuilt(String accessToken, Boolean includeArchived,
        ZoneId timeZone) throws UserAuthorizationRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltRewardSupplierQueryBuilder queryBuilder =
                builtRewardSupplierQueryService.createQueryBuilder(authorization)
                    .withType(RewardSupplierType.TANGO_V2);

            if (Boolean.TRUE.equals(includeArchived)) {
                queryBuilder.includeArchived();
            }

            List<BuiltTangoRewardSupplier> rewardSuppliers = queryBuilder.list();
            return rewardSuppliers.stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toBuiltTangoRewardSupplierResponse(
                    rewardSupplier, timeZone, getMetaData(authorization, rewardSupplier.getUtid())))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public BuiltTangoRewardSupplierV2Response getBuilt(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<BuiltRewardSupplier> rewardSupplier =
                builtRewardSupplierQueryService.createQueryBuilder(authorization)
                    .withId(Id.valueOf(rewardSupplierId))
                    .withType(RewardSupplierType.TANGO_V2)
                    .list()
                    .stream()
                    .findFirst();
            if (rewardSupplier.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                    .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                    .addParameter("reward_supplier_id", rewardSupplierId)
                    .build();
            }
            BuiltTangoRewardSupplier tangoRewardSupplier = (BuiltTangoRewardSupplier) rewardSupplier.get();
            return rewardSupplierRestMapper.toBuiltTangoRewardSupplierResponse(
                tangoRewardSupplier, timeZone, getMetaData(authorization, tangoRewardSupplier.getUtid()));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public TangoRewardSupplierV2Response update(String accessToken, String rewardSupplierId,
        TangoRewardSupplierUpdateV2Request request, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        TangoConnectionRestException, TangoRewardSupplierCreationRestException,
        TangoRewardSupplierValidationRestException, CampaignComponentValidationRestException,
        RewardSupplierCreationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return updateRewardSupplier(rewardSupplierId, request, timeZone, authorization);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", request.getPartnerRewardSupplierId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (TangoRewardSupplierCashBackLimitsOutOfBoundsException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierValidationRestException.class)
                .withErrorCode(TangoRewardSupplierValidationRestException.CASH_BACK_LIMITS_OUT_OF_BOUNDS)
                .addParameter("utid", e.getUtid())
                .addParameter("min_cash_back", e.getMinCashBack().doubleValue())
                .addParameter("max_cash_back", e.getMaxCashBack().doubleValue())
                .addParameter("min_brand_item_value", e.getMinBrandValue().doubleValue())
                .addParameter("max_brand_item_value", e.getMaxBrandValue().doubleValue())
                .withCause(e)
                .build();
        } catch (TangoBrandItemNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.CATALOG_ITEM_NOT_FOUND)
                .addParameter("utid", e.getUtid())
                .withCause(e)
                .build();
        } catch (TangoRewardSupplierFaceValueOutOfBoundsException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierValidationRestException.class)
                .withErrorCode(TangoRewardSupplierValidationRestException.FACE_VALUE_OUT_OF_BOUNDS)
                .addParameter("utid", e.getUtid())
                .addParameter("face_value", e.getFaceValue().doubleValue())
                .addParameter("min_brand_item_value", e.getMinFaceValue().doubleValue())
                .addParameter("max_brand_item_value", e.getMaxFaceValue().doubleValue())
                .withCause(e)
                .build();
        } catch (TangoRewardSupplierUnsupportedFaceValueAlgorithmTypeException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierValidationRestException.class)
                .withErrorCode(TangoRewardSupplierValidationRestException.UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE)
                .addParameter("utid", e.getUtid())
                .addParameter("face_value_algorithm_type", e.getFaceValueAlgorithmType())
                .withCause(e)
                .build();
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        } catch (TangoBrandItemInvalidValueException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (RewardSupplierInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e).build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e).build();
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
    public TangoRewardSupplierV2Response archive(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            TangoRewardSupplier rewardSupplier = rewardSupplierService.archive(authorization,
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId)));
            return rewardSupplierRestMapper.toTangoRewardSupplierResponse(
                rewardSupplier, timeZone, getMetaData(authorization, rewardSupplier.getUtid()));
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (RewardSupplierIsReferencedException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierArchiveRestException.class)
                .withErrorCode(RewardSupplierArchiveRestException.REWARD_SUPPLIER_IS_REFERENCED)
                .addParameter("references", e.getReferences())
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (Exception e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
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

    private RewardSupplierMetaData getMetaData(Authorization authorization, String utid) {
        try {
            for (TangoBrand brand : tangoCatalogCache.getCatalog(authorization)) {
                for (TangoBrandItem item : brand.getItems()) {
                    if (item.getUtid().equals(utid)) {
                        return RewardSupplierMetaData.create(brand, item);
                    }
                }
            }
        } catch (TangoServiceUnavailableException | AuthorizationException e) {
            return RewardSupplierMetaData.empty();
        }
        return RewardSupplierMetaData.empty();
    }

    private TangoRewardSupplierV2Response createRewardSupplier(TangoRewardSupplierCreationV2Request creationRequest,
        ZoneId timeZone, Authorization authorization)
        throws TangoRewardSupplierCreationRestException, AuthorizationException, ClientTangoSettingsNotDefinedException,
        TangoAccountNotFoundException, TangoBrandItemNotFoundException, TangoServiceUnavailableException,
        BuildRewardSupplierException, RewardSupplierValidationException, PartnerRewardSupplierIdTooLongException,
        CampaignComponentValidationRestException, InvalidComponentReferenceException, RewardSupplierInvalidTagException,
        MoreThanOneComponentReferenceException {
        if (creationRequest.getUtid() == null) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.UTID_MISSING)
                .build();
        }
        if (creationRequest.getAccountId() == null) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.ACCOUNT_ID_MISSING)
                .build();
        }

        ClientTangoSettings settings = clientTangoSettingsService.get(authorization);
        TangoAccount account =
            clientTangoSettingsService.selectAccount(settings, Id.valueOf(creationRequest.getAccountId()));
        TangoBrandItem tangoBrandItem =
            tangoRewardSupplierService.getTangoBrandItem(authorization, creationRequest.getUtid());
        TangoRewardSupplierBuilder rewardSupplierBuilder =
            rewardSupplierService.create(authorization, RewardSupplierType.TANGO_V2,
                Provided.of(tangoBrandItem.getCurrencyCode()));
        rewardSupplierBuilder.withBrandItemDefaults(tangoBrandItem)
            .withCustomerId(settings.getCustomerId())
            .withAccountId(account.getAccountId())
            .withFaceValue(creationRequest.getFaceValue());

        creationRequest.getPartnerRewardSupplierId()
            .ifPresent(partnerRewardSupplierId -> rewardSupplierBuilder
                .withPartnerRewardSupplierId(partnerRewardSupplierId));
        creationRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        creationRequest.getName()
            .ifPresent(name -> rewardSupplierBuilder.withName(name));
        creationRequest.getDescription()
            .ifPresent(description -> rewardSupplierBuilder.withDescription(description));
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

        TangoRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toTangoRewardSupplierResponse(rewardSupplier, timeZone,
            getMetaData(authorization, rewardSupplier.getUtid()));
    }

    private TangoRewardSupplierV2Response updateRewardSupplier(String rewardSupplierId,
        TangoRewardSupplierUpdateV2Request updateRequest, ZoneId timeZone, Authorization authorization)
        throws AuthorizationException, RewardSupplierNotFoundException, TangoServiceUnavailableException,
        TangoBrandItemNotFoundException, PartnerRewardSupplierIdTooLongException, BuildRewardSupplierException,
        CampaignComponentValidationRestException, RewardSupplierValidationException, InvalidComponentReferenceException,
        RewardSupplierInvalidTagException, MoreThanOneComponentReferenceException {
        TangoRewardSupplier supplier =
            (TangoRewardSupplier) rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId));
        TangoBrandItem tangoBrandItem = tangoRewardSupplierService.getTangoBrandItem(authorization, supplier.getUtid());
        TangoRewardSupplierBuilder rewardSupplierBuilder = rewardSupplierService.update(authorization, supplier);
        rewardSupplierBuilder.withBrandItem(tangoBrandItem);

        updateRequest.getPartnerRewardSupplierId()
            .ifPresent(partnerRewardSupplierId -> rewardSupplierBuilder
                .withPartnerRewardSupplierId(partnerRewardSupplierId));
        updateRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        updateRequest.getName()
            .ifPresent(name -> rewardSupplierBuilder.withName(name));
        updateRequest.getDescription()
            .ifPresent(description -> rewardSupplierBuilder.withDescription(description));
        updateRequest.getFaceValueAlgorithmType()
            .ifPresent(faceValueAlgorithmType -> rewardSupplierBuilder
                .withFaceValueAlgorithmType(rewardSupplierRestMapper.toFaceValueAlgorithmType(faceValueAlgorithmType)));
        updateRequest.getFaceValue()
            .ifPresent(faceValue -> rewardSupplierBuilder.withFaceValue(faceValue));
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

        TangoRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toTangoRewardSupplierResponse(rewardSupplier, timeZone,
            getMetaData(authorization, rewardSupplier.getUtid()));
    }

}
