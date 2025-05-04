package com.extole.api.model;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ReportRunner extends EventEntity {

    String getType();

    String getId();

    String getName();

    String getReportTypeName();

    String[] getFormats();

    String getCreatedDate();

    String getUpdatedDate();

    Map<String, String> getParameters();

    String[] getScopes();

    String[] getTags();

    String getUserId();

    @Nullable
    String getSftpServerId();

    @Nullable
    PauseInfo pauseInfo();

    @Nullable
    MergingConfiguration mergingConfiguration();

    boolean isLegacySftpReportNameFormat();

    interface PauseInfo {
        String getUserId();

        String getDescription();

        String getUpdatedDate();
    }

    interface MergingConfiguration {
        String[] getSortBy();

        String[] getUniqueBy();

        String[] getFormats();
    }

}
