package com.extole.client.rest.reward.supplier.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ManualCouponRewardSupplierUploadCouponsRestException extends ExtoleRestException {

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> CONCURRENT_UPLOAD =
        new ErrorCode<>("coupons_concurrent_upload", 400,
            "Could not perform the operation. Coupons are currently updated by other process/user",
            "client_id", "reward_supplier_id");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> COUPONS_MISSING =
        new ErrorCode<>("coupons_missing", 400, "Missing coupons", "reward_supplier_id");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> COUPON_CODE_BLANK =
        new ErrorCode<>("coupon_code_blank", 400, "At least one of the coupon codes was blank", "reward_supplier_id",
            "line_number");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> COUPON_CODE_TOO_LONG =
        new ErrorCode<>("coupon_code_too_long", 400, "At least one of the coupon codes was longer than 255 characters",
            "reward_supplier_id", "coupon_code");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> COUPON_CODE_INVALID =
        new ErrorCode<>("coupon_code_invalid", 400, "A coupon code is invalid", "reward_supplier_id", "coupon_code");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> EXISTING_COUPON_CODE =
        new ErrorCode<>("existing_coupon_code", 400, "A coupon with the same code already exists",
            "reward_supplier_id", "coupon_code");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> DUPLICATE_COUPON_CODE_IN_LIST =
        new ErrorCode<>("duplicate_coupon_code_in_list", 400, "Duplicate coupon code in the list of coupons",
            "reward_supplier_id", "coupon_code");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> CORRUPTED_FILE =
        new ErrorCode<>("corrupted_uploaded_file", 400,
            "Uploaded file is empty/corrupted or is other format than csv, txt or xlsx",
            "reward_supplier_id");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> INVALID_FILE_LINE =
        new ErrorCode<>("invalid_file_line", 400,
            "At least one line had more than one value/was not properly formatted",
            "reward_supplier_id", "line_number");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> COUPON_FILENAME_EMPTY =
        new ErrorCode<>("coupon_filename_empty", 400, "Coupon filename is empty.",
            "reward_supplier_id");

    public static final ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> COUPON_FILENAME_TOO_LONG =
        new ErrorCode<>("coupon_filename_empty", 400, "Coupon filename is too long.",
            "reward_supplier_id", "filename");

    public ManualCouponRewardSupplierUploadCouponsRestException(String uniqueId,
        ErrorCode<ManualCouponRewardSupplierUploadCouponsRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
