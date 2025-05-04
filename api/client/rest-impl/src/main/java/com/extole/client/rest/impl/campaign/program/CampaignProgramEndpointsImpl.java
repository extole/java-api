package com.extole.client.rest.impl.campaign.program;

import java.time.ZoneId;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.label.CampaignLabelValidationRestException;
import com.extole.client.rest.campaign.program.CampaignProgramEndpoints;
import com.extole.client.rest.campaign.program.CampaignProgramLabelRenameRequest;
import com.extole.client.rest.campaign.program.CampaignProgramLabelRenameRestException;
import com.extole.client.rest.campaign.program.CampaignProgramRestException;
import com.extole.client.rest.campaign.program.CampaignProgramUpdateRequest;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignProgramTypeEmptyException;
import com.extole.model.service.campaign.CampaignProgramTypeInvalidException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.label.CampaignLabelIllegalCharacterInNameException;
import com.extole.model.service.campaign.label.CampaignLabelNameLengthException;
import com.extole.model.service.campaign.program.CampaignProgramLabelAlreadyInUseException;
import com.extole.model.service.campaign.program.CampaignProgramLabelNotFoundException;
import com.extole.model.service.campaign.program.CampaignProgramService;

@Provider
public class CampaignProgramEndpointsImpl implements CampaignProgramEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProgramService campaignProgramService;

    @Inject
    public CampaignProgramEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CampaignProgramService campaignProgramService) {
        this.authorizationProvider = authorizationProvider;
        this.campaignProgramService = campaignProgramService;
    }

    @Override
    public SuccessResponse renameCampaignsProgramLabel(String accessToken, String programLabel,
        Optional<CampaignProgramLabelRenameRequest> request)
        throws UserAuthorizationRestException, CampaignProgramLabelRenameRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignLabelValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (request.isEmpty() || Strings.isNullOrEmpty(request.get().getProgramLabel())) {
            throw RestExceptionBuilder.newBuilder(CampaignProgramLabelRenameRestException.class)
                .withErrorCode(CampaignProgramLabelRenameRestException.PROGRAM_LABEL_MISSING).build();
        }

        try {
            campaignProgramService.renameCampaignsProgramLabels(authorization, programLabel,
                request.get().getProgramLabel());
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignLabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignProgramLabelRenameRestException.class)
                .withErrorCode(CampaignProgramLabelRenameRestException.PROGRAM_LABEL_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", request.get().getProgramLabel())
                .withCause(e).build();
        } catch (CampaignLabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignProgramLabelNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignProgramLabelRenameRestException.class)
                .withErrorCode(CampaignProgramLabelRenameRestException.PROGRAM_LABEL_NOT_FOUND)
                .addParameter("name", programLabel)
                .withCause(e).build();
        } catch (CampaignProgramLabelAlreadyInUseException e) {
            throw RestExceptionBuilder.newBuilder(CampaignProgramLabelRenameRestException.class)
                .withErrorCode(CampaignProgramLabelRenameRestException.PROGRAM_LABEL_ALREADY_IN_USE)
                .addParameter("name", request.get().getProgramLabel())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }

        return SuccessResponse.SUCCESS;
    }

    @Override
    public SuccessResponse updateCampaignsByProgramLabel(String accessToken, String programLabel,
        CampaignProgramUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignProgramRestException, CampaignUpdateRestException,
        CampaignRestException {
        if (Strings.isNullOrEmpty(updateRequest.getProgramType())) {
            return new SuccessResponse("PROGRAM_TYPE_EMPTY");
        }
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            campaignProgramService.updateProgramTypeByProgramLabel(authorization, programLabel,
                updateRequest.getProgramType());
            return SuccessResponse.SUCCESS;
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (CampaignProgramTypeInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignProgramRestException.class)
                .withErrorCode(CampaignProgramRestException.INVALID_PROGRAM_TYPE)
                .addParameter("program_type", updateRequest.getProgramType())
                .withCause(e)
                .build();
        } catch (CampaignProgramTypeEmptyException e) {
            throw RestExceptionBuilder.newBuilder(CampaignProgramRestException.class)
                .withErrorCode(CampaignProgramRestException.PROGRAM_TYPE_EMPTY)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

}
