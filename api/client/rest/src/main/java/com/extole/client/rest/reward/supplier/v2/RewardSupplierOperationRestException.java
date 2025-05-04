package com.extole.client.rest.reward.supplier.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardSupplierOperationRestException extends ExtoleRestException {

    public static final ErrorCode<RewardSupplierOperationRestException> OPERATION_ID_MISSING =
        new ErrorCode<>("operation_id_missing", 400, "Query parameter operation_id is missing");

    public static final ErrorCode<RewardSupplierOperationRestException> OPERATION_NOT_FOUND =
        new ErrorCode<>("operation_not_found", 400, "Operation not found", "operation_id");

    public static final ErrorCode<RewardSupplierOperationRestException> NOT_AN_UPLOAD_OPERATION =
        new ErrorCode<>("not_an_upload_operation", 400, "Not an upload operation", "operation_id");

    public RewardSupplierOperationRestException(String uniqueId,
        ErrorCode<RewardSupplierOperationRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
