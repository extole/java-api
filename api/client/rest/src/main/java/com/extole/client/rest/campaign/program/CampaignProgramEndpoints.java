package com.extole.client.rest.campaign.program;

import java.time.ZoneId;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignEnableRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.CampaignValidationRestException;
import com.extole.client.rest.campaign.label.CampaignLabelValidationRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/campaigns/program")
public interface CampaignProgramEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{programLabel}/rename")
    SuccessResponse renameCampaignsProgramLabel(@UserAccessTokenParam String accessToken,
        @PathParam("programLabel") String programLabel, Optional<CampaignProgramLabelRenameRequest> request)
        throws UserAuthorizationRestException, CampaignProgramLabelRenameRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignLabelValidationRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{program_label}/update")
    SuccessResponse updateCampaignsByProgramLabel(@UserAccessTokenParam String accessToken,
        @PathParam("program_label") String programLabel,
        CampaignProgramUpdateRequest updateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignEnableRestException, BuildCampaignRestException,
        CampaignValidationRestException, CampaignProgramRestException, CampaignUpdateRestException,
        CampaignRestException;

}
