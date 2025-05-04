package com.extole.client.rest.impl.client;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.client.ClientCoreSettingsV2Response;
import com.extole.client.rest.client.ClientCreationRequest;
import com.extole.client.rest.client.ClientEndpoints;
import com.extole.client.rest.client.ClientResponse;
import com.extole.client.rest.client.ClientRestException;
import com.extole.client.rest.client.ClientUpdateRequest;
import com.extole.client.rest.client.ClientValidationRestException;
import com.extole.common.client.pod.ClientPod;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.Client;
import com.extole.model.entity.client.ClientType;
import com.extole.model.entity.client.core.ClientCoreSettings;
import com.extole.model.entity.client.security.PasswordStrength;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeLockedCredentialsException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordChangeLimitException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordLengthException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordReuseException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordStrengthException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordTooCommonException;
import com.extole.model.service.client.ClientBuilder;
import com.extole.model.service.client.ClientBuilder.UserBuilder;
import com.extole.model.service.client.ClientDuplicateException;
import com.extole.model.service.client.ClientDuplicateShortNameException;
import com.extole.model.service.client.ClientInvalidNameException;
import com.extole.model.service.client.ClientInvalidShortNameException;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.client.IdentityKeyRestrictedCharactersException;
import com.extole.model.service.client.IncompatibleRewardRuleIdentityKeyException;
import com.extole.model.service.client.InvalidIdentityKeyLengthException;
import com.extole.model.service.client.core.ClientCoreSettingCreativeRespondsHtmlEnabledIllegalStateException;
import com.extole.model.service.client.support.SupportFieldValidationException;
import com.extole.model.service.program.ProgramDomainStartsWithReservedWordException;
import com.extole.model.service.program.ProgramInvalidProgramDomainException;
import com.extole.model.service.property.PropertyInvalidNameException;
import com.extole.model.service.property.PropertyNameLengthException;
import com.extole.model.service.property.PropertyNullValueException;
import com.extole.model.service.property.PropertyValueTooLongException;
import com.extole.model.service.user.UserFirstNameInvalidLengthException;
import com.extole.model.service.user.UserInvalidEmailException;
import com.extole.model.service.user.UserLastNameInvalidLengthException;
import com.extole.model.service.verification.code.VerificationCodeAlreadyUsedException;
import com.extole.model.service.verification.code.VerificationCodeNotFoundException;
import com.extole.model.service.verification.code.VerificationCodeService;
import com.extole.security.backend.BackendAuthorization;
import com.extole.security.backend.BackendAuthorizationProvider;

