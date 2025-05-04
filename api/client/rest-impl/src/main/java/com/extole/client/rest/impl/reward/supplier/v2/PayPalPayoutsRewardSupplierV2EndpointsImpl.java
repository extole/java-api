package com.extole.client.rest.impl.reward.supplier.v2;

import static com.extole.model.service.reward.supplier.RewardSupplierService.XOOM_CLIENT_ID;
import static com.extole.model.service.reward.supplier.RewardSupplierService.XOOM_SANDBOX_CLIENT_ID;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.BuildRewardSupplierExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.PayPalPayoutsRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.v2.PayPalPayoutsRewardSupplierCreationV2Request;
import com.extole.client.rest.reward.supplier.v2.PayPalPayoutsRewardSupplierUpdateV2Request;
import com.extole.client.rest.reward.supplier.v2.PayPalPayoutsRewardSupplierV2Endpoints;
import com.extole.client.rest.reward.supplier.v2.PayPalPayoutsRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltPayPalPayoutsRewardSupplierV2Response;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.RewardSupplierType;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.entity.reward.supplier.built.paypal.payouts.BuiltPayPalPayoutsRewardSupplier;
import com.extole.model.entity.reward.supplier.paypal.payouts.PayPalPayoutsRewardSupplier;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.reward.supplier.PartnerRewardSupplierIdTooLongException;
import com.extole.model.service.reward.supplier.RewardSupplierInvalidTagException;
import com.extole.model.service.reward.supplier.RewardSupplierIsReferencedException;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierService;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryBuilder;
import com.extole.model.service.reward.supplier.built.BuiltRewardSupplierQueryService;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalCashBackMinMaxLimitsException;
import com.extole.model.service.reward.supplier.built.RewardSupplierIllegalRateLimitsException;
import com.extole.model.service.reward.supplier.built.RewardSupplierNameMissingException;
import com.extole.model.service.reward.supplier.built.paypal.payouts.RewardSupplierUnsupportedFaceValueException;
import com.extole.model.service.reward.supplier.built.paypal.payouts.UnsupportedDecimalFaceValueTypeException;
import com.extole.model.service.reward.supplier.built.paypal.payouts.UnsupportedFaceValueTypeException;
import com.extole.model.service.reward.supplier.paypal.payouts.PayPalPayoutsRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.paypal.payouts.PayPalPayoutsRewardSupplierService;

@Provider
public class PayPalPayoutsRewardSupplierV2EndpointsImpl implements PayPalPayoutsRewardSupplierV2Endpoints {

    private final ClientAuthorizationProvider authorizationProvider;

