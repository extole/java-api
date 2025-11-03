package com.extole.block;

import java.time.Instant;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.entity.blocks.Block;
import com.extole.model.entity.blocks.BlockHandle;
import com.extole.model.entity.blocks.FilterType;
import com.extole.model.entity.blocks.ListType;
import com.extole.model.entity.client.Client;
import com.extole.model.entity.user.User;

final class BlockImpl implements Block {
    private final ListType listType;
    private final String source;
    private final String value;
    private final Instant createdDate;
    private final Id<BlockHandle> id;

    BlockImpl(ListType listType, String source, String value, Instant createdDate) {
        this.listType = listType;
        this.source = source;
        this.value = value;
        this.createdDate = createdDate;
        this.id = Id.valueOf(DigestUtils.md5Hex(listType.name() + "-" + source + "-" + value));
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.BLACK;
    }

    @Override
    public ListType getListType() {
        return listType;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Id<ClientHandle> getClientId() {
        return Client.EXTOLE_CLIENT_ID;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Instant getCreatedDate() {
        return createdDate;
    }

    @Override
    public Optional<Id<User>> getUserId() {
        return Optional.empty();
    }

    @Override
    public Optional<Instant> getDeletedDate() {
        return Optional.empty();
    }

    @Override
    public Id<BlockHandle> getId() {
        return id;
    }
}
