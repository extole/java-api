package com.extole.reporting.rest.impl.batch;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.rest.batch.BatchJobLocalEndpoints;
import com.extole.reporting.rest.batch.BatchJobProgressRestException;
import com.extole.reporting.rest.batch.BatchJobRestException;
import com.extole.reporting.rest.batch.BatchJobStatusResponse;
import com.extole.reporting.service.batch.BatchJobEventsProcessed;
import com.extole.reporting.service.batch.BatchJobNotFoundException;
import com.extole.reporting.service.batch.BatchJobProgressNotFoundException;
import com.extole.reporting.service.batch.BatchJobProgressRetrievalException;
import com.extole.reporting.service.batch.BatchJobService;

@Provider
public class BatchJobLocalEndpointsImpl implements BatchJobLocalEndpoints {

    private final BatchJobService batchJobService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    public BatchJobLocalEndpointsImpl(BatchJobService batchJobService,
        ClientAuthorizationProvider authorizationProvider) {
        this.batchJobService = batchJobService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public BatchJobStatusResponse getLocalBatchJobStatus(String accessToken, String batchId)
        throws UserAuthorizationRestException, BatchJobRestException, BatchJobProgressRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<BatchJobEventsProcessed> eventsProcessed =
                batchJobService.getEventsProcessed(authorization, Id.valueOf(batchId));
            return eventsProcessed
                .map(item -> new BatchJobStatusResponse(item.getEventsProcessed(), item.getEventsToProcess()))
                .orElse(new BatchJobStatusResponse(0, 0));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BatchJobNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.NOT_FOUND)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (BatchJobProgressRetrievalException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobProgressRestException.class)
                .withErrorCode(BatchJobProgressRestException.PROGRESS_RETRIEVAL_FAILURE)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (BatchJobProgressNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobProgressRestException.class)
                .withErrorCode(BatchJobProgressRestException.PROGRESS_NOT_FOUND)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        }
    }
}
