package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface DynamicAudienceList extends AudienceList {
    String getReportRunnerId();
}
