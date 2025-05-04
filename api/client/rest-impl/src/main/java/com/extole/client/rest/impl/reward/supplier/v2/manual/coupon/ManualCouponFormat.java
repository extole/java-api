package com.extole.client.rest.impl.reward.supplier.v2.manual.coupon;

import java.util.Arrays;
import java.util.Optional;

public enum ManualCouponFormat {
    TEXT("text/plain", "txt"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    CSV("text/csv", "csv");

    private final String mimeType;
    private final String extension;

    ManualCouponFormat(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public static ManualCouponFormat fromExtension(String extension) {
        Optional<ManualCouponFormat> format =
            Arrays.stream(ManualCouponFormat.values())
                .filter(formatEntry -> formatEntry.extension.equalsIgnoreCase(extension))
                .findAny();
        return format.orElseThrow(
            () -> new IllegalArgumentException("Could not determine format from extension " + extension));
    }
}
