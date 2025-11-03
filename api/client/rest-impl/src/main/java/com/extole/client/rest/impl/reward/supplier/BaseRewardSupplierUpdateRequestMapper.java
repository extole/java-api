package com.extole.client.rest.impl.reward.supplier;

import java.util.List;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.BuildRewardSupplierExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.CustomRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.PayPalPayoutsRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierUpdateRequest;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierCreateRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierValidationRestException;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.PartnerRewardKeyType;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.reward.supplier.PartnerRewardSupplierIdTooLongException;
import com.extole.model.service.reward.supplier.RewardSupplierBuilder;
import com.extole.model.service.reward.supplier.RewardSupplierInvalidTagException;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierValidationException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.reward.supplier.custom.reward.InvalidMissingFulfillmentAutoFailDelayException;

public abstract class BaseRewardSupplierUpdateRequestMapper<REQUEST extends RewardSupplierUpdateRequest, SUPPLIER extends RewardSupplier, BUILDER extends RewardSupplierBuilder<
    SUPPLIER, ?>>
    implements RewardSupplierUpdateRequestMapper<REQUEST, SUPPLIER> {

    private final ComponentService componentService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    public BaseRewardSupplierUpdateRequestMapper(ComponentService componentService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.componentService = componentService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public SUPPLIER update(Authorization authorization, String rewardSupplierId, REQUEST updateRequest)
        throws CustomRewardSupplierRestException, UserAuthorizationRestException, RewardSupplierRestException,
        RewardSupplierCreationRestException, BuildRewardSupplierRestException,
        CampaignComponentValidationRestException, SalesforceCouponRewardSupplierCreateRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        SalesforceConnectionRestException, TangoRewardSupplierCreationRestException, TangoConnectionRestException,
        TangoRewardSupplierValidationRestException, PayPalPayoutsRewardSupplierRestException {
        return buildRewardSupplier(authorization, rewardSupplierId, updateRequest,
            (auth, id, request) -> this.initialize(auth, id, (REQUEST) request),
            (auth, builder) -> this.complete(auth, builder, updateRequest,
                componentService.buildDefaultComponentReferenceContext(authorization)));
    }

    private SUPPLIER buildRewardSupplier(Authorization authorization, String rewardSupplierId,
        RewardSupplierUpdateRequest updateRequest,
        UpdateInitializer<RewardSupplierUpdateRequest, RewardSupplierBuilder<SUPPLIER, ?>> initializer,
        UpdateCompleter<RewardSupplierBuilder<SUPPLIER, ?>, SUPPLIER> completer)
        throws BuildRewardSupplierRestException, RewardSupplierCreationRestException, RewardSupplierRestException,
        CustomRewardSupplierRestException, UserAuthorizationRestException, CampaignComponentValidationRestException,
        SalesforceCouponRewardSupplierCreateRestException, ClientSalesforceSettingsRestException,
        SalesforceCouponRewardSupplierValidationRestException, SalesforceConnectionRestException,
        TangoRewardSupplierCreationRestException, TangoConnectionRestException,
        TangoRewardSupplierValidationRestException, PayPalPayoutsRewardSupplierRestException {
        try {
            RewardSupplierBuilder<SUPPLIER, ?> builder =
                initializer.initialize(authorization, rewardSupplierId, updateRequest);

            updateRequest.getName()
                .ifPresent(name -> builder.withName(name));
            updateRequest.getDisplayName()
                .ifPresent(displayName -> builder.withDisplayName(displayName));
            updateRequest.getFaceValueAlgorithmType()
                .ifPresent(faceValueAlgorithmType -> builder.withFaceValueAlgorithmType(
                    rewardSupplierRestMapper.toFaceValueAlgorithmType(faceValueAlgorithmType)));
            updateRequest.getCashBackPercentage()
                .ifPresent(cashBackPercentage -> builder.withCashBackPercentage(cashBackPercentage));
            updateRequest.getMinCashBack()
                .ifPresent(minCashBack -> builder.withMinCashBack(minCashBack));
            updateRequest.getMaxCashBack()
                .ifPresent(maxCashBack -> builder.withMaxCashBack(maxCashBack));
            updateRequest.getPartnerRewardSupplierId()
                .ifPresent(partnerRewardSupplierId -> builder
                    .withPartnerRewardSupplierId(partnerRewardSupplierId));
            updateRequest.getPartnerRewardKeyType()
                .ifPresent(partnerRewardKeyType -> builder
                    .withPartnerRewardKeyType(
                        PartnerRewardKeyType.valueOf(partnerRewardKeyType.name())));
            updateRequest.getDisplayType()
                .ifPresent(displayType -> builder.withDisplayType(displayType));
            updateRequest.getDescription()
                .ifPresent(description -> builder.withDescription(description));
            updateRequest.getLimitPerDay()
                .ifPresent(limitPerDay -> builder.withLimitPerDay(limitPerDay));
            updateRequest.getLimitPerHour()
                .ifPresent(limitPerHour -> builder.withLimitPerHour(limitPerHour));
            updateRequest.getTags()
                .ifPresent(tags -> builder.withTags(tags));
            updateRequest.getData()
                .ifPresent(data -> builder.withData(data));
            updateRequest.getEnabled()
                .ifPresent(enabled -> builder.withEnabled(enabled));
            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(builder, componentIds);
            });
            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(builder, componentReferences);
            });

            return completer.complete(authorization, builder);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", updateRequest.getPartnerRewardSupplierId())
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

    public abstract BUILDER initialize(Authorization authorization, String rewardSupplierId, REQUEST createRequest)
        throws CustomRewardSupplierRestException, AuthorizationException, RewardSupplierCreationRestException,
        BuildRewardSupplierRestException, TangoRewardSupplierCreationRestException, TangoConnectionRestException,
        RewardSupplierNotFoundException;

    protected SUPPLIER complete(Authorization authorization, RewardSupplierBuilder<SUPPLIER, ?> rewardSupplierBuilder,
        REQUEST createRequest, ComponentReferenceContext componentReferenceContext)
        throws InvalidComponentReferenceException, BuildRewardSupplierException, AuthorizationException,
        RewardSupplierValidationException, CustomRewardSupplierRestException, BuildRewardSupplierRestException,
        SalesforceCouponRewardSupplierValidationRestException, SalesforceCouponRewardSupplierCreateRestException,
        UserAuthorizationRestException, ClientSalesforceSettingsRestException, SalesforceConnectionRestException,
        TangoRewardSupplierValidationRestException, PayPalPayoutsRewardSupplierRestException,
        RewardSupplierCreationRestException, MoreThanOneComponentReferenceException {
        return rewardSupplierBuilder.save(() -> componentReferenceContext);
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

    interface UpdateInitializer<REQUEST extends RewardSupplierUpdateRequest, BUILDER extends RewardSupplierBuilder<?,
        ?>> {
        BUILDER initialize(Authorization authorization, String rewardSupplierId, REQUEST request)
            throws CustomRewardSupplierRestException, AuthorizationException, RewardSupplierCreationRestException,
            BuildRewardSupplierRestException, TangoRewardSupplierCreationRestException,
            TangoConnectionRestException, RewardSupplierNotFoundException;
    }

    interface UpdateCompleter<BUILDER extends RewardSupplierBuilder<?, ?>, KEY> {
        KEY complete(Authorization authorization, BUILDER builder)
            throws InvalidComponentReferenceException, BuildRewardSupplierException, AuthorizationException,
            RewardSupplierValidationException, CustomRewardSupplierRestException, BuildRewardSupplierRestException,
            SalesforceCouponRewardSupplierValidationRestException, SalesforceCouponRewardSupplierCreateRestException,
            UserAuthorizationRestException, ClientSalesforceSettingsRestException, SalesforceConnectionRestException,
            TangoRewardSupplierValidationRestException, PayPalPayoutsRewardSupplierRestException,
            RewardSupplierCreationRestException, MoreThanOneComponentReferenceException;
    }
}
