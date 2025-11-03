package com.extole.security.backend;

import static com.extole.authorization.service.Authorization.Scope;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.change.model.ClientChangeOperation;
import com.extole.common.event.KafkaRetryException;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.metrics.GuavaCacheMetrics;
import com.extole.event.model.change.ClientChangeEvent;
import com.extole.event.model.change.ClientChangeEventListener;
import com.extole.id.Id;
import com.extole.model.entity.client.Client;
import com.extole.model.pojo.client.ClientPojo;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.client.ClientService;

@Component
public class BackendAuthorizationProvider {

    private static final String EXCEPTION_MESSAGE = "Unable to get authorization for client=";
    private static final Set<Authorization.Scope> BACKEND_SCOPES = Set.of(
        Authorization.Scope.USER_SUPPORT,
        Authorization.Scope.CLIENT_ADMIN,
        Authorization.Scope.BACKEND);

    private LoadingCache<Id<ClientHandle>, BackendAuthorization> backendAuthorizationCache;
    private LoadingCache<Id<ClientHandle>, BackendAuthorization> backendSuperUserAuthorizationCache;

    private final ClientService clientService;
    private final ExtoleMetricRegistry metricRegistry;
    private final String instanceName;

    @Autowired
    BackendAuthorizationProvider(ClientService clientService,
        ExtoleMetricRegistry metricRegistry,
        @Value("${extole.instance.name}") String instanceName) {
        this.clientService = clientService;
        this.metricRegistry = metricRegistry;
        this.instanceName = instanceName;
    }

    @PostConstruct
    public void initialize() {
        Set<Scope> superUserScopes = ImmutableSet.<Scope>builder().addAll(BACKEND_SCOPES)
            .add(Scope.CLIENT_SUPERUSER)
            .build();

        this.backendAuthorizationCache =
            CacheBuilder.newBuilder().recordStats()
                .build(new CacheLoader<>() {
                    @Override
                    public BackendAuthorization load(Id<ClientHandle> clientId) throws Exception {
                        if (Client.EXTOLE_CLIENT_ID.equals(clientId)) {
                            return createAuthorization(Client.EXTOLE_CLIENT_ID, superUserScopes);
                        }
                        return createAuthorization(clientId, BACKEND_SCOPES);
                    }
                });
        metricRegistry.registerAll(
            GuavaCacheMetrics.metricsFor("backendAuthorizationCache", this.backendAuthorizationCache));

        this.backendSuperUserAuthorizationCache =
            CacheBuilder.newBuilder().recordStats()
                .build(new CacheLoader<>() {
                    @Override
                    public BackendAuthorization load(Id<ClientHandle> clientId) throws Exception {
                        if (Client.EXTOLE_CLIENT_ID.equals(clientId)) {
                            return createAuthorization(Client.EXTOLE_CLIENT_ID, superUserScopes);
                        }
                        return createAuthorization(clientId, superUserScopes);
                    }
                });
        metricRegistry.registerAll(
            GuavaCacheMetrics.metricsFor("backendSuperUserAuthorizationCache", this.backendAuthorizationCache));
    }

    public BackendAuthorization getAuthorizationForBackend(Id<ClientHandle> clientId) throws AuthorizationException {
        try {
            return backendAuthorizationCache.get(clientId);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof AuthorizationException) {
                throw (AuthorizationException) e.getCause();
            }
            throw new AuthorizationException(EXCEPTION_MESSAGE + clientId, e);
        }
    }

    public BackendAuthorization getSuperuserAuthorizationForBackend(Id<ClientHandle> clientId)
        throws AuthorizationException {
        try {
            return backendSuperUserAuthorizationCache.get(clientId);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof AuthorizationException) {
                throw (AuthorizationException) e.getCause();
            }
            throw new AuthorizationException(EXCEPTION_MESSAGE + clientId, e);
        }
    }

    public BackendAuthorization getSuperuserAuthorizationForBackend() {
        try {
            return backendSuperUserAuthorizationCache.get(Client.EXTOLE_CLIENT_ID);
        } catch (ExecutionException e) {
            throw new BackendAuthorizationProviderRuntimeException(
                "Could not create backend authorization for extole client", e);
        }
    }

    private BackendAuthorization createAuthorization(Id<ClientHandle> clientId, Set<Scope> scopes)
        throws AuthorizationException {
        validateNonArchiveClientExists(clientId);
        if (Client.EXTOLE_CLIENT_ID.equals(clientId)) {
            scopes = ImmutableSet.<Scope>builder().addAll(BACKEND_SCOPES)
                .add(Scope.CLIENT_SUPERUSER)
                .build();
        }

        return BackendAuthorizationImpl.create()
            .withClientId(clientId)
            .withInstanceName(instanceName)
            .withScopes(scopes)
            .build();
    }

    private void validateNonArchiveClientExists(Id<ClientHandle> clientId) throws AuthorizationException {
        try {
            clientService.getPublicClientById(clientId);
        } catch (ClientNotFoundException e) {
            throw new AuthorizationException("Client " + clientId + " is archived or does not exist", e);
        }
    }

    @Component
    class BackendAuthorizationClientChangeListener implements ClientChangeEventListener<ClientPojo> {

        @Override
        public void handleEvent(ClientChangeEvent<ClientPojo> event) throws KafkaRetryException {
            if (event.getOperation() != ClientChangeOperation.DELETED) {
                return;
            }
            backendAuthorizationCache.invalidate(event.getClientId());
            backendSuperUserAuthorizationCache.invalidate(event.getClientId());
        }

        @Override
        public Class<ClientPojo> getKeyClass() {
            return ClientPojo.class;
        }
    }

    private static class BackendAuthorizationProviderRuntimeException extends RuntimeException {
        BackendAuthorizationProviderRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
