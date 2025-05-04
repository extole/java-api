package com.extole.client.rest.impl.v0;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.actions.Action;
import com.extole.actions.ActionNotFoundByIdException;
import com.extole.actions.ActionQueryBuilder;
import com.extole.actions.ActionService;
import com.extole.actions.ReviewStatusUpdateService;
import com.extole.actions.review.ReviewStatusUpdate;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.v0.ActionDetailResponse;
import com.extole.client.rest.v0.ActionEndpoints;
import com.extole.client.rest.v0.ActionResponse;
import com.extole.client.rest.v0.ActionRestException;
import com.extole.client.rest.v0.ActionType;
import com.extole.client.rest.v0.DataResponse;
import com.extole.client.rest.v0.QualityScore;
import com.extole.client.rest.v0.ResultList;
import com.extole.client.rest.v0.ReviewStatus;
import com.extole.client.rest.v0.ReviewStatusCauseType;
import com.extole.client.rest.v0.ReviewStatusUpdateResponse;
import com.extole.client.rest.v0.ReviewStatusUpdateTriggerResponse;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.rewards.service.Reward;
import com.extole.rewards.service.RewardService;

@Provider
public class ActionEndpointsImpl implements ActionEndpoints {
    private static final int REWARD_PAGE_LIMIT = 100;
    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardService rewardService;
    private final ActionService actionService;
    private final ReviewStatusUpdateService reviewStatusUpdateService;

