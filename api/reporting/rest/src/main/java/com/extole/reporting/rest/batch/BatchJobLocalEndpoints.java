package com.extole.reporting.rest.batch;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v6/batches/local")
public interface BatchJobLocalEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{batchId}/events-processed")
    BatchJobStatusResponse getLocalBatchJobStatus(@UserAccessTokenParam String accessToken,
        @PathParam("batchId") String batchId)
        throws UserAuthorizationRestException, BatchJobRestException, BatchJobProgressRestException;
}
