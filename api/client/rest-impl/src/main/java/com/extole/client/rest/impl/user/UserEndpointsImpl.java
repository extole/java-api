package com.extole.client.rest.impl.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.AuthorizationMissingScopeException;
import com.extole.authorization.service.client.user.UserAuthorization;
import com.extole.client.rest.user.PasswordResetRequest;
import com.extole.client.rest.user.ResendInviteEmailRequest;
import com.extole.client.rest.user.UserCreateRestException;
import com.extole.client.rest.user.UserCreationRequest;
import com.extole.client.rest.user.UserEndpoints;
import com.extole.client.rest.user.UserPasswordResetRestException;
import com.extole.client.rest.user.UserPasswordUpdateRequest;
import com.extole.client.rest.user.UserResponse;
import com.extole.client.rest.user.UserRestException;
import com.extole.client.rest.user.UserScope;
import com.extole.client.rest.user.UserUpdateRequest;
import com.extole.client.rest.user.UserUpdateRestException;
import com.extole.client.rest.user.UserValidationRestException;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.id.Id;
import com.extole.model.entity.client.security.PasswordStrength;
import com.extole.model.entity.user.User;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeCredentialsService;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeLockedCredentialsException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordChangeLimitException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordLengthException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordResetUnsupportedException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordReuseException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordStrengthException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypePasswordTooCommonException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeUnknownEmailException;
import com.extole.model.service.user.UserAccountScope;
import com.extole.model.service.user.UserBuilder;
import com.extole.model.service.user.UserDuplicateException;
import com.extole.model.service.user.UserFirstNameInvalidLengthException;
import com.extole.model.service.user.UserInvalidEmailException;
import com.extole.model.service.user.UserLastNameInvalidLengthException;
import com.extole.model.service.user.UserNotFoundException;
import com.extole.model.service.user.UserScopeModificationException;
import com.extole.model.service.user.UserScopesMissingException;
import com.extole.model.service.user.UserService;
import com.extole.model.service.user.UserUnauthorizedScopeException;

