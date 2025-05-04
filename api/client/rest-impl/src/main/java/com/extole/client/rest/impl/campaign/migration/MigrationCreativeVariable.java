package com.extole.client.rest.impl.campaign.migration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;
import com.extole.model.entity.campaign.CreativeArchiveId;
import com.extole.model.entity.campaign.CreativeVariable;

final class MigrationCreativeVariable implements CreativeVariable {
    private final String name;
    private final String label;
    private final Scope scope;
    private final Scope defaultScope;
    private final Type type;
    private final String[] tags;
    private final Map<String, String> values;
    private final boolean visible;
    private final CreativeArchiveId creativeArchiveId;
    private final List<String> output;

    MigrationCreativeVariable(String name, String label, Scope scope, Scope defaultScope, Type type,
        String[] tags, Map<String, String> values, boolean visible, CreativeArchiveId creativeArchiveId,
        List<String> output) {
        this.name = name;
        this.label = label;
        this.scope = scope;
        this.defaultScope = defaultScope;
        this.type = type;
        this.tags = Arrays.copyOf(tags, tags.length);
        this.values = ImmutableMap.copyOf(Objects.requireNonNullElse(values, Map.of()));
        this.visible = visible;
        this.creativeArchiveId = creativeArchiveId;
        this.output = ImmutableList.copyOf(Objects.requireNonNullElse(output, List.of()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public Scope getDefaultScope() {
        return defaultScope;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Override
    public Map<String, String> getValues() {
        return values;
    }

    @Override
    public Boolean isVisible() {
        return Boolean.valueOf(visible);
    }

    @Override
    public CreativeArchiveId getCreativeArchiveId() {
        return creativeArchiveId;
    }

    @Override
    public List<String> getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
