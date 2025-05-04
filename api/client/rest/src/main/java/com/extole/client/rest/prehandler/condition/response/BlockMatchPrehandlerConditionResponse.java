package com.extole.client.rest.prehandler.condition.response;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.CollectionUtils;

import com.extole.client.rest.blocks.ListType;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;

@Schema(description = "Condition that adds the block list types to be checked.")
public class BlockMatchPrehandlerConditionResponse extends PrehandlerConditionResponse {

    static final String TYPE = "BLOCK_MATCH";

    private static final String JSON_CONSIDERED_BLOCK_LIST_TYPES = "considered_block_list_types";

    private final Set<ListType> consideredBlockListTypes;

    protected BlockMatchPrehandlerConditionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_CONSIDERED_BLOCK_LIST_TYPES) Set<ListType> consideredBlockListTypes) {
        super(id, PrehandlerConditionType.BLOCK_MATCH);
        this.consideredBlockListTypes =
            CollectionUtils.isEmpty(consideredBlockListTypes) ? Set.of() : Set.copyOf(consideredBlockListTypes);
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_CONSIDERED_BLOCK_LIST_TYPES)
    @Schema
    public Set<ListType> getConsideredBlockListTypes() {
        return consideredBlockListTypes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private Set<ListType> consideredBlockListTypes;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withConsideredBlockListTypes(Set<ListType> consideredBlockListTypes) {
            this.consideredBlockListTypes = consideredBlockListTypes;
            return this;
        }

        public BlockMatchPrehandlerConditionResponse build() {
            return new BlockMatchPrehandlerConditionResponse(id, consideredBlockListTypes);
        }
    }
}
