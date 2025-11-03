package com.extole.client.rest.impl.reward.supplier.tango;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.reward.supplier.BaseRewardSupplierCreateRequestMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.CustomRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.PayPalPayoutsRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierCreateRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.tango.TangoRewardSupplierCreateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.tango.ClientTangoSettings;
import com.extole.model.entity.client.tango.TangoAccount;
import com.extole.model.entity.reward.supplier.tango.TangoRewardSupplier;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.tango.ClientTangoSettingsNotDefinedException;
import com.extole.model.service.client.tango.ClientTangoSettingsService;
import com.extole.model.service.reward.supplier.RewardSupplierBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.tango.TangoAccountNotFoundException;
import com.extole.model.service.reward.supplier.tango.TangoBrandItem;
import com.extole.model.service.reward.supplier.tango.TangoBrandItemNotFoundException;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierCashBackLimitsOutOfBoundsException;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierFaceValueOutOfBoundsException;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierService;
import com.extole.model.service.reward.supplier.tango.TangoRewardSupplierUnsupportedFaceValueAlgorithmTypeException;
import com.extole.model.service.tango.TangoServiceUnavailableException;

@Component
public class TangoRewardSupplierCreateRequestMapper extends
    BaseRewardSupplierCreateRequestMapper<TangoRewardSupplierCreateRequest, TangoRewardSupplier,
        TangoRewardSupplierBuilder> {

    private final TangoRewardSupplierService tangoRewardSupplierService;
    private final RewardSupplierService rewardSupplierService;
    private final ClientTangoSettingsService clientTangoSettingsService;

    public TangoRewardSupplierCreateRequestMapper(ComponentService componentService,
        TangoRewardSupplierService tangoRewardSupplierService,
        RewardSupplierService rewardSupplierService,
        ClientTangoSettingsService clientTangoSettingsService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(componentService, rewardSupplierRestMapper, componentReferenceRequestMapper);
        this.tangoRewardSupplierService = tangoRewardSupplierService;
        this.rewardSupplierService = rewardSupplierService;
        this.clientTangoSettingsService = clientTangoSettingsService;
    }

    @Override
    public TangoRewardSupplierBuilder initialize(Authorization authorization,
        TangoRewardSupplierCreateRequest createRequest)
        throws CustomRewardSupplierRestException, AuthorizationException, TangoRewardSupplierCreationRestException,
        RewardSupplierCreationRestException, BuildRewardSupplierRestException, TangoConnectionRestException {
        if (createRequest.getUtid() == null) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.UTID_MISSING)
                .build();
        }
        if (createRequest.getAccountId() == null) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.ACCOUNT_ID_MISSING)
                .build();
        }
        if (!createRequest.getFaceValue().isPresent()) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.FACE_VALUE_MISSING)
                .build();
        }

        try {
            ClientTangoSettings settings = clientTangoSettingsService.get(authorization);
            TangoAccount account =
                clientTangoSettingsService.selectAccount(settings, Id.valueOf(createRequest.getAccountId()));
            TangoBrandItem tangoBrandItem =
                tangoRewardSupplierService.getTangoBrandItem(authorization, createRequest.getUtid());
            TangoRewardSupplierBuilder supplierBuilder = rewardSupplierService.create(authorization,
                com.extole.model.entity.reward.supplier.RewardSupplierType.valueOf(getType().name()),
                Provided.of(tangoBrandItem.getCurrencyCode()));
            supplierBuilder.withBrandItemDefaults(tangoBrandItem)
                .withCustomerId(settings.getCustomerId())
                .withAccountId(account.getAccountId())
                .withFaceValue(createRequest.getFaceValue().getValue());
            return supplierBuilder;
        } catch (ClientTangoSettingsNotDefinedException | TangoAccountNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.ACCOUNT_NOT_FOUND)
                .addParameter("account_id", createRequest.getAccountId())
                .withCause(e).build();
        } catch (TangoBrandItemNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoRewardSupplierCreationRestException.class)
                .withErrorCode(TangoRewardSupplierCreationRestException.CATALOG_ITEM_NOT_FOUND)
                .addParameter("utid", createRequest.getUtid())
                .withCause(e).build();
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e).build();
        }
    }

    @Override
    protected TangoRewardSupplier complete(Authorization authorization,
        RewardSupplierBuilder<TangoRewardSupplier, ?> supplierBuilder,
        TangoRewardSupplierCreateRequest createRequest, ComponentReferenceContext componentReferenceContext)
        throws InvalidComponentReferenceException, CustomRewardSupplierRestException, BuildRewardSupplierException,
        AuthorizationException, RewardSupplierValidationException, BuildRewardSupplierRestException,
        UserAuthorizationRestException, SalesforceCouponRewardSupplierCreateRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        SalesforceConnectionRestException, TangoRewardSupplierValidationRestException,
        PayPalPayoutsRewardSupplierRestException, RewardSupplierCreationRestException,
        MoreThanOneComponentReferenceException {
        TangoRewardSupplierBuilder builder = (TangoRewardSupplierBuilder) supplierBuilder;

        try {
            return super.complete(authorization, builder, createRequest, componentReferenceContext);
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
        }
    }

    @Override
    public RewardSupplierType getType() {
        return RewardSupplierType.TANGO_V2;
    }
}
