package com.extole.api.model;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface LocalSftpDestination extends EventEntity {

    boolean isFileProcessingEnabled();

    String[] getKeyIds();

    String getId();

    String getName();

    String getUsername();

    @Nullable
    String getPartnerKeyId();

    String getExtoleKeyId();

    String getDropboxPath();

    String getHost();

    int getPort();

    String getCreatedDate();

    String getUpdatedDate();

}
