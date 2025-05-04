package com.extole.api.event.asset;

import java.util.List;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Asset {

    enum Type {
        PUBLIC, PRIVATE
    }

    enum Status {
        PENDING_REVIEW, APPROVED, DENIED
    }

    String getId();

    String getName();

    String getFilename();

    String getMimeType();

    Long getSize();

    String getStatus();

    List<String> getTags();

    String getType();

    String getCreatedDate();

    String getUpdatedDate();

    @Nullable
    String getDeletedDate();

}
