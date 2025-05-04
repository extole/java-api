package com.extole.client.rest.impl.tango;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.tango.ClientTangoSettingsCreditCardEndpoints;
import com.extole.client.rest.tango.TangoAccountRestException;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.client.rest.tango.TangoCreditCardContactInformation;
import com.extole.client.rest.tango.TangoCreditCardRegistrationRestException;
import com.extole.client.rest.tango.TangoCreditCardResponse;
import com.extole.client.rest.tango.TangoCreditCardRestException;
import com.extole.client.rest.tango.TangoCreditCardStatus;
import com.extole.client.rest.tango.TangoTestCreditCardRegistrationRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.client.tango.AccountNotFoundException;
import com.extole.model.service.client.tango.ClientTangoSettingsNotDefinedException;
import com.extole.model.service.client.tango.ClientTangoSettingsService;
import com.extole.model.service.client.tango.CreditCardNotFoundException;
import com.extole.model.service.client.tango.TangoCreditCard;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressAddressLine1InvalidException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressAddressLine2InvalidException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressCityInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressCountryException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressEmailAddressInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressFirstNameInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressLastNameInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressPostalCodeInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardBillingAddressStateInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardContactInformationEmailAddressInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardContactInformationFullNameInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardContactInformationInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardInformationExpirationInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardInformationInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardInformationNumberInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardInformationVerificationNumberInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardIpAddressInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardLabelInvalidException;
import com.extole.model.service.client.tango.TangoCreditCardRegistrationBuilder;
import com.extole.model.service.client.tango.TangoCreditCardValidationException;
import com.extole.model.service.tango.TangoServiceUnavailableException;
import com.extole.tango.api.fund.TangoFundApi;

@Provider
public class ClientTangoSettingsCreditCardEndpointsImpl implements ClientTangoSettingsCreditCardEndpoints {

    private static final String SOURCE_IP = "X-Forwarded-For";
    private static final Logger LOG = LoggerFactory.getLogger(ClientTangoSettingsCreditCardEndpointsImpl.class);
    public static final String DEFAULT_IP_ADDRESS = "0.0.0.0";

    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientTangoSettingsService clientTangoSettingsService;
    private final HttpServletRequest servletRequest;

