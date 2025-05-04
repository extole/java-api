package com.extole.api.impl.event.asset;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import com.extole.api.event.asset.Asset;
import com.extole.common.lang.ToString;
import com.extole.profile.asset.ProfileAssetPojo;

public class AssetImpl implements Asset {

    private final String id;
    private final String name;
    private final String filename;
    private final String mimeType;
    private final Long size;
    private final String status;
    private final List<String> tags;
    private final String type;
    private final String createdDate;
    private final String updatedDate;
    private final String deletedDate;

    public AssetImpl(ProfileAssetPojo asset) {
        this.id = asset.getId().getValue();
        this.name = asset.getName();
        this.filename = asset.getFilename();
        this.mimeType = asset.getMimeType();
        this.size = Long.valueOf(asset.getSize());
        this.status = asset.getStatus().name();
        this.tags = ImmutableList.copyOf(asset.getTags());
        this.type = asset.getType().name();
        this.createdDate = asset.getCreatedDate().toString();
        this.updatedDate = asset.getUpdatedDate().toString();
        this.deletedDate = asset.getDeletedDate().map(Instant::toString).orElse(null);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getUpdatedDate() {
        return updatedDate;
    }

    @Nullable
    @Override
    public String getDeletedDate() {
        return deletedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
