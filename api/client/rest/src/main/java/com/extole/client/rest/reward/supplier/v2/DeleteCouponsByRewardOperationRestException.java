package com.extole.client.rest.reward.supplier.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class DeleteCouponsByRewardOperationRestException extends ExtoleRestException {

    public static final ErrorCode<DeleteCouponsByRewardOperationRestException> CONCURRENT_DELETE =
        new ErrorCode<>("coupons_concurrent_delete", 400,
            "Could not perform the operation. Coupons are currently deleted by other process/user",
            "client_id", "reward_supplier_id");

    public static final ErrorCode<DeleteCouponsByRewardOperationRestException> DELETE_COUPONS_BY_UNSUPPORTED_OPERATION =
        new ErrorCode<>("delete_coupons_by_operation_not_supported", 400,
            "Coupons cannot be deleted by this operation", "operation_id");

    public static final ErrorCode<DeleteCouponsByRewardOperationRestException> COUPONS_ALREADY_DELETED_BY_OPERATION =
        new ErrorCode<>("coupons_already_deleted_by_operation", 400,
            "Coupons already deleted by this operation", "operation_id");

    public DeleteCouponsByRewardOperationRestException(String uniqueId,
        ErrorCode<DeleteCouponsByRewardOperationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
