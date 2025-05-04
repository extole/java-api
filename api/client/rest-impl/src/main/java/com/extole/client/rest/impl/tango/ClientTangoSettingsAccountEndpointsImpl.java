package com.extole.client.rest.impl.tango;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.tango.ClientTangoSettingsAccountCreationRequest;
import com.extole.client.rest.tango.ClientTangoSettingsAccountCreationRestException;
import com.extole.client.rest.tango.ClientTangoSettingsAccountCreditCardDepositRequest;
import com.extole.client.rest.tango.ClientTangoSettingsAccountEndpoints;
import com.extole.client.rest.tango.ClientTangoSettingsAccountResponse;
import com.extole.client.rest.tango.ClientTangoSettingsAccountUpdateRequest;
import com.extole.client.rest.tango.TangoAccountRestException;
import com.extole.client.rest.tango.TangoAccountStatus;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.client.rest.tango.TangoCreditCardRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.tango.TangoAccount;
import com.extole.model.service.client.tango.AccountNotFoundException;
import com.extole.model.service.client.tango.ClientTangoSettingsAccountLimitReachedException;
import com.extole.model.service.client.tango.ClientTangoSettingsBuilder;
import com.extole.model.service.client.tango.ClientTangoSettingsNotDefinedException;
import com.extole.model.service.client.tango.ClientTangoSettingsService;
import com.extole.model.service.client.tango.CreditCardNotFoundException;
import com.extole.model.service.client.tango.TangoAccountBuilder;
import com.extole.model.service.client.tango.TangoAccountDetail;
import com.extole.model.service.client.tango.TangoAccountIllegalFundsAmountWarningLimitException;
import com.extole.model.service.client.tango.TangoCreditCardDeposit;
import com.extole.model.service.tango.TangoServiceUnavailableException;

@Provider
public class ClientTangoSettingsAccountEndpointsImpl implements ClientTangoSettingsAccountEndpoints {

