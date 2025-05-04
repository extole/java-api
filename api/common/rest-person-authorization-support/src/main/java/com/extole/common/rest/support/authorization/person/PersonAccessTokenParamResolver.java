package com.extole.common.rest.support.authorization.person;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.common.rest.support.authorization.BaseAccessTokenParamResolver;

public class PersonAccessTokenParamResolver {

    public static class AuthorizationProviderSupplier implements Supplier<PersonAuthorizationProvider> {

        private final PersonAuthorizationService authorizationService;
        private final HttpServletRequest servletRequest;

        @Inject
        public AuthorizationProviderSupplier(PersonAuthorizationService authorizationService,
            HttpServletRequest servletRequest) {
            this.authorizationService = authorizationService;
            this.servletRequest = servletRequest;
        }

        @Override
        public PersonAuthorizationProvider get() {
            return new PersonAuthorizationProvider(authorizationService, servletRequest);
        }
    }

    public static class AccessTokenBinder extends BaseAccessTokenParamResolver.BaseAccessTokenBinder {

        @Override
        protected void configure() {
            super.configure();
            bindFactory(AuthorizationProviderSupplier.class).to(PersonAuthorizationProvider.class);
        }
    }
}
