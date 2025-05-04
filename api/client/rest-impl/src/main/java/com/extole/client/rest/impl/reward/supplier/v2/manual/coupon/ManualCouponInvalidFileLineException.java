package com.extole.client.rest.impl.reward.supplier.v2.manual.coupon;

public class ManualCouponInvalidFileLineException extends ManualCouponFileParsingException {

    private final long lineNumber;

    public ManualCouponInvalidFileLineException(String message, long lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public ManualCouponInvalidFileLineException(String message, Throwable cause, long lineNumber) {
        super(message, cause);
        this.lineNumber = lineNumber;
    }

    public long getLineNumber() {
        return lineNumber;
    }
}