@Provider
public class ClientEndpointsImpl implements ClientEndpoints {
    private final ClientService clientService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final VerificationCodeService verificationCodeService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    public ClientEndpointsImpl(ClientService clientService,
        BackendAuthorizationProvider backendAuthorizationProvider,
        VerificationCodeService verificationCodeService,
        ClientAuthorizationProvider authorizationProvider) {
        this.clientService = clientService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.verificationCodeService = verificationCodeService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<ClientResponse> clients(String accessToken) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return clientService.getAll(authorization).stream()
                .map(client -> toClientResponse(client))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public ClientResponse client(String accessToken, String clientId) throws ClientRestException,
        UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toClientResponse(clientService.getById(userAuthorization, Id.valueOf(clientId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientRestException.class)
                .withErrorCode(ClientRestException.INVALID_CLIENT_ID)
                .addParameter("client_id", clientId)
                .withCause(e).build();
        }
    }

    @Override
    public ClientResponse create(String accessToken, ClientCreationRequest request)
        throws ClientValidationRestException, UserAuthorizationRestException {

        ClientAuthorization authorization = null;
        if (!Strings.isNullOrEmpty(accessToken)) {
            authorization = authorizationProvider.getClientAuthorization(accessToken);
        }

        String name = getName(request);
        String shortName = getShortName(request);
        String userEmail = request.getUserEmail();
        String userPassword = request.getUserPassword();
        try {
            Client client;
            boolean isClientSuperuser = authorization != null && authorization.getScopes()
                .contains(Authorization.Scope.CLIENT_SUPERUSER);
            if (isClientSuperuser) {
                ClientBuilder clientBuilder = createClient(authorization, request);

                if (!Strings.isNullOrEmpty(userEmail) && !Strings.isNullOrEmpty(userPassword)) {
                    UserBuilder userBuilder = clientBuilder.addDefaultUser()
                        .withUserEmail(userEmail)
                        .withUserPassword(userPassword);
                    if (!Strings.isNullOrEmpty(request.getUserFirstName())) {
                        userBuilder.withFirstName(request.getUserFirstName());
                    }
                    if (!Strings.isNullOrEmpty(request.getUserLastName())) {
                        userBuilder.withLastName(request.getUserLastName());
                    }
                }
                client = clientBuilder.save();
            } else {
                validateVerificationCode(request.getVerificationCode());

                BackendAuthorization superuserAuthorization =
                    backendAuthorizationProvider.getSuperuserAuthorizationForBackend();

                ClientBuilder clientBuilder = createClient(superuserAuthorization, request);

                UserBuilder userBuilder = clientBuilder.addDefaultUser()
                    .withUserEmail(userEmail)
                    .withUserPassword(userPassword);
                if (!Strings.isNullOrEmpty(request.getUserFirstName())) {
                    userBuilder.withFirstName(request.getUserFirstName());
                }
                if (!Strings.isNullOrEmpty(request.getUserLastName())) {
                    userBuilder.withLastName(request.getUserLastName());
                }
                client = clientBuilder.save();
                verificationCodeService.use(Id.valueOf(request.getVerificationCode()));
            }
            return toClientResponse(client);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ClientInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_NAME)
                .addParameter("name", name)
                .withCause(e).build();
        } catch (ClientDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.DUPLICATE_CLIENT)
                .addParameter("name", name)
                .withCause(e).build();
        } catch (ProgramDomainStartsWithReservedWordException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.SHORT_NAME_RESERVED_KEYWORD_FOR_PROGRAM_DOMAIN)
                .addParameter("short_name", shortName)
                .addParameter("reserved_word", e.getReservedWord())
                .withCause(e).build();
        } catch (ClientInvalidShortNameException | ProgramInvalidProgramDomainException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_SHORT_NAME)
                .addParameter("short_name", shortName)
                .withCause(e).build();
        } catch (ClientDuplicateShortNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.DUPLICATE_DOMAIN)
                .addParameter("domain_name", shortName)
                .withCause(e).build();
        } catch (SupportFieldValidationException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_SLACK_CHANNEL_NAME)
                .withCause(e).build();
        } catch (UserInvalidEmailException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_USER_EMAIL)
                .addParameter("user_email", userEmail)
                .withCause(e).build();
        } catch (ExtoleAuthProviderTypePasswordLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_PASSWORD_LENGTH)
                .addParameter("minimum_length", Integer.valueOf(e.getMinimumPasswordLength()))
                .addParameter("maximum_length", Integer.valueOf(e.getMaximumPasswordLength()))
                .withCause(e).build();
        } catch (ExtoleAuthProviderTypePasswordReuseException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.PASSWORD_ALREADY_USED)
                .withCause(e).build();
        } catch (ExtoleAuthProviderTypePasswordChangeLimitException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_PASSWORD_CHANGE_LIMIT)
                .withCause(e).build();
        } catch (ExtoleAuthProviderTypePasswordStrengthException e) {
            ErrorCode<ClientValidationRestException> errorCode =
                e.getPasswordStrength() == PasswordStrength.LETTERS_AND_DIGITS
                    ? ClientValidationRestException.INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS
                    : ClientValidationRestException.INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS_PUNCTUATION;
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(errorCode)
                .withCause(e).build();
        } catch (ExtoleAuthProviderTypePasswordTooCommonException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.COMMON_PASSWORD)
                .withCause(e).build();
        } catch (VerificationCodeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_VERIFICATION_CODE)
                .addParameter("verification_code", request.getVerificationCode())
                .withCause(e).build();
        } catch (VerificationCodeAlreadyUsedException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.VERIFICATION_CODE_ALREADY_USED)
                .addParameter("verification_code", request.getVerificationCode())
                .withCause(e).build();
        } catch (ExtoleAuthProviderTypeLockedCredentialsException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.ACCOUNT_DISABLED)
                .withCause(e).build();
        } catch (PropertyValueTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.PROPERTY_VALUE_TOO_LONG)
                .addParameter("name", e.getName())
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (PropertyNullValueException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.PROPERTY_NULL_VALUE)
                .addParameter("name", e.getName())
                .addParameter("value", e.getValue())
                .withCause(e).build();
        } catch (PropertyInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.PROPERTY_NAME_INVALID)
                .addParameter("name", e.getName())
                .withCause(e).build();
        } catch (PropertyNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.PROPERTY_NAME_TOO_LONG)
                .addParameter("name", e.getName())
                .withCause(e).build();
        } catch (UserFirstNameInvalidLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_USER_FIRST_NAME)
                .addParameter("first_name", request.getUserFirstName())
                .withCause(e).build();
        } catch (UserLastNameInvalidLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_USER_LAST_NAME)
                .addParameter("last_name", request.getUserLastName())
                .withCause(e).build();
        } catch (ClientCoreSettingCreativeRespondsHtmlEnabledIllegalStateException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        } catch (InvalidIdentityKeyLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(ClientValidationRestException.class)
                .withCause(e)
                .withErrorCode(ClientValidationRestException.IDENTITY_KEY_INVALID_LENGTH)
                .addParameter("max_allowed_length", Integer.valueOf(e.getMaxLength()))
                .addParameter("min_allowed_length", Integer.valueOf(e.getMinLength()))
                .build();
        } catch (IncompatibleRewardRuleIdentityKeyException e) {
            throw RestExceptionBuilder
                .newBuilder(ClientValidationRestException.class)
                .withCause(e)
                .withErrorCode(ClientValidationRestException.IDENTITY_KEY_INCOMPATIBLE_USAGE)
                .addParameter("incompatible_reward_rules_by_campaign_id", e.getRewardRulesByCampaign())
                .build();
        } catch (IdentityKeyRestrictedCharactersException e) {
            throw RestExceptionBuilder
                .newBuilder(ClientValidationRestException.class)
                .withCause(e)
                .withErrorCode(ClientValidationRestException.IDENTITY_KEY_CONTAINS_RESTRICTED_CHARACTERS)
                .build();
        }
    }

    @Override
    public ClientResponse delete(String accessToken, String clientId) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (!authorization.getClientId().getValue().equals(clientId)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
        try {
            Client deleteClient = clientService.deleteClient(authorization);
            return toClientResponse(deleteClient);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public ClientResponse undelete(String accessToken, String clientId) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Client undeletedClient = clientService.undeleteClient(authorization, Id.valueOf(clientId));
            return toClientResponse(undeletedClient);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public ClientResponse update(String accessToken, String clientId, ClientUpdateRequest clientUpdateRequest)
        throws UserAuthorizationRestException, ClientValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (!authorization.getClientId().getValue().equals(clientId)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
        try {
            ClientBuilder clientBuilder = clientService.updateClient(authorization);
            if (!Strings.isNullOrEmpty(clientUpdateRequest.getName())) {
                clientBuilder.withClientName(clientUpdateRequest.getName());
            }
            if (clientUpdateRequest.getIdentityKey().isPresent()) {
                clientBuilder.withIdentityKey(clientUpdateRequest.getIdentityKey().getValue());
            }
            if (clientUpdateRequest.getClientType() != null) {
                clientBuilder.withClientType(ClientType.valueOf(clientUpdateRequest.getClientType().toString()));
            }
            if (!Strings.isNullOrEmpty(clientUpdateRequest.getPod())) {
                clientBuilder.withPod(new ClientPod(clientUpdateRequest.getPod()));
            }
            clientUpdateRequest.getSlackChannelName().ifPresent(slackChannelName -> {
                if (!Strings.isNullOrEmpty(slackChannelName)) {
                    clientBuilder.withSlackChannelName(slackChannelName);
                }
            });
            return toClientResponse(clientBuilder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ClientInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_NAME)
                .addParameter("name", clientUpdateRequest.getName())
                .withCause(e).build();
        } catch (ClientDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.DUPLICATE_CLIENT)
                .addParameter("name", clientUpdateRequest.getName())
                .withCause(e).build();
        } catch (SupportFieldValidationException e) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_SLACK_CHANNEL_NAME)
                .withCause(e).build();
        } catch (InvalidIdentityKeyLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(ClientValidationRestException.class)
                .withCause(e)
                .withErrorCode(ClientValidationRestException.IDENTITY_KEY_INVALID_LENGTH)
                .addParameter("max_allowed_length", Integer.valueOf(e.getMaxLength()))
                .addParameter("min_allowed_length", Integer.valueOf(e.getMinLength()))
                .build();
        } catch (IdentityKeyRestrictedCharactersException e) {
            throw RestExceptionBuilder
                .newBuilder(ClientValidationRestException.class)
                .withCause(e)
                .withErrorCode(ClientValidationRestException.IDENTITY_KEY_CONTAINS_RESTRICTED_CHARACTERS)
                .build();
        } catch (IncompatibleRewardRuleIdentityKeyException e) {
            throw RestExceptionBuilder
                .newBuilder(ClientValidationRestException.class)
                .withCause(e)
                .withErrorCode(ClientValidationRestException.IDENTITY_KEY_INCOMPATIBLE_USAGE)
                .addParameter("incompatible_reward_rules_by_campaign_id", e.getRewardRulesByCampaign())
                .build();
        } catch (ClientInvalidShortNameException | UserLastNameInvalidLengthException
            | UserFirstNameInvalidLengthException | ExtoleAuthProviderTypePasswordLengthException
            | UserInvalidEmailException | ExtoleAuthProviderTypePasswordTooCommonException
            | ExtoleAuthProviderTypeLockedCredentialsException | ExtoleAuthProviderTypePasswordStrengthException
            | ExtoleAuthProviderTypePasswordReuseException | ExtoleAuthProviderTypePasswordChangeLimitException
            | ClientCoreSettingCreativeRespondsHtmlEnabledIllegalStateException
            | ProgramInvalidProgramDomainException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private void validateVerificationCode(String verificationCode) throws VerificationCodeAlreadyUsedException,
        VerificationCodeNotFoundException, ClientValidationRestException {
        if (Strings.isNullOrEmpty(verificationCode)) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.MISSING_VERIFICATION_CODE)
                .build();
        }
        if (!verificationCodeService.isValid(Id.valueOf(verificationCode))) {
            throw new VerificationCodeAlreadyUsedException(Id.valueOf(verificationCode));
        }
    }

    private String getName(ClientCreationRequest request) throws ClientValidationRestException {
        String name = request.getName();
        if (Strings.isNullOrEmpty(name)) {
            throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
                .withErrorCode(ClientValidationRestException.INVALID_NAME)
                .addParameter("name", name)
                .build();
        }
        return name;
    }

    private String getShortName(ClientCreationRequest request) throws ClientValidationRestException {
        String shortName = request.getShortName();

        if (Strings.isNullOrEmpty(shortName)) {
            return request.getName().trim().replaceAll("[^\\w\\d]+", "-").toLowerCase(Locale.ENGLISH);
        } else if (shortName.equals(shortName.toLowerCase(Locale.ENGLISH))) {
            return shortName;
        }
        throw RestExceptionBuilder.newBuilder(ClientValidationRestException.class)
            .withErrorCode(ClientValidationRestException.INVALID_CASE_DOMAIN_NAME)
            .addParameter("domain_name", shortName)
            .build();
    }

    private ClientBuilder createClient(ClientAuthorization authorization, ClientCreationRequest request)
        throws ClientValidationRestException, ClientDuplicateException, ClientInvalidNameException,
        ClientDuplicateShortNameException, ClientInvalidShortNameException, PropertyInvalidNameException,
        PropertyNullValueException, PropertyNameLengthException, PropertyValueTooLongException,
        InvalidIdentityKeyLengthException, IdentityKeyRestrictedCharactersException {

        String name = getName(request);
        String shortName = getShortName(request);

        ClientBuilder builder = clientService.createClient(authorization)
            .withClientName(name)
            .withShortName(shortName)
            .withDomainName(shortName);

        if (request.getIdentityKey().isPresent()) {
            builder.withIdentityKey(request.getIdentityKey().getValue());
        }

        if (request.getClientType() != null) {
            builder.withClientType(ClientType.valueOf(request.getClientType().toString()));
        }
        if (!Strings.isNullOrEmpty(request.getPod())) {
            builder.withPod(new ClientPod(request.getPod()));
        }
        if (request.getProperties() != null) {
            for (Map.Entry<String, String> entry : request.getProperties().entrySet()) {
                builder.addProperty(entry.getKey(), entry.getValue());
            }
        }
        request.getSlackChannelName().ifPresent(slackChannelName -> {
            if (!Strings.isNullOrEmpty(slackChannelName)) {
                builder.withSlackChannelName(slackChannelName);
            }
        });

        return builder;
    }

    private ClientResponse toClientResponse(Client client) {
        return new ClientResponse(client.getId().toString(), client.getName(), client.getShortName(),
            com.extole.client.rest.client.ClientType.valueOf(client.getClientType().toString()), client.getVersion(),
            client.getPod().getName(), toClientCoreSettingsResponse(client.getCoreSettings()),
            client.getIdentityKey());
    }

    private ClientCoreSettingsV2Response toClientCoreSettingsResponse(ClientCoreSettings coreSettings) {
        return new ClientCoreSettingsV2Response(coreSettings.getCoreVersion());
    }

}
