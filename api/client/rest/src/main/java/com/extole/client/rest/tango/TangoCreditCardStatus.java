package com.extole.client.rest.tango;

import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum TangoCreditCardStatus {
    ACTIVE, DELETED, UNSUPPORTED;

    public static TangoCreditCardStatus parse(String status) {
        status = Strings.nullToEmpty(status);
        try {
            return TangoCreditCardStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return TangoCreditCardStatus.UNSUPPORTED;
        }
    }
}
