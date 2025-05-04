package com.extole.api.model.campaign.built;

import java.util.Map;

import javax.annotation.Nullable;

import com.extole.evaluateable.RuntimeEvaluatable;

public interface BuiltVariable {

    String getName();

    @Nullable
    String getDisplayName();

    String getType();

    Map<String, RuntimeEvaluatable<Object, Object>> getSourcedValues();

    String getSource();

    @Nullable
    String getDescription();

    String[] getTags();
}
