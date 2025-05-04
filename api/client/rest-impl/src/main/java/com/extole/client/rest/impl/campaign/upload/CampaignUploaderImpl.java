package com.extole.client.rest.impl.campaign.upload;

import static com.extole.model.entity.campaign.Campaign.GLOBAL_CAMPAIGN_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.client.ClientAuthorization;
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
import com.extole.client.rest.campaign.configuration.CampaignComponentAssetConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSettingConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepAppConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepMetricConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFrontendControllerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignLabelConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.configuration.IncentiveConfiguration;
import com.extole.client.rest.campaign.configuration.QualityRuleConfiguration;
import com.extole.client.rest.campaign.configuration.RewardRuleConfiguration;
import com.extole.client.rest.campaign.configuration.StepDataConfiguration;
import com.extole.client.rest.campaign.configuration.TransitionRuleConfiguration;
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
import com.extole.client.rest.impl.campaign.CampaignRestMapper;
import com.extole.client.rest.impl.campaign.component.CampaignComponentUploader;
import com.extole.client.rest.impl.campaign.component.asset.UploadedAssetId;
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
import com.extole.client.rest.impl.campaign.controller.CampaignStepUploader;
import com.extole.client.rest.impl.campaign.flow.step.CampaignFlowStepUploader;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignComponentAsset;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.CampaignControllerAction;
import com.extole.model.entity.campaign.CampaignControllerTrigger;
import com.extole.model.entity.campaign.CampaignFlowStep;
import com.extole.model.entity.campaign.CampaignFlowStepApp;
import com.extole.model.entity.campaign.CampaignFlowStepMetric;
import com.extole.model.entity.campaign.CampaignLabel;
import com.extole.model.entity.campaign.CampaignLabelType;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.ExpressionType;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.RewardRule;
import com.extole.model.entity.campaign.RewardRuleExpression;
import com.extole.model.entity.campaign.Rewardee;
import com.extole.model.entity.campaign.RuleActionType;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.StepData;
import com.extole.model.entity.campaign.TransitionRule;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignGlobalRenameException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignProgramTypeEmptyException;
import com.extole.model.service.campaign.CampaignProgramTypeInvalidException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceDescriptionLengthException;
import com.extole.model.service.campaign.CampaignServiceDuplicateNameException;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceTagInvalidException;
import com.extole.model.service.campaign.CampaignServiceTagsLengthException;
import com.extole.model.service.campaign.CampaignThemeNameInvalidException;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.CampaignComponentDescriptionLengthException;
import com.extole.model.service.campaign.component.CampaignComponentDisplayNameLengthException;
import com.extole.model.service.campaign.component.CampaignComponentIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.component.CampaignComponentIllegalCharacterInNameException;
import com.extole.model.service.campaign.component.CampaignComponentNameLengthException;
import com.extole.model.service.campaign.component.CampaignComponentRootRenameException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentMissingException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentSizeTooBigException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetDescriptionLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameLengthException;
import com.extole.model.service.campaign.label.CampaignLabelBuilder;
import com.extole.model.service.campaign.label.CampaignLabelIllegalCharacterInNameException;
import com.extole.model.service.campaign.label.CampaignLabelNameAlreadyDefinedException;
import com.extole.model.service.campaign.label.CampaignLabelNameLengthException;
import com.extole.model.service.campaign.quality.rule.QualityRuleBuilder;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceIllegalCharacterInKeyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceIllegalCharacterInValueException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidBooleanPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidCountryPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidDomainPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidExpressionTypeException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidIntegerPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidLongPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidRegexpPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidSubnetPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidTemporalUnitPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceInvalidTimeUnitPropertyException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceKeyLengthException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServicePropertyNotSupportedException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServicePropertyValueCountException;
import com.extole.model.service.campaign.quality.rule.QualityRuleServiceValueLengthException;
import com.extole.model.service.campaign.reward.rule.ExpressionInvalidException;
import com.extole.model.service.campaign.reward.rule.ExpressionLengthException;
import com.extole.model.service.campaign.reward.rule.ExpressionMissingException;
import com.extole.model.service.campaign.reward.rule.ExpressionTypeNotSupportedException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInMinCartValueException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInReferralsPerRewardException;
import com.extole.model.service.campaign.reward.rule.IllegalValueInRewardEveryXFriendActionsException;
import com.extole.model.service.campaign.reward.rule.RewardRuleBuilder;
import com.extole.model.service.campaign.reward.rule.RewardRuleNotFoundException;
import com.extole.model.service.campaign.setting.ComponentBuildSettingException;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.model.service.campaign.transition.rule.IllegalValueInTransitionPeriodException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleBuilder;
import com.extole.model.service.campaign.transition.rule.TransitionRuleNotFoundException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;

@Component
public class CampaignUploaderImpl implements CampaignUploader {
    private static final Logger LOG = LoggerFactory.getLogger(CampaignUploaderImpl.class);
    private final ClientAuthorizationProvider clientAuthorizationProvider;
    private final CampaignService campaignService;
    private final CampaignRestMapper campaignRestMapper;
    private final CampaignComponentUploader campaignComponentUploader;
    private final CampaignStepUploader campaignStepUploader;
    private final CampaignFlowStepUploader campaignFlowStepUploader;
    private final CampaignComponentSettingRestMapper settingRestMapper;
    private final UploadExceptionTranslator uploadExceptionTranslator = new UploadExceptionTranslator();

