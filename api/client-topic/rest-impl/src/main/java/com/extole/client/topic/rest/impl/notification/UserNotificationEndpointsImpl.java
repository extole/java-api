package com.extole.client.topic.rest.impl.notification;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.topic.rest.NotificationCursorRequest;
import com.extole.client.topic.rest.NotificationCursorResponse;
import com.extole.client.topic.rest.NotificationCursorRestException;
import com.extole.client.topic.rest.NotificationGetRequest;
import com.extole.client.topic.rest.NotificationResponse;
import com.extole.client.topic.rest.UserNotificationEndpoints;
import com.extole.client.topic.rest.UserNotificationLocalEndpoints;
import com.extole.client.topic.rest.UserNotificationRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.ExtoleRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.user.User;
import com.extole.model.service.user.UserService;
import com.extole.model.shared.user.UserCache;

@Provider
public class UserNotificationEndpointsImpl implements UserNotificationEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(UserNotificationEndpointsImpl.class);
    private static final Comparator<NotificationResponse> RECORD_COMPARATOR =
        Comparator.comparing(NotificationResponse::getEventTime);

    private final ClientAuthorizationProvider authorizationProvider;
    private final UserNotificationLocalEndpointsProvider endpointsProvider;
    private final UserCache userCache;
    private final NotificationRestMapper notificationRestMapper;
    private final UserService userService;
    private final HttpServletRequest servletRequest;

    @Inject
    public UserNotificationEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        UserNotificationLocalEndpointsProvider endpointsProvider,
        UserCache userCache,
        NotificationRestMapper notificationRestMapper,
        UserService userService,
        HttpServletRequest servletRequest) {
        this.authorizationProvider = authorizationProvider;
        this.endpointsProvider = endpointsProvider;
        this.userCache = userCache;
        this.notificationRestMapper = notificationRestMapper;
        this.userService = userService;
        this.servletRequest = servletRequest;
    }

    @Override
    public List<NotificationResponse> getNotifications(String accessToken, NotificationGetRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = userCache.getByAuthorization(authorization)
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build());
        try {
            return getNotifications(authorization, user, request, timeZone);
        } catch (UserNotificationRestException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<NotificationResponse> getNotificationsByUserId(@UserAccessTokenParam String accessToken,
        String userId, NotificationGetRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = userCache.getById(authorization, Id.valueOf(userId))
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(UserNotificationRestException.class)
                .withErrorCode(UserNotificationRestException.USER_NOT_FOUND)
                .addParameter("user_id", userId)
                .addParameter("client_id", authorization.getClientId())
                .addParameter("request_uri", servletRequest.getRequestURI())
                .build());
        return getNotifications(authorization, user, request, timeZone);
    }

    @Override
    public NotificationCursorResponse getNotificationCursor(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = userCache.getByAuthorization(authorization)
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build());
        return notificationRestMapper.toNotificationCursorResponse(userService.getNotificationCursor(user), timeZone);
    }

    @Override
    public NotificationCursorResponse updateNotificationCursor(String accessToken,
        NotificationCursorRequest notificationCursorRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, NotificationCursorRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = userCache.getByAuthorization(authorization)
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build());
        return updateNotificationCursor(notificationCursorRequest, timeZone, authorization, user);
    }

    @Override
    public NotificationCursorResponse getNotificationCursorByUserId(String accessToken, String userId, ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = userCache.getById(authorization, Id.valueOf(userId))
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(UserNotificationRestException.class)
                .withErrorCode(UserNotificationRestException.USER_NOT_FOUND)
                .addParameter("user_id", userId)
                .addParameter("client_id", authorization.getClientId())
                .addParameter("request_uri", servletRequest.getRequestURI())
                .build());
        return notificationRestMapper.toNotificationCursorResponse(userService.getNotificationCursor(user), timeZone);
    }

    @Override
    public NotificationCursorResponse updateNotificationCursorByUserId(String accessToken, String userId,
        NotificationCursorRequest notificationCursorRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException, NotificationCursorRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = userCache.getById(authorization, Id.valueOf(userId))
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(UserNotificationRestException.class)
                .withErrorCode(UserNotificationRestException.USER_NOT_FOUND)
                .addParameter("user_id", userId)
                .addParameter("client_id", authorization.getClientId())
                .addParameter("request_uri", servletRequest.getRequestURI())
                .build());
        return updateNotificationCursor(notificationCursorRequest, timeZone, authorization, user);
    }

    private NotificationCursorResponse updateNotificationCursor(NotificationCursorRequest notificationCursorRequest,
        ZoneId timeZone, ClientAuthorization authorization, User user)
        throws NotificationCursorRestException, UserAuthorizationRestException {
        try {
            if (notificationCursorRequest.getDateTime() == null) {
                throw RestExceptionBuilder.newBuilder(NotificationCursorRestException.class)
                    .withErrorCode(NotificationCursorRestException.MISSING_DATE_TIME)
                    .build();
            }

            userService.updateNotificationCursor(authorization, user,
                notificationCursorRequest.getDateTime().toInstant());
            return notificationRestMapper
                .toNotificationCursorResponse(notificationCursorRequest.getDateTime().toInstant(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private List<NotificationResponse> getNotifications(Authorization authorization, User user,
        NotificationGetRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException {
        List<NotificationResponse> notifications = new ArrayList<>();
        for (UserNotificationLocalEndpoints localEndpoints : endpointsProvider.getLocalEndpoints()) {
            try {
                List<NotificationResponse> localNotifications =
                    localEndpoints.getNotifications(authorization.getAccessToken(), user.getId().getValue(), request,
                        timeZone);
                if (localNotifications != null) {
                    notifications.addAll(localNotifications);
                }
            } catch (ExtoleRestRuntimeException e) {
                LOG.error("Unexpected error while retrieving notifications for local cluster " +
                    "for client_id: {}, user_id: {}, error_code: {}, error_parameters: {}",
                    authorization.getClientId(), user.getId(), e.getErrorCode(), e.getParameters(), e);
            }
        }

        Set<String> eventIds = new HashSet<>();
        notifications.removeIf(event -> !eventIds.add(event.getEventId()));
        return notifications.stream()
            .sorted(RECORD_COMPARATOR.reversed())
            .collect(Collectors.toList());
    }
}
