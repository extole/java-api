package com.extole.client.rest.campaign.component;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;

public abstract class ComponentElementRequest {

    protected static final String JSON_COMPONENT_REFERENCES = "component_references";
    protected static final String JSON_COMPONENT_IDS = "component_ids";

    private final Omissible<List<ComponentReferenceRequest>> componentReferences;
    private final Omissible<List<Id<ComponentResponse>>> componentIds;

    @JsonCreator
    public ComponentElementRequest(
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds) {
        this.componentReferences = componentReferences;
        this.componentIds = componentIds;
    }

    @JsonProperty(JSON_COMPONENT_REFERENCES)
    public Omissible<List<ComponentReferenceRequest>> getComponentReferences() {
        return componentReferences;
    }

    @Deprecated // TODO remove after UI changes ENG-23427
    @JsonProperty(JSON_COMPONENT_IDS)
    public Omissible<List<Id<ComponentResponse>>> getComponentIds() {
        return componentIds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public abstract static class Builder<BUILDER extends Builder<BUILDER>> {

        protected List<ComponentReferenceRequest.Builder<?>> componentReferenceBuilders =
            Lists.newArrayList();
        protected Omissible<List<Id<ComponentResponse>>> componentIds = Omissible.omitted();

        protected Builder() {

        }

        public <CALLER> ComponentReferenceRequest.Builder<CALLER> addComponentReference(CALLER caller) {
            ComponentReferenceRequest.Builder<CALLER> builder = ComponentReferenceRequest.builder(caller);
            this.componentReferenceBuilders.add(builder);
            return builder;
        }

        public BUILDER withComponentIds(List<Id<ComponentResponse>> componentIds) {
            this.componentIds = Omissible.of(componentIds);
            return (BUILDER) this;
        }

        public abstract ComponentElementRequest build();

    }

}
