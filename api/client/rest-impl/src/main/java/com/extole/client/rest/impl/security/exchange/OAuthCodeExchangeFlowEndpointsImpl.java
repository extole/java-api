package com.extole.client.rest.impl.security.exchange;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.security.exchange.OAuthCodeExchangeFlowEndpoints;
import com.extole.client.rest.security.exchange.OAuthCodeExchangeFlowRestException;
import com.extole.client.rest.security.exchange.OAuthFlowCodeExchangeRequest;
import com.extole.client.rest.security.exchange.OAuthFlowCodeExchangeResponse;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.client.security.exchange.KeyExchangeExternalServiceException;
import com.extole.model.service.client.security.exchange.KeyExchangeInvalidExchangeResponseException;
import com.extole.model.service.client.security.exchange.OAuthCodeClientAlreadyExistsException;
import com.extole.model.service.client.security.exchange.OAuthCodeExchangeClientKeyCreationFailedException;
import com.extole.model.service.client.security.exchange.OAuthCodeExchangeClientKeyNotFoundException;
import com.extole.model.service.client.security.exchange.OAuthCodeExchangeKeyExchangerNotFoundException;
import com.extole.model.service.client.security.exchange.OAuthCodeExchangeService;
import com.extole.model.service.client.security.exchange.OAuthKeyExchangeType;

@Provider
public class OAuthCodeExchangeFlowEndpointsImpl implements OAuthCodeExchangeFlowEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final OAuthCodeExchangeService oAuthCodeExchangeService;

    @Autowired
    public OAuthCodeExchangeFlowEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        OAuthCodeExchangeService oAuthCodeExchangeService) {
        this.authorizationProvider = authorizationProvider;
        this.oAuthCodeExchangeService = oAuthCodeExchangeService;
    }

    @Override
    public OAuthFlowCodeExchangeResponse exchange(String accessToken, OAuthFlowCodeExchangeRequest request)
        throws UserAuthorizationRestException, OAuthCodeExchangeFlowRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            com.extole.client.rest.security.exchange.OAuthKeyExchangeType oAuthKeyExchangeType =
                request.getOAuthKeyExchangeType()
                    .orElse(com.extole.client.rest.security.exchange.OAuthKeyExchangeType.STANDARD);
            Id<ClientKey> exchangedClientKey =
                oAuthCodeExchangeService.exchange(authorization,
                    Id.valueOf(request.getClientKeyId()), OAuthKeyExchangeType.valueOf(oAuthKeyExchangeType.name()),
                    request.getCode(), request.getRedirectUri());

            return new OAuthFlowCodeExchangeResponse(Id.valueOf(exchangedClientKey.getValue()));
        } catch (OAuthCodeExchangeClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(OAuthCodeExchangeFlowRestException.class)
                .withErrorCode(OAuthCodeExchangeFlowRestException.EXCHANGE_CLIENT_KEY_NOT_FOUND)
                .addParameter("client_key_internal_tag", e.getClientKeyId())
                .withCause(e).build();
        } catch (OAuthCodeExchangeKeyExchangerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(OAuthCodeExchangeFlowRestException.class)
                .withErrorCode(OAuthCodeExchangeFlowRestException.CLIENT_KEY_EXCHANGER_NOT_FOUND)
                .addParameter("client_key_exchanger_algorithm", e.getKeyType())
                .withCause(e).build();
        } catch (KeyExchangeExternalServiceException e) {
            throw RestExceptionBuilder.newBuilder(OAuthCodeExchangeFlowRestException.class)
                .withErrorCode(OAuthCodeExchangeFlowRestException.KEY_EXCHANGE_EXCEPTION)
                .addParameter("details", e.getLocalizedMessage())
                .withCause(e).build();
        } catch (OAuthCodeExchangeClientKeyCreationFailedException e) {
            throw RestExceptionBuilder.newBuilder(OAuthCodeExchangeFlowRestException.class)
                .withErrorCode(OAuthCodeExchangeFlowRestException.EXCHANGED_CLIENT_KEY_CREATION_FAILED)
                .withCause(e).build();
        } catch (KeyExchangeInvalidExchangeResponseException e) {
            throw RestExceptionBuilder.newBuilder(OAuthCodeExchangeFlowRestException.class)
                .withErrorCode(OAuthCodeExchangeFlowRestException.INVALID_KEY_EXCHANGE_RESPONSE)
                .withCause(e).build();
        } catch (OAuthCodeClientAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(OAuthCodeExchangeFlowRestException.class)
                .withErrorCode(OAuthCodeExchangeFlowRestException.CLIENT_KEY_ALREADY_EXISTS)
                .addParameter("client_key_id", e.getClientKeyId())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }
}