    @Inject
    public ClientTangoSettingsCreditCardEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        TangoFundApi fundApi, ClientTangoSettingsService clientTangoSettingsService,
        @Context HttpServletRequest servletRequest) {
        this.authorizationProvider = authorizationProvider;
        this.clientTangoSettingsService = clientTangoSettingsService;
        this.servletRequest = servletRequest;
    }

    @Override
    public List<TangoCreditCardResponse> listCreditCards(String accessToken, String accountId)
        throws UserAuthorizationRestException, TangoAccountRestException,
        TangoConnectionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return clientTangoSettingsService.listCreditCards(authorization, Id.valueOf(accountId)).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (ClientTangoSettingsNotDefinedException | AccountNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoAccountRestException.class)
                .withErrorCode(TangoAccountRestException.INVALID_ACCOUNT_ID)
                .withCause(e)
                .build();
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        }
    }

    @Override
    public TangoCreditCardResponse registerCreditCard(String accessToken, String accountId,
        TangoTestCreditCardRegistrationRequest request)
        throws UserAuthorizationRestException, TangoAccountRestException,
        TangoConnectionRestException,
        TangoCreditCardRegistrationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            TangoCreditCardRegistrationBuilder builder =
                clientTangoSettingsService.registerCreditCard(authorization, Id.valueOf(accountId));
            builder.configureBillingAddress()
                .withFirstName(request.getBillingAddressFirstName())
                .withLastName(request.getBillingAddressLastName())
                .withAddressLine1(request.getBillingAddressLine1())
                .withAddressLine2(request.getBillingAddressLine2())
                .withCity(request.getBillingAddressCity())
                .withState(request.getBillingAddressState())
                .withPostalCode(request.getBillingAddressPostalCode())
                .withCountry(request.getBillingAddressCountry())
                .withEmailAddress(request.getBillingAddressEmail());

            List<TangoCreditCardContactInformation> contacts = request.getContacts();
            contacts.stream().forEach(contact -> {
                builder.addContactInformation().withEmailAddress(contact.getEmailAddress())
                    .withFullName(contact.getFullName());
            });
            builder.withIpAddress(readSourceIp(servletRequest).getHostAddress());
            builder.withLabel(request.getLabel());
            return toResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (ClientTangoSettingsNotDefinedException | AccountNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoAccountRestException.class)
                .withErrorCode(TangoAccountRestException.INVALID_ACCOUNT_ID)
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressAddressLine1InvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_LINE_1_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressAddressLine2InvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_LINE_2_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressCityInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_CITY_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressCountryException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_COUNTRY_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressEmailAddressInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_EMAIL_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressFirstNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_FIRST_NAME_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressLastNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_LAST_NAME_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressPostalCodeInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_POSTAL_CODE_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardBillingAddressStateInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.BILLING_ADDRESS_STATE_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardContactInformationInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.CONTACT_INFORMATION_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardContactInformationEmailAddressInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.CONTACT_INFORMATION_EMAIL_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardContactInformationFullNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.CONTACT_INFORMATION_FULL_NAME_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardLabelInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.LABEL_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardIpAddressInvalidException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRegistrationRestException.class)
                .withErrorCode(TangoCreditCardRegistrationRestException.IP_ADDRESS_INVALID)
                .addParameter(TangoCreditCardRegistrationRestException.CAUSE, e.getMessage())
                .withCause(e)
                .build();
        } catch (TangoCreditCardInformationInvalidException | TangoCreditCardValidationException
            | TangoCreditCardInformationNumberInvalidException | TangoCreditCardInformationExpirationInvalidException
            | TangoCreditCardInformationVerificationNumberInvalidException | TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        }
    }

    @Override
    public TangoCreditCardResponse getCreditCard(String accessToken, String accountId, String creditCardToken)
        throws UserAuthorizationRestException, TangoConnectionRestException,
        TangoCreditCardRestException, TangoAccountRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toResponse(
                clientTangoSettingsService.getCreditCard(authorization, Id.valueOf(accountId),
                    Id.valueOf(creditCardToken)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (ClientTangoSettingsNotDefinedException | AccountNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoAccountRestException.class)
                .withErrorCode(TangoAccountRestException.INVALID_ACCOUNT_ID)
                .withCause(e)
                .build();
        } catch (CreditCardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRestException.class)
                .withErrorCode(TangoCreditCardRestException.INVALID_CREDIT_CARD_ID)
                .withCause(e)
                .build();
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SuccessResponse unregisterCreditCard(String accessToken, String accountId, String creditCardToken)
        throws UserAuthorizationRestException, TangoAccountRestException,
        TangoCreditCardRestException, TangoConnectionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            clientTangoSettingsService.unregisterCreditCard(authorization, Id.valueOf(accountId),
                Id.valueOf(creditCardToken));
            return SuccessResponse.SUCCESS;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (ClientTangoSettingsNotDefinedException | AccountNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoAccountRestException.class)
                .withErrorCode(TangoAccountRestException.INVALID_ACCOUNT_ID)
                .withCause(e)
                .build();
        } catch (CreditCardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TangoCreditCardRestException.class)
                .withErrorCode(TangoCreditCardRestException.INVALID_CREDIT_CARD_ID)
                .withCause(e)
                .build();
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        }
    }

    private TangoCreditCardResponse toResponse(TangoCreditCard card) {
        return new TangoCreditCardResponse(card.getId().getValue(), card.getAccountIdentifier().getValue(),
            card.getActivationDate(), card.getCreatedDate(), card.getCustomerIdentifier().getValue(),
            card.getExprationDate(), card.getLabel(), card.getLastFourDigits(),
            TangoCreditCardStatus.parse(card.getStatus().toString()));
    }

    private InetAddress readSourceIp(HttpServletRequest request) {
        InetAddress ip = getDefaultSourceIp();
        String sourceIp = request.getHeader(SOURCE_IP);
        if (!Strings.isNullOrEmpty(sourceIp)) {
            if (sourceIp.contains(",")) {
                String[] sourceIps = sourceIp.split(",");
                sourceIp = Arrays.stream(sourceIps)
                    .filter(ipAddress -> InetAddressValidator.getInstance().isValid(ipAddress))
                    .findFirst()
                    .orElse(DEFAULT_IP_ADDRESS);
            }
            if (InetAddressValidator.getInstance().isValid(sourceIp)) {
                try {
                    ip = InetAddress.getByName(sourceIp);
                } catch (UnknownHostException e) {
                    LOG.warn("Invalid source IP={} due to {}", sourceIp, e.toString());
                }
            }
        }
        return ip;
    }

    private InetAddress getDefaultSourceIp() {
        try {
            return InetAddress.getByName(DEFAULT_IP_ADDRESS);
        } catch (UnknownHostException e) {
            // this should never happen
            throw new Error("Unable to create dummy IP address: 0.0.0.0", e);
        }
    }

}
