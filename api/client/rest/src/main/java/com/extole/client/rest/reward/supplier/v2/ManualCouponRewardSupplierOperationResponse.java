package com.extole.client.rest.reward.supplier.v2;

import java.time.ZonedDateTime;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ManualCouponRewardSupplierOperationResponse {

    private static final String ID = "id";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String STATUS = "status";
    private static final String REQUEST_MESSAGE = "request_message";
    private static final String RESULT_MESSAGE = "result_message";
    private static final String OPERATION_TYPE = "operation_type";
    private static final String NUMBER_OF_COUPONS_TO_UPLOAD = "number_of_coupons_to_upload";
    private static final String FILENAME = "filename";
    private static final String CREATED_AT = "created_at";
    // TODO remove deprecated field ENG-12921
    private static final String COUPONS = "coupons";
    private static final String UPLOADED_COUPONS = "uploaded_coupons";

    private final String id;
    private final String rewardSupplierId;
    private final ManualCouponOperationStatus status;
    private final String requestMessage;
    private final String resultMessage;
    private final ManualCouponOperationType operationType;
    private final String filename;
    private final Integer numberOfCouponsToUpload;
    private final ZonedDateTime createdAt;
    private final List<String> coupons;
    private final List<ManualCouponUploadResponse> uploadedCoupons;

    public ManualCouponRewardSupplierOperationResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(REWARD_SUPPLIER_ID) String rewardSupplierId,
        @JsonProperty(STATUS) ManualCouponOperationStatus status,
        @JsonProperty(REQUEST_MESSAGE) String requestMessage,
        @JsonProperty(RESULT_MESSAGE) String resultMessage,
        @JsonProperty(OPERATION_TYPE) ManualCouponOperationType operationType,
        @Nullable @JsonProperty(FILENAME) String filename,
        @JsonProperty(NUMBER_OF_COUPONS_TO_UPLOAD) Integer numberOfCouponsToUpload,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @Deprecated // TODO remove deprecated field ENG-12921
        @JsonProperty(COUPONS) List<String> coupons,
        @JsonProperty(UPLOADED_COUPONS) List<ManualCouponUploadResponse> uploadedCoupons) {
        this.id = id;
        this.rewardSupplierId = rewardSupplierId;
        this.status = status;
        this.requestMessage = requestMessage;
        this.resultMessage = resultMessage;
        this.operationType = operationType;
        this.filename = filename;
        this.numberOfCouponsToUpload = numberOfCouponsToUpload;
        this.createdAt = createdAt;
        this.coupons = coupons;
        this.uploadedCoupons = uploadedCoupons;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @JsonProperty(STATUS)
    public ManualCouponOperationStatus getStatus() {
        return status;
    }

    @JsonProperty(REQUEST_MESSAGE)
    public String getRequestMessage() {
        return requestMessage;
    }

    @JsonProperty(RESULT_MESSAGE)
    public String getResultMessage() {
        return resultMessage;
    }

    @JsonProperty(OPERATION_TYPE)
    public ManualCouponOperationType getOperationType() {
        return operationType;
    }

    @JsonProperty(NUMBER_OF_COUPONS_TO_UPLOAD)
    public Integer getNumberOfCouponsToUpload() {
        return numberOfCouponsToUpload;
    }

    @Nullable
    @JsonProperty(FILENAME)
    public String getFilename() {
        return filename;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Deprecated // TODO remove deprecated field ENG-12921
    @JsonProperty(COUPONS)
    public List<String> getCoupons() {
        return coupons;
    }

    @JsonProperty(UPLOADED_COUPONS)
    public List<ManualCouponUploadResponse> getUploadedCoupons() {
        return uploadedCoupons;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
