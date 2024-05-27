package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface StaticAudienceList extends AudienceList {
    String getReportId();
}
