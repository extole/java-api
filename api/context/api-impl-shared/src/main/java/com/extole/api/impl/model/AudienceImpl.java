package com.extole.api.impl.model;

import com.extole.api.audience.AudienceBuildtimeContext;
import com.extole.api.model.Audience;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.event.model.change.audience.AudiencePojo;

final class AudienceImpl implements Audience {
    private final AudiencePojo audience;

    AudienceImpl(AudiencePojo audience) {
        this.audience = audience;
    }

    @Override
    public String getId() {
        return audience.getId().getValue();
    }

    @Override
    public BuildtimeEvaluatable<AudienceBuildtimeContext, String> getName() {
        return audience.getName();
    }

    @Override
    public String getCreatedDate() {
        return audience.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return audience.getUpdatedDate().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
