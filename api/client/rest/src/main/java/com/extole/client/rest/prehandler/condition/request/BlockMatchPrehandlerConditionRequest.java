package com.extole.client.rest.prehandler.condition.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.blocks.ListType;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.common.rest.omissible.Omissible;

@Schema(description = "Condition that adds the block list types to be checked.")
public final class BlockMatchPrehandlerConditionRequest extends PrehandlerConditionRequest {

    static final String TYPE = "BLOCK_MATCH";

    private static final String JSON_CONSIDERED_BLOCK_LIST_TYPES = "considered_block_list_types";

    private final Omissible<Set<ListType>> consideredBlockListTypes;

    @JsonCreator
    private BlockMatchPrehandlerConditionRequest(
        @JsonProperty(JSON_CONSIDERED_BLOCK_LIST_TYPES) Omissible<Set<ListType>> consideredBlockListTypes) {
        super(PrehandlerConditionType.BLOCK_MATCH);
        this.consideredBlockListTypes = consideredBlockListTypes;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_CONSIDERED_BLOCK_LIST_TYPES)
    @Schema(description = "Condition evaluates to true if any value from the list of any list type blocks matches the" +
        " value from the event being evaluated. By default all list type blocks are evaluated.")
    public Omissible<Set<ListType>> getConsideredBlockListTypes() {
        return consideredBlockListTypes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Builder() {
        }

        private Omissible<Set<ListType>> consideredBlockListTypes = Omissible.omitted();

        public Builder withConsideredBlockListTypes(Set<ListType> consideredBlockListTypes) {
            this.consideredBlockListTypes = Omissible.of(consideredBlockListTypes);
            return this;
        }

        public BlockMatchPrehandlerConditionRequest build() {
            return new BlockMatchPrehandlerConditionRequest(consideredBlockListTypes);
        }
    }
}
