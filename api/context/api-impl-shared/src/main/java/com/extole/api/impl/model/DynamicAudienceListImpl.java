package com.extole.api.impl.model;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import com.extole.api.model.DynamicAudienceList;
import com.extole.common.lang.ToString;
import com.extole.reporting.pojo.audience.list.DynamicAudienceListPojo;

final class DynamicAudienceListImpl implements DynamicAudienceList {
    private final DynamicAudienceListPojo dynamicAudienceList;

    DynamicAudienceListImpl(DynamicAudienceListPojo dynamicAudienceList) {
        this.dynamicAudienceList = dynamicAudienceList;
    }

    @Override
    public String getId() {
        return dynamicAudienceList.getId().getValue();
    }

    @Override
    public String getName() {
        return dynamicAudienceList.getName();
    }

    @Override
    public String getCreatedDate() {
        return dynamicAudienceList.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return dynamicAudienceList.getUpdatedDate().toString();
    }

    @Override
    public String[] getTags() {
        return dynamicAudienceList.getTags().toArray(String[]::new);
    }

    @Override
    public String getType() {
        return dynamicAudienceList.getType().name();
    }

    @Nullable
    @Override
    public String getDescription() {
        return dynamicAudienceList.getDescription().orElse(null);
    }

    @Override
    public String[] getEventColumns() {
        return dynamicAudienceList.getEventColumns().toArray(String[]::new);
    }

    @Override
    public Map<String, String> getEventData() {
        return ImmutableMap.copyOf(dynamicAudienceList.getEventData());
    }

    @Override
    public String getReportRunnerId() {
        return dynamicAudienceList.getReportRunnerId().getValue();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
