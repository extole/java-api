package com.extole.client.rest.impl.blocks;

import java.time.ZoneId;

import com.extole.client.rest.blocks.BlockCheckResponse;
import com.extole.client.rest.blocks.BlockCheckResult;
import com.extole.client.rest.blocks.BlockResponse;
import com.extole.client.rest.blocks.FilterType;
import com.extole.client.rest.blocks.ListType;
import com.extole.id.Id;
import com.extole.model.entity.blocks.Block;
import com.extole.model.shared.blocklist.BlockEvaluationResult;

final class BlocksRestMapper {

    private BlocksRestMapper() {
    }

    static BlockResponse toResponse(Block block, ZoneId timeZone) {
        return new BlockResponse(
            block.getId().getValue(),
            FilterType.valueOf(block.getFilterType().name()),
            ListType.valueOf(block.getListType().name()),
            block.getSource(),
            block.getClientId().getValue(),
            block.getValue(),
            block.getCreatedDate().atZone(timeZone),
            block.getUserId().map(Id::getValue).orElse(null));
    }

    static BlockCheckResponse toCheckResponse(BlockEvaluationResult evaluationResult) {
        return new BlockCheckResponse(BlockCheckResult.valueOf(evaluationResult.getEvaluationResultType().name()),
            evaluationResult.getBlockId().isPresent() ? evaluationResult.getBlockId().get().getValue() : null);
    }
}
