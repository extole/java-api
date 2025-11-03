package com.extole.client.rest.impl.campaign.label;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.label.CampaignLabelCreateRequest;
import com.extole.client.rest.campaign.label.CampaignLabelEndpoints;
import com.extole.client.rest.campaign.label.CampaignLabelResponse;
import com.extole.client.rest.campaign.label.CampaignLabelRestException;
import com.extole.client.rest.campaign.label.CampaignLabelUpdateRequest;
import com.extole.client.rest.campaign.label.CampaignLabelValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignLabel;
import com.extole.model.entity.campaign.CampaignLabelType;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.label.CampaignLabelBuildException;
import com.extole.model.service.campaign.label.CampaignLabelBuilder;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelIllegalCharacterInNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.label.CampaignLabelNameAlreadyDefinedException;
import com.extole.model.service.campaign.label.CampaignLabelNameLengthException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CampaignLabelEndpointsImpl implements CampaignLabelEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;
    private final CampaignLabelRestMapper campaignLabelRestMapper;

    @Autowired
    public CampaignLabelEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CampaignService campaignService, CampaignProvider campaignProvider,
        CampaignLabelRestMapper campaignLabelRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
        this.campaignLabelRestMapper = campaignLabelRestMapper;
    }

    @Override
    public CampaignLabelResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        CampaignLabelCreateRequest request)
        throws CampaignRestException, CampaignLabelValidationRestException, UserAuthorizationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }

        try {
            CampaignLabelBuilder labelBuilder = campaignBuilder.addLabel();
            if (request.getName() != null) {
                labelBuilder.withName(request.getName());
            }
            if (request.getType() != null) {
                labelBuilder.withType(CampaignLabelType.valueOf(request.getType().name()));
            }
            CampaignLabel label = labelBuilder.save();
            return campaignLabelRestMapper.toCampaignLabelResponse(label, ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignLabelMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_MISSING)
                .withCause(e).build();
        } catch (CampaignLabelDuplicateNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_ALREADY_IN_USE)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (CampaignLabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (CampaignLabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignLabelNameAlreadyDefinedException e) {
            // should not happen, on create we set the name only once
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public CampaignLabelResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String labelName,
        CampaignLabelUpdateRequest request)
        throws CampaignRestException, UserAuthorizationRestException,
        CampaignLabelRestException, BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignBuilder campaignBuilder;
        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }

        CampaignLabel campaignLabel = getCampaignLabel(campaign, labelName);
        try {
            CampaignLabelBuilder labelBuilder = campaignBuilder.updateLabel(campaignLabel);
            if (request.getType() != null) {
                labelBuilder.withType(CampaignLabelType.valueOf(request.getType().name()));
            }
            CampaignLabel updatedLabel = labelBuilder.save();
            return campaignLabelRestMapper.toCampaignLabelResponse(updatedLabel, ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignLabelMissingNameException | CampaignLabelDuplicateNameException e) {
            // should not happen, name cannot be changed on update
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public CampaignLabelResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String labelName)
        throws CampaignRestException, CampaignLabelRestException, UserAuthorizationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign;
        CampaignBuilder campaignBuilder;
        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }

        CampaignLabel campaignLabel = getCampaignLabel(campaign, labelName);
        try {
            campaignBuilder.removeLabel(campaignLabel).save();
            return campaignLabelRestMapper.toCampaignLabelResponse(campaignLabel, ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CreativeArchiveJavascriptException | CreativeArchiveBuilderException
            | CampaignServiceNameLengthException | CampaignServiceIllegalCharacterInNameException
            | CreativeVariableUnsupportedException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | CampaignFlowStepException
            | StepDataBuildException | CampaignComponentException | CampaignScheduleException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentFacetsNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public List<CampaignLabelResponse> list(String accessToken, String campaignId, String version)
        throws UserAuthorizationRestException, CampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);

        return campaign.getLabels().stream()
            .map(label -> campaignLabelRestMapper.toCampaignLabelResponse(label, ZoneOffset.UTC))
            .collect(Collectors.toList());
    }

    private CampaignLabel getCampaignLabel(Campaign campaign, String labelName)
        throws CampaignLabelRestException {
        return campaign.getLabels().stream()
            .filter(label -> label.getName().equalsIgnoreCase(labelName))
            // FIXME ENG-3905 remove this filter after campaignId is removed as default target label
            .filter(label -> label.getType() != CampaignLabelType.PROGRAM
                || !label.getName().equals(campaign.getId().getValue()))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignLabelRestException.class)
                .withErrorCode(CampaignLabelRestException.INVALID_CAMPAIGN_LABEL_NAME)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("label_name", labelName)
                .build());
    }

}
