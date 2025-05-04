package com.extole.consumer.rest.impl.signal;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.signal.Signal;
import com.extole.consumer.rest.signal.SignalEndpoints;
import com.extole.consumer.rest.signal.SignalResponse;
import com.extole.event.pending.operation.PendingOperationStatus;
import com.extole.event.pending.operation.signal.SignalPendingOperationEvent;
import com.extole.id.Id;
import com.extole.signal.service.event.SignalPendingOperationReadService;

@Provider
public class SignalEndpointsImpl implements SignalEndpoints {

    private final ConsumerRequestContextService consumerRequestContextService;
    private final SignalPendingOperationReadService signalPendingOperationReadService;
    private final HttpServletRequest servletRequest;

    @Autowired
    public SignalEndpointsImpl(ConsumerRequestContextService consumerRequestContextService,
        SignalPendingOperationReadService signalPendingOperationReadService,
        @Context HttpServletRequest servletRequest) {
        this.consumerRequestContextService = consumerRequestContextService;
        this.signalPendingOperationReadService = signalPendingOperationReadService;
        this.servletRequest = servletRequest;
    }

    @Override
    public SignalResponse getStatus(String accessToken, String id)
        throws AuthorizationRestException {
        consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build();
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        List<SignalPendingOperationEvent> signalEvents =
            signalPendingOperationReadService.get(authorization, Id.valueOf(id));
        return toSignalResponse(id, signalEvents);
    }

    private SignalResponse toSignalResponse(String id, List<SignalPendingOperationEvent> signalEvents) {
        List<Signal> signals = signalEvents.stream()
            .filter(signalEvent -> signalEvent.getStatus() == PendingOperationStatus.SUCCEEDED)
            .map(signalEvent -> new Signal(signalEvent.getName(), signalEvent.getData()))
            .collect(Collectors.toList());

        return new SignalResponse(id, signals);
    }

    private Authorization getAuthorizationFromRequest(String accessToken) throws AuthorizationRestException {
        return consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
    }

}
