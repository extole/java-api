package com.extole.client.rest.impl.prehandler.condition.request;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.client.rest.prehandler.condition.exception.BlockMatchPrehandlerConditionRestException;
import com.extole.client.rest.prehandler.condition.request.BlockMatchPrehandlerConditionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.blocks.ListType;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.condition.BlockMatchPrehandlerConditionBuilder;
import com.extole.model.service.prehandler.condition.exception.EmptyConsideredBlockListTypesPrehandlerConditionException;

@Component
public class BlockMatchPrehandlerConditionRequestMapper
    implements PrehandlerConditionRequestMapper<BlockMatchPrehandlerConditionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, BlockMatchPrehandlerConditionRequest condition)
        throws BlockMatchPrehandlerConditionRestException {
        try {
            BlockMatchPrehandlerConditionBuilder conditionBuilder =
                prehandlerBuilder.addCondition(com.extole.model.entity.prehandler.PrehandlerConditionType.BLOCK_MATCH);

            condition.getConsideredBlockListTypes().ifPresent(consideredBlockListTypes -> {
                Set<ListType> blockListTypes = consideredBlockListTypes.stream()
                    .filter(listType -> listType != null)
                    .map(listType -> ListType.valueOf(listType.name()))
                    .collect(Collectors.toUnmodifiableSet());
                conditionBuilder.withConsideredBlockListTypes(blockListTypes);
            });

            conditionBuilder.done();
        } catch (EmptyConsideredBlockListTypesPrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(BlockMatchPrehandlerConditionRestException.class)
                .withErrorCode(BlockMatchPrehandlerConditionRestException.EMPTY_LIST_TYPES)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.BLOCK_MATCH;
    }
}
