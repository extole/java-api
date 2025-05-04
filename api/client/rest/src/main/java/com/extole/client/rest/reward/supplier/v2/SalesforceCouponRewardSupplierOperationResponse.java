package com.extole.client.rest.reward.supplier.v2;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SalesforceCouponRewardSupplierOperationResponse {

    private static final String ID = "id";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String STATUS = "status";
    private static final String REQUEST_MESSAGE = "request_message";
    private static final String RESULT_MESSAGE = "result_message";
    private static final String OPERATION_TYPE = "operation_type";
    private static final String OFFSET = "offset";
    private static final String NUMBER_OF_REQUESTED_COUPONS = "number_of_requested_coupons";
    private static final String NUMBER_OF_RECEIVED_COUPONS = "number_of_received_coupons";
    private static final String CREATED_AT = "created_at";

    private final String id;
    private final String rewardSupplierId;
    private final SalesforceCouponRewardSupplierOperationStatus status;
    private final String requestMessage;
    private final String resultMessage;
    private final SalesforceCouponRewardSupplierOperationType operationType;
    private final Integer offset;
    private final Integer numberOfRequestedCoupons;
    private final Integer numberOfReceivedCoupons;
    private final ZonedDateTime createdAt;

    public SalesforceCouponRewardSupplierOperationResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(REWARD_SUPPLIER_ID) String rewardSupplierId,
        @JsonProperty(STATUS) SalesforceCouponRewardSupplierOperationStatus status,
        @JsonProperty(REQUEST_MESSAGE) String requestMessage,
        @JsonProperty(RESULT_MESSAGE) String resultMessage,
        @JsonProperty(OPERATION_TYPE) SalesforceCouponRewardSupplierOperationType operationType,
        @JsonProperty(OFFSET) Integer offset,
        @JsonProperty(NUMBER_OF_REQUESTED_COUPONS) Integer numberOfRequestedCoupons,
        @JsonProperty(NUMBER_OF_RECEIVED_COUPONS) Integer numberOfReceivedCoupons,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt) {
        this.id = id;
        this.rewardSupplierId = rewardSupplierId;
        this.status = status;
        this.requestMessage = requestMessage;
        this.resultMessage = resultMessage;
        this.operationType = operationType;
        this.offset = offset;
        this.numberOfRequestedCoupons = numberOfRequestedCoupons;
        this.numberOfReceivedCoupons = numberOfReceivedCoupons;
        this.createdAt = createdAt;
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
    public SalesforceCouponRewardSupplierOperationStatus getStatus() {
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
    public SalesforceCouponRewardSupplierOperationType getOperationType() {
        return operationType;
    }

    @JsonProperty(OFFSET)
    public Integer getOffset() {
        return offset;
    }

    @JsonProperty(NUMBER_OF_REQUESTED_COUPONS)
    public Integer getNumberOfRequestedCoupons() {
        return numberOfRequestedCoupons;
    }

    @JsonProperty(NUMBER_OF_RECEIVED_COUPONS)
    public Integer getNumberOfReceivedCoupons() {
        return numberOfReceivedCoupons;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
