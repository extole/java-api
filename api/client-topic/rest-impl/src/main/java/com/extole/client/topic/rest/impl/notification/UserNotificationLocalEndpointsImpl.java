package com.extole.client.topic.rest.impl.notification;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.topic.event.service.NotificationEventService;
import com.extole.client.topic.event.service.NotificationRecentEvent;
import com.extole.client.topic.rest.NotificationGetRequest;
import com.extole.client.topic.rest.NotificationResponse;
import com.extole.client.topic.rest.UserNotificationLocalEndpoints;
import com.extole.client.topic.rest.UserNotificationRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.user.User;
import com.extole.model.shared.user.UserCache;

@Provider
public class UserNotificationLocalEndpointsImpl implements UserNotificationLocalEndpoints {
    private static final Comparator<NotificationResponse> RECORD_COMPARATOR =
        Comparator.comparing(NotificationResponse::getEventTime);

    private final ClientAuthorizationProvider authorizationProvider;
    private final NotificationEventService notificationEventService;
    private final UserCache userCache;
    private final NotificationRestMapper notificationRestMapper;
    private final HttpServletRequest servletRequest;

    @Inject
    public UserNotificationLocalEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        NotificationEventService notificationEventService,
        UserCache userCache,
        NotificationRestMapper notificationRestMapper,
        HttpServletRequest servletRequest) {
        this.authorizationProvider = authorizationProvider;
        this.notificationEventService = notificationEventService;
        this.userCache = userCache;
        this.notificationRestMapper = notificationRestMapper;
        this.servletRequest = servletRequest;
    }

    @Override
    public List<NotificationResponse> getNotifications(@UserAccessTokenParam String accessToken,
        String userId, NotificationGetRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = userCache.getById(authorization, Id.valueOf(userId)).orElseThrow(() -> {
            return RestExceptionBuilder.newBuilder(UserNotificationRestException.class)
                .withErrorCode(UserNotificationRestException.USER_NOT_FOUND)
                .addParameter("user_id", userId)
                .addParameter("client_id", authorization.getClientId())
                .addParameter("request_uri", servletRequest.getRequestURI())
                .build();
        });
        return getNotifications(authorization, user, request, timeZone);
    }

    private List<NotificationResponse> getNotifications(Authorization authorization, User user,
        NotificationGetRequest request, ZoneId timeZone) {
        NotificationEventService.NotificationRecentEventQueryBuilder notificationRecentEventQueryBuilder =
            notificationEventService.createRecentEventQuery(authorization.getClientId(), authorization.getIdentityId(),
                user.getId());
        if (request.getLimit().isPresent()) {
            if (request.getLimit().get().intValue() > 0) {
                notificationRecentEventQueryBuilder.withLimit(request.getLimit().get());
            } else {
                return Collections.emptyList();
            }
        }
        if (request.getOffset().isPresent() && request.getOffset().get().intValue() >= 0) {
            notificationRecentEventQueryBuilder.withOffset(request.getOffset().get());
        }
        if (request.wasSnoozed().isPresent()) {
            notificationRecentEventQueryBuilder
                .addFilter(event -> event.getSnoozeId().isPresent() == request.wasSnoozed().get().booleanValue());
        }
        if (request.getHavingAllTags().isPresent()) {
            notificationRecentEventQueryBuilder.addFilter(event -> {
                Set<String> eventTags = new HashSet<>(event.getTags());
                eventTags.add(event.getName());
                return eventTags.containsAll(request.getHavingAllTags().get());
            });
        }
        if (request.getEventId().isPresent()) {
            notificationRecentEventQueryBuilder
                .addFilter(event -> event.getEventId().getValue().equals(request.getEventId().get()));
        }
        if (request.getSubscriptionId().isPresent()) {
            notificationRecentEventQueryBuilder
                .addFilter(event -> event.getSubscriptionId().getValue().equals(request.getSubscriptionId().get()));
        }
        List<NotificationRecentEvent> events = notificationRecentEventQueryBuilder.query();
        return events.stream()
            .map(event -> notificationRestMapper.toNotificationResponse(event, timeZone))
            .sorted(RECORD_COMPARATOR.reversed())
            .collect(Collectors.toList());
    }
}
