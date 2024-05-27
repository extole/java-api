package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Component {

    String getName();

    String getDisplayName();

    @Nullable
    String getDescription();

    ElementsQueryBuilder createElementsQuery();

}
