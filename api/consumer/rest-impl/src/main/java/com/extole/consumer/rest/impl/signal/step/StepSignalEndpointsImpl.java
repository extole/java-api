package com.extole.consumer.rest.impl.signal.step;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.signal.step.PartnerEventIdResponse;
import com.extole.consumer.rest.signal.step.QualityResults;
import com.extole.consumer.rest.signal.step.QualityResults.QualityScore;
import com.extole.consumer.rest.signal.step.QualityRuleResult;
import com.extole.consumer.rest.signal.step.StepSignalEndpoints;
import com.extole.consumer.rest.signal.step.StepSignalResponse;
import com.extole.event.pending.operation.PendingOperationStatus;
import com.extole.event.pending.operation.signal.StepSignalPendingOperationEvent;
import com.extole.event.pending.operation.signal.step.StepSignal;
import com.extole.id.Id;
import com.extole.signal.service.event.StepSignalPendingOperationReadService;

@Provider
public class StepSignalEndpointsImpl implements StepSignalEndpoints {

    private final ConsumerRequestContextService consumerRequestContextService;
    private final StepSignalPendingOperationReadService stepSignalPendingOperationReadService;
    private final HttpServletRequest servletRequest;

    @Autowired
    public StepSignalEndpointsImpl(ConsumerRequestContextService consumerRequestContextService,
        StepSignalPendingOperationReadService stepSignalPendingOperationReadService,
        @Context HttpServletRequest servletRequest) {
        this.consumerRequestContextService = consumerRequestContextService;
        this.stepSignalPendingOperationReadService = stepSignalPendingOperationReadService;
        this.servletRequest = servletRequest;
    }

    @Override
    public StepSignalResponse getStatus(String accessToken, String pollingId) throws AuthorizationRestException {
        Authorization authorization = getAuthorization(accessToken);

        List<StepSignalPendingOperationEvent> signalEvents =
            stepSignalPendingOperationReadService.get(authorization, Id.valueOf(pollingId));
        return toStepSignalResponse(pollingId, signalEvents);
    }

    private StepSignalResponse toStepSignalResponse(String id,
        List<? extends StepSignalPendingOperationEvent> signalEvents) {
        List<com.extole.consumer.rest.signal.step.StepSignal> signals = signalEvents.stream()
            .filter(signalEvent -> signalEvent.getStatus() == PendingOperationStatus.SUCCEEDED)
            .map(signalEvent -> toStepSignal(signalEvent.getSignal()))
            .collect(Collectors.toList());

        return new StepSignalResponse(id, signals);
    }

    private com.extole.consumer.rest.signal.step.StepSignal
        toStepSignal(StepSignal signal) {
        return new com.extole.consumer.rest.signal.step.StepSignal(signal.getName(), signal.isFirstSiteVisit(),
            signal.getPartnerEventId()
                .map(partnerEventId -> new PartnerEventIdResponse(partnerEventId.getName(), partnerEventId.getValue())),
            signal.getData(), signal.getAliases(), toQualityRuleResult(signal));
    }

    private QualityResults toQualityRuleResult(StepSignal signal) {
        List<QualityRuleResult> qualityRuleResults = signal
            .getQualityResults()
            .getQualityRuleResults()
            .stream()
            .map(item -> new QualityRuleResult(item.getRuleType(),
                QualityScore.valueOf(item.getQualityScore().name())))
            .collect(Collectors.toList());

        return new QualityResults(QualityScore.valueOf(signal.getQualityResults().getScore().name()),
            qualityRuleResults);
    }

    private Authorization getAuthorization(String accessToken) throws AuthorizationRestException {
        return consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build().getAuthorization();
    }
}
