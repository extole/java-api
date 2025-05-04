package com.extole.client.rest.tango;

import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum TangoAccountStatus {
    ACTIVE, INACTIVE, UNSUPPORTED;

    public static TangoAccountStatus lookup(String status) {
        try {
            return TangoAccountStatus.valueOf(Strings.nullToEmpty(status).toUpperCase());
        } catch (Exception e) {
            return TangoAccountStatus.UNSUPPORTED;
        }
    }
}
