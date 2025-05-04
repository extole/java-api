package com.extole.reporting.rest.fixup.transformation;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class ConditionalAliasChangeFixupTransformationUpdateRequest {
    private static final String JSON_CLIENT_ID_FILTER_COLUMN = "client_id_filter_column";
    private static final String JSON_PROGRAM_LABEL_FILTER_COLUMN = "program_label_filter_column";
    private static final String JSON_STEP_NAME_FILTER_COLUMN = "step_name_filter_column";
    private static final String JSON_ALIASES_TO_ADD_COLUMN = "aliases_to_add_column";
    private static final String JSON_ALIASES_TO_REMOVE_COLUMN = "aliases_to_remove_column";
    private static final String JSON_FILE_ASSET_ID = "file_asset_id";

    private final Omissible<String> clientIdFilterColumn;
    private final Omissible<Optional<String>> programLabelFilterColumn;
    private final Omissible<Optional<String>> stepNameFilterColumn;
    private final Omissible<Optional<String>> aliasesToAddColumn;
    private final Omissible<Optional<String>> aliasesToRemoveColumn;
    private final Omissible<String> fileAssetId;

    @JsonCreator
    public ConditionalAliasChangeFixupTransformationUpdateRequest(
        @JsonProperty(JSON_CLIENT_ID_FILTER_COLUMN) Omissible<String> clientIdFilterColumn,
        @JsonProperty(JSON_PROGRAM_LABEL_FILTER_COLUMN) Omissible<Optional<String>> programLabelFilterColumn,
        @JsonProperty(JSON_STEP_NAME_FILTER_COLUMN) Omissible<Optional<String>> stepNameFilterColumn,
        @JsonProperty(JSON_ALIASES_TO_ADD_COLUMN) Omissible<Optional<String>> aliasesToAddColumn,
        @JsonProperty(JSON_ALIASES_TO_REMOVE_COLUMN) Omissible<Optional<String>> aliasesToRemoveColumn,
        @JsonProperty(JSON_FILE_ASSET_ID) Omissible<String> fileAssetId) {
        this.clientIdFilterColumn = clientIdFilterColumn;
        this.programLabelFilterColumn = programLabelFilterColumn;
        this.stepNameFilterColumn = stepNameFilterColumn;
        this.aliasesToAddColumn = aliasesToAddColumn;
        this.aliasesToRemoveColumn = aliasesToRemoveColumn;
        this.fileAssetId = fileAssetId;
    }

    @JsonProperty(JSON_CLIENT_ID_FILTER_COLUMN)
    public Omissible<String> getClientIdFilterColumn() {
        return clientIdFilterColumn;
    }

    @JsonProperty(JSON_PROGRAM_LABEL_FILTER_COLUMN)
    public Omissible<Optional<String>> getProgramLabelFilterColumn() {
        return programLabelFilterColumn;
    }

    @JsonProperty(JSON_STEP_NAME_FILTER_COLUMN)
    public Omissible<Optional<String>> getStepNameFilterColumn() {
        return stepNameFilterColumn;
    }

    @JsonProperty(JSON_ALIASES_TO_ADD_COLUMN)
    public Omissible<Optional<String>> getAliasesToAddColumn() {
        return aliasesToAddColumn;
    }

    @JsonProperty(JSON_ALIASES_TO_REMOVE_COLUMN)
    public Omissible<Optional<String>> getAliasesToRemoveColumn() {
        return aliasesToRemoveColumn;
    }

    @JsonProperty(JSON_FILE_ASSET_ID)
    public Omissible<String> getFileAssetId() {
        return fileAssetId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
