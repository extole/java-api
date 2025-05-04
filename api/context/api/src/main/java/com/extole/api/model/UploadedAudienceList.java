package com.extole.api.model;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface UploadedAudienceList extends AudienceList {
    String getFileAssetId();

    @Nullable
    String getAudienceId();
}
