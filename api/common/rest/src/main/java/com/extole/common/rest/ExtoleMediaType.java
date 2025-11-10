package com.extole.common.rest;

import javax.ws.rs.core.MediaType;

public final class ExtoleMediaType extends MediaType {
    private ExtoleMediaType() {
    }

    public static final String TEXT_CSV = "text/csv";

    public static final String APPLICATION_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

}
