package com.extole.api.impl.service;

import com.extole.api.impl.ContextApiRuntimeException;
import com.extole.api.service.AudienceMembershipService;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.service.audience.AudienceNotFoundException;
import com.extole.person.service.audience.membership.AudienceMembershipNotFoundException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonNotIdentifiedException;
import com.extole.security.backend.BackendAuthorization;
import com.extole.security.backend.BackendAuthorizationProvider;

public class AudienceMembershipServiceImpl implements AudienceMembershipService {

    private final Id<ClientHandle> clientId;
    private final com.extole.audience.membership.service.AudienceMembershipService audienceMembershipService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;

    public AudienceMembershipServiceImpl(
        Id<ClientHandle> clientId,
        com.extole.audience.membership.service.AudienceMembershipService audienceMembershipService,
        BackendAuthorizationProvider backendAuthorizationProvider) {
        this.clientId = clientId;
        this.audienceMembershipService = audienceMembershipService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
    }

    @Override
    public void create(String audienceId, String personId) {
        Authorization authorization = getAuthorization();
        try {
            audienceMembershipService.create(authorization, Id.valueOf(audienceId), Id.valueOf(personId));
        } catch (AuthorizationException | PersonNotFoundException | PersonNotIdentifiedException
            | AudienceNotFoundException e) {
            throw new ContextApiRuntimeException("Failed to create audience membership for client " + clientId + " " +
                "person " + personId + " and audience " + audienceId, e);
        }
    }

    @Override
    public void remove(String audienceId, String personId) {
        Authorization authorization = getAuthorization();
        try {
            audienceMembershipService.delete(authorization, Id.valueOf(audienceId), Id.valueOf(personId));
        } catch (AuthorizationException | PersonNotFoundException | AudienceMembershipNotFoundException
            | AudienceNotFoundException e) {
            throw new ContextApiRuntimeException("Failed to delete audience membership for client " + clientId + " " +
                "person " + personId + " and audience " + audienceId, e);
        }
    }

    private BackendAuthorization getAuthorization() {
        try {
            return backendAuthorizationProvider.getAuthorizationForBackend(clientId);
        } catch (AuthorizationException e) {
            throw new ContextApiRuntimeException("Failed to get authorization for client " + clientId, e);
        }
    }
}
