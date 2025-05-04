package com.extole.api.model;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ExternalSftpDestination extends EventEntity {

    String getKeyId();

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
