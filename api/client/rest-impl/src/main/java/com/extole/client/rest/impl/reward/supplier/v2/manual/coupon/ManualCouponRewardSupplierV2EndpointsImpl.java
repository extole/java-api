package com.extole.client.rest.impl.reward.supplier.v2.manual.coupon;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.io.FileBackedOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import com.extole.client.rest.reward.supplier.v2.CouponStatsResponse;
import com.extole.client.rest.reward.supplier.v2.DeleteCouponsByRewardOperationRestException;
import com.extole.client.rest.reward.supplier.v2.ManualCouponOperationStatus;
import com.extole.client.rest.reward.supplier.v2.ManualCouponOperationType;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRequest;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierCreationV2Request;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierDownloadCouponsResponse;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierOperationResponse;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierUpdateV2Request;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierUploadCouponsRequest;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierUploadCouponsRestException;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierV2Endpoints;
import com.extole.client.rest.reward.supplier.v2.ManualCouponRewardSupplierV2Response;
import com.extole.client.rest.reward.supplier.v2.ManualCouponUploadResponse;
import com.extole.client.rest.reward.supplier.v2.RewardSupplierOperationRestException;
import com.extole.client.rest.reward.supplier.v2.UploadCouponParams;
import com.extole.client.rest.reward.supplier.v2.built.BuiltManualCouponRewardSupplierV2Response;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.reward.supplier.RewardState;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.RewardSupplierType;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.entity.reward.supplier.built.manual.coupon.BuiltManualCouponRewardSupplier;
import com.extole.model.entity.reward.supplier.manual.coupon.ManualCouponRewardSupplier;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
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
import com.extole.model.service.reward.supplier.coupon.CouponRewardSupplierIllegalValueInWarningAmountException;
import com.extole.model.service.reward.supplier.coupon.CouponRewardSupplierIllegalValueOfMinimumCouponLifetimeException;
import com.extole.model.service.reward.supplier.manual.coupon.ManualCouponRewardSupplierBuilder;
import com.extole.model.service.reward.supplier.manual.coupon.ManualCouponRewardSupplierService;
import com.extole.rewards.service.coupon.Coupon;
import com.extole.rewards.service.coupon.CouponBuilder;
import com.extole.rewards.service.coupon.CouponCodeBlankException;
import com.extole.rewards.service.coupon.CouponCodeInvalidException;
import com.extole.rewards.service.coupon.CouponCodeTooLongException;
import com.extole.rewards.service.coupon.CouponFilenameEmptyException;
import com.extole.rewards.service.coupon.CouponFilenameTooLongException;
import com.extole.rewards.service.coupon.CouponService;
import com.extole.rewards.service.coupon.CouponStats;
import com.extole.rewards.service.coupon.CouponsConcurrentOperationException;
import com.extole.rewards.service.coupon.CouponsMissingException;
import com.extole.rewards.service.coupon.DuplicateCouponCodeInListException;
import com.extole.rewards.service.coupon.ExistingCouponCodeException;
import com.extole.rewards.service.operation.manual.coupon.ManualCouponAlreadyDeletedBySpecifiedOperationException;
import com.extole.rewards.service.operation.manual.coupon.ManualCouponDeletionByInvalidOperationException;
import com.extole.rewards.service.operation.manual.coupon.ManualCouponOperation;
import com.extole.rewards.service.operation.manual.coupon.ManualCouponOperationIllegalTypeException;
import com.extole.rewards.service.operation.manual.coupon.ManualCouponOperationNotFoundException;
import com.extole.rewards.service.operation.manual.coupon.ManualCouponOperationQueryBuilder;
import com.extole.rewards.service.operation.manual.coupon.ManualCouponOperationService;
import com.extole.rewards.service.operation.manual.coupon.ManualCouponQueryBuilder;
import com.extole.spring.ServiceLocator;

@Provider
public class ManualCouponRewardSupplierV2EndpointsImpl implements ManualCouponRewardSupplierV2Endpoints {

    private static final int DEFAULT_COUPONS_DOWNLOAD_LIMIT_FOR_JSON = 100;
    private static final int DEFAULT_COUPONS_DOWNLOAD_LIMIT_FOR_FILE = 10_000;
    private static final int DEFAULT_COUPONS_DOWNLOAD_OFFSET = 0;
    private static final int DEFAULT_OPERATIONS_QUERY_LIMIT = 100;
    private static final int DEFAULT_OPERATIONS_QUERY_OFFSET = 0;

