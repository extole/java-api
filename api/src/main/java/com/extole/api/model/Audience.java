package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.audience.AudienceBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

@Schema
public interface Audience extends EventEntity {
    String getId();

    BuildtimeEvaluatable<AudienceBuildtimeContext, String> getName();

    String getCreatedDate();

    String getUpdatedDate();
}
