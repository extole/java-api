package com.extole.client.rest.impl.reward.supplier.v2;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.BuildRewardSupplierExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.reward.supplier.RewardSupplierRestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.CustomRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierArchiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.v2.CustomRewardSupplierCreationV2Request;
import com.extole.client.rest.reward.supplier.v2.CustomRewardSupplierUpdateV2Request;
import com.extole.client.rest.reward.supplier.v2.CustomRewardSupplierV2Endpoints;
import com.extole.client.rest.reward.supplier.v2.CustomRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.built.BuiltCustomRewardSupplierV2Response;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.PartnerRewardKeyType;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.RewardSupplierType;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.entity.reward.supplier.built.custom.reward.BuiltCustomRewardSupplier;
import com.extole.model.entity.reward.supplier.custom.reward.CustomRewardSupplier;
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
import com.extole.model.service.reward.supplier.custom.reward.CustomRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.custom.reward.CustomRewardSupplierService;
import com.extole.model.service.reward.supplier.custom.reward.InvalidMissingFulfillmentAutoFailDelayException;
import com.extole.model.service.reward.supplier.custom.reward.NegativeMissingFulfillmentAlertDelayException;
import com.extole.model.service.reward.supplier.custom.reward.NegativeMissingFulfillmentAutoFailDelayException;

