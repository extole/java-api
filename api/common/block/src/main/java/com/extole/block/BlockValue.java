package com.extole.block;

import java.time.Instant;

import com.extole.common.lang.ToString;

public final class BlockValue {
    private final BlockResourceType blockResourceType;
    private final BlockFileContentType blockFileContentType;
    private final String resourceName;
    private final String url;
    private final Instant createdDate;
    private final String value;

    BlockValue(BlockResourceType blockResourceType,
        BlockFileContentType blockFileContentType,
        String resourceName,
        String url,
        Instant createdDate,
        String value) {
        this.blockResourceType = blockResourceType;
        this.blockFileContentType = blockFileContentType;
        this.resourceName = resourceName;
        this.url = url;
        this.createdDate = createdDate;
        this.value = value;
    }

    public BlockResourceType getBlockResourceType() {
        return blockResourceType;
    }

    public BlockFileContentType getBlockContentType() {
        return blockFileContentType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getUrl() {
        return url;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
