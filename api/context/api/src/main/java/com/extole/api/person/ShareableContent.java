package com.extole.api.person;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ShareableContent {

    @Nullable
    String getPartnerContentId();

    @Nullable
    String getTitle();

    @Nullable
    String getImageUrl();

    @Nullable
    String getDescription();

    @Nullable
    String getUrl();

}
