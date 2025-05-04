package com.extole.client.rest.campaign.component.asset;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignComponentAssetValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignComponentAssetValidationRestException> NAME_MISSING =
        new ErrorCode<>("campaign_component_asset_name_missing", 400, "Campaign Component Asset name is missing");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("campaign_component_asset_name_contains_illegal_character", 400,
            "Campaign component asset name can only contain alphanumeric, dash and underscore characters", "name");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> FILENAME_MISSING =
        new ErrorCode<>("campaign_component_asset_filename_missing", 400,
            "Campaign Component Asset filename is missing");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> FILENAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("campaign_component_asset_name_contains_illegal_character", 400,
            "Campaign component asset filename can only contain alphanumeric, dash, period and underscore characters",
            "filename");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> CONTENT_MISSING =
        new ErrorCode<>("campaign_component_asset_content_missing", 400, "Campaign Component Asset content is missing",
            "asset_name");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> CONTENT_UPLOAD_ERROR =
        new ErrorCode<>("campaign_component_asset_content_upload_error", 400,
            "Campaign Component Asset content failed to upload");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_component_asset_name_length_out_of_range", 400,
            "Campaign Component Asset name length is out of range", "name", "min_length", "max_length");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> FILENAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_component_asset_name_length_out_of_range", 400,
            "Campaign Component Asset filename length is out of range", "filename", "min_length", "max_length");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> CONTENT_SIZE_OUT_OF_RANGE =
        new ErrorCode<>("campaign_component_asset_content_length_out_of_range", 400,
            "Campaign Component Asset content size is out of range", "size", "max_size");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_component_asset_description_length_out_of_range", 400,
            "Campaign Component Asset description length is out of range", "description", "max_length");

    public static final ErrorCode<CampaignComponentAssetValidationRestException> DUPLICATED_NAME =
        new ErrorCode<>("campaign_component_asset_name_duplicated", 400,
            "Campaign Component Asset with such name already exists, please specify an unique name", "name");

    public CampaignComponentAssetValidationRestException(String uniqueId,
        ErrorCode<CampaignComponentAssetValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