    private static final int FILE_BACKED_OUTPUT_STREAM_THRESHOLD = 256 * 1024;
    private static final char NEW_LINE = '\n';
    private static final String WHITESPACES = "\\s+";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DASH = "-";
    private static final char CSV_DELIMITER = ',';

    private final ManualCouponRewardSupplierService manualCouponRewardSupplierService;
    private final RewardSupplierService rewardSupplierService;
    private final ManualCouponOperationService manualCouponOperationService;
    private final CouponService couponService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final boolean cashBackEnabled;
    private final ManualCouponFileReader manualCouponFileReader;
    private final ServiceLocator serviceLocator;
    private final BuiltRewardSupplierQueryService builtRewardSupplierQueryService;
    private final RewardSupplierRestMapper rewardSupplierRestMapper;
    private final ComponentService componentService;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public ManualCouponRewardSupplierV2EndpointsImpl(
        ManualCouponRewardSupplierService manualCouponRewardSupplierService,
        RewardSupplierService rewardSupplierService,
        ManualCouponOperationService manualCouponOperationService,
        CouponService couponService,
        ClientAuthorizationProvider authorizationProvider,
        ManualCouponFileReader manualCouponFileReader,
        ServiceLocator serviceLocator,
        BuiltRewardSupplierQueryService builtRewardSupplierQueryService,
        RewardSupplierRestMapper rewardSupplierRestMapper,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper,
        @Value("${reward.supplier.manual.coupon.cash.back.enabled:true}") boolean cashBackEnabled) {
        this.manualCouponOperationService = manualCouponOperationService;
        this.couponService = couponService;
        this.authorizationProvider = authorizationProvider;
        this.manualCouponRewardSupplierService = manualCouponRewardSupplierService;
        this.rewardSupplierService = rewardSupplierService;
        this.manualCouponFileReader = manualCouponFileReader;
        this.serviceLocator = serviceLocator;
        this.builtRewardSupplierQueryService = builtRewardSupplierQueryService;
        this.rewardSupplierRestMapper = rewardSupplierRestMapper;
        this.componentService = componentService;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
        this.cashBackEnabled = cashBackEnabled;
    }

