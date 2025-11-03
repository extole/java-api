package com.extole.reporting.rest.impl.processing_lag;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.reporting.rest.processing_lag.ProcessingStageRestException;
import com.extole.reporting.rest.processing_lag.ProcessingStageStatusResponse;
import com.extole.reporting.rest.processing_lag.ProcessingStatusEndpoints;
import com.extole.reporting.rest.processing_lag.ProcessingStatusResponse;
import com.extole.reporting.service.processing_lag.ProcessingLagService;
import com.extole.reporting.service.processing_lag.ProcessingStage;
import com.extole.reporting.service.processing_lag.ProcessingStageStatus;
import com.extole.reporting.service.processing_lag.ProcessingStatus;

@Provider
public class ProcessingStatusEndpointsImpl implements ProcessingStatusEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ProcessingLagService processingLagService;

    @Autowired
    public ProcessingStatusEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ProcessingLagService processingLagService) {
        this.authorizationProvider = authorizationProvider;
        this.processingLagService = processingLagService;
    }

    @Override
    public ProcessingStatusResponse get(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toResponse(processingLagService.get(authorization));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public Optional<ProcessingStageStatusResponse> get(String accessToken, String processingStage)
        throws UserAuthorizationRestException, ProcessingStageRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ProcessingStage stage = ProcessingStage.valueOf(processingStage.toUpperCase());
            return processingLagService.get(authorization, stage).map(this::toResponse);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(ProcessingStageRestException.class)
                .withErrorCode(ProcessingStageRestException.PROCESSING_STAGE_NOT_SUPPORTED)
                .addParameter("processing_stage", processingStage)
                .withCause(e)
                .build();
        }
    }

    private ProcessingStatusResponse toResponse(ProcessingStatus status) {
        return new ProcessingStatusResponse(status.processingStages().entrySet().stream()
            .collect(Collectors.toMap(
                entry -> com.extole.reporting.rest.processing_lag.ProcessingStage.valueOf(entry.getKey().name()),
                entry -> entry.getValue().map(this::toResponse))));
    }

    private ProcessingStageStatusResponse toResponse(ProcessingStageStatus stageStatus) {
        return new ProcessingStageStatusResponse(
            com.extole.reporting.rest.processing_lag.ProcessingStage.valueOf(stageStatus.processingStage().name()),
            stageStatus.processedUpToTime(), stageStatus.lastUpdated());
    }
}
