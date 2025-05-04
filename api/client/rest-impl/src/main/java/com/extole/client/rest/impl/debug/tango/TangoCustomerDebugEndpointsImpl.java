package com.extole.client.rest.impl.debug.tango;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.debug.tango.TangoDebugAccountViewSummary;
import com.extole.client.rest.debug.tango.TangoDebugCustomerEndpoints;
import com.extole.client.rest.debug.tango.TangoDebugCustomerViewSummary;
import com.extole.client.rest.debug.tango.TangoDebugRestException;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.tango.api.TangoApiException;
import com.extole.tango.api.customer.CustomerIdentifierInvalidException;
import com.extole.tango.api.customer.TangoCustomerApi;

@Provider
public class TangoCustomerDebugEndpointsImpl implements TangoDebugCustomerEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final TangoCustomerApi tangoCustomerApi;

    @Autowired
    public TangoCustomerDebugEndpointsImpl(TangoCustomerApi tangoCustomerApi,
        ClientAuthorizationProvider authorizationProvider) {
        this.tangoCustomerApi = tangoCustomerApi;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<TangoDebugCustomerViewSummary> list(String accessToken)
        throws UserAuthorizationRestException, TangoConnectionRestException {
        requireSuperUser(authorizationProvider.getClientAuthorization(accessToken));
        try {
            return tangoCustomerApi.list().stream().map(customer -> {
                return mapCustomer(customer);
            }).collect(Collectors.toList());
        } catch (TangoApiException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e).build();
        }
    }

    @Override
    public TangoDebugCustomerViewSummary getByCustomerIdentifier(String accessToken, String customerIdentifier)
        throws UserAuthorizationRestException, TangoDebugRestException, TangoConnectionRestException {
        requireSuperUser(authorizationProvider.getClientAuthorization(accessToken));
        try {
            return mapCustomer(tangoCustomerApi.getByCustomerIdentifier(customerIdentifier));
        } catch (CustomerIdentifierInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoDebugRestException.class)
                .withErrorCode(TangoDebugRestException.INVALID_CUSTOMER_ID)
                .addParameter("customer_id", customerIdentifier)
                .withCause(e).build();
        } catch (TangoApiException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e).build();
        }
    }

    @Override
    public List<TangoDebugAccountViewSummary> listCustomerAccounts(String accessToken, String customerIdentifier)
        throws UserAuthorizationRestException, TangoDebugRestException, TangoConnectionRestException {
        requireSuperUser(authorizationProvider.getClientAuthorization(accessToken));
        try {
            return tangoCustomerApi.listCustomerAccounts(customerIdentifier).stream().map(account -> {
                return mapAccount(account);
            }).collect(Collectors.toList());
        } catch (CustomerIdentifierInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoDebugRestException.class)
                .withErrorCode(TangoDebugRestException.INVALID_CUSTOMER_ID)
                .addParameter("customer_id", customerIdentifier)
                .withCause(e).build();
        } catch (TangoApiException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e).build();
        }
    }

    private TangoDebugAccountViewSummary mapAccount(com.extole.tango.api.customer.AccountViewSummary account) {
        return new TangoDebugAccountViewSummary(account.getAccountIdentifier(), account.getCreatedAt(),
            account.getDisplayName(), account.getStatus());
    }

    private TangoDebugCustomerViewSummary mapCustomer(com.extole.tango.api.customer.CustomerViewSummary customer) {
        List<TangoDebugAccountViewSummary> accounts = customer.getAccounts().stream().map(account -> {
            return mapAccount(account);
        }).collect(Collectors.toList());
        return new TangoDebugCustomerViewSummary(customer.getCustomerIdentifier(), customer.getDisplayName(),
            customer.getStatus(), customer.getCreatedAt(), accounts);
    }

    private void requireSuperUser(Authorization authorization) throws UserAuthorizationRestException {
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

}
