package com.extole.client.rest.impl.targeting.settings;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.targeting.settings.TargetingSettingsEndpoints;
import com.extole.client.rest.targeting.settings.TargetingSettingsResponse;
import com.extole.client.rest.targeting.settings.TargetingSettingsRestException;
import com.extole.client.rest.targeting.settings.TargetingSettingsUpdateRequest;
import com.extole.client.rest.targeting.settings.TargetingVersion;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.client.targeting.TargetingSettings;
import com.extole.model.service.client.targeting.TargetingSettingsBuilder;
import com.extole.model.service.client.targeting.TargetingSettingsDryRunVersionMatchesExecutionVersionException;
import com.extole.model.service.client.targeting.TargetingSettingsService;
import com.extole.model.service.client.targeting.TargetingSettingsVersionIsMissingException;

@Provider
public class TargetingSettingsEndpointsImpl implements TargetingSettingsEndpoints {

    private final TargetingSettingsService targetingSettingsService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    public TargetingSettingsEndpointsImpl(
        TargetingSettingsService targetingSettingsService,
        ClientAuthorizationProvider authorizationProvider) {
        this.targetingSettingsService = targetingSettingsService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public TargetingSettingsResponse get(String accessToken)
        throws UserAuthorizationRestException, TargetingSettingsRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            TargetingSettings settings = targetingSettingsService.get(authorization);

            return toResponse(settings);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public TargetingSettingsResponse update(String accessToken, TargetingSettingsUpdateRequest request)
        throws UserAuthorizationRestException, TargetingSettingsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            TargetingSettingsBuilder builder = targetingSettingsService.createOrUpdate(authorization);

            request.getVersion().ifPresent(version -> {
                try {
                    builder.withVersion(
                        com.extole.model.entity.client.targeting.TargetingVersion.valueOf(version.name()));
                } catch (TargetingSettingsVersionIsMissingException e) {
                    throw RestExceptionBuilder.newBuilder(TargetingSettingsRestException.class)
                        .withErrorCode(TargetingSettingsRestException.INVALID_TARGETING_VERSION)
                        .withCause(e)
                        .build();
                }
            });

            request.getDryRunVersion().ifPresent(version -> {
                if (version.isEmpty()) {
                    builder.clearDryRunVersion();
                } else {
                    builder.withDryRunVersion(
                        com.extole.model.entity.client.targeting.TargetingVersion.valueOf(version.get().name()));
                }
            });

            return toResponse(builder.save());
        } catch (TargetingSettingsDryRunVersionMatchesExecutionVersionException e) {
            throw RestExceptionBuilder.newBuilder(TargetingSettingsRestException.class)
                .withErrorCode(TargetingSettingsRestException.INVALID_TARGETING_VERSION)
                .addParameter("version", e.getVersion())
                .addParameter("dry_run_version", e.getDryRunVersion())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private TargetingSettingsResponse toResponse(TargetingSettings settings) {
        TargetingSettingsResponse.Builder builder = TargetingSettingsResponse.builder()
            .withVersion(TargetingVersion.valueOf(settings.getVersion().name()));
        settings.getDryRunVersion().ifPresent(
            version -> builder.withDryRunVersion(TargetingVersion.valueOf(version.name())));
        return builder.build();
    }
}
