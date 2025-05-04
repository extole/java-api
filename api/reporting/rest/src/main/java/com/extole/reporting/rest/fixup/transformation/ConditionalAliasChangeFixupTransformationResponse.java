package com.extole.reporting.rest.fixup.transformation;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ConditionalAliasChangeFixupTransformationResponse extends FixupTransformationResponse {

    private static final String JSON_CLIENT_ID_FILTER_COLUMN = "client_id_filter_column";
    private static final String JSON_PROGRAM_LABEL_FILTER_COLUMN = "program_label_filter_column";
    private static final String JSON_STEP_NAME_FILTER_COLUMN = "step_name_filter_column";
    private static final String JSON_ALIASES_TO_ADD_COLUMN = "aliases_to_add_column";
    private static final String JSON_ALIASES_TO_REMOVE_COLUMN = "aliases_to_remove_column";
    private static final String JSON_FILE_ASSET_ID = "file_asset_id";

    private final String clientIdFilterColumn;
    private final Optional<String> programLabelFilterColumn;
    private final Optional<String> stepNameFilterColumn;
    private final Optional<String> aliasesToAddColumn;
    private final Optional<String> aliasesToRemoveColumn;
    private final String fileAssetId;

    @JsonCreator
    public ConditionalAliasChangeFixupTransformationResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupTransformationType type,
        @JsonProperty(JSON_CLIENT_ID_FILTER_COLUMN) String clientIdFilterColumn,
        @JsonProperty(JSON_PROGRAM_LABEL_FILTER_COLUMN) Optional<String> programLabelFilterColumn,
        @JsonProperty(JSON_STEP_NAME_FILTER_COLUMN) Optional<String> stepNameFilterColumn,
        @JsonProperty(JSON_ALIASES_TO_ADD_COLUMN) Optional<String> aliasesToAddColumn,
        @JsonProperty(JSON_ALIASES_TO_REMOVE_COLUMN) Optional<String> aliasesToRemoveColumn,
        @JsonProperty(JSON_FILE_ASSET_ID) String fileAssetId) {
        super(id, type);
        this.clientIdFilterColumn = clientIdFilterColumn;
        this.programLabelFilterColumn = programLabelFilterColumn;
        this.stepNameFilterColumn = stepNameFilterColumn;
        this.aliasesToAddColumn = aliasesToAddColumn;
        this.aliasesToRemoveColumn = aliasesToRemoveColumn;
        this.fileAssetId = fileAssetId;
    }

    public String getClientIdFilterColumn() {
        return clientIdFilterColumn;
    }

    public Optional<String> getProgramLabelFilterColumn() {
        return programLabelFilterColumn;
    }

    public Optional<String> getStepNameFilterColumn() {
        return stepNameFilterColumn;
    }

    public Optional<String> getAliasesToAddColumn() {
        return aliasesToAddColumn;
    }

    public Optional<String> getAliasesToRemoveColumn() {
        return aliasesToRemoveColumn;
    }

    public String getFileAssetId() {
        return fileAssetId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