    public CampaignUploaderImpl(ClientAuthorizationProvider clientAuthorizationProvider,
        CampaignService campaignService,
        CampaignRestMapper campaignRestMapper,
        CampaignComponentUploader campaignComponentUploader,
        CampaignStepUploader campaignStepUploader,
        CampaignFlowStepUploader campaignFlowStepUploader,
        CampaignComponentSettingRestMapper settingRestMapper) {
        this.clientAuthorizationProvider = clientAuthorizationProvider;
        this.campaignService = campaignService;
        this.campaignRestMapper = campaignRestMapper;
        this.campaignComponentUploader = campaignComponentUploader;
        this.campaignStepUploader = campaignStepUploader;
        this.campaignFlowStepUploader = campaignFlowStepUploader;
        this.settingRestMapper = settingRestMapper;
    }

    @Override
    public CampaignUploadBuilder newUpload() {
        return new SimpleCampaignUploadBuilder();
    }

    private CampaignResponse saveCampaign(FormDataContentDisposition contentDispositionHeader,
        Authorization authorization, CampaignBuilder campaignBuilder, CampaignConfiguration uploaded,
        ZoneId timeZone, Optional<Id<Campaign>> existingCampaignId)
        throws CampaignValidationRestException, CampaignComponentValidationRestException, CreativeArchiveRestException,
        CampaignLabelValidationRestException, CampaignControllerValidationRestException,
        CampaignFlowStepValidationRestException, BuildCampaignRestException, CampaignControllerTriggerRestException,
        CampaignControllerActionRestException, SettingValidationRestException,
        CampaignComponentAssetValidationRestException, CampaignFlowStepAppValidationRestException,
        CampaignFlowStepMetricValidationRestException, CampaignComponentRestException, GlobalCampaignRestException,
        CampaignFrontendControllerValidationRestException {
        try {
            if (com.extole.client.rest.campaign.configuration.CampaignState.LIVE == uploaded.getState()) {
                campaignBuilder.withStartDateNow();
            }
            Campaign campaign = campaignBuilder.save();
            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (ComponentBuildSettingException e) {
            Map<String, RestExceptionResponse> exceptionResponses = Maps.newHashMap();
            e.getSuppressedExceptions().forEach((variableName, buildException) -> {
                RestException restException =
                    settingRestMapper.mapToBuildSettingRestException(buildException, e.getEntityId());
                exceptionResponses.put(variableName, new RestExceptionResponseBuilder(restException).build());
            });
            throw RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.SETTINGS_BUILD_FAILED)
                .addParameter("errors", exceptionResponses)
                .withCause(e)
                .build();
        } catch (Exception e) {
            uploadExceptionTranslator.translateAndRethrow(authorization.getClientId(),
                existingCampaignId.orElse(Id.valueOf(StringUtils.EMPTY)),
                uploaded.getName(), contentDispositionHeader, e);
        }
        throw new IllegalStateException("Should not reach here");
    }

