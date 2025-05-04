package com.extole.client.rest.impl.security.key;

import java.time.ZoneId;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyIntegrationEndpoints;
import com.extole.client.rest.security.key.ClientKeyResponse;
import com.extole.client.rest.security.key.integration.IntegrationClientKeyRestException;
import com.extole.client.rest.security.key.integration.IntegrationCreateRequest;
import com.extole.client.rest.security.key.integration.IntegrationType;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.client.security.IntegrationAppplicationAuthenticationClientKeyException;
import com.extole.model.service.client.security.IntegrationInvalidIntegrationCodeClientKeyException;
import com.extole.model.service.client.security.IntegrationInvalidRedirectUrlClientKeyException;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.IntegrationAssociatedWebhooksAreInUseException;
import com.extole.model.service.client.security.key.IntegrationNotFoundException;
import com.extole.model.service.client.security.key.InvalidClientKeyCodeException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.UnsupportedIntegrationTypeClientKeyException;
import com.extole.model.service.client.security.key.built.MissingClientKeyNameException;
import com.extole.model.service.client.security.key.exception.ClientKeyBuildException;
import com.extole.model.service.client.security.key.exception.InvalidClientKeyTagException;
import com.extole.model.service.client.security.key.integration.IntegrationInvalidClientKeyDescriptionException;
import com.extole.model.service.client.security.key.integration.IntegrationInvalidClientKeyNameException;
import com.extole.model.service.client.security.key.integration.IntegrationKeyBuilder;
import com.extole.model.service.client.security.key.integration.IntegrationService;

@Provider
public class ClientKeyIntegrationEndpointsImpl implements ClientKeyIntegrationEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;

    private final ClientKeyResponseMapperRepository clientKeyResponseMapperRepository;
    private final IntegrationService integrationService;

    @Autowired
    public ClientKeyIntegrationEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientKeyResponseMapperRepository clientKeyResponseMapperRepository,
        IntegrationService integrationService) {
        this.authorizationProvider = authorizationProvider;
        this.clientKeyResponseMapperRepository = clientKeyResponseMapperRepository;
        this.integrationService = integrationService;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public ClientKeyResponse create(String accessToken,
        IntegrationType integrationType,
        IntegrationCreateRequest createRequest, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, IntegrationClientKeyRestException, BuildClientKeyRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (Strings.isNullOrEmpty(createRequest.getCode())) {
            throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_MISSING_CODE)
                .build();
        }

        try {
            IntegrationKeyBuilder clientKeyBuilder = integrationService.createClientKey(authorization,
                com.extole.model.service.client.security.key.integration.IntegrationType
                    .valueOf(integrationType.name()));
            clientKeyBuilder.withCode(createRequest.getCode());
            createRequest.getDescription().ifPresent(description -> clientKeyBuilder.withDescription(description));
            createRequest.getName().ifPresent(name -> clientKeyBuilder.withName(name));
            createRequest.getTags().ifPresent(tags -> clientKeyBuilder.withTags(tags));
            ClientKey clientKey = clientKeyBuilder.build();
            return clientKeyResponseMapperRepository.getClientKeyResponseMapper(clientKey.getAlgorithm())
                .toResponse(clientKey, timeZone);
        } catch (UnsupportedIntegrationTypeClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_NOT_SUPPORTED)
                .addParameter("message", e.getMessage())
                .withCause(e)
                .build();
        } catch (IntegrationInvalidClientKeyNameException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_INVALID_NAME)
                .addParameter("name", createRequest.getName())
                .withCause(e)
                .build();
        } catch (InvalidClientKeyCodeException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_INVALID_NAME)
                .addParameter("code", createRequest.getCode())
                .withCause(e)
                .build();
        } catch (IntegrationInvalidClientKeyDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientKeyRestException.class)
                .withErrorCode(BuildClientKeyRestException.CLIENT_KEY_INVALID_DESCRIPTION)
                .addParameter("description", createRequest.getDescription())
                .withCause(e)
                .build();
        } catch (ClientKeyBuildException e) {
            if (e instanceof IntegrationInvalidIntegrationCodeClientKeyException) {
                throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                    .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_INVALID_CODE)
                    .addParameter("message", e.getMessage())
                    .withCause(e)
                    .build();
            } else if (e instanceof IntegrationAppplicationAuthenticationClientKeyException) {
                throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                    .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_INVALID_APP_AUTHENTICATION)
                    .addParameter("message", e.getMessage())
                    .withCause(e)
                    .build();
            } else if (e instanceof IntegrationInvalidRedirectUrlClientKeyException) {
                throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                    .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_INVALID_REDIRECT_URL)
                    .addParameter("message", e.getMessage())
                    .withCause(e)
                    .build();
            }
            throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_SETUP_FAILED)
                .addParameter("message", e.getMessage())
                .withCause(e)
                .build();
        } catch (AuthorizationException | MissingClientKeyAlgorithmException | ShortClientKeyException
            | MissingClientKeyException | InvalidClientKeyException | MissingClientKeyNameException
            | DuplicateClientKeyPartnerKeyIdException | IntegrationNotFoundException | InvalidClientKeyTagException e) {
            throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_SETUP_FAILED)
                .addParameter("message", e.getMessage())
                .withCause(e)
                .build();
        } catch (IntegrationAssociatedWebhooksAreInUseException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClientKeyResponse delete(String accessToken, IntegrationType integrationType,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, IntegrationClientKeyRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientKey clientKey = integrationService.deleteIntegration(authorization,
                com.extole.model.service.client.security.key.integration.IntegrationType.valueOf(
                    integrationType.name()));

            return clientKeyResponseMapperRepository.getClientKeyResponseMapper(clientKey.getAlgorithm())
                .toResponse(clientKey, timeZone);
        } catch (IntegrationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_NOT_FOUND)
                .addParameter("message", e.getMessage())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        } catch (IntegrationAssociatedWebhooksAreInUseException e) {
            throw RestExceptionBuilder.newBuilder(IntegrationClientKeyRestException.class)
                .withErrorCode(IntegrationClientKeyRestException.INTEGRATION_IN_USE)
                .addParameter("associated_webhook_action_ids", e.getWebhookActionsAssociatedWithWebhooks())
                .withCause(e)
                .build();
        }
    }
}
