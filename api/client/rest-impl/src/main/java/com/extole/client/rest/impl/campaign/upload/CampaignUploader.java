package com.extole.client.rest.impl.campaign.upload;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignArchiveRestException;
import com.extole.client.rest.campaign.CampaignResponse;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignValidationRestException;
import com.extole.client.rest.campaign.GlobalCampaignRestException;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetValidationRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignFrontendControllerValidationRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepValidationRestException;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppValidationRestException;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricValidationRestException;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleValidationRestException;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleValidationRestException;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleValidationRestException;
import com.extole.client.rest.campaign.label.CampaignLabelValidationRestException;
import com.extole.client.rest.component.type.ComponentTypeRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.model.entity.campaign.Campaign;

public interface CampaignUploader {

    int THRESHOLD = 256 * 1024;
    String CAMPAIGN_JSON_FILENAME = "campaign.json";
    String FOLDER_OR_FILE_REGEX = "[0-9a-zA-Z_\\-.\u0020]+";

    String COMPONENTS_FOLDER_NAME = "components";
    Pattern COMPONENT_ASSET_PATH_PATTERN = Pattern.compile("^" + FOLDER_OR_FILE_REGEX +
        "((/" + COMPONENTS_FOLDER_NAME + "/" + FOLDER_OR_FILE_REGEX + ")*)" +
        "/assets/" + "(" + FOLDER_OR_FILE_REGEX + ")$");

    CampaignUploadBuilder newUpload();

    interface CampaignUploadBuilder {
        CampaignUploadBuilder withCampaign(Campaign campaign);

        CampaignUploadBuilder withInputStream(InputStream inputStream);

        CampaignUploadBuilder withContentDispositionHeader(FormDataContentDisposition contentDispositionHeader);

        CampaignUploadBuilder withTimeZone(ZoneId timeZone);

        CampaignUploadBuilder withAccessToken(String accessToken);

        CampaignUploadBuilder withObjectMapper(ObjectMapper mapper);

        CampaignResponse upload() throws UserAuthorizationRestException, CampaignArchiveRestException,
            CampaignValidationRestException, RewardRuleValidationRestException,
            CampaignControllerValidationRestException, CampaignControllerActionRestException,
            CampaignControllerTriggerRestException, TransitionRuleValidationRestException,
            QualityRuleValidationRestException, CampaignLabelValidationRestException, CreativeArchiveRestException,
            CampaignFlowStepValidationRestException, CampaignRestException, SettingValidationRestException,
            BuildCampaignRestException, CampaignComponentValidationRestException,
            CampaignComponentAssetValidationRestException, CampaignFlowStepMetricValidationRestException,
            CampaignFlowStepAppValidationRestException, CampaignComponentRestException, GlobalCampaignRestException,
            ComponentTypeRestException, CampaignFrontendControllerValidationRestException;

    }
}
