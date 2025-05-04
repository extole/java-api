package com.extole.client.rest.impl.rewards;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import org.springframework.stereotype.Component;

import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.rewards.CanceledRewardStateResponse;
import com.extole.client.rest.rewards.FailedRewardStateResponse;
import com.extole.client.rest.rewards.FulfilledRewardStateResponse;
import com.extole.client.rest.rewards.RedeemedRewardStateResponse;
import com.extole.client.rest.rewards.RevokedRewardStateResponse;
import com.extole.client.rest.rewards.RewardResponse;
import com.extole.client.rest.rewards.RewardStateResponse;
import com.extole.client.rest.rewards.RewardStateSummaryResponse;
import com.extole.client.rest.rewards.RewardStateType;
import com.extole.client.rest.rewards.SentRewardStateResponse;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.reward.PersonReward;
import com.extole.rewards.service.Reward;
import com.extole.rewards.service.state.RewardState;
import com.extole.rewards.service.state.cancel.CanceledRewardState;
import com.extole.rewards.service.state.fail.FailedRewardState;
import com.extole.rewards.service.state.fulfillment.FulfilledRewardState;
import com.extole.rewards.service.state.redeem.RedeemedRewardState;
import com.extole.rewards.service.state.revoke.RevokedRewardState;
import com.extole.rewards.service.state.send.SentRewardState;
import com.extole.rewards.service.state.summary.RewardStateSummary;

@Component
public class RewardRestMapper {

    public RewardResponse toRewardResponse(Reward reward, @Nullable Person person,
        ZoneId timeZone, @Nullable String partnerRewardSupplierId) {
        Optional<PersonReward> personReward = Optional.empty();

        Map<String, String> data = Collections.emptyMap();
        if (person != null) {
            personReward = person.getRewards()
                .stream()
                .filter(rewardCandidate -> rewardCandidate.getRewardId().equals(reward.getId()))
                .findFirst();

            if (personReward.isPresent()) {
                data = personReward.get().getData();
            }
        }
        return new RewardResponse(
            reward.getId().getValue(),
            reward.getCauseEventId().getValue(),
            reward.getRootEventId().getValue(),
            reward.getActionId().getValue(),
            reward.getRewardSupplierId().getValue(),
            partnerRewardSupplierId,
            Optional.ofNullable(reward.getPartnerRewardKeyType())
                .map(partnerRewardKeyType -> PartnerRewardKeyType.valueOf(partnerRewardKeyType.name()))
                .orElse(PartnerRewardKeyType.ID),
            reward.getCampaignId().getValue(),
            reward.getPersonId().getValue(),
            person != null ? person.getPartnerUserId() : null,
            person != null ? person.getEmail() : null,
            RewardStateType.valueOf(reward.getState().name()),
            reward.getFaceValue(),
            FaceValueType.valueOf(reward.getFaceValueType().name()),
            reward.getPartnerRewardId().orElse(null),
            reward.getCreatedAt().atZone(timeZone),
            data,
            personReward.map(value -> value.getJourneyName().getValue()).orElse("reward"),
            personReward.flatMap(value -> value.getJourneyKey()
                .map(journeyKey -> new JourneyKey(journeyKey.getName(), journeyKey.getValue()))),
            reward.getSandbox(),
            reward.getContainer());
    }

    public RewardStateResponse toRewardStateResponse(RewardState rewardState, ZoneId timezone) {
        return new RewardStateResponse(RewardStateType.valueOf(rewardState.getRewardStateType().name()),
            rewardState.getMessage().orElse(null),
            rewardState.isSuccessful(),
            rewardState.getOperatorUserId().map(Id::getValue).orElse(null),
            rewardState.getCreatedAt().atZone(timezone));
    }

