package com.extole.reporting.rest.processing_lag;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v1/processing-status")
public interface ProcessingStatusEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    ProcessingStatusResponse get(@UserAccessTokenParam String accessToken) throws UserAuthorizationRestException;

    @GET
    @Path("/{stageName}")
    @Produces(MediaType.APPLICATION_JSON)
    Optional<ProcessingStageStatusResponse> get(@UserAccessTokenParam String accessToken,
        @PathParam("stageName") String stageName)
        throws UserAuthorizationRestException, ProcessingStageRestException;
}
