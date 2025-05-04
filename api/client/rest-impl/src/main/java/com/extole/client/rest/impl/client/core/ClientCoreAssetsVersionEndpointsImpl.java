package com.extole.client.rest.impl.client.core;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.client.core.ClientCoreAssetsVersionEndpoints;
import com.extole.client.rest.client.core.ClientCoreAssetsVersionResponse;
import com.extole.client.rest.client.core.ClientCoreAssetsVersionRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.client.core.ClientCoreAssetsVersion;
import com.extole.model.service.client.core.ClientCoreAssetsVersionNotFoundException;
import com.extole.model.service.client.core.ClientCoreAssetsVersionService;

@Provider
public class ClientCoreAssetsVersionEndpointsImpl implements ClientCoreAssetsVersionEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientCoreAssetsVersionService clientCoreAssetsVersionService;
    private final ClientService clientService;

    @Inject
    public ClientCoreAssetsVersionEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientCoreAssetsVersionService clientCoreAssetsVersionService, ClientService clientService) {
        this.authorizationProvider = authorizationProvider;
        this.clientCoreAssetsVersionService = clientCoreAssetsVersionService;
        this.clientService = clientService;
    }

    @Override
    public ClientCoreAssetsVersionResponse incrementClientCoreAssetsVersion(String accessToken, String clientId,
        ZoneId timeZone) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (!authorization.getClientId().getValue().equals(clientId)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
        try {
            clientService.incrementClientCoreAssetsVersion(authorization);
            return toResponse(clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId()),
                timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ClientCoreAssetsVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public List<ClientCoreAssetsVersionResponse> list(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        // TODO move scope validations to service layer ENG-20365
        checkUserSupportAuthorizationScope(authorization);
        return clientCoreAssetsVersionService.getAll(authorization.getClientId()).stream()
            .map(clientCoreAssetsVersion -> toResponse(clientCoreAssetsVersion, timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public ClientCoreAssetsVersionResponse getLatestCoreAssetsVersion(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        checkUserSupportAuthorizationScope(authorization);
        try {
            ClientCoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId());
            return toResponse(coreAssetsVersion, timeZone);
        } catch (ClientCoreAssetsVersionNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public ClientCoreAssetsVersionResponse getCoreAssetsVersion(String accessToken, Long coreAssetsVersion,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientCoreAssetsVersionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        checkUserSupportAuthorizationScope(authorization);
        Optional<ClientCoreAssetsVersion> clientCoreAssetsVersion =
            clientCoreAssetsVersionService.getCoreAssetsVersion(authorization.getClientId(), coreAssetsVersion);
        if (clientCoreAssetsVersion.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(ClientCoreAssetsVersionRestException.class)
                .withErrorCode(ClientCoreAssetsVersionRestException.CORE_ASSETS_VERSION_NOT_FOUND)
                .addParameter("core_assets_version", coreAssetsVersion).build();
        }
        return toResponse(clientCoreAssetsVersion.get(), timeZone);
    }

    private ClientCoreAssetsVersionResponse toResponse(ClientCoreAssetsVersion coreAssetsVersion, ZoneId timeZone) {
        return new ClientCoreAssetsVersionResponse(coreAssetsVersion.getClientId().getValue(),
            coreAssetsVersion.getClientVersion(), coreAssetsVersion.getCoreAssetsVersion().getValue(),
            coreAssetsVersion.getCreatedDate().atZone(timeZone), coreAssetsVersion.getDebugMessage().orElse(null));
    }

    private void checkUserSupportAuthorizationScope(Authorization authorization) throws UserAuthorizationRestException {
        checkAuthorizationScope(authorization, Authorization.Scope.USER_SUPPORT);
    }

    private void checkAuthorizationScope(Authorization authorization, Authorization.Scope scope)
        throws UserAuthorizationRestException {
        if (!authorization.getScopes().contains(scope)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

}
