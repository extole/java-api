package com.extole.client.rest.campaign.migration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = MigratedCreativeResponse.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MigratedCreativeActionCreativeResponse.class,
        name = MigratedCreativeActionCreativeResponse.TYPE),
})
@Schema(discriminatorProperty = MigratedCreativeResponse.JSON_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = MigratedCreativeActionCreativeResponse.TYPE,
        schema = MigratedCreativeActionCreativeResponse.class),
})
public abstract class MigratedCreativeResponse {
    static final String JSON_TYPE = "type";
    static final String CREATIVE_ID = "creative_id";
    static final String COMPONENT_NAME = "component_name";
    static final String DEDUPED_LEGACY_VARIABLES = "deduped_legacy_variables";

    private final String creativeId;
    private final String componentName;
    private final List<String> dedupedLegacyVariables;

    protected MigratedCreativeResponse(
        String creativeId,
        String componentName,
        List<String> dedupedLegacyVariables) {
        this.creativeId = creativeId;
        this.componentName = componentName;
        this.dedupedLegacyVariables = dedupedLegacyVariables;
    }

    public abstract MigratedCreativeResponseType getType();

    @JsonProperty(CREATIVE_ID)
    public String getCreativeId() {
        return creativeId;
    }

    @JsonProperty(COMPONENT_NAME)
    public String getComponentName() {
        return componentName;
    }

    @JsonProperty(DEDUPED_LEGACY_VARIABLES)
    public List<String> getDedupedLegacyVariables() {
        return dedupedLegacyVariables;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
