package com.extole.client.rest.impl.subscription;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.subscription.channel.request.UserSubscriptionChannelRequestMapperRegistry;
import com.extole.client.rest.impl.subscription.channel.response.UserSubscriptionChannelResponseMapperRegistry;
import com.extole.client.rest.subcription.UserSubscriptionCreationRestException;
import com.extole.client.rest.subcription.UserSubscriptionEndpoints;
import com.extole.client.rest.subcription.UserSubscriptionRequest;
import com.extole.client.rest.subcription.UserSubscriptionResponse;
import com.extole.client.rest.subcription.UserSubscriptionRestException;
import com.extole.client.rest.subcription.UserSubscriptionUpdateRequest;
import com.extole.client.rest.subcription.UserSubscriptionValidationRestException;
import com.extole.client.rest.subcription.channel.SlackUserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.ThirdPartyEmailUserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.UserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.WebhookUserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.request.SubscriptionChannelRequest;
import com.extole.client.rest.subcription.channel.response.SubscriptionChannelResponse;
import com.extole.client.rest.user.UserRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.expression.EvaluatableRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.subscription.FilteringLevel;
import com.extole.model.entity.subscription.UserSubscription;
import com.extole.model.service.subscription.InvalidDedupeDurationException;
import com.extole.model.service.subscription.InvalidJavascriptSubscriptionFilterExpressionException;
import com.extole.model.service.subscription.InvalidUserSubscriptionTagsException;
import com.extole.model.service.subscription.UserSubscriptionBuilder;
import com.extole.model.service.subscription.UserSubscriptionDuplicateException;
import com.extole.model.service.subscription.UserSubscriptionInvalidChannelTypesForZeroDedupeDurationException;
import com.extole.model.service.subscription.UserSubscriptionNotFoundException;
import com.extole.model.service.subscription.UserSubscriptionService;
import com.extole.model.service.subscription.channel.InvalidWebhookTypeUserSubscriptionChannelBuildException;
import com.extole.model.service.subscription.channel.UserSubscriptionChannelBuildException;
import com.extole.model.service.subscription.channel.WebhookNotFoundUserSubscriptionChannelBuildException;
import com.extole.model.service.subscription.channel.email.ThirdPartyEmailUserSubscriptionChannelInvalidEmailException;
import com.extole.model.service.subscription.channel.email.ThirdPartyEmailUserSubscriptionChannelMissingRecipientException;
import com.extole.model.service.subscription.channel.email.ThirdPartyEmailUserSubscriptionUserExistsForEmailException;
import com.extole.model.service.user.UserNotFoundException;

@Provider
public class UserSubscriptionEndpointsImpl implements UserSubscriptionEndpoints {
    private final UserSubscriptionService userSubscriptionService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final UserSubscriptionChannelRequestMapperRegistry requestMapperRegistry;
    private final UserSubscriptionChannelResponseMapperRegistry responseMapperRegistry;

    @Inject
    public UserSubscriptionEndpointsImpl(UserSubscriptionService userSubscriptionService,
        ClientAuthorizationProvider authorizationProvider,
        UserSubscriptionChannelRequestMapperRegistry requestMapperRegistry,
        UserSubscriptionChannelResponseMapperRegistry responseMapperRegistry) {
        this.userSubscriptionService = userSubscriptionService;
        this.authorizationProvider = authorizationProvider;
        this.requestMapperRegistry = requestMapperRegistry;
        this.responseMapperRegistry = responseMapperRegistry;
    }

