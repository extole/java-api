package com.extole.client.rest.impl.webhook.reward.filter.tags;

import static com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterRestException.TAGS_REWARD_WEBHOOK_FILTER_BUILD_FAILED;
import static com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterRestException.TAGS_REWARD_WEBHOOK_FILTER_NOT_FOUND;
import static com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterRestException.TAGS_REWARD_WEBHOOK_FILTER_REWARD_TAGS_EMPTY;
import static com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterRestException.TAGS_REWARD_WEBHOOK_FILTER_REWARD_TAGS_MISSING;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterCreateRequest;
import com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterEndpoints;
import com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterRestException;
import com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterUpdateRequest;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.webhook.reward.filter.tags.TagsRewardWebhookFilter;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookControllerActionException;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookUserSubscriptionChannelException;
import com.extole.model.service.webhook.built.BuildWebhookException;
import com.extole.model.service.webhook.reward.RewardWebhookNotFoundException;
import com.extole.model.service.webhook.reward.RewardWebhookService;
import com.extole.model.service.webhook.reward.filter.RewardWebhookFilterBuilderType;
import com.extole.model.service.webhook.reward.filter.tags.TagsRewardWebhookFilterBuilder;
import com.extole.model.service.webhook.reward.filter.tags.TagsRewardWebhookFilterEmptyTagsException;
import com.extole.model.service.webhook.reward.filter.tags.TagsRewardWebhookFilterNotFoundException;
import com.extole.model.service.webhook.reward.filter.tags.TagsRewardWebhookFilterQueryBuilder;
import com.extole.model.service.webhook.reward.filter.tags.TagsRewardWebhookFilterService;

@Provider
public class TagsRewardWebhookFilterEndpointsImpl
    implements TagsRewardWebhookFilterEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardWebhookService rewardWebhookService;
    private final ComponentService componentService;
    private final TagsRewardWebhookFilterService tagsRewardWebhookFilterService;
    private final TagsRewardWebhookFilterRestMapper tagsRewardWebhookFilterRestMapper;

    @Autowired
    public TagsRewardWebhookFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        RewardWebhookService rewardWebhookService,
        ComponentService componentService,
        TagsRewardWebhookFilterService tagsRewardWebhookFilterService,
        TagsRewardWebhookFilterRestMapper tagsRewardWebhookFilterRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.rewardWebhookService = rewardWebhookService;
        this.componentService = componentService;
        this.tagsRewardWebhookFilterService = tagsRewardWebhookFilterService;
        this.tagsRewardWebhookFilterRestMapper = tagsRewardWebhookFilterRestMapper;
    }

    @Override
    public List<TagsRewardWebhookFilterResponse> listTagsRewardWebhookFilters(String accessToken,
        String webhookId, ZoneId timeZone) throws UserAuthorizationRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            TagsRewardWebhookFilterQueryBuilder tagsRewardWebhookFilterQueryBuilder =
                tagsRewardWebhookFilterService.createRewardWebhookTagsFilterQueryBuilder(authorization,
                    Id.valueOf(webhookId));

            return tagsRewardWebhookFilterQueryBuilder.list().stream()
                .map(rewardWebhookTagsFilter -> tagsRewardWebhookFilterRestMapper
                    .toRewardWebhookTagsFilterResponse(rewardWebhookTagsFilter,
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
    public TagsRewardWebhookFilterResponse getTagsRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, TagsRewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            TagsRewardWebhookFilter tagsRewardWebhookFilter =
                tagsRewardWebhookFilterService.getRewardWebhookTagsFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));
            return tagsRewardWebhookFilterRestMapper.toRewardWebhookTagsFilterResponse(
                tagsRewardWebhookFilter,
                timeZone);

        } catch (TagsRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TagsRewardWebhookFilterRestException.class)
                .withErrorCode(TAGS_REWARD_WEBHOOK_FILTER_NOT_FOUND)
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
    public TagsRewardWebhookFilterResponse createTagsRewardWebhookFilter(String accessToken, String webhookId,
        TagsRewardWebhookFilterCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, TagsRewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {

            if (createRequest.getTags() == null) {
                throw RestExceptionBuilder.newBuilder(TagsRewardWebhookFilterRestException.class)
                    .withErrorCode(TAGS_REWARD_WEBHOOK_FILTER_REWARD_TAGS_MISSING)
                    .addParameter("webhook_id", webhookId)
                    .build();
            }

            TagsRewardWebhookFilter savedFilter =
                rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                    .addFilter(RewardWebhookFilterBuilderType.TAGS)
                    .withTags(createRequest.getTags())
                    .save();

            return tagsRewardWebhookFilterRestMapper.toRewardWebhookTagsFilterResponse(
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
        } catch (TagsRewardWebhookFilterEmptyTagsException e) {
            throw RestExceptionBuilder.newBuilder(TagsRewardWebhookFilterRestException.class)
                .withErrorCode(TAGS_REWARD_WEBHOOK_FILTER_REWARD_TAGS_EMPTY)
                .addParameter("webhook_id", webhookId)
                .build();
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(TagsRewardWebhookFilterRestException.class)
                .withErrorCode(TAGS_REWARD_WEBHOOK_FILTER_BUILD_FAILED)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        }
    }

    @Override
    public TagsRewardWebhookFilterResponse updateTagsRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, TagsRewardWebhookFilterUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException,
        TagsRewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {

            TagsRewardWebhookFilter webhookTagsFilter =
                tagsRewardWebhookFilterService.getRewardWebhookTagsFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));

            TagsRewardWebhookFilterBuilder updateBuilder =
                rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                    .updateFilter(webhookTagsFilter);

            updateRequest.getTags().ifPresent(tags -> updateBuilder.withTags(tags));
            return tagsRewardWebhookFilterRestMapper.toRewardWebhookTagsFilterResponse(updateBuilder.save(), timeZone);

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
        } catch (TagsRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TagsRewardWebhookFilterRestException.class)
                .withErrorCode(TAGS_REWARD_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
        } catch (TagsRewardWebhookFilterEmptyTagsException e) {
            throw RestExceptionBuilder.newBuilder(TagsRewardWebhookFilterRestException.class)
                .withErrorCode(TAGS_REWARD_WEBHOOK_FILTER_REWARD_TAGS_EMPTY)
                .addParameter("webhook_id", webhookId)
                .build();
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(TagsRewardWebhookFilterRestException.class)
                .withErrorCode(TAGS_REWARD_WEBHOOK_FILTER_BUILD_FAILED)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        }
    }

    @Override
    public TagsRewardWebhookFilterResponse archiveTagsRewardWebhookFilter(String accessToken,
        String webhookId, String filterId, ZoneId timeZone) throws UserAuthorizationRestException,
        TagsRewardWebhookFilterRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            TagsRewardWebhookFilter webhookTagsFilter =
                tagsRewardWebhookFilterService.getRewardWebhookTagsFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));

            rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                .removeFilter(webhookTagsFilter)
                .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));

            return tagsRewardWebhookFilterRestMapper
                .toRewardWebhookTagsFilterResponse(webhookTagsFilter, timeZone);
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
        } catch (TagsRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TagsRewardWebhookFilterRestException.class)
                .withErrorCode(TAGS_REWARD_WEBHOOK_FILTER_NOT_FOUND)
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
