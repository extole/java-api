package com.extole.client.rest.impl.settings;

import java.time.ZoneId;
import java.util.Collection;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.settings.ClientSettingsEndpoints;
import com.extole.client.rest.settings.ClientSettingsRequest;
import com.extole.client.rest.settings.ClientSettingsResponse;
import com.extole.client.rest.settings.ClientSettingsRestException;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.settings.ClientSettings;
import com.extole.model.service.client.settings.ClientSettingsBuilder;
import com.extole.model.service.client.settings.ClientSettingsService;
import com.extole.model.service.client.settings.InvalidTimeZoneException;
import com.extole.model.service.client.settings.UploadSshKeyException;

@Provider
public class ClientSettingsEndpointsImpl implements ClientSettingsEndpoints {

    private final ClientSettingsService settingService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    public ClientSettingsEndpointsImpl(ClientSettingsService settingService,
        ClientAuthorizationProvider authorizationProvider) {
        this.settingService = settingService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public ClientSettingsResponse update(String accessToken, ClientSettingsRequest request)
        throws ClientSettingsRestException, UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientSettingsBuilder builder = settingService.updateClientSettings(authorization);
            request.getTimeZone().ifPresent(timeZone -> builder.withTimeZone(timeZone));
            request.getHasPriorStepRespectContainer().ifPresent(
                hasPriorStepRespectContainer -> builder
                    .withHasPriorStepRespectContainer(hasPriorStepRespectContainer.booleanValue()));
            request.getIsolateOldDevices().ifPresent(
                isolateOldDevices -> builder.withIsolateOldDevices(isolateOldDevices.booleanValue()));

            ClientSettings settings = builder.save();
            return toResponse(settings);
        } catch (InvalidTimeZoneException e) {
            throw createClientSettingsException(ClientSettingsRestException.TIME_ZONE_INVALID,
                authorization.getClientId(), e.getTimeZone(), e);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (UploadSshKeyException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public ClientSettingsResponse get(String accessToken) throws UserAuthorizationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            ClientSettings settings = settingService.getSettings(authorization);
            return toResponse(settings);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public Collection<String> getAvailableTimezones(String accessToken) throws UserAuthorizationRestException {
        authorizationProvider.getClientAuthorization(accessToken);
        return new TreeSet<>(ZoneId.getAvailableZoneIds());
    }

    private static ClientSettingsResponse toResponse(ClientSettings clientSettings) {
        return new ClientSettingsResponse(
            clientSettings.getTimeZone().getId(),
            Boolean.valueOf(clientSettings.getHasPriorStepRespectContainer()),
            Boolean.valueOf(clientSettings.getIsolateOldDevices()));
    }

    private static ClientSettingsRestException createClientSettingsException(
        ErrorCode<ClientSettingsRestException> code, Id<ClientHandle> clientId, String timeZone, Exception e) {
        return createDefaultRestExceptionBuilder(code, clientId, e)
            .addParameter("time_zone", timeZone)
            .build();
    }

    private static RestExceptionBuilder<ClientSettingsRestException> createDefaultRestExceptionBuilder(
        ErrorCode<ClientSettingsRestException> code, Id<ClientHandle> clientId, Exception e) {
        return RestExceptionBuilder.newBuilder(ClientSettingsRestException.class)
            .withErrorCode(code)
            .addParameter("client_id", clientId.getValue())
            .withCause(e);
    }

}
