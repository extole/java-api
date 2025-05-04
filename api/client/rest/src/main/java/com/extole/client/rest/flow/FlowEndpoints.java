package com.extole.client.rest.flow;

import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/flows")
public interface FlowEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<FlowStepResponse> getFlow(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam FlowQueryParams flowQueryParams)
        throws UserAuthorizationRestException, CampaignRestException, FlowRestException;

}
