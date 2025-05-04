package com.extole.consumer.rest.barcode;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BarcodeRestException extends ExtoleRestException {

    public static final ErrorCode<BarcodeRestException> INVALID_HEIGHT =
        new ErrorCode<>("invalid_barcode_height", 400, "Height must be a positive number", "height");

    public static final ErrorCode<BarcodeRestException> INVALID_WIDTH =
        new ErrorCode<>("invalid_barcode_width", 400, "Width must be a positive number", "width");

    public static final ErrorCode<BarcodeRestException> INVALID_MARGIN_WIDTH =
        new ErrorCode<>("invalid_barcode_margin_width", 400, "Margin width must be a non negative number",
            "margin_width");

    public static final ErrorCode<BarcodeRestException> INVALID_CONTENT =
        new ErrorCode<>("invalid_barcode_content", 400, "The provided barcode content is not valid");

    public static final ErrorCode<BarcodeRestException> MISSING_TYPE =
        new ErrorCode<>("missing_type", 400, "Type is missing");

    public BarcodeRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
