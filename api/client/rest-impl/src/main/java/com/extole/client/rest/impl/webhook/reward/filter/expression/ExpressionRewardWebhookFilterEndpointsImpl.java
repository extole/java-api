package com.extole.client.rest.impl.webhook.reward.filter.expression;

import static com.extole.client.rest.webhook.reward.filter.expression.ExpressionRewardWebhookFilterRestException.EXPRESSION_REWARD_WEBHOOK_FILTER_BUILD_FAILED;
import static com.extole.client.rest.webhook.reward.filter.expression.ExpressionRewardWebhookFilterRestException.EXPRESSION_REWARD_WEBHOOK_FILTER_NOT_FOUND;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.client.rest.webhook.reward.filter.expression.ExpressionRewardWebhookFilterCreateRequest;
import com.extole.client.rest.webhook.reward.filter.expression.ExpressionRewardWebhookFilterEndpoints;
import com.extole.client.rest.webhook.reward.filter.expression.ExpressionRewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.expression.ExpressionRewardWebhookFilterRestException;
import com.extole.client.rest.webhook.reward.filter.expression.ExpressionRewardWebhookFilterUpdateRequest;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.webhook.reward.filter.expression.ExpressionRewardWebhookFilter;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookControllerActionException;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookUserSubscriptionChannelException;
import com.extole.model.service.webhook.built.BuildWebhookException;
import com.extole.model.service.webhook.reward.RewardWebhookNotFoundException;
import com.extole.model.service.webhook.reward.RewardWebhookService;
import com.extole.model.service.webhook.reward.filter.RewardWebhookFilterBuilderType;
import com.extole.model.service.webhook.reward.filter.expression.ExpressionRewardWebhookFilterBuilder;
import com.extole.model.service.webhook.reward.filter.expression.ExpressionRewardWebhookFilterNotFoundException;
import com.extole.model.service.webhook.reward.filter.expression.ExpressionRewardWebhookFilterQueryBuilder;
import com.extole.model.service.webhook.reward.filter.expression.ExpressionRewardWebhookFilterService;

@Provider
public class ExpressionRewardWebhookFilterEndpointsImpl implements ExpressionRewardWebhookFilterEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardWebhookService rewardWebhookService;
    private final ComponentService componentService;
    private final ExpressionRewardWebhookFilterService expressionRewardWebhookFilterService;
    private final ExpressionRewardWebhookFilterRestMapper expressionRewardWebhookFilterRestMapper;

    @Autowired
    public ExpressionRewardWebhookFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        RewardWebhookService rewardWebhookService,
        ComponentService componentService,
        ExpressionRewardWebhookFilterService expressionRewardWebhookFilterService,
        ExpressionRewardWebhookFilterRestMapper expressionRewardWebhookFilterRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.rewardWebhookService = rewardWebhookService;
        this.componentService = componentService;
        this.expressionRewardWebhookFilterService = expressionRewardWebhookFilterService;
        this.expressionRewardWebhookFilterRestMapper = expressionRewardWebhookFilterRestMapper;
    }

    @Override
    public List<ExpressionRewardWebhookFilterResponse> listExpressionRewardWebhookFilters(String accessToken,
        String webhookId, ZoneId timeZone) throws UserAuthorizationRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ExpressionRewardWebhookFilterQueryBuilder expressionRewardWebhookFilterQueryBuilder =
                expressionRewardWebhookFilterService.createRewardWebhookExpressionFilterQueryBuilder(authorization,
                    Id.valueOf(webhookId));

            return expressionRewardWebhookFilterQueryBuilder.list().stream()
                .map(rewardWebhookExpressionFilter -> expressionRewardWebhookFilterRestMapper
                    .toRewardWebhookExpressionFilterResponse(rewardWebhookExpressionFilter,
                        timeZone))
                .collect(Collectors.toList());
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
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
    public ExpressionRewardWebhookFilterResponse getExpressionRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, ExpressionRewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ExpressionRewardWebhookFilter expressionRewardWebhookFilter =
                expressionRewardWebhookFilterService.getRewardWebhookExpressionFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));
            return expressionRewardWebhookFilterRestMapper.toRewardWebhookExpressionFilterResponse(
                expressionRewardWebhookFilter, timeZone);

        } catch (ExpressionRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionRewardWebhookFilterRestException.class)
                .withErrorCode(EXPRESSION_REWARD_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
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
    public ExpressionRewardWebhookFilterResponse createExpressionRewardWebhookFilter(String accessToken,
        String webhookId, ExpressionRewardWebhookFilterCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, ExpressionRewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ExpressionRewardWebhookFilter savedFilter =
                rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                    .addFilter(RewardWebhookFilterBuilderType.EXPRESSION)
                    .withExpression(createRequest.getExpression())
                    .save();

            return expressionRewardWebhookFilterRestMapper.toRewardWebhookExpressionFilterResponse(
                savedFilter, timeZone);

        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionRewardWebhookFilterRestException.class)
                .withErrorCode(EXPRESSION_REWARD_WEBHOOK_FILTER_BUILD_FAILED)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        }
    }

    @Override
    public ExpressionRewardWebhookFilterResponse updateExpressionRewardWebhookFilter(String accessToken,
        String webhookId, String filterId, ExpressionRewardWebhookFilterUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException,
        ExpressionRewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {

            ExpressionRewardWebhookFilter webhookExpressionFilter =
                expressionRewardWebhookFilterService.getRewardWebhookExpressionFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));

            ExpressionRewardWebhookFilterBuilder<ExpressionRewardWebhookFilter> updateBuilder =
                rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                    .updateFilter(webhookExpressionFilter);

            updateRequest.getExpression().ifPresent(expression -> updateBuilder.withExpression(expression));
            return expressionRewardWebhookFilterRestMapper.toRewardWebhookExpressionFilterResponse(updateBuilder.save(),
                timeZone);

        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ExpressionRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionRewardWebhookFilterRestException.class)
                .withErrorCode(EXPRESSION_REWARD_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionRewardWebhookFilterRestException.class)
                .withErrorCode(EXPRESSION_REWARD_WEBHOOK_FILTER_BUILD_FAILED)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        }
    }

    @Override
    public ExpressionRewardWebhookFilterResponse archiveExpressionRewardWebhookFilter(String accessToken,
        String webhookId, String filterId, ZoneId timeZone) throws UserAuthorizationRestException,
        ExpressionRewardWebhookFilterRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ExpressionRewardWebhookFilter webhookExpressionFilter =
                expressionRewardWebhookFilterService.getRewardWebhookExpressionFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));

            rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                .removeFilter(webhookExpressionFilter)
                .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));

            return expressionRewardWebhookFilterRestMapper
                .toRewardWebhookExpressionFilterResponse(webhookExpressionFilter, timeZone);
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ExpressionRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionRewardWebhookFilterRestException.class)
                .withErrorCode(EXPRESSION_REWARD_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException | BuildWebhookException
            | WebhookAssociatedWithWebhookControllerActionException
            | WebhookAssociatedWithWebhookUserSubscriptionChannelException | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