    private final ClientTangoSettingsService clientTangoSettingsService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public ClientTangoSettingsAccountEndpointsImpl(ClientTangoSettingsService clientTangoSettingsService,
        ClientAuthorizationProvider authorizationProvider) {
        this.clientTangoSettingsService = clientTangoSettingsService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<ClientTangoSettingsAccountResponse> list(String accessToken)
        throws UserAuthorizationRestException, TangoConnectionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return clientTangoSettingsService.listAccounts(authorization).stream().map(this::convert)
                .collect(Collectors.toList());
        } catch (ClientTangoSettingsNotDefinedException e) {
            return Collections.emptyList();
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientTangoSettingsAccountResponse create(String accessToken,
        ClientTangoSettingsAccountCreationRequest request) throws UserAuthorizationRestException,
        ClientTangoSettingsAccountCreationRestException, TangoConnectionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            if (request.getFundsAmountWarnLimit() == null) {
                throw RestExceptionBuilder.newBuilder(ClientTangoSettingsAccountCreationRestException.class)
                    .withErrorCode(ClientTangoSettingsAccountCreationRestException.MISSING_FUNDS_AMOUNT_WARN_LIMIT)
                    .build();
            }

            ClientTangoSettingsBuilder settingsBuilder;

            try {
                clientTangoSettingsService.get(authorization);
                settingsBuilder = clientTangoSettingsService.edit(authorization);
            } catch (ClientTangoSettingsNotDefinedException ex) {
                settingsBuilder = clientTangoSettingsService.create(authorization);
            }

            TangoAccountBuilder builder = settingsBuilder.addTangoAccount();
            builder.withFundsAmountWarnLimit(request.getFundsAmountWarnLimit());

            if (request.getEnabled() != null) {
                builder.withEnabled(request.getEnabled().booleanValue());
            }
            if (request.isHourlyAmountLimitEnabled() != null) {
                builder.withHourlyAmountLimitEnabled(request.isHourlyAmountLimitEnabled().booleanValue());
            }
            if (request.isDailyAmountLimitEnabled() != null) {
                builder.withDailyAmountLimitEnabled(request.isDailyAmountLimitEnabled().booleanValue());
            }
            if (request.getDailyAmountLimit() != null) {
                builder.withDailyAmountLimit(request.getDailyAmountLimit());
            }
            if (request.getHourlyAmountLimit() != null) {
                builder.withHourlyAmountLimit(request.getHourlyAmountLimit());
            }
            return convert(clientTangoSettingsService.getAccountById(authorization, builder.save().getAccountId()));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (TangoAccountIllegalFundsAmountWarningLimitException e) {
            throw RestExceptionBuilder.newBuilder(ClientTangoSettingsAccountCreationRestException.class)
                .withErrorCode(ClientTangoSettingsAccountCreationRestException.INVALID_FUNDS_AMOUNT_WARN_LIMIT)
                .addParameter("funds_amount_warn_limit", request.getFundsAmountWarnLimit())
                .withCause(e).build();
        } catch (ClientTangoSettingsAccountLimitReachedException e) {
            throw RestExceptionBuilder.newBuilder(ClientTangoSettingsAccountCreationRestException.class)
                .withErrorCode(ClientTangoSettingsAccountCreationRestException.ACCOUNT_LIMIT_REACHED)
                .addParameter("account_limit", Integer.valueOf(e.getLimit()))
                .withCause(e).build();
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        } catch (ClientTangoSettingsNotDefinedException | AccountNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public ClientTangoSettingsAccountResponse get(String accessToken, String accountId)
        throws UserAuthorizationRestException, TangoAccountRestException,
        TangoConnectionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return convert(clientTangoSettingsService.getAccountById(authorization, Id.valueOf(accountId)));
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
    public ClientTangoSettingsAccountResponse update(String accessToken, String accountId,
        ClientTangoSettingsAccountUpdateRequest request) throws UserAuthorizationRestException,
        TangoAccountRestException, TangoConnectionRestException, ClientTangoSettingsAccountCreationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        TangoAccount account;
        try {
            TangoAccountBuilder builder =
                clientTangoSettingsService.edit(authorization).editTangoAccount(Id.valueOf(accountId));
            if (request.getEnabled() != null) {
                builder.withEnabled(request.getEnabled().booleanValue());
            }
            if (request.isHourlyAmountLimitEnabled() != null) {
                builder.withHourlyAmountLimitEnabled(request.isHourlyAmountLimitEnabled().booleanValue());
            }
            if (request.isDailyAmountLimitEnabled() != null) {
                builder.withDailyAmountLimitEnabled(request.isDailyAmountLimitEnabled().booleanValue());
            }
            if (request.getDailyAmountLimit() != null) {
                builder.withDailyAmountLimit(request.getDailyAmountLimit());
            }
            if (request.getHourlyAmountLimit() != null) {
                builder.withHourlyAmountLimit(request.getHourlyAmountLimit());
            }
            if (request.getFundsAmountWarnLimit() != null) {
                builder.withFundsAmountWarnLimit(request.getFundsAmountWarnLimit());
            }
            account = builder.save();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (TangoAccountIllegalFundsAmountWarningLimitException e) {
            throw RestExceptionBuilder.newBuilder(ClientTangoSettingsAccountCreationRestException.class)
                .withErrorCode(ClientTangoSettingsAccountCreationRestException.INVALID_FUNDS_AMOUNT_WARN_LIMIT)
                .addParameter("funds_amount_warn_limit", request.getFundsAmountWarnLimit())
                .withCause(e).build();
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

        try {
            return convert(clientTangoSettingsService.getAccountById(authorization, account.getAccountId()));
        } catch (TangoServiceUnavailableException e) {
            throw RestExceptionBuilder.newBuilder(TangoConnectionRestException.class)
                .withErrorCode(TangoConnectionRestException.TANGO_SERVICE_UNAVAILABLE)
                .withCause(e)
                .build();
        } catch (ClientTangoSettingsNotDefinedException | AccountNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public SuccessResponse createCreditCardDeposit(String accessToken, String accountId,
        ClientTangoSettingsAccountCreditCardDepositRequest request)
        throws UserAuthorizationRestException, TangoAccountRestException,
        TangoConnectionRestException, TangoCreditCardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            TangoCreditCardDeposit deposit =
                clientTangoSettingsService.createCreditCardDeposit(authorization, Id.valueOf(accountId),
                    Id.valueOf(request.getCreditCardId()),
                    request.getAmount());
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

    private ClientTangoSettingsAccountResponse convert(TangoAccountDetail account) {
        return new ClientTangoSettingsAccountResponse(account.getCustomerId().getValue(),
            account.getAccountId().getValue(), account.isEnabled(), account.isHourlyAmountLimitEnabled(),
            account.isDailyAmountLimitEnabled(),
            FaceValueType.valueOf(account.getFaceValueType().toString()),
            TangoAccountStatus.lookup(account.getStatus().toString()),
            account.getContactEmail(), account.getCurrentBalance(), account.getHourlyAmountLimit(),
            account.getDailyAmountLimit(), account.getFundsAmountWarnLimit());
    }

}
