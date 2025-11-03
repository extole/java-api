package com.extole.block;

import com.extole.common.lang.ToString;

final class BlockResourceReference {

    private final BlockResourceType blockResourceType;
    private final BlockFileContentType blockFileContentType;
    private final String url;
    private final String resourceName;

    BlockResourceReference(BlockResourceType blockResourceType,
        BlockFileContentType blockFileContentType,
        String url,
        String resourceName) {
        this.blockResourceType = blockResourceType;
        this.blockFileContentType = blockFileContentType;
        this.url = url;
        this.resourceName = resourceName;
    }

    BlockResourceType getBlockResourceType() {
        return blockResourceType;
    }

    BlockFileContentType getBlockContentType() {
        return blockFileContentType;
    }

    String getResourceName() {
        return resourceName;
    }

    String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
