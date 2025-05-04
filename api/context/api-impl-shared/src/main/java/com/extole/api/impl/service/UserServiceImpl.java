package com.extole.api.impl.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.impl.user.UserImpl;
import com.extole.api.service.UserService;
import com.extole.api.user.User;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.shared.user.UserCache;
import com.extole.security.backend.BackendAuthorization;
import com.extole.security.backend.BackendAuthorizationProvider;

public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final Id<ClientHandle> clientId;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final UserCache userCache;

    public UserServiceImpl(
        Id<ClientHandle> clientId,
        BackendAuthorizationProvider backendAuthorizationProvider,
        UserCache userCache) {
        this.clientId = clientId;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.userCache = userCache;
    }

    @Override
    public User getUserById(String userId) {
        Optional<com.extole.model.entity.user.User> userById = Optional.empty();
        try {
            BackendAuthorization authorization = backendAuthorizationProvider.getAuthorizationForBackend(clientId);
            userById = userCache.getById(authorization, Id.valueOf(userId));
        } catch (AuthorizationException e) {
            LOG.error("Unable to lookup user for clientId={} with userId={}", clientId, userId, e);
            return null;
        }

        return userById.map(value -> new UserImpl(value)).orElse(null);
    }

}