    private final PayPalPayoutsRewardSupplierService payPalPayoutsRewardSupplierService;
    private final RewardSupplierService rewardSupplierService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final BuiltRewardSupplierQueryService builtRewardSupplierQueryService;
    private final ComponentService componentService;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public PayPalPayoutsRewardSupplierV2EndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PayPalPayoutsRewardSupplierService payPalPayoutsRewardSupplierService,
        RewardSupplierService rewardSupplierService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        BuiltRewardSupplierQueryService builtRewardSupplierQueryService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.payPalPayoutsRewardSupplierService = payPalPayoutsRewardSupplierService;
        this.rewardSupplierService = rewardSupplierService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
        this.builtRewardSupplierQueryService = builtRewardSupplierQueryService;
        this.componentService = componentService;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<PayPalPayoutsRewardSupplierV2Response> list(String accessToken,
        @Nullable Boolean includeArchived, ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return payPalPayoutsRewardSupplierService.createQueryBuilder(userAuthorization)
                .withIncludeArchived(Boolean.TRUE.equals(includeArchived))
                .list()
                .stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toPayPalPayoutsRewardSupplierResponse(
                    rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PayPalPayoutsRewardSupplierV2Response get(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PayPalPayoutsRewardSupplier rewardSupplier =
                payPalPayoutsRewardSupplierService.get(userAuthorization, Id.valueOf(rewardSupplierId));
            return rewardSupplierRestMapper.toPayPalPayoutsRewardSupplierResponse(rewardSupplier, timeZone);
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
    public List<BuiltPayPalPayoutsRewardSupplierV2Response> listBuilt(String accessToken,
        @Nullable Boolean includeArchived, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltRewardSupplierQueryBuilder queryBuilder =
                builtRewardSupplierQueryService.createQueryBuilder(userAuthorization)
                    .withType(RewardSupplierType.PAYPAL_PAYOUTS);

            if (Boolean.TRUE.equals(includeArchived)) {
                queryBuilder.includeArchived();
            }

            List<BuiltPayPalPayoutsRewardSupplier> rewardSuppliers = queryBuilder.list();

            return rewardSuppliers.stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toBuiltPayPalPayoutsRewardSupplierResponse(
                    rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltPayPalPayoutsRewardSupplierV2Response getBuilt(String accessToken, String rewardSupplierId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<BuiltRewardSupplier> rewardSupplier = builtRewardSupplierQueryService
                .createQueryBuilder(userAuthorization)
                .withId(Id.valueOf(rewardSupplierId))
                .withType(RewardSupplierType.PAYPAL_PAYOUTS)
                .list()
                .stream()
                .findFirst();
            if (rewardSupplier.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                    .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                    .addParameter("reward_supplier_id", rewardSupplierId)
                    .build();
            }
            BuiltPayPalPayoutsRewardSupplier builtRewardSupplier =
                (BuiltPayPalPayoutsRewardSupplier) rewardSupplier.get();
            return rewardSupplierRestMapper.toBuiltPayPalPayoutsRewardSupplierResponse(builtRewardSupplier, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PayPalPayoutsRewardSupplierV2Response create(String accessToken,
        PayPalPayoutsRewardSupplierCreationV2Request creationRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        PayPalPayoutsRewardSupplierRestException, RewardSupplierRestException,
        CampaignComponentValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            checkCreationAccessRights(authorization);
            return createRewardSupplier(creationRequest, timeZone, authorization);
        } catch (RewardSupplierUnsupportedFaceValueException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.ZERO_FACE_VALUE)
                .withCause(e)
                .build();
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", creationRequest.getPartnerRewardSupplierId())
                .withCause(e)
                .build();
        } catch (UnsupportedFaceValueTypeException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.UNSUPPORTED_FACE_VALUE_TYPE)
                .addParameter("face_value_type", e.getFaceValueType())
                .addParameter("supported_face_value_types", e.getSupportedFaceValueTypes())
                .build();
        } catch (UnsupportedDecimalFaceValueTypeException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.UNSUPPORTED_DECIMAL_FACE_VALUE_TYPE)
                .addParameter("face_value_type", e.getFaceValueType())
                .addParameter("supported_decimal_face_value_types", e.getSupportedDecimalFaceValueTypes())
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

    private PayPalPayoutsRewardSupplierV2Response createRewardSupplier(
        PayPalPayoutsRewardSupplierCreationV2Request creationRequest, ZoneId timeZone, Authorization authorization)
        throws RewardSupplierCreationRestException, BuildRewardSupplierException, AuthorizationException,
        InvalidComponentReferenceException, PartnerRewardSupplierIdTooLongException,
        CampaignComponentValidationRestException, RewardSupplierValidationException, RewardSupplierInvalidTagException,
        MoreThanOneComponentReferenceException {

        if (StringUtils.isBlank(creationRequest.getMerchantToken())) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.MERCHANT_TOKEN_MISSING)
                .build();
        }

        PayPalPayoutsRewardSupplierBuilder rewardSupplierBuilder =
            rewardSupplierService.create(authorization, RewardSupplierType.PAYPAL_PAYOUTS,
                rewardSupplierRestMapper.toFaceValueType(creationRequest.getFaceValueType()));
        rewardSupplierBuilder
            .withName(creationRequest.getName())
            .withMerchantToken(creationRequest.getMerchantToken())
            .withFaceValue(creationRequest.getFaceValue());

        creationRequest.getFaceValueAlgorithmType()
            .ifPresent(faceValueAlgorithmType -> rewardSupplierBuilder
                .withFaceValueAlgorithmType(rewardSupplierRestMapper.toFaceValueAlgorithmType(faceValueAlgorithmType)));
        creationRequest.getCashBackPercentage()
            .ifPresent(cashBackPercentage -> rewardSupplierBuilder.withCashBackPercentage(cashBackPercentage));
        creationRequest.getMinCashBack()
            .ifPresent(minCashBack -> rewardSupplierBuilder.withMinCashBack(minCashBack));
        creationRequest.getMaxCashBack()
            .ifPresent(maxCashBack -> rewardSupplierBuilder.withMaxCashBack(maxCashBack));
        creationRequest.getPartnerRewardSupplierId()
            .ifPresent(partnerRewardSupplierId -> rewardSupplierBuilder
                .withPartnerRewardSupplierId(partnerRewardSupplierId));
        creationRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        creationRequest.getDescription()
            .ifPresent(description -> rewardSupplierBuilder.withDescription(description));
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

        PayPalPayoutsRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toPayPalPayoutsRewardSupplierResponse(rewardSupplier, timeZone);
    }

    @Override
    public PayPalPayoutsRewardSupplierV2Response update(String accessToken, String rewardSupplierId,
        PayPalPayoutsRewardSupplierUpdateV2Request updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        PayPalPayoutsRewardSupplierRestException, CampaignComponentValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return updateRewardSupplier(rewardSupplierId, updateRequest, timeZone, authorization);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (RewardSupplierUnsupportedFaceValueException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.ZERO_FACE_VALUE)
                .withCause(e)
                .build();
        } catch (UnsupportedFaceValueTypeException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.UNSUPPORTED_FACE_VALUE_TYPE)
                .addParameter("face_value_type", e.getFaceValueType())
                .addParameter("supported_face_value_types", e.getSupportedFaceValueTypes())
                .build();
        } catch (UnsupportedDecimalFaceValueTypeException e) {
            throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                .withErrorCode(PayPalPayoutsRewardSupplierRestException.UNSUPPORTED_DECIMAL_FACE_VALUE_TYPE)
                .addParameter("face_value_type", e.getFaceValueType())
                .addParameter("supported_decimal_face_value_types", e.getSupportedDecimalFaceValueTypes())
                .build();
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", updateRequest.getPartnerRewardSupplierId())
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
        } catch (RewardSupplierNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.NAME_MISSING)
                .build();
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

    private PayPalPayoutsRewardSupplierV2Response updateRewardSupplier(String rewardSupplierId,
        PayPalPayoutsRewardSupplierUpdateV2Request updateRequest, ZoneId timeZone, Authorization authorization)
        throws RewardSupplierNotFoundException, AuthorizationException, BuildRewardSupplierException,
        PayPalPayoutsRewardSupplierRestException, PartnerRewardSupplierIdTooLongException,
        CampaignComponentValidationRestException, InvalidComponentReferenceException, RewardSupplierValidationException,
        RewardSupplierInvalidTagException, MoreThanOneComponentReferenceException {
        PayPalPayoutsRewardSupplierBuilder rewardSupplierBuilder = rewardSupplierService.update(authorization,
            rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId)));

        if (updateRequest.getMerchantToken().isPresent()) {
            if (StringUtils.isBlank(updateRequest.getMerchantToken().getValue())) {
                throw RestExceptionBuilder.newBuilder(PayPalPayoutsRewardSupplierRestException.class)
                    .withErrorCode(PayPalPayoutsRewardSupplierRestException.BLANK_MERCHANT_TOKEN)
                    .build();
            }
            rewardSupplierBuilder.withMerchantToken(updateRequest.getMerchantToken().getValue());
        }

        updateRequest.getName()
            .ifPresent(name -> rewardSupplierBuilder.withName(name));
        updateRequest.getFaceValue()
            .ifPresent(faceValue -> rewardSupplierBuilder.withFaceValue(faceValue));
        updateRequest.getFaceValueAlgorithmType()
            .ifPresent(faceValueAlgorithmType -> rewardSupplierBuilder
                .withFaceValueAlgorithmType(rewardSupplierRestMapper.toFaceValueAlgorithmType(faceValueAlgorithmType)));
        updateRequest.getCashBackPercentage()
            .ifPresent(cashBackPercentage -> rewardSupplierBuilder.withCashBackPercentage(cashBackPercentage));
        updateRequest.getMinCashBack()
            .ifPresent(minCashBack -> rewardSupplierBuilder.withMinCashBack(minCashBack));
        updateRequest.getMaxCashBack()
            .ifPresent(maxCashBack -> rewardSupplierBuilder.withMaxCashBack(maxCashBack));
        updateRequest.getFaceValueType()
            .ifPresent(faceValueType -> rewardSupplierBuilder
                .withFaceValueType(rewardSupplierRestMapper.toFaceValueType(faceValueType)));
        updateRequest.getPartnerRewardSupplierId()
            .ifPresent(partnerRewardSupplierId -> rewardSupplierBuilder
                .withPartnerRewardSupplierId(partnerRewardSupplierId));
        updateRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        updateRequest.getDescription()
            .ifPresent(description -> rewardSupplierBuilder.withDescription(description));
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

        updateRequest.getTags()
            .ifPresent(tags -> rewardSupplierBuilder.withTags(tags));
        updateRequest.getData().ifPresent(data -> rewardSupplierBuilder.withData(data));
        updateRequest.getEnabled().ifPresent(enabled -> rewardSupplierBuilder.withEnabled(enabled));

        PayPalPayoutsRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toPayPalPayoutsRewardSupplierResponse(rewardSupplier, timeZone);
    }

    @Override
    public PayPalPayoutsRewardSupplierV2Response archive(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PayPalPayoutsRewardSupplier rewardSupplier = rewardSupplierService.archive(authorization,
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId)));
            return rewardSupplierRestMapper.toPayPalPayoutsRewardSupplierResponse(rewardSupplier, timeZone);
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
        } catch (UnsupportedDecimalFaceValueTypeException | RewardSupplierIllegalCashBackMinMaxLimitsException
            | RewardSupplierIllegalRateLimitsException | UnsupportedFaceValueTypeException
            | RewardSupplierUnsupportedFaceValueException | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
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

    private void checkCreationAccessRights(Authorization authorization) throws AuthorizationException {
        if (!XOOM_CLIENT_ID.equals(authorization.getClientId()) &&
            !XOOM_SANDBOX_CLIENT_ID.equals(authorization.getClientId())) {
            if (!authorization.isAuthorized(authorization.getClientId(), Authorization.Scope.CLIENT_SUPERUSER)) {
                throw new AuthorizationException("Access denied");
            }
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

}