    @Autowired
    public ActionEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        RewardService rewardService,
        ActionService actionService,
        ReviewStatusUpdateService reviewStatusUpdateService) {
        this.authorizationProvider = authorizationProvider;
        this.rewardService = rewardService;
        this.actionService = actionService;
        this.reviewStatusUpdateService = reviewStatusUpdateService;
    }

    @Override
    public ResultList<ActionResponse> listActions(String campaignId,
        Long startDate,
        Long endDate,
        String actionChannel,
        List<ActionType> actionTypes,
        List<QualityScore> qualityScores,
        List<ReviewStatus> reviewStatuses,
        String search,
        String personId,
        Integer offset,
        Integer limit,
        String accessToken,
        ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            List<ActionResponse> actionResponses = new ArrayList<>();
            ActionQueryBuilder actionQueryBuilder = actionService.createActionQueryBuilder(authorization);
            if (!Strings.isNullOrEmpty(campaignId)) {
                actionQueryBuilder.withCampaignId(Id.valueOf(campaignId));
            }
            if (startDate != null) {
                actionQueryBuilder.withStartDate(startDate);
            }
            if (endDate != null) {
                actionQueryBuilder.withEndDate(endDate);
            }
            if (!Strings.isNullOrEmpty(actionChannel)) {
                actionQueryBuilder.withChannel(actionChannel);
            }
            if (actionTypes != null) {
                actionQueryBuilder.withActionTypes(actionTypes.stream()
                    .map(actionType -> com.extole.actions.ActionType.valueOf(actionType.name()))
                    .collect(Collectors.toList()));
            }
            if (qualityScores != null) {
                actionQueryBuilder.withQualityScores(qualityScores.stream()
                    .map(qualityScore -> com.extole.quality.model.QualityScore.valueOf(qualityScore.name()))
                    .collect(Collectors.toList()));
            }
            if (reviewStatuses != null) {
                actionQueryBuilder.withReviewStatuses(reviewStatuses.stream()
                    .map(reviewStatus -> com.extole.actions.review.ReviewStatus.valueOf(reviewStatus.name()))
                    .collect(Collectors.toList()));
            }
            if (!Strings.isNullOrEmpty(search)) {
                actionQueryBuilder.withSearch(search);

                List<ActionResponse> actionResponsesByCouponCode = rewardService.createRewardQueryBuilder(authorization)
                    .withPartnerRewardIds(Collections.singletonList(search.trim()))
                    .withSuccessfulOnly(false)
                    .withLimit(Integer.valueOf(REWARD_PAGE_LIMIT))
                    .withOffset(Integer.valueOf(0))
                    .list()
                    .stream()
                    .map(Reward::getRootEventId)
                    .map(id -> actionService.findById(authorization, Id.valueOf(id.getValue())))
                    .filter(Optional::isPresent)
                    .map(action -> toResponse(action.get(), false, true, timeZone))
                    .collect(Collectors.toList());
                actionResponses.addAll(actionResponsesByCouponCode);
            }
            if (!Strings.isNullOrEmpty(personId)) {
                actionQueryBuilder.withPersonId(Id.valueOf(personId));
            }
            if (offset != null) {
                actionQueryBuilder.withOffset(offset);
            }
            if (limit != null) {
                actionQueryBuilder.withLimit(limit);
            }

            for (Action action : actionQueryBuilder.list()) {
                actionResponses.add(toResponse(authorization, action, timeZone));
            }
            int defaultedLimit = limit == null ? ActionQueryBuilder.DEFAULT_LIMIT : limit.intValue();
            boolean moreResults = actionResponses.size() > defaultedLimit;
            return new ResultList<>(moreResults,
                moreResults ? actionResponses.subList(0, defaultedLimit) : actionResponses);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        }
    }

    @Override
    public DataResponse getActionDetail(String actionIdAsString, String accessToken, ZoneId timeZone)
        throws ActionRestException, UserAuthorizationRestException {
        validateActionId(actionIdAsString);
        Id<Action> actionId = Id.valueOf(actionIdAsString);
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Action action = actionService.findById(authorization, actionId)
                .orElseThrow(() -> RestExceptionBuilder.newBuilder(ActionRestException.class)
                    .withErrorCode(ActionRestException.ACTION_NOT_FOUND)
                    .addParameter("action_id", actionId)
                    .build());

            List<ReviewStatusUpdate> reviewUpdates =
                reviewStatusUpdateService.getUpdatesByActionId(authorization, actionId);

            List<ReviewStatusUpdateResponse> reviewStatusUpdateResponses = Lists.newArrayList(
                reviewUpdates.stream().map(review -> toResponse(review)).collect(Collectors.toUnmodifiableList()));

            Optional<Action> share =
                action.getViaShareId() == null
                    || action.getViaShareId().toString().equals(action.getActionId().getValue()) ? Optional.empty()
                        : actionService.findById(authorization, Id.valueOf(action.getViaShareId().toString()));
            List<Action> conversionsAndRegistrations = Lists.newArrayList();
            if (action.getActionType().equals(com.extole.actions.ActionType.SHARE)) {
                conversionsAndRegistrations = actionService.findOutcomesByShare(authorization, actionId);
            } else if (action.getActionType().equals(com.extole.actions.ActionType.CLICK)) {
                conversionsAndRegistrations = actionService.findOutcomesByClick(authorization, actionId);
            }
            List<ActionResponse> conversions = Lists.newArrayList();
            List<ActionResponse> registrations = Lists.newArrayList();
            for (Action candidateAction : conversionsAndRegistrations) {
                if (ActionType.PURCHASE.name().equals(candidateAction.getActionType().name())) {
                    conversions.add(toResponse(authorization, candidateAction, timeZone));
                } else if (ActionType.REGISTER.name().equals(candidateAction.getActionType().name())) {
                    registrations.add(toResponse(authorization, candidateAction, timeZone));
                }
            }

            ActionDetailResponse detailResponse =
                new ActionDetailResponse(toResponse(authorization, action, timeZone), reviewStatusUpdateResponses,
                    conversions, registrations,
                    share.map(shareAction -> toResponse(shareAction, false, false, timeZone)).orElse(null));
            return new DataResponse(Collections.singletonList(detailResponse));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public void deleteActionById(String actionId, String accessToken)
        throws ActionRestException, UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            validateActionId(actionId);
            actionService.deleteAction(authorization, Id.valueOf(actionId));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ActionNotFoundByIdException e) {
            throw RestExceptionBuilder.newBuilder(ActionRestException.class)
                .withErrorCode(ActionRestException.ACTION_NOT_FOUND)
                .addParameter("action_id", actionId)
                .withCause(e)
                .build();
        }
    }

    private void validateActionId(String actionIdAsString) throws ActionRestException {
        try {
            Long.parseLong(actionIdAsString.trim());
        } catch (NumberFormatException e) {
            throw RestExceptionBuilder.newBuilder(ActionRestException.class)
                .withErrorCode(ActionRestException.INVALID_ACTION_ID)
                .addParameter("action_id", actionIdAsString)
                .build();
        }
    }

    private ReviewStatusUpdateResponse toResponse(ReviewStatusUpdate review) {
        return new ReviewStatusUpdateResponse(
            review.getId().getValue(),
            review.getMessage(),
            review.getCauseId(),
            ReviewStatus.valueOf(review.getReviewStatus().name()),
            review.getName(),
            Long.valueOf(review.getUpdateDate()),
            ReviewStatusCauseType.valueOf(review.getCauseType().name()),
            review.getDebugMessage(),
            getTriggerResults(review),
            review.getData());
    }

    private List<ReviewStatusUpdateTriggerResponse> getTriggerResults(ReviewStatusUpdate review) {
        return review.getTriggerResults()
            .stream()
            .map(value -> new ReviewStatusUpdateTriggerResponse(
                value.getName(),
                value.getMessage(),
                QualityScore.valueOf(value.getQualityScore().name()),
                value.getLogMessages()))
            .collect(toUnmodifiableList());
    }

    private boolean isRewarded(Authorization authorization, Action action) throws AuthorizationException {
        return !rewardService.createRewardQueryBuilder(authorization)
            .withActionIds(Collections.singletonList(Id.valueOf(action.getActionId().getValue())))
            .withSuccessfulOnly(false)
            .withLimit(Integer.valueOf(1))
            .withOffset(Integer.valueOf(0))
            .list()
            .isEmpty();
    }

    private ActionResponse toResponse(Authorization authorization, Action action, ZoneId timeZone)
        throws AuthorizationException {
        return toResponse(action, action.getActionType().equals(com.extole.actions.ActionType.PURCHASE),
            isRewarded(authorization, action), timeZone);
    }

    private ActionResponse toResponse(Action action, boolean conversionsFlag, boolean rewarded, ZoneId timeZone) {
        return new ActionResponse(action.getActionId().getValue(),
            action.getClientId().getValue(),
            action.getCampaignId().getValue(),
            action.getChannel().getName(),
            ActionType.valueOf(action.getActionType().name()),
            Instant.ofEpochMilli(action.getActionDate()).atZone(timeZone),
            action.getZoneName(),
            action.getSiteId(),
            action.getProgramDomain(),
            action.getApiVersion(),
            action.getSource(),
            action.getEmail(),
            action.getChannelMessage(),
            action.getRecipients(),
            action.getSourceUrl(),
            action.getViaShareId() != null ? action.getViaShareId().toString() : null,
            action.getViaClickId().map(viaClickId -> viaClickId.toString()).orElse(null),
            action.getPersonId() != null ? action.getPersonId().getValue() : null,
            action.getBrowserId() != null ? action.getBrowserId().toString() : null,
            action.getSourceIp(),
            action.getClientParams(),
            action.getHttpHeaders(),
            action.getPartnerUserId(),
            action.getPartnerConversionId(),
            QualityScore.valueOf(action.getQualityScore().name()),
            ReviewStatus.valueOf(action.getReviewStatus().name()),
            action.getShareableId(),
            conversionsFlag,
            rewarded);
    }

}