    @Override
    public List<UserSubscriptionResponse> list(String accessToken, String userId)
        throws UserAuthorizationRestException, UserRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return userSubscriptionService.createQueryBuilder(authorization)
                .withUserId(Id.valueOf(userId))
                .list()
                .stream()
                .map(this::toUserSubscriptionResponse)
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .build();
        }
    }

    @Override
    public UserSubscriptionResponse get(String accessToken, String userId, String subscriptionId)
        throws UserAuthorizationRestException, UserRestException, UserSubscriptionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            UserSubscription userSubscription =
                userSubscriptionService.get(authorization, Id.valueOf(userId), Id.valueOf(subscriptionId));
            return toUserSubscriptionResponse(userSubscription);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .build();
        } catch (UserSubscriptionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionRestException.class)
                .withErrorCode(UserSubscriptionRestException.INVALID_SUBSCRIPTION_ID)
                .addParameter("subscription_id", subscriptionId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public UserSubscriptionResponse create(String accessToken, String userId, UserSubscriptionRequest request)
        throws UserAuthorizationRestException, UserRestException, UserSubscriptionCreationRestException,
        UserSubscriptionValidationRestException, WebhookUserSubscriptionChannelValidationRestException,
        SlackUserSubscriptionChannelValidationRestException, EvaluatableRestException,
        ThirdPartyEmailUserSubscriptionChannelValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            UserSubscriptionBuilder builder = userSubscriptionService.create(authorization, Id.valueOf(userId))
                .withHavingAllTags(request.getHavingAllTags())
                .withLevel(FilteringLevel.valueOf(request.getLevel().name()));

            if (request.getFilterExpression().isPresent()) {
                builder.withFilterExpression(request.getFilterExpression().getValue());
            }
            if (request.getDedupeDurationMs().isPresent()) {
                builder.withDedupeDurationMs(request.getDedupeDurationMs().getValue());
            }
            if (request.getChannels().isPresent()) {
                for (SubscriptionChannelRequest item : request.getChannels().getValue()) {
                    requestMapperRegistry.getRequestMapper(item.getType()).update(builder, item);
                }
            }

            UserSubscription userSubscription = builder.save();
            return toUserSubscriptionResponse(userSubscription);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .build();
        } catch (InvalidUserSubscriptionTagsException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionValidationRestException.class)
                .withErrorCode(UserSubscriptionValidationRestException.INVALID_TAGS)
                .addParameter("having_all_tags", request.getHavingAllTags())
                .withCause(e)
                .build();
        } catch (UserSubscriptionDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionCreationRestException.class)
                .withErrorCode(UserSubscriptionCreationRestException.DUPLICATE_USER_SUBSCRIPTION)
                .addParameter("having_all_tags", request.getHavingAllTags())
                .addParameter("filtering_level", request.getLevel())
                .addParameter("user_id", userId)
                .withCause(e)
                .build();
        } catch (InvalidDedupeDurationException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionValidationRestException.class)
                .withErrorCode(UserSubscriptionValidationRestException.INVALID_DEDUPE_DURATION)
                .addParameter("dedupe_duration_ms", Long.valueOf(e.getDedupeDurationMs()))
                .addParameter("min_dedupe_duration_ms", Long.valueOf(e.getMinDedupeDurationMs()))
                .addParameter("max_dedupe_duration_ms", Long.valueOf(e.getMaxDedupeDurationMs()))
                .withCause(e)
                .build();
        } catch (UserSubscriptionInvalidChannelTypesForZeroDedupeDurationException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionValidationRestException.class)
                .withErrorCode(UserSubscriptionValidationRestException.INVALID_CHANNEL_TYPES_FOR_ZERO_DEDUPE_DURATION)
                .addParameter("invalid_channel_types", e.getChannelTypes())
                .withCause(e)
                .build();
        } catch (WebhookNotFoundUserSubscriptionChannelBuildException e) {
            throw RestExceptionBuilder.newBuilder(WebhookUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(WebhookUserSubscriptionChannelValidationRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", e.getWebhookId())
                .withCause(e)
                .build();
        } catch (InvalidWebhookTypeUserSubscriptionChannelBuildException e) {
            throw RestExceptionBuilder.newBuilder(WebhookUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(WebhookUserSubscriptionChannelValidationRestException.INVALID_WEBHOOK_TYPE)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("webhook_type", e.getWebhookType())
                .withCause(e)
                .build();
        } catch (ThirdPartyEmailUserSubscriptionChannelInvalidEmailException e) {
            throw RestExceptionBuilder.newBuilder(ThirdPartyEmailUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(ThirdPartyEmailUserSubscriptionChannelValidationRestException.INVALID_RECIPIENT)
                .addParameter("recipient", e.getRecipient())
                .withCause(e)
                .build();
        } catch (ThirdPartyEmailUserSubscriptionChannelMissingRecipientException e) {
            throw RestExceptionBuilder
                .newBuilder(ThirdPartyEmailUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(ThirdPartyEmailUserSubscriptionChannelValidationRestException.MISSING_RECIPIENT)
                .withCause(e)
                .build();
        } catch (ThirdPartyEmailUserSubscriptionUserExistsForEmailException e) {
            throw RestExceptionBuilder
                .newBuilder(ThirdPartyEmailUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(ThirdPartyEmailUserSubscriptionChannelValidationRestException.RECIPIENT_IS_A_USER)
                .addParameter("recipient", e.getRecipient())
                .withCause(e)
                .build();
        } catch (InvalidJavascriptSubscriptionFilterExpressionException e) {
            throw RestExceptionBuilder.newBuilder(EvaluatableRestException.class)
                .withErrorCode(EvaluatableRestException.JAVASCRIPT_EXPRESSION_INVALID_SYNTAX)
                .addParameter("description", e.getOutput().toString())
                .withCause(e).build();
        } catch (UserSubscriptionChannelValidationRestException e) {
            tryToTranslateAndRethrow(e);
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (UserSubscriptionChannelBuildException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public UserSubscriptionResponse update(String accessToken, String userId, String subscriptionId,
        UserSubscriptionUpdateRequest request)
        throws UserAuthorizationRestException, UserRestException, UserSubscriptionCreationRestException,
        UserSubscriptionValidationRestException, UserSubscriptionRestException,
        WebhookUserSubscriptionChannelValidationRestException, SlackUserSubscriptionChannelValidationRestException,
        EvaluatableRestException, ThirdPartyEmailUserSubscriptionChannelValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            UserSubscriptionBuilder builder =
                userSubscriptionService.update(authorization, Id.valueOf(userId), Id.valueOf(subscriptionId));
            request.getHavingAllTags().ifPresent(havingAllTags -> builder.withHavingAllTags(havingAllTags));
            request.getLevel().ifPresent(level -> builder.withLevel(FilteringLevel.valueOf(level.name())));
            if (request.getFilterExpression().isPresent()) {
                builder.withFilterExpression(request.getFilterExpression().getValue());
            }
            request.getDedupeDurationMs().ifPresent(durationMs -> builder.withDedupeDurationMs(durationMs));
            request.getChannels().ifPresent(channels -> {
                builder.clearChannels();
                for (SubscriptionChannelRequest item : channels) {
                    requestMapperRegistry.getRequestMapper(item.getType()).update(builder, item);
                }
            });

            UserSubscription userSubscription = builder.save();
            return toUserSubscriptionResponse(userSubscription);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .build();
        } catch (UserSubscriptionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionRestException.class)
                .withErrorCode(UserSubscriptionRestException.INVALID_SUBSCRIPTION_ID)
                .addParameter("subscription_id", subscriptionId)
                .withCause(e)
                .build();
        } catch (InvalidUserSubscriptionTagsException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionValidationRestException.class)
                .withErrorCode(UserSubscriptionValidationRestException.INVALID_TAGS)
                .addParameter("having_all_tags", request.getHavingAllTags())
                .withCause(e)
                .build();
        } catch (UserSubscriptionDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionCreationRestException.class)
                .withErrorCode(UserSubscriptionCreationRestException.DUPLICATE_USER_SUBSCRIPTION)
                .addParameter("having_all_tags", request.getHavingAllTags())
                .addParameter("filtering_level", request.getLevel())
                .addParameter("user_id", userId)
                .withCause(e)
                .build();
        } catch (InvalidDedupeDurationException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionValidationRestException.class)
                .withErrorCode(UserSubscriptionValidationRestException.INVALID_DEDUPE_DURATION)
                .addParameter("dedupe_duration_ms", Long.valueOf(e.getDedupeDurationMs()))
                .addParameter("min_dedupe_duration_ms", Long.valueOf(e.getMinDedupeDurationMs()))
                .addParameter("max_dedupe_duration_ms", Long.valueOf(e.getMaxDedupeDurationMs()))
                .withCause(e)
                .build();
        } catch (UserSubscriptionInvalidChannelTypesForZeroDedupeDurationException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionValidationRestException.class)
                .withErrorCode(UserSubscriptionValidationRestException.INVALID_CHANNEL_TYPES_FOR_ZERO_DEDUPE_DURATION)
                .addParameter("invalid_channel_types", e.getChannelTypes())
                .withCause(e)
                .build();
        } catch (WebhookNotFoundUserSubscriptionChannelBuildException e) {
            throw RestExceptionBuilder.newBuilder(WebhookUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(WebhookUserSubscriptionChannelValidationRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", e.getWebhookId())
                .withCause(e)
                .build();
        } catch (InvalidWebhookTypeUserSubscriptionChannelBuildException e) {
            throw RestExceptionBuilder.newBuilder(WebhookUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(WebhookUserSubscriptionChannelValidationRestException.INVALID_WEBHOOK_TYPE)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("webhook_type", e.getWebhookType())
                .withCause(e)
                .build();
        } catch (ThirdPartyEmailUserSubscriptionChannelInvalidEmailException e) {
            throw RestExceptionBuilder.newBuilder(ThirdPartyEmailUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(ThirdPartyEmailUserSubscriptionChannelValidationRestException.INVALID_RECIPIENT)
                .addParameter("recipient", e.getRecipient())
                .withCause(e)
                .build();
        } catch (ThirdPartyEmailUserSubscriptionChannelMissingRecipientException e) {
            throw RestExceptionBuilder
                .newBuilder(ThirdPartyEmailUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(ThirdPartyEmailUserSubscriptionChannelValidationRestException.MISSING_RECIPIENT)
                .withCause(e)
                .build();
        } catch (ThirdPartyEmailUserSubscriptionUserExistsForEmailException e) {
            throw RestExceptionBuilder
                .newBuilder(ThirdPartyEmailUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(ThirdPartyEmailUserSubscriptionChannelValidationRestException.RECIPIENT_IS_A_USER)
                .addParameter("recipient", e.getRecipient())
                .withCause(e)
                .build();
        } catch (InvalidJavascriptSubscriptionFilterExpressionException e) {
            throw RestExceptionBuilder.newBuilder(EvaluatableRestException.class)
                .withErrorCode(EvaluatableRestException.JAVASCRIPT_EXPRESSION_INVALID_SYNTAX)
                .addParameter("description", e.getOutput().toString())
                .withCause(e).build();
        } catch (UserSubscriptionChannelValidationRestException e) {
            tryToTranslateAndRethrow(e);
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (UserSubscriptionChannelBuildException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public SuccessResponse delete(String accessToken, String userId, String subscriptionId)
        throws UserAuthorizationRestException, UserRestException, UserSubscriptionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            userSubscriptionService.update(authorization, Id.valueOf(userId), Id.valueOf(subscriptionId))
                .withDeleted()
                .save();
            return SuccessResponse.SUCCESS;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .build();
        } catch (UserSubscriptionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserSubscriptionRestException.class)
                .withErrorCode(UserSubscriptionRestException.INVALID_SUBSCRIPTION_ID)
                .addParameter("subscription_id", subscriptionId)
                .withCause(e)
                .build();
        } catch (UserSubscriptionDuplicateException | UserSubscriptionChannelBuildException
            | UserSubscriptionInvalidChannelTypesForZeroDedupeDurationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }

    }

    private List<SubscriptionChannelResponse> mapChannels(UserSubscription userSubscription) {
        return userSubscription.getChannels().stream()
            .map(item -> responseMapperRegistry.getMapper(item.getType()).toResponse(item))
            .collect(Collectors.toList());
    }

    private UserSubscriptionResponse toUserSubscriptionResponse(UserSubscription userSubscription) {
        return new UserSubscriptionResponse(
            userSubscription.getId().getValue(),
            userSubscription.getHavingAllTags(),
            com.extole.client.rest.subcription.FilteringLevel.valueOf(userSubscription.getFilteringLevel().name()),
            userSubscription.getFilterExpression(),
            userSubscription.getDedupeDuration().toMillis(),
            mapChannels(userSubscription));
    }

    private void tryToTranslateAndRethrow(UserSubscriptionChannelValidationRestException exception)
        throws SlackUserSubscriptionChannelValidationRestException {
        if (exception.getClass().equals(SlackUserSubscriptionChannelValidationRestException.class)) {
            throw (SlackUserSubscriptionChannelValidationRestException) exception;
        }
    }
}
