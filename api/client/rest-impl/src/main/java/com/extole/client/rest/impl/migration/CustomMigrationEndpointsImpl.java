package com.extole.client.rest.impl.migration;

import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;

import com.google.common.annotations.Beta;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.migration.CustomMigrationEndpoints;
import com.extole.client.rest.migration.CustomMigrationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.Client;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.migration.custom.CustomMigrationService;
import com.extole.model.service.migration.custom.UnknownMigrationNameException;
import com.extole.security.backend.BackendAuthorization;
import com.extole.security.backend.BackendAuthorizationProvider;

@Provider
@Singleton
@Beta
@Path("/v1/custom-migrations")
public class CustomMigrationEndpointsImpl implements CustomMigrationEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CustomMigrationService customMigrationService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final ClientService clientService;

    @Autowired
    public CustomMigrationEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CustomMigrationService customMigrationService,
        BackendAuthorizationProvider backendAuthorizationProvider,
        ClientService clientService) {
        this.authorizationProvider = authorizationProvider;
        this.customMigrationService = customMigrationService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.clientService = clientService;
    }

    @POST
    @Path("/{migration_name}")
    public String start(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("migration_name") String migrationName,
        @QueryParam("client_ids") String clientIds,
        @QueryParam("migrate_all_clients") boolean migrateAllClients,
        @QueryParam("dry_run") boolean dryRun)
        throws UserAuthorizationRestException, CustomMigrationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            Set<Id<ClientHandle>> ids;
            if (migrateAllClients) {
                ids = getAllClients().stream().map(client -> client.getId()).collect(Collectors.toSet());
            } else {
                ids = Stream.of(StringUtils.split(Strings.nullToEmpty(clientIds), ","))
                    .map(clientId -> clientId.trim())
                    .filter(clientId -> StringUtils.isNotEmpty(clientId))
                    .map(clientId -> Id.<ClientHandle>valueOf(clientId))
                    .collect(Collectors.toSet());
            }
            return customMigrationService.start(authorization, migrationName, ids, dryRun).getValue();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UnknownMigrationNameException e) {
            throw RestExceptionBuilder.newBuilder(CustomMigrationRestException.class)
                .withErrorCode(CustomMigrationRestException.UNKNOWN_MIGRATION)
                .addParameter("reference", "name:" + migrationName)
                .build();
        } catch (RejectedExecutionException e) {
            throw RestExceptionBuilder.newBuilder(CustomMigrationRestException.class)
                .withCause(e)
                .withErrorCode(CustomMigrationRestException.CAPACITY_LIMIT)
                .build();
        }
    }

    @POST
    @Path("/{migration_name}/campaign")
    public String migrateCampaign(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("migration_name") String migrationName,
        @QueryParam("campaign_id") String campaignId)
        throws UserAuthorizationRestException, CustomMigrationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            customMigrationService
                .migrateCampaign(authorization, migrationName, authorization.getClientId(), Id.valueOf(campaignId));
            return campaignId;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UnknownMigrationNameException e) {
            throw RestExceptionBuilder.newBuilder(CustomMigrationRestException.class)
                .withErrorCode(CustomMigrationRestException.UNKNOWN_MIGRATION)
                .addParameter("reference", "name:" + migrationName)
                .build();
        } catch (RejectedExecutionException e) {
            throw RestExceptionBuilder.newBuilder(CustomMigrationRestException.class)
                .withCause(e)
                .withErrorCode(CustomMigrationRestException.CAPACITY_LIMIT)
                .build();
        }
    }

    private List<Client> getAllClients() {
        BackendAuthorization authorization = backendAuthorizationProvider.getSuperuserAuthorizationForBackend();
        try {
            return clientService.getAll(authorization);
        } catch (AuthorizationException e) {
            throw new IllegalStateException("Unexpected Error while loading all clients ", e);
        }
    }

}
