package com.extole.client.rest.impl.version;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.change.monitor.service.ClientVersionService;
import com.extole.client.change.monitor.service.ClientVersionStatus;
import com.extole.client.change.monitor.service.UnknownClientVersion;
import com.extole.client.rest.version.ClientVersionEndpoints;
import com.extole.client.rest.version.ClientVersionResponse;
import com.extole.client.rest.version.ClientVersionRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;

@Provider
public class ClientVersionEndpointsImpl implements ClientVersionEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientVersionService clientVersionService;

    @Autowired
    public ClientVersionEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientVersionService clientVersionService) {
        this.authorizationProvider = authorizationProvider;
        this.clientVersionService = clientVersionService;
    }

    @Override
    public ClientVersionResponse get(String accessToken, String clientIdAsString, Integer version)
        throws UserAuthorizationRestException, ClientVersionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Id<ClientHandle> clientId = authorization.getClientId();
        if (!Strings.isNullOrEmpty(clientIdAsString)) {
            clientId = Id.valueOf(clientIdAsString);
        }
        try {
            if (version != null) {
                return toResponse(clientVersionService.getClientVersionStatus(authorization, clientId, version));
            }
            return toResponse(clientVersionService.getLatestClientVersionStatus(authorization, clientId));
        } catch (UnknownClientVersion e) {
            throw RestExceptionBuilder.newBuilder(ClientVersionRestException.class)
                .withErrorCode(ClientVersionRestException.UNKNOWN_CLIENT_VERSION)
                .addParameter("latest_known_version", e.getLatestKnownVersion())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private ClientVersionResponse toResponse(ClientVersionStatus clientVersionStatus) {
        return new ClientVersionResponse(clientVersionStatus.getClientId().getValue(), clientVersionStatus.getVersion(),
            clientVersionStatus.changesPending());
    }
}