@Provider
public class CustomRewardSupplierV2EndpointsImpl implements CustomRewardSupplierV2Endpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CustomRewardSupplierService customRewardSupplierService;
    private final RewardSupplierService rewardSupplierService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final BuiltRewardSupplierQueryService builtRewardSupplierQueryService;
    private final ComponentService componentService;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CustomRewardSupplierV2EndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CustomRewardSupplierService customRewardSupplierService,
        RewardSupplierService rewardSupplierService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        BuiltRewardSupplierQueryService builtRewardSupplierQueryService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.customRewardSupplierService = customRewardSupplierService;
        this.rewardSupplierService = rewardSupplierService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
        this.builtRewardSupplierQueryService = builtRewardSupplierQueryService;
        this.componentService = componentService;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<CustomRewardSupplierV2Response> list(String accessToken, @Nullable Boolean includeArchived,
        ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return customRewardSupplierService.createQueryBuilder(userAuthorization)
                .withIncludeArchived(Boolean.TRUE.equals(includeArchived))
                .list()
                .stream()
                .map(
                    rewardSupplier -> rewardSupplierRestMapper.toCustomRewardSupplierResponse(rewardSupplier, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CustomRewardSupplierV2Response get(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CustomRewardSupplier rewardSupplier =
                customRewardSupplierService.get(userAuthorization, Id.valueOf(rewardSupplierId));
            return rewardSupplierRestMapper.toCustomRewardSupplierResponse(rewardSupplier, timeZone);
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
    public List<BuiltCustomRewardSupplierV2Response> listBuilt(String accessToken, @Nullable Boolean includeArchived,
        ZoneId timeZone) throws UserAuthorizationRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltRewardSupplierQueryBuilder queryBuilder =
                builtRewardSupplierQueryService.createQueryBuilder(userAuthorization)
                    .withType(RewardSupplierType.CUSTOM_REWARD);

            if (Boolean.TRUE.equals(includeArchived)) {
                queryBuilder.includeArchived();
            }

            List<BuiltCustomRewardSupplier> rewardSuppliers = queryBuilder.list();

            return rewardSuppliers.stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toBuiltCustomRewardSupplierResponse(rewardSupplier,
                    timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltCustomRewardSupplierV2Response getBuilt(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            Optional<BuiltRewardSupplier> rewardSupplier = builtRewardSupplierQueryService
                .createQueryBuilder(userAuthorization)
                .withId(Id.valueOf(rewardSupplierId))
                .withType(RewardSupplierType.CUSTOM_REWARD)
                .list()
                .stream()
                .findFirst();
            if (rewardSupplier.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                    .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                    .addParameter("reward_supplier_id", rewardSupplierId)
                    .build();
            }
            BuiltCustomRewardSupplier builtRewardSupplier =
                (BuiltCustomRewardSupplier) rewardSupplier.get();
            return rewardSupplierRestMapper.toBuiltCustomRewardSupplierResponse(builtRewardSupplier, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CustomRewardSupplierV2Response create(String accessToken,
        CustomRewardSupplierCreationV2Request creationRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        RewardSupplierCreationRestException, CustomRewardSupplierRestException, RewardSupplierRestException,
        CampaignComponentValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return createCustomRewardSupplier(creationRequest, timeZone, authorization);
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", creationRequest.getPartnerRewardSupplierId())
                .withCause(e).build();
        } catch (NegativeMissingFulfillmentAlertDelayException e) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.NEGATIVE_ALERT_DELAY)
                .addParameter("missing_fulfillment_alert_delay_ms", e.getMissingFulfillmentAlertDelayMs())
                .withCause(e).build();
        } catch (NegativeMissingFulfillmentAutoFailDelayException e) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.NEGATIVE_AUTO_FAIL_DELAY)
                .addParameter("missing_fulfillment_auto_fail_delay_ms", e.getMissingFulfillmentAutoFailDelayMs())
                .withCause(e).build();
        } catch (InvalidMissingFulfillmentAutoFailDelayException e) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.INVALID_AUTO_FAIL_DELAY)
                .addParameter("missing_fulfillment_alert_delay_ms", e.getMissingFulfillmentAlertDelayMs())
                .addParameter("missing_fulfillment_auto_fail_delay_ms", e.getMissingFulfillmentAutoFailDelayMs())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
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

    private CustomRewardSupplierV2Response createCustomRewardSupplier(
        CustomRewardSupplierCreationV2Request creationRequest, ZoneId timeZone, Authorization authorization)
        throws CustomRewardSupplierRestException, AuthorizationException, BuildRewardSupplierException,
        PartnerRewardSupplierIdTooLongException, NegativeMissingFulfillmentAlertDelayException,
        NegativeMissingFulfillmentAutoFailDelayException, CampaignComponentValidationRestException,
        RewardSupplierValidationException, InvalidComponentReferenceException, RewardSupplierInvalidTagException,
        MoreThanOneComponentReferenceException {
        if (creationRequest.getType() == null) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.CUSTOM_REWARD_TYPE_MISSING)
                .build();
        }

        CustomRewardSupplierBuilder rewardSupplierBuilder =
            rewardSupplierService.create(authorization, RewardSupplierType.CUSTOM_REWARD,
                rewardSupplierRestMapper.toFaceValueType(creationRequest.getFaceValueType()));

        rewardSupplierBuilder.withType(com.extole.model.entity.reward.supplier.custom.reward.CustomRewardType.valueOf(
            creationRequest.getType().name()));

        creationRequest.getName()
            .ifPresent(name -> rewardSupplierBuilder.withName(name));
        creationRequest.getFaceValue()
            .ifPresent(faceValue -> rewardSupplierBuilder.withFaceValue(faceValue));
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
        creationRequest.getPartnerRewardKeyType()
            .ifPresent(partnerRewardKeyType -> rewardSupplierBuilder
                .withPartnerRewardKeyType(PartnerRewardKeyType.valueOf(partnerRewardKeyType.name())));
        creationRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        creationRequest.isAutoSendRewardEmailEnabled()
            .ifPresent(isAutoSendRewardEmailEnabled -> rewardSupplierBuilder
                .withRewardEmailAutoSendEnabled(isAutoSendRewardEmailEnabled.booleanValue()));
        creationRequest.isAutoFulfillmentEnabled()
            .ifPresent(isAutoFulfillmentEnabled -> rewardSupplierBuilder
                .withAutoFulfillmentEnabled(isAutoFulfillmentEnabled.booleanValue()));
        creationRequest.isMissingFulfillmentAlertEnabled()
            .ifPresent(isMissingFulfillmentAlertEnabled -> rewardSupplierBuilder
                .withMissingFulfillmentAlertEnabled(isMissingFulfillmentAlertEnabled.booleanValue()));
        creationRequest.getMissingFulfillmentAlertDelayMs()
            .ifPresent(missingFulfillmentAlertDelayMs -> rewardSupplierBuilder
                .withMissingFulfillmentAlertDelayMs(missingFulfillmentAlertDelayMs));
        creationRequest.isMissingFulfillmentAutoFailEnabled()
            .ifPresent(isMissingFulfillmentAutoFailEnabled -> rewardSupplierBuilder
                .withMissingFulfillmentAutoFailEnabled(isMissingFulfillmentAutoFailEnabled.booleanValue()));
        creationRequest.getMissingFulfillmentAutoFailDelayMs()
            .ifPresent(FulfillmentAutoFailDelayMs -> rewardSupplierBuilder
                .withMissingFulfillmentAutoFailDelayMs(FulfillmentAutoFailDelayMs));
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
        creationRequest.getStateTransitions().ifPresent(stateTransitions -> {
            rewardSupplierBuilder.withStateTransitions(mapStateTransitions(stateTransitions));
        });

        CustomRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toCustomRewardSupplierResponse(rewardSupplier, timeZone);
    }

    @Override
    public CustomRewardSupplierV2Response update(String accessToken, String rewardSupplierId,
        CustomRewardSupplierUpdateV2Request updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        CustomRewardSupplierRestException, CampaignComponentValidationRestException,
        RewardSupplierCreationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RewardSupplier rewardSupplier =
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId));
            return updateCustomRewardSupplier(updateRequest, timeZone, authorization,
                rewardSupplierService.update(authorization, rewardSupplier));
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
        } catch (NegativeMissingFulfillmentAlertDelayException e) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.NEGATIVE_ALERT_DELAY)
                .addParameter("missing_fulfillment_alert_delay_ms", e.getMissingFulfillmentAlertDelayMs())
                .withCause(e)
                .build();
        } catch (NegativeMissingFulfillmentAutoFailDelayException e) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.NEGATIVE_AUTO_FAIL_DELAY)
                .addParameter("missing_fulfillment_auto_fail_delay_ms", e.getMissingFulfillmentAutoFailDelayMs())
                .withCause(e)
                .build();
        } catch (InvalidMissingFulfillmentAutoFailDelayException e) {
            throw RestExceptionBuilder.newBuilder(CustomRewardSupplierRestException.class)
                .withErrorCode(CustomRewardSupplierRestException.INVALID_AUTO_FAIL_DELAY)
                .addParameter("missing_fulfillment_alert_delay_ms", e.getMissingFulfillmentAlertDelayMs())
                .addParameter("missing_fulfillment_auto_fail_delay_ms", e.getMissingFulfillmentAutoFailDelayMs())
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
        } catch (RewardSupplierInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e).build();
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

    private CustomRewardSupplierV2Response updateCustomRewardSupplier(CustomRewardSupplierUpdateV2Request updateRequest,
        ZoneId timeZone, Authorization authorization, CustomRewardSupplierBuilder rewardSupplierBuilder)
        throws BuildRewardSupplierException, PartnerRewardSupplierIdTooLongException,
        NegativeMissingFulfillmentAlertDelayException, NegativeMissingFulfillmentAutoFailDelayException,
        CampaignComponentValidationRestException, RewardSupplierValidationException, InvalidComponentReferenceException,
        AuthorizationException, RewardSupplierInvalidTagException, MoreThanOneComponentReferenceException {
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
        updateRequest.getPartnerRewardKeyType()
            .ifPresent(partnerRewardKeyType -> rewardSupplierBuilder
                .withPartnerRewardKeyType(PartnerRewardKeyType.valueOf(partnerRewardKeyType.name())));
        updateRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        updateRequest.getType().ifPresent(type -> rewardSupplierBuilder
            .withType(com.extole.model.entity.reward.supplier.custom.reward.CustomRewardType.valueOf(type.name())));
        updateRequest.isAutoSendRewardEmailEnabled()
            .ifPresent(isAutoSendRewardEmailEnabled -> rewardSupplierBuilder
                .withRewardEmailAutoSendEnabled(isAutoSendRewardEmailEnabled.booleanValue()));
        updateRequest.isAutoFulfillmentEnabled()
            .ifPresent(isAutoFulfillmentEnabled -> rewardSupplierBuilder
                .withAutoFulfillmentEnabled(isAutoFulfillmentEnabled.booleanValue()));
        updateRequest.isMissingFulfillmentAlertEnabled()
            .ifPresent(isMissingFulfillmentAlertEnabled -> rewardSupplierBuilder
                .withMissingFulfillmentAlertEnabled(isMissingFulfillmentAlertEnabled.booleanValue()));
        updateRequest.getMissingFulfillmentAlertDelayMs()
            .ifPresent(missingFulfillmentAlertDelayMs -> rewardSupplierBuilder
                .withMissingFulfillmentAlertDelayMs(missingFulfillmentAlertDelayMs));
        updateRequest.isMissingFulfillmentAutoFailEnabled()
            .ifPresent(isMissingFulfillmentAutoFailEnabled -> rewardSupplierBuilder
                .withMissingFulfillmentAutoFailEnabled(isMissingFulfillmentAutoFailEnabled.booleanValue()));
        updateRequest.getMissingFulfillmentAutoFailDelayMs()
            .ifPresent(FulfillmentAutoFailDelayMs -> rewardSupplierBuilder
                .withMissingFulfillmentAutoFailDelayMs(FulfillmentAutoFailDelayMs));
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
        updateRequest.getStateTransitions().ifPresent(stateTransitions -> {
            rewardSupplierBuilder.withStateTransitions(mapStateTransitions(stateTransitions));
        });

        CustomRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toCustomRewardSupplierResponse(rewardSupplier, timeZone);
    }

    @Override
    public CustomRewardSupplierV2Response archive(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CustomRewardSupplier rewardSupplier = rewardSupplierService.archive(authorization,
                rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId)));
            return rewardSupplierRestMapper.toCustomRewardSupplierResponse(rewardSupplier, timeZone);
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

    private static Map<com.extole.model.entity.reward.supplier.RewardState,
        List<com.extole.model.entity.reward.supplier.RewardState>> mapStateTransitions(
            Map<RewardState, List<RewardState>> stateTransitions) {
        return stateTransitions
            .entrySet()
            .stream()
            .collect(Collectors
                .toMap(entry -> com.extole.model.entity.reward.supplier.RewardState.valueOf(entry.getKey()
                    .name()), entry -> entry.getValue()
                        .stream()
                        .map(item -> com.extole.model.entity.reward.supplier.RewardState.valueOf(item.name()))
                        .collect(Collectors.toList())));
    }

}
