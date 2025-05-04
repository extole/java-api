package com.extole.client.rest.impl.prehandler.condition.response;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.blocks.ListType;
import com.extole.client.rest.prehandler.condition.response.BlockMatchPrehandlerConditionResponse;
import com.extole.model.entity.prehandler.PrehandlerConditionType;
import com.extole.model.entity.prehandler.condition.BlockMatchPrehandlerCondition;

@Component
public class BlockMatchPrehandlerConditionResponseMapper
    implements PrehandlerConditionResponseMapper<BlockMatchPrehandlerCondition, BlockMatchPrehandlerConditionResponse> {

    @Override
    public BlockMatchPrehandlerConditionResponse toResponse(BlockMatchPrehandlerCondition condition) {
        Set<ListType> consideredBlockListTypes = condition.getConsideredBlockListTypes().stream()
            .map(listType -> ListType.valueOf(listType.name()))
            .collect(Collectors.toUnmodifiableSet());
        return BlockMatchPrehandlerConditionResponse.builder()
            .withId(condition.getId().getValue())
            .withConsideredBlockListTypes(consideredBlockListTypes)
            .build();
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.BLOCK_MATCH;
    }
}