@Provider
public class UserEndpointsImpl implements UserEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(UserEndpointsImpl.class);

    private final UserService userService;
    private final ExtoleAuthProviderTypeCredentialsService extoleAuthProviderTypeCredentialsService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final VerifiedEmailService verifiedEmailService;

    @Autowired
    public UserEndpointsImpl(UserService userService,
        ExtoleAuthProviderTypeCredentialsService extoleAuthProviderTypeCredentialsService,
        ClientAuthorizationProvider authorizationProvider,
        VerifiedEmailService verifiedEmailService) {
        this.userService = userService;
        this.extoleAuthProviderTypeCredentialsService = extoleAuthProviderTypeCredentialsService;
        this.authorizationProvider = authorizationProvider;
        this.verifiedEmailService = verifiedEmailService;
    }

    @Override
    public UserResponse get(String accessToken, String userId)
        throws UserAuthorizationRestException, UserRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            User user = userService.getById(authorization, Id.valueOf(userId));
            return mapToUserResponse(authorization, user);
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<UserResponse> getAll(String accessToken, String userAccountScope)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<UserResponse> response = new ArrayList<>();
            for (User user : userService.getAll(authorization, mapUserAccountScope(userAccountScope))) {
                UserResponse userResponse = mapToUserResponse(authorization, user);
                response.add(userResponse);
            }
            return response;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public UserResponse create(String accessToken, UserCreationRequest request)
        throws UserAuthorizationRestException, UserCreateRestException, UserValidationRestException {
        UserAuthorization authorization = authorizationProvider.getUserAuthorization(accessToken);
        try {
            UserBuilder userBuilder = userService.createUser(authorization);
            userBuilder.withUserEmail(request.getEmail());
            request.getFirstName().ifPresent(firstName -> userBuilder.withFirstName(firstName));
            request.getLastName().ifPresent(lastName -> userBuilder.withLastName(lastName));
            if (request.getScopes().isPresent()) {
                userBuilder.withScopes(request.getScopes().getValue().stream()
                    .map(scope -> User.Scope.valueOf(scope.name())).collect(Collectors.toUnmodifiableSet()));
            }
            User newUser = userBuilder.save();
            extoleAuthProviderTypeCredentialsService.sendInviteEmail(authorization, newUser.getNormalizedEmail());

            if (request.getPassword().isPresent()) {
                extoleAuthProviderTypeCredentialsService.createCredentialsBuilder(authorization)
                    .withEmail(newUser.getNormalizedEmail())
                    .withPassword(request.getPassword().getValue())
                    .withChangePasswordEmailNotification(false)
                    .save();
            }
            return mapToUserResponse(authorization, newUser);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(UserCreateRestException.class)
                .withErrorCode(UserCreateRestException.DUPLICATE_USER)
                .addParameter("email", request.getEmail())
                .withCause(e)
                .build();
        } catch (UserInvalidEmailException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_USER_EMAIL)
                .addParameter("email", request.getEmail())
                .withCause(e)
                .build();
        } catch (UserFirstNameInvalidLengthException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_USER_FIRST_NAME)
                .addParameter("first_name", request.getEmail())
                .withCause(e)
                .build();
        } catch (UserLastNameInvalidLengthException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_USER_LAST_NAME)
                .addParameter("last_name", request.getEmail())
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordLengthException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_PASSWORD_LENGTH)
                .addParameter("minimum_length", Integer.valueOf(e.getMinimumPasswordLength()))
                .addParameter("maximum_length", Integer.valueOf(e.getMaximumPasswordLength()))
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordReuseException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.PASSWORD_ALREADY_USED)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordChangeLimitException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.PASSWORD_CHANGE_LIMIT)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordStrengthException e) {
            ErrorCode<UserValidationRestException> passwordStrengthErrorCode;
            if (e.getPasswordStrength() == PasswordStrength.LETTERS_AND_DIGITS) {
                passwordStrengthErrorCode = UserValidationRestException.INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS;
            } else {
                passwordStrengthErrorCode =
                    UserValidationRestException.INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS_PUNCTUATION;
            }
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(passwordStrengthErrorCode)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordTooCommonException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.COMMON_PASSWORD)
                .withCause(e)
                .build();
        } catch (UserUnauthorizedScopeException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.UNAUTHORIZED_SCOPE)
                .addParameter("role", e.getScope())
                .withCause(e)
                .build();
        } catch (UserScopesMissingException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.USER_SCOPES_MISSING)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypeLockedCredentialsException e) {
            throw RestExceptionBuilder.newBuilder(UserCreateRestException.class)
                .withErrorCode(UserCreateRestException.ACCOUNT_DISABLED)
                .withCause(e)
                .build();
        } catch (UserScopeModificationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SuccessResponse resendInviteEmail(ResendInviteEmailRequest request) throws UserAuthorizationRestException {
        try {
            if (Strings.isNullOrEmpty(request.getAccessToken())) {
                throw new AuthorizationException("Access denied");
            }
            extoleAuthProviderTypeCredentialsService.resendInviteEmail(request.getAccessToken());
            return SuccessResponse.SUCCESS;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public UserResponse update(String accessToken, String userId, UserUpdateRequest request)
        throws UserAuthorizationRestException, UserRestException, UserUpdateRestException, UserValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            UserBuilder userBuilder = userService.updateUser(authorization, Id.valueOf(userId));

            if (request.getEmail().isPresent()) {
                userBuilder.withUserEmail(request.getEmail().getValue());
            }
            request.getFirstName().ifPresent(firstName -> userBuilder.withFirstName(firstName.orElse(null)));
            request.getLastName().ifPresent(lastName -> userBuilder.withLastName(lastName.orElse(null)));
            if (request.getScopes().isPresent()) {
                userBuilder.withScopes(request.getScopes().getValue().stream()
                    .map(scope -> User.Scope.valueOf(scope.name())).collect(Collectors.toUnmodifiableSet()));
            }
            User user = userBuilder.save();

            if (request.getPassword().isPresent()) {
                extoleAuthProviderTypeCredentialsService.createCredentialsBuilder(authorization)
                    .withEmail(user.getNormalizedEmail())
                    .withPassword(request.getPassword().getValue())
                    .withChangePasswordEmailNotification(true)
                    .save();
            }
            return mapToUserResponse(authorization, user);
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .withCause(e)
                .build();
        } catch (AuthorizationMissingScopeException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_SCOPE)
                .addParameter("scope", e.getMissingScope())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(UserUpdateRestException.class)
                .withErrorCode(UserUpdateRestException.DUPLICATE_USER)
                .addParameter("email", request.getEmail())
                .withCause(e)
                .build();
        } catch (UserInvalidEmailException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_USER_EMAIL)
                .addParameter("email", request.getEmail())
                .withCause(e)
                .build();
        } catch (UserFirstNameInvalidLengthException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_USER_FIRST_NAME)
                .addParameter("first_name", request.getEmail())
                .withCause(e)
                .build();
        } catch (UserLastNameInvalidLengthException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_USER_LAST_NAME)
                .addParameter("last_name", request.getEmail())
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordLengthException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_PASSWORD_LENGTH)
                .addParameter("minimum_length", Integer.valueOf(e.getMinimumPasswordLength()))
                .addParameter("maximum_length", Integer.valueOf(e.getMaximumPasswordLength()))
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordReuseException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.PASSWORD_ALREADY_USED)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordChangeLimitException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.PASSWORD_CHANGE_LIMIT)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordStrengthException e) {
            ErrorCode<UserValidationRestException> passwordStrengthErrorCode;
            if (e.getPasswordStrength() == PasswordStrength.LETTERS_AND_DIGITS) {
                passwordStrengthErrorCode = UserValidationRestException.INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS;
            } else {
                passwordStrengthErrorCode =
                    UserValidationRestException.INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS_PUNCTUATION;
            }
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(passwordStrengthErrorCode)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordTooCommonException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.COMMON_PASSWORD)
                .withCause(e)
                .build();
        } catch (UserUnauthorizedScopeException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.UNAUTHORIZED_SCOPE)
                .addParameter("role", e.getScope())
                .withCause(e)
                .build();
        } catch (UserScopeModificationException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.USER_SELF_SCOPES_UPDATE)
                .withCause(e)
                .build();
        } catch (UserScopesMissingException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.USER_SCOPES_MISSING)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypeLockedCredentialsException e) {
            throw RestExceptionBuilder.newBuilder(UserUpdateRestException.class)
                .withErrorCode(UserUpdateRestException.ACCOUNT_DISABLED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public UserResponse updatePassword(String accessToken, UserPasswordUpdateRequest request)
        throws UserAuthorizationRestException, UserUpdateRestException, UserValidationRestException {
        Authorization authorization = authorizationProvider.getUserAuthorization(accessToken);
        try {
            User user = userService.get(authorization);

            extoleAuthProviderTypeCredentialsService.createCredentialsBuilder(authorization)
                .withEmail(user.getNormalizedEmail())
                .withPassword(request.getPassword())
                .withChangePasswordEmailNotification(true)
                .save();

            return mapToUserResponse(authorization, user);
        } catch (UserNotFoundException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordLengthException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.INVALID_PASSWORD_LENGTH)
                .addParameter("minimum_length", Integer.valueOf(e.getMinimumPasswordLength()))
                .addParameter("maximum_length", Integer.valueOf(e.getMaximumPasswordLength()))
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordReuseException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.PASSWORD_ALREADY_USED)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordChangeLimitException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.PASSWORD_CHANGE_LIMIT)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordStrengthException e) {
            ErrorCode<UserValidationRestException> passwordStrengthErrorCode;
            if (e.getPasswordStrength() == PasswordStrength.LETTERS_AND_DIGITS) {
                passwordStrengthErrorCode = UserValidationRestException.INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS;
            } else {
                passwordStrengthErrorCode =
                    UserValidationRestException.INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS_PUNCTUATION;
            }
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(passwordStrengthErrorCode)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypePasswordTooCommonException e) {
            throw RestExceptionBuilder.newBuilder(UserValidationRestException.class)
                .withErrorCode(UserValidationRestException.COMMON_PASSWORD)
                .withCause(e)
                .build();
        } catch (ExtoleAuthProviderTypeLockedCredentialsException e) {
            throw RestExceptionBuilder.newBuilder(UserUpdateRestException.class)
                .withErrorCode(UserUpdateRestException.ACCOUNT_DISABLED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SuccessResponse delete(String accessToken, String userId) throws UserAuthorizationRestException,
        UserRestException {
        Authorization clientAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            userService.updateUser(clientAuthorization, Id.valueOf(userId)).withDeleted().save();
            return SuccessResponse.SUCCESS;
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserDuplicateException | UserUnauthorizedScopeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SuccessResponse unlock(String accessToken, String userId)
        throws UserAuthorizationRestException, UserRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            String normalizedEmail = userService.getById(authorization, Id.valueOf(userId)).getNormalizedEmail();

            extoleAuthProviderTypeCredentialsService.unlock(authorization, normalizedEmail);
            return SuccessResponse.SUCCESS;
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SuccessResponse resetPassword(PasswordResetRequest request) throws UserPasswordResetRestException {
        String email = request.getEmail();
        try {
            String normalizedEmail = verifiedEmailService.verifyEmail(email).getEmail().getNormalizedAddress();
            extoleAuthProviderTypeCredentialsService.sendResetPasswordEmail(normalizedEmail);
            return SuccessResponse.SUCCESS;
        } catch (ExtoleAuthProviderTypeUnknownEmailException e) {
            LOG.warn("Failed reset password attempt, user not found for email: {} ", request.getEmail(), e);
            return SuccessResponse.SUCCESS;
        } catch (ExtoleAuthProviderTypePasswordResetUnsupportedException e) {
            LOG.warn("Failed reset password attempt, reset password for user with email {} is not allowed ",
                request.getEmail(), e);
            return SuccessResponse.SUCCESS;
        } catch (InvalidEmailDomainException | InvalidEmailAddress e) {
            throw RestExceptionBuilder.newBuilder(UserPasswordResetRestException.class)
                .withErrorCode(UserPasswordResetRestException.EMAIL_INVALID)
                .addParameter("email", request.getEmail())
                .withCause(e)
                .build();
        }
    }

    private UserResponse mapToUserResponse(Authorization authorization, User user)
        throws AuthorizationException {
        return UserResponse.builder()
            .withUserId(user.getId().getValue())
            .withEmail(user.getNormalizedEmail())
            .withFirstName(user.getFirstName().orElse(null))
            .withLastName(user.getLastName().orElse(null))
            .withScopes(user.getScopes().stream().map(scope -> UserScope.valueOf(scope.name()))
                .collect(Collectors.toUnmodifiableSet()))
            .withLastLoggedIn(getLastLoggedInAsString(user))
            .withLocked(extoleAuthProviderTypeCredentialsService.isLocked(authorization, user.getNormalizedEmail()))
            .build();
    }

    private UserAccountScope mapUserAccountScope(String userAccountScope) {
        if (!Strings.isNullOrEmpty(userAccountScope)
            && EnumUtils.isValidEnum(UserAccountScope.class, userAccountScope.toUpperCase())) {
            return UserAccountScope.valueOf(userAccountScope.toUpperCase());
        } else {
            return UserAccountScope.ACCOUNT;
        }
    }

    @Nullable
    private String getLastLoggedInAsString(User user) {
        Optional<Instant> lastLoggedIn = userService.getLastLoggedIn(user);
        return lastLoggedIn.map(instant -> instant.toString()).orElse(null);
    }

}
