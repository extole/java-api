package com.extole.client.rest.creative;

import java.util.Map;

import com.extole.client.rest.settings.ClientSettingsRestException;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CreativeArchiveRestException extends ExtoleRestException {

    public static final ErrorCode<CreativeArchiveRestException> INVALID_CAMPAIGN_ID =
        new ErrorCode<>("invalid_campaign_id", 400, "Invalid Campaign Id", "campaign_id");

    public static final ErrorCode<CreativeArchiveRestException> INVALID_ARCHIVE_ID = new ErrorCode<>(
        "invalid_archive_id", 400, "Creative archive not found for campaign", "campaign_id", "creative_archive_id");

    public static final ErrorCode<CreativeArchiveRestException> JAVASCRIPT_ERROR = new ErrorCode<>("javascript_error",
        400, "Error with archive's javascript", "file", "creativeArchiveId", "output");

    public static final ErrorCode<CreativeArchiveRestException> DOWNLOAD_ERROR = new ErrorCode<>("download_error", 400,
        "An error occurred downloading the creative archive", "creativeArchiveId");

    public static final ErrorCode<CreativeArchiveRestException> UPLOAD_ERROR =
        new ErrorCode<>("upload_error", 400, "Error uploading archive", "file");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_ASSET_FILE_PATH_TOO_LONG = new ErrorCode<>(
        "creative_asset_file_path_too_long", 400, "Asset file path length is greater than max allowed length",
        "file_path", "file_path_length", "max_allowed_file_path_length");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_ASSET_FILE_PATH_INVALID =
        new ErrorCode<>("creative_asset_file_path_invalid", 400,
            "Asset file path is invalid (not an URI or contains invalid characters)", "file_path");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_ASSET_FILE_SIZE_TOO_BIG =
        new ErrorCode<>("creative_asset_file_size_too_big", 400, "Asset file size is greater than max allowed size",
            "file_path", "file_size", "max_allowed_file_size");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_ASSET_INVALID_CHARACTER_ENCODING =
        new ErrorCode<>("creative_asset_invalid_character_encoding", 400, "Asset file has invalid character encoding",
            "file_path");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_HAS_UNKNOWN_CLASSIFICATION = new ErrorCode<>(
        "creative_has_unknown_classification", 400,
        "Unable to determine creative classification. Creative archive must contain one of the required render files",
        "file_paths");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_HAS_INVALID_API_VERSION = new ErrorCode<>(
        "creative_has_invalid_api_version", 400, "Creative archive has an invalid api version",
        "archive_id", "api_version");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_MISMATCH_API_VERSION = new ErrorCode<>(
        "creative_mismatch_api_version", 400, "Creative mismatch api version",
        "api_version", "expected_api_version");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_HAS_INCOMPATIBLE_API_VERSION = new ErrorCode<>(
        "creative_has_incompatible_api_version", 400, "Creative archive has an incompatible api version",
        "archive_id", "api_version");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_HAS_INVALID_LOCALE = new ErrorCode<>(
        "creative_has_invalid_locale", 400, "Creative archive has an invalid locale", "locale");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_HAS_DEFAULT_LOCALE_DISABLED = new ErrorCode<>(
        "creative_has_default_locale_disabled", 400, "Default locale must be in the list of enabled locales",
        "default_locale", "enabled_locales");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_IS_MISSING_RENDERER =
        new ErrorCode<>("creative_is_missing_renderer", 400,
            "Creative archive is missing one of the required renderer files", "file_paths");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_IS_MISSING_ATTRIBUTES = new ErrorCode<>(
        "creative_is_missing_attributes", 400, "Creative archive is missing required attributes", "attributes");

    public static final ErrorCode<CreativeArchiveRestException> CREATIVE_ARCHIVE_SIZE_TOO_BIG = new ErrorCode<>(
        "creative_archive_size_too_big", 400, "Submitted archive is greater than max allowed size", "archive_size",
        "max_allowed_size");

    public CreativeArchiveRestException(String uniqueId, ErrorCode<ClientSettingsRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
