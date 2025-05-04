package com.extole.client.rest.impl.signal;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.signal.ClientSignalEndpoints;
import com.extole.client.rest.signal.Signal;
import com.extole.client.rest.signal.SignalResponse;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.event.pending.operation.PendingOperationStatus;
import com.extole.event.pending.operation.signal.SignalPendingOperationEvent;
import com.extole.id.Id;
import com.extole.signal.service.event.SignalPendingOperationReadService;

@Provider
public class ClientSignalEndpointsImpl implements ClientSignalEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final SignalPendingOperationReadService signalPendingOperationReadService;

    @Autowired
    public ClientSignalEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        SignalPendingOperationReadService signalPendingOperationReadService) {
        this.authorizationProvider = authorizationProvider;
        this.signalPendingOperationReadService = signalPendingOperationReadService;
    }

    @Override
    public SignalResponse getStatus(String accessToken, String id) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(authorization, Authorization.Scope.USER_SUPPORT);

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

    private void checkAccessRights(Authorization authorization, Authorization.Scope scope)
        throws UserAuthorizationRestException {
        if (!authorization.isAuthorized(authorization.getClientId(), scope)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }
}
