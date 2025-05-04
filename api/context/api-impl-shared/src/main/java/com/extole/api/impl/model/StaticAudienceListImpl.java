package com.extole.api.impl.model;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import com.extole.api.model.StaticAudienceList;
import com.extole.common.lang.ToString;
import com.extole.reporting.pojo.audience.list.StaticAudienceListPojo;

final class StaticAudienceListImpl implements StaticAudienceList {
    private final StaticAudienceListPojo staticAudienceList;

    StaticAudienceListImpl(StaticAudienceListPojo staticAudienceList) {
        this.staticAudienceList = staticAudienceList;
    }

    @Override
    public String getId() {
        return staticAudienceList.getId().getValue();
    }

    @Override
    public String getName() {
        return staticAudienceList.getName();
    }

    @Override
    public String getCreatedDate() {
        return staticAudienceList.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return staticAudienceList.getUpdatedDate().toString();
    }

    @Override
    public String[] getTags() {
        return staticAudienceList.getTags().toArray(String[]::new);
    }

    @Override
    public String getType() {
        return staticAudienceList.getType().name();
    }

    @Nullable
    @Override
    public String getDescription() {
        return staticAudienceList.getDescription().orElse(null);
    }

    @Override
    public String[] getEventColumns() {
        return staticAudienceList.getEventColumns().toArray(String[]::new);
    }

    @Override
    public Map<String, String> getEventData() {
        return ImmutableMap.copyOf(staticAudienceList.getEventData());
    }

    @Override
    public String getReportId() {
        return staticAudienceList.getReportId().getValue();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
