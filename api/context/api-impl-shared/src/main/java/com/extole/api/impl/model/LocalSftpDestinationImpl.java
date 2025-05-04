package com.extole.api.impl.model;

import javax.annotation.Nullable;

import com.extole.api.model.LocalSftpDestination;
import com.extole.common.lang.ToString;
import com.extole.event.model.change.client.sftp.LocalSftpDestinationPojo;

final class LocalSftpDestinationImpl implements LocalSftpDestination {

    private final LocalSftpDestinationPojo localSftpDestination;
    private final String[] keyIds;

    LocalSftpDestinationImpl(LocalSftpDestinationPojo localSftpDestination) {
        this.localSftpDestination = localSftpDestination;
        this.keyIds =
            localSftpDestination.getKeyIds().stream().map(keyId -> keyId.getValue()).toArray(String[]::new);
    }

    @Override
    public boolean isFileProcessingEnabled() {
        return localSftpDestination.isFileProcessingEnabled();
    }

    @Override
    public String[] getKeyIds() {
        return keyIds;
    }

    @Override
    public String getId() {
        return localSftpDestination.getId().getValue();
    }

    @Override
    public String getName() {
        return localSftpDestination.getName();
    }

    @Override
    public String getUsername() {
        return localSftpDestination.getUsername();
    }

    @Nullable
    @Override
    public String getPartnerKeyId() {
        return localSftpDestination.getPartnerKeyId().map(value -> value.getValue()).orElse(null);
    }

    @Override
    public String getExtoleKeyId() {
        return localSftpDestination.getExtoleKeyId().getValue();
    }

    @Override
    public String getDropboxPath() {
        return localSftpDestination.getDropboxPath();
    }

    @Override
    public String getHost() {
        return localSftpDestination.getHost();
    }

    @Override
    public int getPort() {
        return localSftpDestination.getPort();
    }

    @Override
    public String getCreatedDate() {
        return localSftpDestination.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return localSftpDestination.getUpdatedDate().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