    private void uploadCampaignComponents(CampaignUploadContext campaignUploadContext, CampaignConfiguration uploaded)
        throws CampaignComponentValidationRestException, SettingValidationRestException,
        CampaignComponentAssetValidationRestException, ComponentTypeRestException {
        for (CampaignComponentConfiguration component : uploaded.getComponents()) {
            try {
                campaignComponentUploader.uploadComponent(campaignUploadContext, component);
            } catch (CampaignComponentNameLengthException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getComponentName())
                    .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            } catch (CampaignComponentDisplayNameLengthException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.DISPLAY_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("display_name", e.getComponentDisplayName())
                    .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            } catch (CampaignComponentIllegalCharacterInNameException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("name", e.getComponentName())
                    .withCause(e)
                    .build();
            } catch (CampaignComponentIllegalCharacterInDisplayNameException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.DISPLAY_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("display_name", e.getComponentDisplayName())
                    .withCause(e)
                    .build();
            } catch (CampaignComponentDescriptionLengthException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            } catch (SettingNameLengthException e) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getName())
                    .addParameter("min_length", Integer.valueOf(e.getNameMinLength()))
                    .addParameter("max_length", Integer.valueOf(e.getNameMaxLength()))
                    .withCause(e)
                    .build();
            } catch (VariableValueKeyLengthException e) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.VALUE_KEY_LENGTH_OUT_OF_RANGE)
                    .addParameter("value_key", e.getValueKey())
                    .addParameter("min_length", Integer.valueOf(e.getValueKeyMinLength()))
                    .addParameter("max_length", Integer.valueOf(e.getValueKeyMaxLength()))
                    .withCause(e)
                    .build();
            } catch (SettingInvalidNameException e) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.RESERVED_NAME)
                    .addParameter("name", e.getName())
                    .withCause(e)
                    .build();
            } catch (SettingDisplayNameLengthException e) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.DISPLAY_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("display_name", e.getDisplayName())
                    .addParameter("min_length", Integer.valueOf(e.getDisplayNameMinLength()))
                    .addParameter("max_length", Integer.valueOf(e.getDisplayNameMaxLength()))
                    .withCause(e)
                    .build();
            } catch (SettingIllegalCharacterInDisplayNameException e) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.DISPLAY_NAME_HAS_ILLEGAL_CHARACTER)
                    .addParameter("display_name", e.getDisplayName())
                    .withCause(e)
                    .build();
            } catch (CampaignComponentAssetNameLengthException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                    .withErrorCode(CampaignComponentAssetValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getName())
                    .addParameter("min_length", Integer.valueOf(e.getNameMinLength()))
                    .addParameter("max_length", Integer.valueOf(e.getNameMaxLength()))
                    .withCause(e)
                    .build();
            } catch (CampaignComponentAssetFilenameLengthException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                    .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("filename", e.getName())
                    .addParameter("min_length", Integer.valueOf(e.getNameMinLength()))
                    .addParameter("max_length", Integer.valueOf(e.getNameMaxLength()))
                    .withCause(e)
                    .build();
            } catch (CampaignComponentAssetNameInvalidException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                    .withErrorCode(CampaignComponentAssetValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("name", e.getName())
                    .withCause(e)
                    .build();
            } catch (CampaignComponentAssetFilenameInvalidException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                    .withErrorCode(CampaignComponentAssetValidationRestException.FILENAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("filename", e.getFilename())
                    .withCause(e)
                    .build();
            } catch (CampaignComponentAssetContentSizeTooBigException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                    .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_SIZE_OUT_OF_RANGE)
                    .addParameter("size", Long.valueOf(e.getContentSize()))
                    .addParameter("max_size", Long.valueOf(e.getMaxContentSize()))
                    .withCause(e)
                    .build();
            } catch (CampaignComponentAssetDescriptionLengthException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                    .withErrorCode(CampaignComponentAssetValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                    .addParameter("description", e.getDescription())
                    .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                    .withCause(e)
                    .build();
            } catch (SettingTagLengthException e) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.TAG_LENGTH_OUT_OF_RANGE)
                    .addParameter("invalid_tag", e.getTag())
                    .addParameter("max_length", Integer.valueOf(e.getTagMaxLength()))
                    .addParameter("min_length", Integer.valueOf(e.getTagMinLength()))
                    .withCause(e)
                    .build();
            } catch (CampaignComponentAssetContentMissingException e) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentAssetValidationRestException.class)
                    .withErrorCode(CampaignComponentAssetValidationRestException.CONTENT_MISSING)
                    .addParameter("asset_name", e.getAssetName())
                    .withCause(e)
                    .build();
            } catch (CampaignComponentRootRenameException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            } catch (ComponentTypeNotFoundException e) {
                throw RestExceptionBuilder.newBuilder(ComponentTypeRestException.class)
                    .withErrorCode(ComponentTypeRestException.COMPONENT_TYPE_NOT_FOUND)
                    .addParameter("name", e.getName())
                    .withCause(e)
                    .build();
            }
        }
    }

    private void uploadSteps(CampaignUploadContext context, CampaignConfiguration campaignJson, ZoneId timeZone)
        throws CampaignControllerActionRestException, CampaignControllerTriggerRestException,
        CampaignComponentValidationRestException, CreativeArchiveRestException {
        for (CampaignStepConfiguration step : campaignJson.getSteps()) {
            campaignStepUploader.uploadStep(context, step, timeZone);
        }
    }

    private void uploadFlowSteps(CampaignUploadContext context, CampaignConfiguration uploaded)
        throws CampaignFlowStepValidationRestException, CampaignComponentValidationRestException {
        for (CampaignFlowStepConfiguration flowStep : uploaded.getFlowSteps()) {
            campaignFlowStepUploader.uploadFlowStep(context, flowStep);
        }
    }

    private void uploadCampaignLabels(CampaignUploadContext context, CampaignConfiguration uploaded)
        throws CampaignLabelValidationRestException {
        List<CampaignLabelConfiguration> labels = uploaded.getLabels().stream()
            // FIXME ENG-3905 remove this filter after campaignId is removed as default target label
            .filter(label -> label.getType() != com.extole.client.rest.campaign.configuration.CampaignLabelType.PROGRAM
                || !label.getName().equals(uploaded.getIncentiveId()))
            .collect(Collectors.toList());
        for (CampaignLabelConfiguration label : labels) {
            try {
                CampaignLabelBuilder labelBuilder = context.get(label);
                if (label.getName() != null) {
                    labelBuilder.withName(label.getName());
                }
                if (label.getType() != null) {
                    labelBuilder.withType(CampaignLabelType.valueOf(label.getType().name()));
                }
            } catch (CampaignLabelIllegalCharacterInNameException e) {
                throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                    .withErrorCode(CampaignLabelValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("name", label.getName())
                    .withCause(e)
                    .build();
            } catch (CampaignLabelNameLengthException e) {
                throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                    .withErrorCode(CampaignLabelValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getLabelName())
                    .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            } catch (CampaignLabelNameAlreadyDefinedException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
        }
    }

    private void createIncentiveFromIncentiveJson(Authorization authorization, IncentiveConfiguration incentive,
        Id<Campaign> sourceCampaignId, CampaignUploadContext context)
        throws QualityRuleValidationRestException, TransitionRuleValidationRestException,
        RewardRuleValidationRestException {
        for (QualityRuleConfiguration qualityRule : incentive.getQualityRules()) {
            QualityRuleBuilder qualityRuleBuilder = context.get(qualityRule);
            Set<RuleActionType> actionTypes = qualityRule.getActionTypes().stream()
                .map(actionType -> RuleActionType.valueOf(actionType.name())).collect(Collectors.toSet());
            qualityRuleBuilder.withActionTypes(actionTypes);
            if (qualityRule.getEnabled() != null) {
                qualityRuleBuilder.withEnabled(qualityRule.getEnabled().booleanValue());
            }
            for (Map.Entry<String, List<String>> entry : qualityRule.getProperties().entrySet()) {
                try {
                    qualityRuleBuilder.setProperty(entry.getKey(), entry.getValue());
                } catch (QualityRuleServiceKeyLengthException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_KEY_INVALID_LENGTH)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceIllegalCharacterInKeyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_KEY_INVALID_CHARACTER)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceValueLengthException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_LENGTH)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceIllegalCharacterInValueException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_CHARACTER)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidIntegerPropertyException
                    | QualityRuleServiceInvalidLongPropertyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_NUMBER)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidBooleanPropertyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_BOOLEAN)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidCountryPropertyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_COUNTRY)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidTimeUnitPropertyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_TIME_UNIT)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServicePropertyNotSupportedException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_UNSUPPORTED)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidDomainPropertyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_DOMAIN)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServicePropertyValueCountException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_INVALID_VALUE_COUNT)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidTemporalUnitPropertyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(
                            QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_VALUE_INVALID_TEMPORAL_UNIT)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidRegexpPropertyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_INVALID_REGEXP)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidSubnetPropertyException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_PROPERTY_INVALID_SUBNET)
                        .withCause(e)
                        .build();
                } catch (QualityRuleServiceInvalidExpressionTypeException e) {
                    throw RestExceptionBuilder.newBuilder(QualityRuleValidationRestException.class)
                        .withErrorCode(QualityRuleValidationRestException.QUALITY_RULE_INVALID_EXPRESSION_TYPE)
                        .addParameter("expression_type", e.getExpressionType())
                        .withCause(e)
                        .build();
                }
            }
        }
        for (TransitionRuleConfiguration transitionRule : incentive.getTransitionRules()) {
            TransitionRuleBuilder transitionRuleBuilder = context.get(transitionRule);
            try {
                if (transitionRule.getActionType() != null) {
                    transitionRuleBuilder.withActionType(RuleActionType.valueOf(transitionRule.getActionType().name()));
                }
                Duration transitionPeriod =
                    Duration.ofMillis(transitionRule.getTransitionPeriodMilliseconds().longValue());
                transitionRuleBuilder.withTransitionPeriod(transitionPeriod);
                transitionRuleBuilder.withApproveHighQuality(transitionRule.getApproveHighQuality().booleanValue());
                transitionRuleBuilder.withApproveLowQuality(transitionRule.getApproveLowQuality().booleanValue());
            } catch (IllegalValueInTransitionPeriodException e) {
                throw RestExceptionBuilder.newBuilder(TransitionRuleValidationRestException.class)
                    .withErrorCode(TransitionRuleValidationRestException.TRANSITION_PERIOD_MILLISECONDS_INVALID)
                    .addParameter("transition_period_milliseconds",
                        transitionRule.getTransitionPeriodMilliseconds())
                    .withCause(e)
                    .build();
            }
        }
        // CLIENT_ADMIN can cross client clone campaigns, reward_suppliers are not available cross client
        if (campaignAvailableForClient(authorization, sourceCampaignId)) {
            for (RewardRuleConfiguration rewardRule : incentive.getRewardRules()) {
                try {
                    duplicateRewardRule(context, rewardRule);
                } catch (IllegalValueInReferralsPerRewardException e) {
                    throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                        .withErrorCode(RewardRuleValidationRestException.ACTION_COUNT_INVALID)
                        .addParameter("action_count", rewardRule.getReferralsPerReward())
                        .withCause(e)
                        .build();
                } catch (IllegalValueInMinCartValueException e) {
                    throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                        .withErrorCode(RewardRuleValidationRestException.MIN_CART_VALUE_INVALID)
                        .addParameter("min_cart_value", rewardRule.getReferralsPerReward())
                        .withCause(e)
                        .build();
                } catch (ExpressionLengthException e) {
                    throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                        .withErrorCode(RewardRuleValidationRestException.EXPRESSION_VALUE_INVALID_LENGTH)
                        .addParameter("expression_value", rewardRule.getExpression().getValue())
                        .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                        .withCause(e)
                        .build();
                } catch (ExpressionTypeNotSupportedException e) {
                    throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                        .withErrorCode(RewardRuleValidationRestException.EXPRESSION_INVALID_TYPE)
                        .addParameter("expression_type", rewardRule.getExpression().getType())
                        .withCause(e)
                        .build();
                } catch (ExpressionMissingException e) {
                    throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                        .withErrorCode(RewardRuleValidationRestException.EXPRESSION_MISSING_VALUE)
                        .withCause(e)
                        .build();
                } catch (ExpressionInvalidException e) {
                    throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                        .withErrorCode(RewardRuleValidationRestException.EXPRESSION_VALUE_INVALID)
                        .addParameter("expression_value", rewardRule.getExpression().getValue())
                        .addParameter("expression_type", rewardRule.getExpression().getType())
                        .withCause(e)
                        .build();
                } catch (IllegalValueInRewardEveryXFriendActionsException e) {
                    throw RestExceptionBuilder.newBuilder(RewardRuleValidationRestException.class)
                        .withErrorCode(RewardRuleValidationRestException.REWARD_EVERY_X_FRIEND_ACTIONS)
                        .addParameter("reward_every_x_friend_actions", rewardRule.getRewardEveryXFriendActions())
                        .withCause(e)
                        .build();
                }
            }
        }
    }

    private class SimpleCampaignUploadBuilder implements CampaignUploadBuilder {

        private Campaign campaign;
        private InputStream inputStream;
        private FormDataContentDisposition contentDispositionHeader;
        private ZoneId timeZone;
        private String accessToken;
        private ObjectMapper mapper;

        @Override
        public CampaignUploadBuilder withCampaign(Campaign campaign) {
            this.campaign = campaign;
            return this;
        }

        @Override
        public CampaignUploadBuilder withInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        @Override
        public CampaignUploadBuilder
            withContentDispositionHeader(FormDataContentDisposition contentDispositionHeader) {
            this.contentDispositionHeader = contentDispositionHeader;
            return this;
        }

        @Override
        public CampaignUploadBuilder withTimeZone(ZoneId timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        @Override
        public CampaignUploadBuilder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        @Override
        public CampaignUploadBuilder withObjectMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        @Override
        public CampaignResponse upload() throws UserAuthorizationRestException, CampaignArchiveRestException,
            CampaignValidationRestException, RewardRuleValidationRestException,
            CampaignControllerValidationRestException, CampaignControllerActionRestException,
            CampaignControllerTriggerRestException, TransitionRuleValidationRestException,
            QualityRuleValidationRestException, CampaignLabelValidationRestException, CreativeArchiveRestException,
            CampaignFlowStepValidationRestException, CampaignRestException, SettingValidationRestException,
            BuildCampaignRestException, CampaignComponentValidationRestException,
            CampaignComponentAssetValidationRestException, CampaignFlowStepMetricValidationRestException,
            CampaignFlowStepAppValidationRestException, CampaignComponentRestException,
            GlobalCampaignRestException, ComponentTypeRestException, CampaignFrontendControllerValidationRestException {

            ClientAuthorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);
            CampaignBuilder campaignBuilder;
            try {
                campaignBuilder = campaign == null ? campaignService.create(authorization)
                    : campaignService.uploadEditCampaign(authorization, campaign.getId());
            } catch (CampaignNotFoundException | CampaignLockedException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> campaignArchive = parseCampaignArchive(inputStream, mapper);
            Map<String, ByteSource> creatives = (Map<String, ByteSource>) campaignArchive.get("creatives");
            Map<UploadedAssetId, ByteSource> componentAssets =
                (Map<UploadedAssetId, ByteSource>) campaignArchive.get("componentAssets");
            CampaignConfiguration uploaded = (CampaignConfiguration) campaignArchive.get("campaignJson");

            if (!uploaded.getCampaignLocks().isEmpty()) {
                throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                    .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                    .addParameter("campaign_id", Optional.ofNullable(campaign)
                        .map(value -> value.getId().getValue())
                        .orElse(StringUtils.EMPTY))
                    .addParameter("campaign_locks", uploaded.getCampaignLocks())
                    .build();
            }

            try {
                if (StringUtils.isNotBlank(uploaded.getName())) {
                    campaignBuilder.withName(uploaded.getName());
                }
            } catch (CampaignServiceNameLengthException e) {
                throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                    .withErrorCode(CampaignValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getName())
                    .addParameter("min_length", e.getMinLength())
                    .addParameter("max_length", e.getMaxLength())
                    .withCause(e)
                    .build();
            } catch (CampaignServiceIllegalCharacterInNameException e) {
                throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                    .withErrorCode(CampaignValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("name", uploaded.getName())
                    .withCause(e)
                    .build();
            } catch (CampaignServiceDuplicateNameException e) {
                throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                    .withErrorCode(CampaignValidationRestException.NAME_ALREADY_USED)
                    .addParameter("name", uploaded.getName())
                    .withCause(e)
                    .build();
            } catch (CampaignGlobalRenameException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
            if (!Strings.isNullOrEmpty(uploaded.getDescription())) {
                try {
                    campaignBuilder.withDescription(uploaded.getDescription());
                } catch (CampaignServiceDescriptionLengthException e) {
                    throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                        .withErrorCode(CampaignValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                        .addParameter("description", uploaded.getDescription())
                        .withCause(e)
                        .build();
                }
            }
            if (!Strings.isNullOrEmpty(uploaded.getProgramType())) {
                try {
                    campaignBuilder.withProgramType(uploaded.getProgramType());
                } catch (CampaignProgramTypeInvalidException e) {
                    throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                        .withErrorCode(CampaignValidationRestException.INVALID_PROGRAM_TYPE)
                        .addParameter("program_type", uploaded.getProgramType())
                        .withCause(e)
                        .build();
                } catch (CampaignProgramTypeEmptyException e) {
                    throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                        .withErrorCode(CampaignValidationRestException.PROGRAM_TYPE_EMPTY)
                        .withCause(e)
                        .build();
                }
            }
            if (uploaded.getThemeName().isPresent()) {
                try {
                    campaignBuilder.withThemeName(uploaded.getThemeName().get());
                } catch (CampaignThemeNameInvalidException e) {
                    throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                        .withErrorCode(CampaignValidationRestException.INVALID_THEME_NAME)
                        .addParameter("theme_name", uploaded.getThemeName().orElse(null))
                        .withCause(e)
                        .build();
                }
            }

            if (!uploaded.getTags().isEmpty()) {
                try {
                    campaignBuilder.withTags(uploaded.getTags());
                } catch (CampaignServiceTagInvalidException e) {
                    throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                        .withErrorCode(CampaignValidationRestException.INVALID_TAG)
                        .addParameter("tag", e.getTag())
                        .withCause(e)
                        .build();
                } catch (CampaignServiceTagsLengthException e) {
                    throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                        .withErrorCode(CampaignValidationRestException.TAGS_TOO_LONG)
                        .addParameter("tags_length", Integer.valueOf(e.getTagsLength()))
                        .addParameter("max_tags_length", Integer.valueOf(e.getMaxTagsLength()))
                        .withCause(e)
                        .build();
                }
            }

            CampaignUploadContextImpl context =
                campaign == null ? new CampaignUploadContextImpl(campaignBuilder, creatives, componentAssets)
                    : new CampaignUploadContextImpl(campaignBuilder, campaign, creatives, componentAssets);
            uploadCampaignComponents(context, uploaded);
            uploadSteps(context, uploaded, timeZone);
            uploadFlowSteps(context, uploaded);
            uploadCampaignLabels(context, uploaded);

            if (GLOBAL_CAMPAIGN_NAME.equals(uploaded.getName())) {
                campaignBuilder.removeQualityRules();
                campaignBuilder.removeTransitionRules();
            }

            IncentiveConfiguration incentive = uploaded.getIncentive();
            if (incentive != null) {
                createIncentiveFromIncentiveJson(authorization, incentive,
                    Id.valueOf(Strings.nullToEmpty(uploaded.getIncentiveId())), context);
            }
            uploaded.getVariantSelector()
                .ifDefined(variantSelector -> campaignBuilder.withVariantSelector(variantSelector));
            uploaded.getVariants().ifDefined(variants -> campaignBuilder.withVariants(variants));
            if (uploaded.getCampaignType() != null) {
                campaignBuilder.withCampaignType(Campaign.CampaignType.valueOf(uploaded.getCampaignType().name()));
            }
            if (campaign != null) {
                removeComponents(campaignBuilder, uploaded);
                removeSteps(campaignBuilder, uploaded);
                removeFlowSteps(campaignBuilder, uploaded);
                removeCampaignLabels(campaignBuilder, uploaded);

                if (incentive != null) {
                    removeRewardRules(campaignBuilder, incentive);
                    removeTransitionRules(campaignBuilder, incentive);
                }
            }
            return saveCampaign(contentDispositionHeader, authorization, campaignBuilder, uploaded, timeZone,
                Optional.ofNullable(campaign).map(value -> value.getId()));
        }

        private void removeCampaignLabels(CampaignBuilder campaignBuilder, CampaignConfiguration uploaded) {
            Set<String> remainingLabels = uploaded.getLabels()
                .stream()
                .map(CampaignLabelConfiguration::getName)
                .collect(Collectors.toSet());
            for (CampaignLabel label : campaign.getLabels()) {
                if (!remainingLabels.contains(label.getName())) {
                    campaignBuilder.removeLabel(label);
                }
            }
        }

        private void removeRewardRules(CampaignBuilder campaignBuilder, IncentiveConfiguration incentive) {
            Set<Id<RewardRuleConfiguration>> remainingRewardRules = incentive.getRewardRules()
                .stream()
                .map(RewardRuleConfiguration::getId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            for (RewardRule rewardRule : campaign.getRewardRules()) {
                if (!remainingRewardRules.contains(rewardRule.getId())) {
                    try {
                        campaignBuilder.removeRewardRule(rewardRule.getId());
                    } catch (RewardRuleNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        private void removeTransitionRules(CampaignBuilder campaignBuilder, IncentiveConfiguration incentive) {
            Set<Id<TransitionRuleConfiguration>> remainingTransitionRules = incentive.getTransitionRules()
                .stream()
                .map(TransitionRuleConfiguration::getTransitionRuleId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            for (TransitionRule transitionRule : campaign.getTransitionRules()) {
                if (!remainingTransitionRules.contains(transitionRule.getId())) {
                    try {
                        campaignBuilder.removeTransitionRule(transitionRule.getId());
                    } catch (TransitionRuleNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        private void removeComponents(CampaignBuilder campaignBuilder, CampaignConfiguration uploaded) {
            Optional<CampaignComponentConfiguration> uploadedRoot = uploaded.getComponents()
                .stream()
                .filter(component -> component.getName().equalsIgnoreCase(CampaignComponent.ROOT))
                .findFirst();
            Set<Id<CampaignComponentConfiguration>> remainingComponents = uploaded.getComponents()
                .stream()
                .map(CampaignComponentConfiguration::getId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            Map<Id<CampaignComponentConfiguration>, List<CampaignComponentSettingConfiguration>> remainingSettings =
                uploaded.getComponents()
                    .stream()
                    .filter(component -> component.getId().isPresent())
                    .collect(Collectors.toMap(component -> component.getId().getValue(),
                        CampaignComponentConfiguration::getSettings));
            Set<Id<CampaignComponentAssetConfiguration>> remainingAssets = uploaded.getComponents()
                .stream().flatMap(component -> component.getAssets().stream())
                .map(CampaignComponentAssetConfiguration::getId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            for (CampaignComponent component : campaign.getComponents()) {
                CampaignComponentBuilder componentBuilder = campaignBuilder.updateComponent(component);
                boolean isRoot = component.getName().equalsIgnoreCase(CampaignComponent.ROOT);
                if (!remainingComponents.contains(component.getId()) && !isRoot) {
                    campaignBuilder.removeComponent(component);
                    continue;
                }
                for (CampaignComponentAsset asset : component.getAssets()) {
                    if (!remainingAssets.contains(asset.getId())) {
                        componentBuilder.removeAsset(asset);
                    }
                }

                if (isRoot) {
                    if (uploadedRoot.isPresent()) {
                        for (Setting setting : component.getSettings()) {
                            if (uploadedRoot.get().getSettings().stream()
                                .noneMatch(candidate -> candidate.getName().equalsIgnoreCase(setting.getName()))) {
                                componentBuilder.removeSetting(setting);
                            }
                        }
                    } else {
                        for (Setting setting : component.getSettings()) {
                            componentBuilder.removeSetting(setting);
                        }
                    }
                } else {
                    for (Setting setting : component.getSettings()) {
                        if (remainingSettings.get(component.getId()).stream()
                            .noneMatch(candidate -> candidate.getName().equalsIgnoreCase(setting.getName()))) {
                            componentBuilder.removeSetting(setting);
                        }
                    }
                }
            }
        }

        private void removeFlowSteps(CampaignBuilder campaignBuilder, CampaignConfiguration uploaded) {
            Set<Id<CampaignFlowStepConfiguration>> remainingFlowSteps = uploaded.getFlowSteps()
                .stream()
                .map(CampaignFlowStepConfiguration::getFlowStepId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            Set<Id<CampaignFlowStepMetricConfiguration>> remainingMetrics = uploaded.getFlowSteps()
                .stream().flatMap(flowStep -> flowStep.getMetrics().stream())
                .map(CampaignFlowStepMetricConfiguration::getId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            Set<Id<CampaignFlowStepAppConfiguration>> remainingApps = uploaded.getFlowSteps()
                .stream().flatMap(flowStep -> flowStep.getApps().stream())
                .map(CampaignFlowStepAppConfiguration::getId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            for (CampaignFlowStep flowStep : campaign.getFlowSteps()) {

                for (CampaignFlowStepMetric metric : flowStep.getMetrics()) {
                    if (!remainingMetrics.contains(metric.getId())) {
                        campaignBuilder.updateFlowStep(flowStep).removeFlowStepMetric(metric);
                    }
                }

                for (CampaignFlowStepApp app : flowStep.getApps()) {
                    if (!remainingApps.contains(app.getId())) {
                        campaignBuilder.updateFlowStep(flowStep).removeFlowStepApp(app);
                    }
                }

                if (!remainingFlowSteps.contains(flowStep.getId())) {
                    campaignBuilder.removeFlowStep(flowStep);
                }
            }
        }

        private void removeSteps(CampaignBuilder campaignBuilder, CampaignConfiguration uploaded) {
            Set<Id<CampaignStepConfiguration>> remainingSteps = uploaded.getSteps()
                .stream()
                .map(CampaignStepConfiguration::getId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            Set<Id<CampaignControllerTriggerConfiguration>> remainingTriggers = uploaded.getSteps()
                .stream()
                .flatMap(step -> step.getTriggers().stream())
                .map(CampaignControllerTriggerConfiguration::getTriggerId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            Set<Id<CampaignControllerActionConfiguration>> remainingControllerActions = uploaded.getSteps()
                .stream()
                .filter(step -> step instanceof CampaignControllerConfiguration)
                .flatMap(step -> ((CampaignControllerConfiguration) step).getActions().stream())
                .map(CampaignControllerActionConfiguration::getActionId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            Set<Id<CampaignControllerActionConfiguration>> remainingFrontendControllerActions = uploaded.getSteps()
                .stream()
                .filter(step -> step instanceof CampaignFrontendControllerConfiguration)
                .flatMap(step -> ((CampaignFrontendControllerConfiguration) step).getActions().stream())
                .map(CampaignControllerActionConfiguration::getActionId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            Set<Id<StepDataConfiguration>> remainingStepData = uploaded.getSteps()
                .stream()
                .flatMap(step -> step.getData().stream())
                .map(StepDataConfiguration::getId)
                .filter(id -> id.isPresent())
                .map(id -> id.getValue())
                .collect(Collectors.toSet());
            for (CampaignStep step : campaign.getSteps()) {
                for (CampaignControllerTrigger trigger : step.getTriggers()) {
                    if (!remainingTriggers.contains(trigger.getId())) {
                        campaignBuilder.updateStep(step).removeTrigger(trigger);
                    }
                }
                for (StepData data : step.getData()) {
                    if (!remainingStepData.contains(data.getId())) {
                        campaignBuilder.updateStep(step).removeStepData(data);
                    }
                }
                if (step instanceof CampaignController) {
                    for (CampaignControllerAction action : ((CampaignController) step).getActions()) {
                        if (!remainingControllerActions.contains(action.getId())) {
                            campaignBuilder.updateController((CampaignController) step).removeAction(action);
                        }
                    }
                }
                if (step instanceof FrontendController) {
                    for (CampaignControllerAction action : ((FrontendController) step).getActions()) {
                        if (!remainingFrontendControllerActions.contains(action.getId())) {
                            campaignBuilder.updateFrontendController((FrontendController) step).removeAction(action);
                        }
                    }
                }
                if (!remainingSteps.contains(step.getId())) {
                    campaignBuilder.removeStep(step);
                }
            }
        }
    }

    private Map<String, Object> parseCampaignArchive(InputStream fileInputStream, ObjectMapper mapper)
        throws CampaignArchiveRestException {
        Map<String, Object> campaignArchive = new HashMap<>();
        CampaignConfiguration uploaded = null;
        Map<String, ByteSource> creatives = new HashMap<>();
        Map<UploadedAssetId, ByteSource> componentAssets = new HashMap<>();
        try (FileBackedOutputStream outputStream = new FileBackedOutputStream(THRESHOLD, true);
            InputStream closeableInputStream = fileInputStream) {
            ByteStreams.copy(closeableInputStream, outputStream);

            try (ZipInputStream zipInputStream = new ZipInputStream(outputStream.asByteSource().openBufferedStream())) {
                ZipEntry entry = zipInputStream.getNextEntry();
                while (entry != null) {
                    if (!entry.isDirectory()) {
                        String filename = Iterables.getLast(Arrays.asList(entry.getName().split("/")));

                        Matcher matcher = COMPONENT_ASSET_PATH_PATTERN.matcher(entry.getName());
                        if (matcher.matches()) {
                            String componentAbsoluteName = matcher.group(1).replace(COMPONENTS_FOLDER_NAME + "/", "");
                            String assetFilename = matcher.group(matcher.groupCount());
                            String assetName = StringUtils.substringBeforeLast(assetFilename, ".");

                            try (FileBackedOutputStream out = new FileBackedOutputStream(THRESHOLD, true)) {
                                ByteStreams.copy(zipInputStream, out);
                                ByteSource byteSource = out.asByteSource();
                                componentAssets.put(new UploadedAssetId(
                                    componentAbsoluteName.isEmpty() ? "/" : componentAbsoluteName, assetName),
                                    byteSource);
                            }
                        } else if (filename.equalsIgnoreCase(CAMPAIGN_JSON_FILENAME)) {
                            try (FileBackedOutputStream out = new FileBackedOutputStream(THRESHOLD, true)) {
                                ByteStreams.copy(zipInputStream, out);
                                try (InputStream in = out.asByteSource().openBufferedStream()) {
                                    String campaignJsonString = IOUtils.toString(in, StandardCharsets.UTF_8);
                                    uploaded = mapper.readValue(campaignJsonString, CampaignConfiguration.class);
                                }
                            }
                        } else {
                            String name = StringUtils.substringBeforeLast(filename, ".");
                            try (FileBackedOutputStream out = new FileBackedOutputStream(THRESHOLD, true)) {
                                ByteStreams.copy(zipInputStream, out);
                                creatives.put(name, out.asByteSource());
                            }
                        }
                    }

                    entry = zipInputStream.getNextEntry();
                }

                if (uploaded == null) {
                    LOG.error("Campaign archive missing " + CAMPAIGN_JSON_FILENAME);
                    throw RestExceptionBuilder.newBuilder(CampaignArchiveRestException.class)
                        .withErrorCode(CampaignArchiveRestException.MISSING_CAMPAIGN_JSON)
                        .build();
                }
            }
            campaignArchive.put("campaignJson", uploaded);
            campaignArchive.put("creatives", creatives);
            campaignArchive.put("componentAssets", componentAssets);

            return campaignArchive;
        } catch (IOException e) {
            throw RestExceptionBuilder.newBuilder(CampaignArchiveRestException.class)
                .withErrorCode(CampaignArchiveRestException.INVALID_CAMPAIGN_ARCHIVE)
                .withCause(e)
                .build();
        }
    }

    private boolean campaignAvailableForClient(Authorization authorization, Id<Campaign> sourceCampaignId) {
        try {
            campaignService.getPublishedOrDraftAnyStateCampaign(authorization, sourceCampaignId);
            return true;
        } catch (CampaignNotFoundException e) {
            return false;
        }
    }

    private void duplicateRewardRule(CampaignUploadContext context, RewardRuleConfiguration rewardRule)
        throws IllegalValueInMinCartValueException, IllegalValueInReferralsPerRewardException,
        ExpressionMissingException, ExpressionInvalidException, ExpressionLengthException,
        ExpressionTypeNotSupportedException, IllegalValueInRewardEveryXFriendActionsException {
        RewardRuleBuilder rewardRuleBuilder = context.get(rewardRule);
        rewardRuleBuilder.withMinCartValue(rewardRule.getMinCartValue());
        rewardRuleBuilder.withReferralsPerReward(rewardRule.getReferralsPerReward());
        rewardRuleBuilder.withRewardee(Rewardee.valueOf(rewardRule.getRewardee().name()));
        rewardRuleBuilder
            .withRuleActionType(RuleActionType.valueOf(rewardRule.getTriggerActionType().name()));
        rewardRuleBuilder.withRewardEveryXFriendActions(rewardRule.getRewardEveryXFriendActions());
        rewardRuleBuilder.withUniqueFriendRequired(rewardRule.getUniqueFriendRequired().booleanValue());
        rewardRuleBuilder.withReferralLoopAllowed(rewardRule.isReferralLoopAllowed().booleanValue());
        rewardRuleBuilder.withEmailRequired(rewardRule.isEmailRequired().booleanValue());
        rewardRuleBuilder
            .withCountRewardsBasedOnPartnerUserId(rewardRule.getCountRewardsBasedOnPartnerUserId().booleanValue());
        if (rewardRule.getExpression() != null && rewardRule.getExpression().getType() != null
            && rewardRule.getExpression().getValue() != null) {
            rewardRuleBuilder.withExpression(new RewardRuleExpression(rewardRule.getExpression().getValue(),
                ExpressionType.valueOf(rewardRule.getExpression().getType().name())));
        }
    }

}
