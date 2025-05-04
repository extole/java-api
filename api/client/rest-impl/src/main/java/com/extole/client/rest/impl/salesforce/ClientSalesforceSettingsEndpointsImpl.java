package com.extole.client.rest.impl.salesforce;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsCreateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsCreateRestException;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsEndpoints;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsResponse;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsUpdateRequest;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsUpdateRestException;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsValidationRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.salesforce.ClientSalesforceSettings;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsAlreadyDefinedException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsBaseUriHasInvalidFormatException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsBaseUriMissingException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsBuilder;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsInvalidBaseUriException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsInvalidSiteIdException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsNameAlreadyInUseException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsNameInvalidException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsNotFoundException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsPasswordMissingException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsService;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsSiteIdMissingException;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsUsedByRewardSuppliers;
import com.extole.model.service.client.salesforce.ClientSalesforceSettingsUsernameMissingException;
import com.extole.model.service.client.salesforce.SalesforceSettingsAssociatedWithSalesforceCouponRewardSupplierException;
import com.extole.salesforce.api.salesforce.SalesforceAuthenticationException;
import com.extole.salesforce.api.salesforce.SalesforceAuthorizationException;
import com.extole.salesforce.api.salesforce.SalesforceServiceUnavailableException;

@Provider
public class ClientSalesforceSettingsEndpointsImpl implements ClientSalesforceSettingsEndpoints {

    private final ClientSalesforceSettingsService salesforceSettingsService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public ClientSalesforceSettingsEndpointsImpl(ClientSalesforceSettingsService salesforceSettingsService,
        ClientAuthorizationProvider authorizationProvider) {
        this.salesforceSettingsService = salesforceSettingsService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<ClientSalesforceSettingsResponse> list(String accessToken)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        List<ClientSalesforceSettings> salesforceSettings = salesforceSettingsService.list(authorization);
        return salesforceSettings.stream()
            .map(item -> toClientSettingsSalesforceResponse(item))
            .collect(Collectors.toList());
    }

    @Override
    public ClientSalesforceSettingsResponse create(String accessToken, ClientSalesforceSettingsCreateRequest request)
        throws UserAuthorizationRestException, ClientSalesforceSettingsCreateRestException,
        SalesforceConnectionRestException, ClientSalesforceSettingsValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ClientSalesforceSettingsBuilder salesforceSettingsBuilder = salesforceSettingsService.create(authorization);

            if (!Strings.isNullOrEmpty(request.getName())) {
                salesforceSettingsBuilder.withSettingsName(request.getName());
            }

            if (request.getBaseUri() != null) {
                salesforceSettingsBuilder.withBaseUri(request.getBaseUri());
            }

            if (request.getSiteId() != null) {
                salesforceSettingsBuilder.withSiteId(request.getSiteId());
            }

            if (request.getUsername() != null) {
                salesforceSettingsBuilder.withUsername(request.getUsername());
            }

            if (request.getPassword() != null) {
                salesforceSettingsBuilder.withPassword(request.getPassword());
            }