    @Override
    public List<ManualCouponRewardSupplierV2Response> list(String accessToken, Boolean includeArchived, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return manualCouponRewardSupplierService.createQueryBuilder(userAuthorization)
                .withIncludeArchived(Boolean.TRUE.equals(includeArchived))
                .list()
                .stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toManualCouponRewardSupplierResponse(
                    rewardSupplier, timeZone))
                .collect(toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public ManualCouponRewardSupplierV2Response get(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ManualCouponRewardSupplier rewardSupplier =
                manualCouponRewardSupplierService.get(userAuthorization, Id.valueOf(rewardSupplierId));
            return rewardSupplierRestMapper.toManualCouponRewardSupplierResponse(rewardSupplier, timeZone);
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
    public List<BuiltManualCouponRewardSupplierV2Response> listBuilt(String accessToken, Boolean includeArchived,
        ZoneId timeZone) throws UserAuthorizationRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltRewardSupplierQueryBuilder queryBuilder =
                builtRewardSupplierQueryService.createQueryBuilder(userAuthorization)
                    .withType(RewardSupplierType.MANUAL_COUPON);

            if (Boolean.TRUE.equals(includeArchived)) {
                queryBuilder.includeArchived();
            }

            List<BuiltManualCouponRewardSupplier> rewardSuppliers = queryBuilder.list();
            return rewardSuppliers.stream()
                .map(rewardSupplier -> rewardSupplierRestMapper.toBuiltManualCouponRewardSupplierResponse(
                    rewardSupplier, timeZone))
                .collect(toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public BuiltManualCouponRewardSupplierV2Response getBuilt(String accessToken, String rewardSupplierId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<BuiltRewardSupplier> rewardSupplier = builtRewardSupplierQueryService
                .createQueryBuilder(userAuthorization)
                .withId(Id.valueOf(rewardSupplierId))
                .withType(RewardSupplierType.MANUAL_COUPON)
                .list()
                .stream()
                .findFirst();
            if (rewardSupplier.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                    .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                    .addParameter("reward_supplier_id", rewardSupplierId)
                    .build();
            }
            BuiltManualCouponRewardSupplier builtRewardSupplier =
                (BuiltManualCouponRewardSupplier) rewardSupplier.get();
            return rewardSupplierRestMapper.toBuiltManualCouponRewardSupplierResponse(builtRewardSupplier, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    private BuiltRewardSupplier getBuiltRewardSupplier(String accessToken, String rewardSupplierId)
        throws UserAuthorizationRestException, AuthorizationException, RewardSupplierNotFoundException,
        BuildRewardSupplierException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltRewardSupplierQueryBuilder queryBuilder =
            builtRewardSupplierQueryService.createQueryBuilder(userAuthorization)
                .withType(RewardSupplierType.MANUAL_COUPON)
                .withId(Id.valueOf(rewardSupplierId));
        return queryBuilder.list().stream().findFirst().orElseThrow(() -> new RewardSupplierNotFoundException(
            "Reward supplier with id " + rewardSupplierId + " not found", Id.valueOf(rewardSupplierId)));
    }

    @Override
    public ManualCouponRewardSupplierV2Response create(String accessToken,
        ManualCouponRewardSupplierCreationV2Request creationRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildRewardSupplierRestException,
        RewardSupplierCreationRestException, RewardSupplierRestException, CampaignComponentValidationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return createRewardSupplier(creationRequest, timeZone, userAuthorization);
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (CouponRewardSupplierIllegalValueInWarningAmountException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_COUPON_COUNT_WARN_LIMIT)
                .addParameter("coupon_count_warn_limit", creationRequest.getCouponCountWarnLimit())
                .withCause(e)
                .build();
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", creationRequest.getPartnerRewardSupplierId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (CouponRewardSupplierIllegalValueOfMinimumCouponLifetimeException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.ILLEGAL_VALUE_OF_MINIMUM_COUPON_LIFETIME)
                .addParameter("minimum_coupon_lifetime", creationRequest.getMinimumCouponLifetime())
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
                .withCause(e)
                .build();
        } catch (RewardSupplierUnsupportedFaceValueAlgorithmTypeException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE)
                .addParameter("face_value_algorithm_type", e.getFaceValueAlgorithmType())
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
    public ManualCouponRewardSupplierV2Response update(String accessToken, String rewardSupplierId,
        ManualCouponRewardSupplierUpdateV2Request updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, BuildRewardSupplierRestException,
        CampaignComponentValidationRestException, RewardSupplierCreationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return updateRewardSupplier(rewardSupplierId, updateRequest, timeZone, userAuthorization);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (CouponRewardSupplierIllegalValueInWarningAmountException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.INVALID_COUPON_COUNT_WARN_LIMIT)
                .addParameter("coupon_count_warn_limit", updateRequest.getCouponCountWarnLimit())
                .withCause(e)
                .build();
        } catch (PartnerRewardSupplierIdTooLongException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_PARTNER_REWARD_SUPPLIER_ID)
                .addParameter("partner_reward_supplier_id", updateRequest.getPartnerRewardSupplierId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (CouponRewardSupplierIllegalValueOfMinimumCouponLifetimeException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.ILLEGAL_VALUE_OF_MINIMUM_COUPON_LIFETIME)
                .addParameter("minimum_coupon_lifetime", updateRequest.getMinimumCouponLifetime())
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
        } catch (RewardSupplierUnsupportedFaceValueAlgorithmTypeException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE)
                .addParameter("face_value_algorithm_type", e.getFaceValueAlgorithmType())
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
    public ManualCouponRewardSupplierOperationResponse uploadCoupons(String accessToken, String rewardSupplierId,
        ManualCouponRewardSupplierUploadCouponsRequest couponRequest,
        UploadCouponParams uploadCouponParams,
        ZoneId timeZone)
        throws UserAuthorizationRestException, ManualCouponRewardSupplierUploadCouponsRestException,
        RewardSupplierRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        List<Coupon> coupons = couponRequest.getCoupons()
            .stream()
            .map(couponCode -> createCoupon(couponCode))
            .collect(toList());

        return uploadCoupons(authorization, Id.valueOf(rewardSupplierId), coupons, Optional.empty(),
            uploadCouponParams, timeZone);
    }

    @Override
    public ManualCouponRewardSupplierOperationResponse uploadCoupons(String accessToken, String rewardSupplierId,
        InputStream inputStream, FormDataContentDisposition contentDispositionHeader,
        UploadCouponParams uploadCouponParams, Optional<ZonedDateTime> defaultExpiryDate, ZoneId timeZone)
        throws UserAuthorizationRestException, ManualCouponRewardSupplierUploadCouponsRestException,
        RewardSupplierRestException {
        if (inputStream == null || contentDispositionHeader == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Optional<String> fileName = Optional.ofNullable(contentDispositionHeader.getFileName());
            List<ManualCouponRequest> couponRequests =
                manualCouponFileReader.readCoupons(inputStream, fileName, timeZone);

            List<Coupon> coupons = createCouponsFromRequests(couponRequests, defaultExpiryDate);

            return uploadCoupons(authorization, Id.valueOf(rewardSupplierId), coupons, fileName, uploadCouponParams,
                timeZone);
        } catch (ManualCouponInvalidExpirationDateFormatException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.INVALID_EXPIRATION_DATE)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .addParameter("line_number", e.getLineNumber())
                .build();
        } catch (ManualCouponInvalidFileLineException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.INVALID_FILE_LINE)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .addParameter("line_number", e.getLineNumber())
                .build();
        } catch (ManualCouponFileReadException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.CORRUPTED_FILE)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .build();
        }
    }

