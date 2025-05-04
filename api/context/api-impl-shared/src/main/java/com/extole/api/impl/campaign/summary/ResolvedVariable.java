package com.extole.api.impl.campaign.summary;

import javax.annotation.Nullable;

public interface ResolvedVariable {

    ResolvedVariable RUNTIME = () -> null;

    @Nullable
    Object get();

}
