package com.extole.client.topic.rest.impl.notification.snooze;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.topic.rest.snooze.SnoozeRequest;
import com.extole.client.topic.rest.snooze.SnoozeResponse;
import com.extole.client.topic.rest.snooze.SnoozeRestException;
import com.extole.client.topic.rest.snooze.SnoozeValidationRestException;
import com.extole.client.topic.rest.snooze.UserNotificationSnoozeEndpoints;
import com.extole.client.topic.service.InvalidExpiresAtException;
import com.extole.client.topic.service.MissingTagsException;
import com.extole.client.topic.service.SnoozeBuilder;
import com.extole.client.topic.service.SnoozeNotFoundException;
import com.extole.client.topic.service.SnoozeService;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.snooze.Snooze;
import com.extole.model.entity.user.User;
import com.extole.model.service.user.UserNotFoundException;
import com.extole.model.shared.user.UserCache;

@Provider
public class UserNotificationSnoozeEndpointsImpl implements UserNotificationSnoozeEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final UserCache userCache;
    private final SnoozeService snoozeService;

    @Inject
    public UserNotificationSnoozeEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        UserCache userCache,
        SnoozeService snoozeService) {
        this.authorizationProvider = authorizationProvider;
        this.userCache = userCache;
        this.snoozeService = snoozeService;
    }

    @Override
    public List<SnoozeResponse> getSnoozes(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUserByAuthorization(authorization);
        return getSnoozes(authorization, user.getId(), timeZone);
    }

    @Override
    public List<SnoozeResponse> getSnoozesByUserId(String accessToken, String userId, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return getSnoozes(authorization, Id.valueOf(userId), timeZone);
    }

    @Override
    public SnoozeResponse createSnooze(String accessToken, SnoozeRequest snoozeRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeValidationRestException, SnoozeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUserByAuthorization(authorization);
        return createSnooze(authorization, user.getId(), snoozeRequest, timeZone);
    }

    @Override
    public SnoozeResponse createSnoozeByUserId(String accessToken, String userId, SnoozeRequest snoozeRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, SnoozeValidationRestException, SnoozeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return createSnooze(authorization, Id.valueOf(userId), snoozeRequest, timeZone);
    }

    @Override
    public SnoozeResponse getSnooze(String accessToken, String snoozeId, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUserByAuthorization(authorization);
        return getSnooze(authorization, user.getId(), snoozeId, timeZone);
    }

    @Override
    public SnoozeResponse getSnoozeByUserId(String accessToken, String userId, String snoozeId, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return getSnooze(authorization, Id.valueOf(userId), snoozeId, timeZone);
    }

    @Override
    public SnoozeResponse deleteSnooze(String accessToken, String snoozeId, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUserByAuthorization(authorization);
        return deleteSnooze(authorization, user.getId(), snoozeId, timeZone);
    }

    @Override
    public SnoozeResponse deleteSnoozeByUserId(String accessToken, String userId, String snoozeId, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return deleteSnooze(authorization, Id.valueOf(userId), snoozeId, timeZone);
    }

    private SnoozeResponse deleteSnooze(Authorization authorization, Id<User> userId, String snoozeId, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException {
        try {
            return toSnoozeResponse(
                snoozeService.deleteSnooze(authorization, userId, Id.valueOf(snoozeId)), timeZone);
        } catch (SnoozeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(
                SnoozeRestException.class)
                .withErrorCode(SnoozeRestException.INVALID_SNOOZE_ID)
                .addParameter("snooze_id", snoozeId)
                .withCause(e)
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SnoozeRestException.class)
                .withErrorCode(SnoozeRestException.INVALID_USER_ID)
                .withCause(e)
                .addParameter("user_id", userId)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(
                    UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private List<SnoozeResponse> getSnoozes(Authorization authorization, Id<User> userId, ZoneId timeZone)
        throws SnoozeRestException, UserAuthorizationRestException {
        try {
            return snoozeService.getSnoozes(authorization, userId).stream()
                .map(snooze -> toSnoozeResponse(snooze, timeZone)).collect(Collectors.toList());
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SnoozeRestException.class)
                .withErrorCode(SnoozeRestException.INVALID_USER_ID)
                .withCause(e)
                .addParameter("user_id", userId)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private SnoozeResponse createSnooze(Authorization authorization, Id<User> userId,
        SnoozeRequest snoozeRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeValidationRestException, SnoozeRestException {
        try {
            SnoozeBuilder snoozeBuilder = snoozeService.createSnooze(authorization, userId);
            if (!Strings.isNullOrEmpty(snoozeRequest.getComment().orElse(""))) {
                snoozeBuilder.withComment(snoozeRequest.getComment().get());
            }
            snoozeBuilder.withHavingExactlyTags(snoozeRequest.getHavingExactlyTags());
            if (snoozeRequest.getExpiresAt().isPresent()) {
                snoozeBuilder.withExpiresAt(snoozeRequest.getExpiresAt().get().toInstant());
            }
            return toSnoozeResponse(snoozeBuilder.save(), timeZone);
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SnoozeRestException.class)
                .withErrorCode(SnoozeRestException.INVALID_USER_ID)
                .withCause(e)
                .addParameter("user_id", userId)
                .build();
        } catch (InvalidExpiresAtException e) {
            throw RestExceptionBuilder.newBuilder(SnoozeValidationRestException.class)
                .withErrorCode(SnoozeValidationRestException.INVALID_EXPIRES_AT)
                .withCause(e)
                .build();
        } catch (MissingTagsException e) {
            throw RestExceptionBuilder.newBuilder(
                SnoozeValidationRestException.class)
                .withErrorCode(SnoozeValidationRestException.MISSING_TAGS)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private SnoozeResponse getSnooze(Authorization authorization, Id<User> userId, String snoozeId, ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException {
        try {
            Snooze snooze = snoozeService.getSnooze(authorization, userId, Id.valueOf(snoozeId));
            return toSnoozeResponse(snooze, timeZone);
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SnoozeRestException.class)
                .withErrorCode(SnoozeRestException.INVALID_USER_ID)
                .withCause(e)
                .addParameter("user_id", userId)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (SnoozeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SnoozeRestException.class)
                .withErrorCode(SnoozeRestException.INVALID_SNOOZE_ID)
                .addParameter("snooze_id", snoozeId)
                .withCause(e)
                .build();
        }
    }

    private User getUserByAuthorization(Authorization authorization) throws SnoozeRestException {
        return userCache.getByAuthorization(authorization)
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(SnoozeRestException.class)
                .withErrorCode(SnoozeRestException.USER_NOT_FOUND)
                .build());
    }

    private SnoozeResponse toSnoozeResponse(Snooze snooze, ZoneId timeZone) {
        return new SnoozeResponse(snooze.getId().getValue(), snooze.getComment(), snooze.getHavingExactlyTags(),
            ZonedDateTime.ofInstant(snooze.getExpiresAt(), timeZone));
    }
}