    @Override
    public ManualCouponRewardSupplierOperationResponse deleteCoupons(String accessToken, String rewardSupplierId,
        String manualCouponOperationId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierOperationRestException,
        DeleteCouponsByRewardOperationRestException {

        if (StringUtils.isBlank(manualCouponOperationId)) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierOperationRestException.class)
                .withErrorCode(RewardSupplierOperationRestException.OPERATION_ID_MISSING)
                .build();
        }

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ManualCouponOperation manualCouponOperation =
                manualCouponOperationService.deleteCouponsByOperationId(authorization,
                    Id.valueOf(rewardSupplierId), Id.valueOf(manualCouponOperationId));

            return manualCouponOperationToResponse(manualCouponOperation, timeZone);
        } catch (CouponsConcurrentOperationException e) {
            throw RestExceptionBuilder.newBuilder(DeleteCouponsByRewardOperationRestException.class)
                .withErrorCode(DeleteCouponsByRewardOperationRestException.CONCURRENT_DELETE)
                .addParameter("client_id", e.getClientId())
                .addParameter("reward_supplier_id", e.getRewardSupplierId())
                .withCause(e)
                .build();
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (ManualCouponOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierOperationRestException.class)
                .withErrorCode(RewardSupplierOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", manualCouponOperationId)
                .withCause(e)
                .build();
        } catch (ManualCouponDeletionByInvalidOperationException e) {
            throw RestExceptionBuilder.newBuilder(DeleteCouponsByRewardOperationRestException.class)
                .withErrorCode(
                    DeleteCouponsByRewardOperationRestException.DELETE_COUPONS_BY_UNSUPPORTED_OPERATION)
                .addParameter("operation_id", manualCouponOperationId)
                .withCause(e)
                .build();
        } catch (ManualCouponAlreadyDeletedBySpecifiedOperationException e) {
            throw RestExceptionBuilder.newBuilder(DeleteCouponsByRewardOperationRestException.class)
                .withErrorCode(
                    DeleteCouponsByRewardOperationRestException.COUPONS_ALREADY_DELETED_BY_OPERATION)
                .addParameter("operation_id", manualCouponOperationId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    // CHECKSTYLE.OFF: ParameterNumber
    @Override
    public Response downloadCoupons(String accessToken, String rewardSupplierId, String extension, String operationId,
        Boolean issued, Boolean includeExpired, String limit, String offset, ZoneId timezone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierOperationRestException,
        QueryLimitsRestException, BuildRewardSupplierRestException {
        List<Coupon> coupons =
            getCoupons(accessToken, rewardSupplierId, Optional.ofNullable(operationId), Optional.ofNullable(issued),
                Optional.ofNullable(includeExpired), parseLimit(limit, DEFAULT_COUPONS_DOWNLOAD_LIMIT_FOR_FILE),
                parseOffset(offset, DEFAULT_COUPONS_DOWNLOAD_OFFSET));

        List<ManualCouponUploadResponse> uploadedCoupons = createCouponUploadResponses(coupons, timezone);
        try {
            return responseWithCouponsFile(
                getDefaultCouponDownloadFilename(getBuiltRewardSupplier(accessToken, rewardSupplierId)),
                extension, uploadedCoupons);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
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
    public ManualCouponRewardSupplierDownloadCouponsResponse downloadCoupons(String accessToken,
        String rewardSupplierId, String operationId, Boolean issued, Boolean includeExpired, String limit,
        String offset, ZoneId timeZone) throws UserAuthorizationRestException,
        RewardSupplierRestException, RewardSupplierOperationRestException, QueryLimitsRestException {
        List<Coupon> coupons =
            getCoupons(accessToken, rewardSupplierId, Optional.ofNullable(operationId), Optional.ofNullable(issued),
                Optional.ofNullable(includeExpired), parseLimit(limit, DEFAULT_COUPONS_DOWNLOAD_LIMIT_FOR_JSON),
                parseOffset(offset, DEFAULT_COUPONS_DOWNLOAD_OFFSET));
        List<ManualCouponUploadResponse> uploadedCoupons = createCouponUploadResponses(coupons, timeZone);
        return new ManualCouponRewardSupplierDownloadCouponsResponse(uploadedCoupons);
    }

    @Override
    public ManualCouponRewardSupplierV2Response archive(String accessToken, String rewardSupplierId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierArchiveRestException,
        CampaignComponentValidationRestException, BuildRewardSupplierRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ManualCouponRewardSupplier rewardSupplier =
                rewardSupplierService.archive(authorization,
                    rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId)));
            return rewardSupplierRestMapper.toManualCouponRewardSupplierResponse(rewardSupplier, timeZone);
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (RewardSupplierIllegalCashBackMinMaxLimitsException | RewardSupplierIllegalRateLimitsException
            | MoreThanOneComponentReferenceException e) {
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
        } catch (RewardSupplierValidationException e) {
            throw RestExceptionBuilder.newBuilder(BuildRewardSupplierRestException.class)
                .withErrorCode(BuildRewardSupplierRestException.REWARD_SUPPLIER_VALIDATION_FAILED)
                .withCause(e)
                .build();
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
    public List<ManualCouponRewardSupplierOperationResponse> getOperations(String accessToken, String rewardSupplierId,
        String filename,
        String limit,
        String offset,
        ZoneId timeZone)
        throws RewardSupplierRestException, UserAuthorizationRestException, QueryLimitsRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            ManualCouponOperationQueryBuilder operationQueryBuilder =
                manualCouponOperationService.createOperationQueryBuilder(userAuthorization,
                    Id.valueOf(rewardSupplierId));

            if (StringUtils.isNotBlank(filename)) {
                operationQueryBuilder.withFilename(filename);
            }

            return operationQueryBuilder
                .withLimit(parseLimit(limit, DEFAULT_OPERATIONS_QUERY_LIMIT))
                .withOffset(parseOffset(offset, DEFAULT_OPERATIONS_QUERY_OFFSET))
                .list()
                .stream()
                .map(manualCouponOperation -> manualCouponOperationToResponse(manualCouponOperation, timeZone))
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

    private ManualCouponRewardSupplierOperationResponse uploadCoupons(ClientAuthorization authorization,
        Id<RewardSupplier> rewardSupplierId, List<Coupon> coupons, Optional<String> filename,
        UploadCouponParams uploadCouponParams, ZoneId timeZone)
        throws UserAuthorizationRestException, ManualCouponRewardSupplierUploadCouponsRestException,
        RewardSupplierRestException {

        try {
            ManualCouponOperation manualCouponOperation;
            if (filename.isPresent()) {
                manualCouponOperation =
                    manualCouponOperationService.uploadCoupons(authorization, rewardSupplierId, coupons,
                        filename.get(), uploadCouponParams.isAllowRestrictedCharacters(),
                        uploadCouponParams.isAllowExpired(), uploadCouponParams.isDiscardDuplicated());
            } else {
                manualCouponOperation =
                    manualCouponOperationService.uploadCoupons(authorization, rewardSupplierId,
                        coupons, uploadCouponParams.isAllowRestrictedCharacters(), uploadCouponParams.isAllowExpired(),
                        uploadCouponParams.isDiscardDuplicated());
            }
            return manualCouponOperationToResponse(manualCouponOperation, timeZone);
        } catch (CouponsConcurrentOperationException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.CONCURRENT_UPLOAD)
                .addParameter("client_id", e.getClientId())
                .addParameter("reward_supplier_id", e.getRewardSupplierId())
                .build();
        } catch (CouponFilenameEmptyException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.COUPON_FILENAME_EMPTY)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .build();
        } catch (CouponFilenameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.COUPON_FILENAME_TOO_LONG)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .addParameter("filename", e.getFilename())
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (CouponsMissingException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.COUPONS_MISSING)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .build();
        } catch (CouponCodeBlankException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.COUPON_CODE_BLANK)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .addParameter("line_number", e.getIndex())
                .build();
        } catch (CouponCodeTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.COUPON_CODE_TOO_LONG)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .addParameter("coupon_code", e.getCouponCode())
                .build();
        } catch (CouponCodeInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.COUPON_CODE_INVALID)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .addParameter("coupon_code", e.getCouponCode())
                .build();
        } catch (ExistingCouponCodeException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.EXISTING_COUPON_CODE)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .addParameter("coupon_code", e.getCouponCode())
                .build();
        } catch (DuplicateCouponCodeInListException e) {
            throw RestExceptionBuilder.newBuilder(ManualCouponRewardSupplierUploadCouponsRestException.class)
                .withErrorCode(ManualCouponRewardSupplierUploadCouponsRestException.DUPLICATE_COUPON_CODE_IN_LIST)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .addParameter("coupon_code", e.getCouponCode())
                .build();
        }
    }

    private List<Coupon> getCoupons(String accessToken, String rewardSupplierId, Optional<String> operationId,
        Optional<Boolean> issued, Optional<Boolean> includeExpired, Integer limit, Integer offset)
        throws UserAuthorizationRestException, RewardSupplierRestException, RewardSupplierOperationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ManualCouponQueryBuilder manualCouponQueryBuilder;

            manualCouponQueryBuilder = manualCouponOperationService.createCouponQueryBuilder(userAuthorization,
                Id.valueOf(rewardSupplierId));
            if (operationId.isPresent()) {
                Id<ManualCouponOperation> manualCouponOperationId = Id.valueOf(operationId.get());
                manualCouponQueryBuilder.withOperationId(manualCouponOperationId);
            }
            issued.ifPresent(manualCouponQueryBuilder::withIssued);
            includeExpired.ifPresent(ie -> manualCouponQueryBuilder.withIncludeExpired(ie.booleanValue()));
            manualCouponQueryBuilder.withLimit(limit.intValue())
                .withOffset(offset.intValue());

            return manualCouponQueryBuilder.list();
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierRestException.class)
                .withErrorCode(RewardSupplierRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", rewardSupplierId)
                .withCause(e)
                .build();
        } catch (ManualCouponOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierOperationRestException.class)
                .withErrorCode(RewardSupplierOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", operationId)
                .withCause(e)
                .build();
        } catch (ManualCouponOperationIllegalTypeException e) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierOperationRestException.class)
                .withErrorCode(RewardSupplierOperationRestException.NOT_AN_UPLOAD_OPERATION)
                .addParameter("operation_id", operationId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private String getDefaultCouponDownloadFilename(BuiltRewardSupplier rewardSupplier) {
        String normalizedName = rewardSupplier.getName().trim().replaceAll(WHITESPACES, DASH);
        String formattedDate = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        return normalizedName + DASH + formattedDate;
    }

    private Response responseWithCouponsFile(String fileName, String extension,
        List<ManualCouponUploadResponse> coupons) {
        try (FileBackedOutputStream fileOutputStream =
            new FileBackedOutputStream(FILE_BACKED_OUTPUT_STREAM_THRESHOLD, true)) {
            for (ManualCouponUploadResponse couponUploadResponse : coupons) {
                fileOutputStream.write(couponUploadResponse.getCouponCode().getBytes());
                if (couponUploadResponse.getExpiresAt().isPresent()) {
                    fileOutputStream.write(CSV_DELIMITER);
                    fileOutputStream.write(couponUploadResponse.getExpiresAt().get().toString().getBytes());
                }

                fileOutputStream.write(NEW_LINE);
            }

            return Response.ok((StreamingOutput) outputStream -> fileOutputStream.asByteSource().copyTo(outputStream))
                .type("application/octet-stream")
                .header("Content-Disposition", "attachment; filename=" + fileName + extension).build();
        } catch (IOException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private ManualCouponRewardSupplierOperationResponse
        manualCouponOperationToResponse(ManualCouponOperation manualCouponOperation, ZoneId timezone) {
        List<Coupon> coupons = manualCouponOperation.getCoupons();
        List<String> couponCodes = getCouponCodes(coupons);
        List<ManualCouponUploadResponse> uploadedCoupons = createCouponUploadResponses(coupons, timezone);

        return new ManualCouponRewardSupplierOperationResponse(manualCouponOperation.getId().getValue(),
            manualCouponOperation.getRewardSupplierId().getValue(),
            ManualCouponOperationStatus.valueOf(manualCouponOperation.getStatus().name()),
            manualCouponOperation.getRequestMessage(),
            manualCouponOperation.getResultMessage(),
            ManualCouponOperationType.valueOf(manualCouponOperation.getOperationType().name()),
            manualCouponOperation.getFilename().orElse(null),
            manualCouponOperation.getNumberOfCoupons(),
            manualCouponOperation.getCreatedAt().atZone(timezone),
            couponCodes,
            uploadedCoupons);
    }

    private List<ManualCouponUploadResponse> createCouponUploadResponses(List<Coupon> coupons,
        ZoneId timezone) {
        return coupons.stream()
            .map(coupon -> createCouponUploadResponse(coupon, timezone))
            .collect(Collectors.toList());
    }

    private ManualCouponRewardSupplierV2Response updateRewardSupplier(String rewardSupplierId,
        ManualCouponRewardSupplierUpdateV2Request updateRequest, ZoneId timeZone, Authorization authorization)
        throws RewardSupplierNotFoundException, AuthorizationException, BuildRewardSupplierException,
        PartnerRewardSupplierIdTooLongException, CouponRewardSupplierIllegalValueInWarningAmountException,
        CouponRewardSupplierIllegalValueOfMinimumCouponLifetimeException, CampaignComponentValidationRestException,
        InvalidComponentReferenceException, RewardSupplierValidationException, RewardSupplierInvalidTagException,
        MoreThanOneComponentReferenceException {

        ManualCouponRewardSupplierBuilder rewardSupplierBuilder = rewardSupplierService.update(authorization,
            rewardSupplierService.findById(authorization, Id.valueOf(rewardSupplierId)));
        rewardSupplierBuilder.withCashBackEnabled(cashBackEnabled);

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
        updateRequest.getCouponCountWarnLimit()
            .ifPresent(couponCountWarnLimit -> rewardSupplierBuilder.withCouponCountWarnLimit(couponCountWarnLimit));
        updateRequest.getMinimumCouponLifetime()
            .ifPresent(minimumCouponLifetime -> rewardSupplierBuilder.withMinimumCouponLifetime(minimumCouponLifetime));
        updateRequest.getDefaultCouponExpiryDate()
            .ifPresent(defaultCouponExpiryDate -> rewardSupplierBuilder
                .withDefaultCouponExpiryDate(defaultCouponExpiryDate.toInstant()));
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

        ManualCouponRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toManualCouponRewardSupplierResponse(rewardSupplier, timeZone);
    }

    private static Map<RewardState, List<RewardState>> mapStateTransitions(
        Map<com.extole.client.rest.reward.supplier.RewardState,
            List<com.extole.client.rest.reward.supplier.RewardState>> stateTransitions) {
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

    private ManualCouponRewardSupplierV2Response createRewardSupplier(
        ManualCouponRewardSupplierCreationV2Request creationRequest, ZoneId timeZone, Authorization authorization)
        throws RewardSupplierCreationRestException, AuthorizationException,
        CouponRewardSupplierIllegalValueInWarningAmountException, BuildRewardSupplierException,
        PartnerRewardSupplierIdTooLongException, CouponRewardSupplierIllegalValueOfMinimumCouponLifetimeException,
        CampaignComponentValidationRestException, InvalidComponentReferenceException, RewardSupplierValidationException,
        RewardSupplierInvalidTagException, MoreThanOneComponentReferenceException {

        if (creationRequest.getCouponCountWarnLimit() == null) {
            throw RestExceptionBuilder.newBuilder(RewardSupplierCreationRestException.class)
                .withErrorCode(RewardSupplierCreationRestException.MISSING_COUPON_COUNT_WARN_LIMIT)
                .build();
        }

        ManualCouponRewardSupplierBuilder rewardSupplierBuilder =
            rewardSupplierService.create(authorization, RewardSupplierType.MANUAL_COUPON,
                rewardSupplierRestMapper.toFaceValueType(creationRequest.getFaceValueType()));
        rewardSupplierBuilder
            .withCouponCountWarnLimit(creationRequest.getCouponCountWarnLimit().intValue())
            .withCashBackEnabled(cashBackEnabled);

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
        creationRequest.getDisplayType()
            .ifPresent(displayType -> rewardSupplierBuilder.withDisplayType(displayType));
        creationRequest.getMinimumCouponLifetime()
            .ifPresent(minimumCouponLifetime -> rewardSupplierBuilder.withMinimumCouponLifetime(minimumCouponLifetime));
        creationRequest.getDefaultCouponExpiryDate()
            .ifPresent(defaultCouponExpiryTime -> rewardSupplierBuilder
                .withDefaultCouponExpiryDate(defaultCouponExpiryTime.toInstant()));
        creationRequest.getLimitPerDay()
            .ifPresent(limitPerDay -> rewardSupplierBuilder.withLimitPerDay(limitPerDay));
        creationRequest.getLimitPerHour()
            .ifPresent(limitPerHour -> rewardSupplierBuilder.withLimitPerHour(limitPerHour));
        creationRequest.getData().ifPresent(data -> rewardSupplierBuilder.withData(data));
        creationRequest.getEnabled().ifPresent(enabled -> rewardSupplierBuilder.withEnabled(enabled));

        creationRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(rewardSupplierBuilder, componentIds);
        });

        creationRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(rewardSupplierBuilder, componentReferences);
        });

        creationRequest.getTags().ifPresent(tags -> rewardSupplierBuilder.withTags(tags));

        ManualCouponRewardSupplier rewardSupplier =
            rewardSupplierBuilder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
        return rewardSupplierRestMapper.toManualCouponRewardSupplierResponse(rewardSupplier, timeZone);
    }

    private ManualCouponUploadResponse createCouponUploadResponse(Coupon coupon, ZoneId timezone) {
        ManualCouponUploadResponse.Builder builder = ManualCouponUploadResponse
            .builder()
            .withCouponCode(coupon.getCode());
        coupon.getExpiryDate()
            .map(expiresAt -> expiresAt.atZone(timezone))
            .map(expiresAt -> builder.withExpiresAt(expiresAt));

        return builder.build();
    }

    private List<String> getCouponCodes(List<Coupon> coupons) {
        return coupons.stream().map(Coupon::getCode).collect(toList());
    }

    private Coupon createCoupon(String couponCode) {
        CouponBuilder couponBuilder = serviceLocator.create(CouponBuilder.class);

        return couponBuilder.withCouponCode(couponCode).build();
    }

    private List<Coupon> createCouponsFromRequests(List<ManualCouponRequest> couponRequests,
        Optional<ZonedDateTime> defaultExpiryDate) {
        List<Coupon> coupons = new ArrayList<>(couponRequests.size());
        for (ManualCouponRequest couponRequest : couponRequests) {
            Coupon coupon = createCouponFromRequest(couponRequest, defaultExpiryDate);
            coupons.add(coupon);
        }
        return coupons;
    }

    private Coupon createCouponFromRequest(ManualCouponRequest request, Optional<ZonedDateTime> defaultExpiryDate) {
        CouponBuilder couponBuilder = serviceLocator.create(CouponBuilder.class);
        couponBuilder.withCouponCode(request.getCouponCode());

        Optional<ZonedDateTime> expiryDate =
            request.getExpiresAt().isPresent() ? request.getExpiresAt() : defaultExpiryDate;
        expiryDate.ifPresent(zonedDateTime -> couponBuilder.withExpiresAt(zonedDateTime.toInstant()));

        return couponBuilder.build();
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
