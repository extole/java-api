package com.extole.reporting.rest.batch;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceValidationRestException;

@Path(BatchJobEndpoints.BATCH_JOB_ENDPOINTS)
@Tag(name = BatchJobEndpoints.BATCH_JOB_ENDPOINTS, description = "BatchJob")
public interface BatchJobEndpoints {

    String BATCH_JOB_ENDPOINTS = "/v6/batches";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a new BatchJob")
    BatchJobResponse create(@UserAccessTokenParam String accessToken,
        @Parameter(description = "BatchJob request", required = true) BatchJobCreateRequest request,
        @Parameter(description = "Optional TimeZone parameter") @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobValidationRestException,
        BatchJobDataSourceValidationRestException;

    @PUT
    @Path("/{batchId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Updates a BatchJob by id")
    BatchJobResponse update(@UserAccessTokenParam String accessToken,
        @Parameter(description = "BatchJob id", required = true) @PathParam("batchId") String batchId,
        BatchJobUpdateRequest request,
        @Parameter(description = "Optional TimeZone parameter") @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobValidationRestException,
        BatchJobRestException, BatchJobDataSourceValidationRestException, OmissibleRestException;

    @POST
    @Path("/{batchId}/cancel")
    @Operation(summary = "Cancel an existing batchJob")
    @Produces(MediaType.APPLICATION_JSON)
    BatchJobResponse cancel(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "BatchJob id") @PathParam("batchId") String batchId,
        @Parameter(description = "Optional TimeZone parameter") @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobRestException;

    @POST
    @Path("/{batchId}/expire")
    @Operation(summary = "Expire an existing batchJob")
    @Produces(MediaType.APPLICATION_JSON)
    BatchJobResponse expire(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "BatchJob id") @PathParam("batchId") String batchId,
        @Parameter(description = "Optional TimeZone parameter") @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobRestException;

    @DELETE
    @Path("/{batchId}")
    @Operation(summary = "Deletes an existing batchJob")
    @Produces(MediaType.APPLICATION_JSON)
    BatchJobResponse delete(@UserAccessTokenParam String accessToken,
        @Parameter(description = "BatchJob id") @PathParam("batchId") String batchId,
        @Parameter(description = "Optional TimeZone parameter") @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with batchJobs", description = "Returns a list with batchJobs")
    List<BatchJobResponse> list(
        @UserAccessTokenParam String accessToken,
        @BeanParam BatchJobQueryParams requestParams,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @GET
    @Path("/{batchId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a BatchJob by id")
    BatchJobResponse get(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "BatchJob id", required = true) @PathParam("batchId") String batchId,
        @Parameter(description = "Optional TimeZone parameter") @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobRestException;

    @GET
    @Path("/{batchId}/events-processed")
    @Produces(MediaType.APPLICATION_JSON)
    @Hidden
    BatchJobStatusResponse getBatchJobStatus(
        @UserAccessTokenParam String accessToken,
        @PathParam("batchId") String batchId)
        throws UserAuthorizationRestException, BatchJobRestException, BatchJobProgressRestException;
}
