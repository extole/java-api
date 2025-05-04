package com.extole.client.rest.blocks;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BlockCheckResponse {
    private static final String RESULT = "result";
    private static final String BLOCK_ID = "block_Id";

    private final BlockCheckResult result;
    private final String blockId;

    @JsonCreator
    public BlockCheckResponse(@JsonProperty(RESULT) BlockCheckResult result,
        @Nullable @JsonProperty(BLOCK_ID) String blockId) {
        this.result = result;
        this.blockId = blockId;
    }

    @JsonProperty(RESULT)
    public BlockCheckResult getResult() {
        return result;
    }

    @Nullable
    @JsonProperty(BLOCK_ID)
    public String getBlockId() {
        return blockId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