    public FulfilledRewardStateResponse toRewardStateFulfillmentResponse(Reward reward,
        FulfilledRewardState fulfilledRewardState,
        ZoneId timezone) {
        return new FulfilledRewardStateResponse(fulfilledRewardState.getRewardId().getValue(),
            fulfilledRewardState.getPartnerRewardId().orElse(null),
            fulfilledRewardState.getCostCode().orElse(null),
            fulfilledRewardState.getAmount().orElse(null),
            reward.getFaceValue(),
            FaceValueType.valueOf(reward.getFaceValueType().name()),
            fulfilledRewardState.getCreatedAt().atZone(timezone),
            fulfilledRewardState.isSuccessful(),
            fulfilledRewardState.getOperatorUserId().map(Id::getValue).orElse(null),
            fulfilledRewardState.getMessage().orElse(null));
    }

    public SentRewardStateResponse toRewardStateSendResponse(SentRewardState sentRewardState, ZoneId timezone) {
        return new SentRewardStateResponse(
            sentRewardState.getRewardId().getValue(),
            sentRewardState.getPartnerRewardSentId().orElse(null),
            sentRewardState.getEmail().orElse(null),
            sentRewardState.getCreatedAt().atZone(timezone),
            sentRewardState.isSuccessful(),
            sentRewardState.getOperatorUserId().map(Id::getValue).orElse(null),
            sentRewardState.getMessage().orElse(null));
    }

    public RedeemedRewardStateResponse toRewardStateRedeemResponse(RedeemedRewardState redeemedRewardState,
        ZoneId timezone) {
        return new RedeemedRewardStateResponse(redeemedRewardState.getRewardId().getValue(),
            redeemedRewardState.getPartnerRewardRedeemId().orElse(null),
            redeemedRewardState.getMessage().orElse(null),
            redeemedRewardState.getOperatorUserId().map(Id::getValue).orElse(null),
            redeemedRewardState.getRootEventName().orElse(null),
            redeemedRewardState.getPartnerEventId().orElse(null),
            redeemedRewardState.getCauseEventId().orElse(null),
            redeemedRewardState.getCreatedAt().atZone(timezone));
    }

    public FailedRewardStateResponse toRewardStateFailResponse(FailedRewardState failedRewardState, ZoneId timezone) {
        return new FailedRewardStateResponse(failedRewardState.getRewardId().getValue(),
            failedRewardState.getCreatedAt().atZone(timezone),
            failedRewardState.getOperatorUserId().map(Id::getValue).orElse(null),
            failedRewardState.getMessage().orElse(null));
    }

    public CanceledRewardStateResponse toRewardStateCancelResponse(CanceledRewardState canceledRewardState,
        ZoneId timezone) {
        return new CanceledRewardStateResponse(canceledRewardState.getRewardId().getValue(),
            canceledRewardState.getCreatedAt().atZone(timezone),
            canceledRewardState.getOperatorUserId().map(Id::getValue).orElse(null),
            canceledRewardState.getMessage().orElse(null));
    }

    public RevokedRewardStateResponse toRewardStateRevokeResponse(RevokedRewardState revokedRewardState,
        ZoneId timezone) {
        return new RevokedRewardStateResponse(revokedRewardState.getRewardId().getValue(),
            revokedRewardState.getCreatedAt().atZone(timezone),
            revokedRewardState.getOperatorUserId().map(Id::getValue).orElse(null),
            revokedRewardState.getMessage().orElse(null));
    }

    public RewardStateSummaryResponse toRewardStateSummaryResponse(RewardStateSummary rewardStateSummary,
        ZoneId timezone) {
        return new RewardStateSummaryResponse(
            rewardStateSummary.getDateFrom().atZone(timezone),
            rewardStateSummary.getDateTo().atZone(timezone),
            rewardStateSummary.getEarnedCount(),
            rewardStateSummary.getFulfilledCount(),
            rewardStateSummary.getSentCount(),
            rewardStateSummary.getCanceledCount(),
            rewardStateSummary.getRedeemedCount(),
            rewardStateSummary.getFailedCount(),
            rewardStateSummary.getRevokedCount());
    }

}
