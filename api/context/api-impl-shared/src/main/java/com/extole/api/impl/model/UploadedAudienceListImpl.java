package com.extole.api.impl.model;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import com.extole.api.model.UploadedAudienceList;
import com.extole.common.lang.ToString;
import com.extole.reporting.pojo.audience.list.UploadedAudienceListPojo;

final class UploadedAudienceListImpl implements UploadedAudienceList {
    private final UploadedAudienceListPojo uploadedAudienceList;

    UploadedAudienceListImpl(UploadedAudienceListPojo uploadedAudienceList) {
        this.uploadedAudienceList = uploadedAudienceList;
    }

    @Override
    public String getId() {
        return uploadedAudienceList.getId().getValue();
    }

    @Override
    public String getName() {
        return uploadedAudienceList.getName();
    }

    @Override
    public String getCreatedDate() {
        return uploadedAudienceList.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return uploadedAudienceList.getUpdatedDate().toString();
    }

    @Override
    public String[] getTags() {
        return uploadedAudienceList.getTags().toArray(String[]::new);
    }

    @Override
    public String getType() {
        return uploadedAudienceList.getType().name();
    }

    @Nullable
    @Override
    public String getDescription() {
        return uploadedAudienceList.getDescription().orElse(null);
    }

    @Override
    public String[] getEventColumns() {
        return uploadedAudienceList.getEventColumns().toArray(String[]::new);
    }

    @Override
    public Map<String, String> getEventData() {
        return ImmutableMap.copyOf(uploadedAudienceList.getEventData());
    }

    @Override
    public String getFileAssetId() {
        return uploadedAudienceList.getFileAssetId().getValue();
    }

    @Nullable
    @Override
    public String getAudienceId() {
        return uploadedAudienceList.getAudienceId().map(value -> value.getValue()).orElse(null);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
