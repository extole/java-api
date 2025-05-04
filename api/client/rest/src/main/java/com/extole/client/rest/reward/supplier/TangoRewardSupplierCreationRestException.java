package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TangoRewardSupplierCreationRestException extends ExtoleRestException {

    public static final ErrorCode<TangoRewardSupplierCreationRestException> UTID_MISSING = new ErrorCode<>(
        "utid_missing", 403, "utid is a required attribute");

    public static final ErrorCode<TangoRewardSupplierCreationRestException> ACCOUNT_ID_MISSING = new ErrorCode<>(
        "account_id_missing", 403, "account_id is a required attribute");

    public static final ErrorCode<TangoRewardSupplierCreationRestException> CATALOG_ITEM_NOT_FOUND = new ErrorCode<>(
        "catalog_item_not_found", 400, "unable to find catalog item", "utid");

    public static final ErrorCode<TangoRewardSupplierCreationRestException> ACCOUNT_NOT_FOUND = new ErrorCode<>(
        "account_not_found", 400, "unable to find account", "account_id");

    public TangoRewardSupplierCreationRestException(String uniqueId,
        ErrorCode<RewardSupplierCreationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
