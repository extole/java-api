package com.extole.api.impl.model;

import javax.annotation.Nullable;

import com.extole.api.model.ExternalSftpDestination;
import com.extole.common.lang.ToString;
import com.extole.event.model.change.client.sftp.ExternalSftpDestinationPojo;

final class ExternalSftpDestinationImpl implements ExternalSftpDestination {

    private final ExternalSftpDestinationPojo externalSftpDestination;

    ExternalSftpDestinationImpl(ExternalSftpDestinationPojo externalSftpDestination) {
        this.externalSftpDestination = externalSftpDestination;
    }

    @Override
    public String getKeyId() {
        return externalSftpDestination.getKeyId().getValue();
    }

    @Override
    public String getId() {
        return externalSftpDestination.getId().getValue();
    }

    @Override
    public String getName() {
        return externalSftpDestination.getName();
    }

    @Override
    public String getUsername() {
        return externalSftpDestination.getUsername();
    }

    @Nullable
    @Override
    public String getPartnerKeyId() {
        return externalSftpDestination.getPartnerKeyId().map(value -> value.getValue()).orElse(null);
    }

    @Override
    public String getExtoleKeyId() {
        return externalSftpDestination.getExtoleKeyId().getValue();
    }

    @Override
    public String getDropboxPath() {
        return externalSftpDestination.getDropboxPath();
    }

    @Override
    public String getHost() {
        return externalSftpDestination.getHost();
    }

    @Override
    public int getPort() {
        return externalSftpDestination.getPort();
    }

    @Override
    public String getCreatedDate() {
        return externalSftpDestination.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return externalSftpDestination.getUpdatedDate().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
