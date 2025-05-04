package com.extole.common.rest.support.authorization.client;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.extole.authorization.service.client.ClientAuthorizationService;
import com.extole.common.rest.support.authorization.BaseAccessTokenParamResolver;

public class ClientAccessTokenParamResolver {

    public static class AuthorizationProviderSupplier implements Supplier<ClientAuthorizationProvider> {

        private final ClientAuthorizationService authorizationService;
        private final HttpServletRequest servletRequest;

        @Inject
        public AuthorizationProviderSupplier(ClientAuthorizationService authorizationService,
            HttpServletRequest servletRequest) {
            this.authorizationService = authorizationService;
            this.servletRequest = servletRequest;
        }

        @Override
        public ClientAuthorizationProvider get() {
            return new ClientAuthorizationProvider(authorizationService, servletRequest);
        }
    }

    public static class AccessTokenBinder extends BaseAccessTokenParamResolver.BaseAccessTokenBinder {

        @Override
        protected void configure() {
            super.configure();
            bindFactory(AuthorizationProviderSupplier.class).to(ClientAuthorizationProvider.class);
        }
    }
}