            return toClientSettingsSalesforceResponse(salesforceSettingsBuilder.save());
        } catch (ClientSalesforceSettingsNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.SETTINGS_NAME_INVALID)
                .addParameter("name", request.getName())
                .withCause(e)
                .build();
        } catch (ClientSalesforceSettingsAlreadyDefinedException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsCreateRestException.class)
                .withErrorCode(ClientSalesforceSettingsCreateRestException.SALESFORCE_SETTINGS_ALREADY_DEFINED)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsBaseUriMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.BASE_URI_MISSING)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsSiteIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.SITE_ID_MISSING)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsUsernameMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.USERNAME_MISSING)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsPasswordMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.PASSWORD_MISSING)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsInvalidBaseUriException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.BASE_URI_NOT_A_VALID_URI)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("base_uri", request.getBaseUri())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsBaseUriHasInvalidFormatException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.BASE_URI_INVALID_FORMAT)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("base_uri", request.getBaseUri())
                .withCause(e).build();
        } catch (SalesforceAuthenticationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceAuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsInvalidSiteIdException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.INVALID_SITE_ID)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("site_id", request.getSiteId())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsNameAlreadyInUseException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsCreateRestException.class)
                .withErrorCode(ClientSalesforceSettingsCreateRestException.SALESFORCE_SETTINGS_NAME_USED)
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e).build();
        }
    }

    @Override
    public ClientSalesforceSettingsResponse update(String accessToken,
        String settingsId, ClientSalesforceSettingsUpdateRequest request)
        throws UserAuthorizationRestException, SalesforceConnectionRestException, ClientSalesforceSettingsRestException,
        ClientSalesforceSettingsValidationRestException, ClientSalesforceSettingsUpdateRestException,
        ClientSalesforceSettingsCreateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ClientSalesforceSettingsBuilder salesforceSettingsBuilder = salesforceSettingsService.edit(authorization,
                Id.valueOf(settingsId));

            if (!Strings.isNullOrEmpty(request.getName())) {
                salesforceSettingsBuilder.withSettingsName(request.getName());
            }

            if (request.getBaseUri() != null) {
                salesforceSettingsBuilder.withBaseUri(request.getBaseUri());
            }

            if (request.getSiteId() != null) {
                salesforceSettingsBuilder.withSiteId(request.getSiteId());
            }

            if (request.getUsername() != null) {
                salesforceSettingsBuilder.withUsername(request.getUsername());
            }

            if (request.getPassword() != null) {
                salesforceSettingsBuilder.withPassword(request.getPassword());
            }

            if (request.getDisabled() != null) {
                salesforceSettingsBuilder.withDisabled(request.getDisabled());
            }

            return toClientSettingsSalesforceResponse(salesforceSettingsBuilder.save());
        } catch (ClientSalesforceSettingsAlreadyDefinedException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsCreateRestException.class)
                .withErrorCode(ClientSalesforceSettingsCreateRestException.SALESFORCE_SETTINGS_ALREADY_DEFINED)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsInvalidBaseUriException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.BASE_URI_NOT_A_VALID_URI)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("base_uri", request.getBaseUri())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsBaseUriHasInvalidFormatException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.BASE_URI_INVALID_FORMAT)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("base_uri", request.getBaseUri())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.SETTINGS_NAME_INVALID)
                .addParameter("name", authorization.getClientId().getValue())
                .withCause(e)
                .build();
        } catch (ClientSalesforceSettingsBaseUriMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.BASE_URI_MISSING)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsSiteIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.SITE_ID_MISSING)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsUsernameMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.USERNAME_MISSING)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsPasswordMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.PASSWORD_MISSING)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsRestException.class)
                .withErrorCode(ClientSalesforceSettingsRestException.SALESFORCE_SETTINGS_NOT_FOUND)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e).build();
        } catch (SalesforceAuthenticationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHENTICATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_SERVICE_UNAVAILABLE)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceAuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(SalesforceConnectionRestException.class)
                .withErrorCode(SalesforceConnectionRestException.SALESFORCE_AUTHORIZATION_ERROR)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (SalesforceSettingsAssociatedWithSalesforceCouponRewardSupplierException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsUpdateRestException.class)
                .withErrorCode(ClientSalesforceSettingsUpdateRestException.ASSOCIATED_TO_REWARD_SUPPLIER)
                .addParameter("client_id", authorization.getClientId().getValue())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsInvalidSiteIdException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsValidationRestException.class)
                .withErrorCode(ClientSalesforceSettingsValidationRestException.INVALID_SITE_ID)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("site_id", request.getSiteId())
                .withCause(e).build();
        } catch (ClientSalesforceSettingsNameAlreadyInUseException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsCreateRestException.class)
                .withErrorCode(ClientSalesforceSettingsCreateRestException.SALESFORCE_SETTINGS_NAME_USED)
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public ClientSalesforceSettingsResponse archive(String accessToken, String settingsId)
        throws UserAuthorizationRestException, ClientSalesforceSettingsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ClientSalesforceSettingsBuilder salesforceSettingsBuilder =
                salesforceSettingsService.edit(authorization, Id.valueOf(settingsId)).withArchived();

            return toClientSettingsSalesforceResponse(salesforceSettingsBuilder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (ClientSalesforceSettingsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsRestException.class)
                .withErrorCode(ClientSalesforceSettingsRestException.SALESFORCE_SETTINGS_NOT_FOUND)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e)
                .build();
        } catch (ClientSalesforceSettingsUsedByRewardSuppliers e) {
            throw RestExceptionBuilder.newBuilder(ClientSalesforceSettingsRestException.class)
                .withErrorCode(ClientSalesforceSettingsRestException.SALESFORCE_SETTINGS_USED_BY_REWARD_SUPPLIERS)
                .addParameter("client_id", authorization.getClientId().getValue())
                .addParameter("reward_supplier_ids", e.getRewardSupplierIds())
                .addParameter("settings_id", e.getSettingsId())
                .withCause(e)
                .build();
        } catch (ClientSalesforceSettingsAlreadyDefinedException | ClientSalesforceSettingsNameInvalidException
            | ClientSalesforceSettingsBaseUriMissingException | ClientSalesforceSettingsSiteIdMissingException
            | ClientSalesforceSettingsUsernameMissingException | ClientSalesforceSettingsPasswordMissingException
            | SalesforceAuthenticationException | SalesforceServiceUnavailableException
            | SalesforceAuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<ClientSalesforceSettingsResponse> listDecrypt(String accessToken)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<ClientSalesforceSettings> salesforceSettings = salesforceSettingsService.listDecrypt(authorization);

            return salesforceSettings.stream().map(item -> buildDecryptSalesforceSettingsResponse(item))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ClientSalesforceSettingsNotFoundException e) {
            return Collections.emptyList();
        }

    }

    private ClientSalesforceSettingsResponse
        toClientSettingsSalesforceResponse(ClientSalesforceSettings salesforceSettings) {
        return new ClientSalesforceSettingsResponse(salesforceSettings.getId().getValue(),
            salesforceSettings.getName(),
            salesforceSettings.getBaseUri().toString(),
            salesforceSettings.getSiteId(),
            salesforceSettings.getUsername(),
            salesforceSettings.getObfuscatedPassword(),
            salesforceSettings.getDisabled());
    }

    private ClientSalesforceSettingsResponse
        buildDecryptSalesforceSettingsResponse(ClientSalesforceSettings salesforceSettings) {
        return new ClientSalesforceSettingsResponse(salesforceSettings.getId().getValue(),
            salesforceSettings.getName(),
            salesforceSettings.getBaseUri().toString(),
            salesforceSettings.getSiteId(),
            salesforceSettings.getUsername(),
            salesforceSettings.getPassword(),
            salesforceSettings.getDisabled());
    }

}
