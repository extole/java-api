package com.extole.api.impl.service;

import java.time.Instant;
import java.util.Optional;

import com.extole.api.ClientDomain;
import com.extole.api.campaign.Campaign;
import com.extole.api.service.EarnRewardCommandEventBuilder;
import com.extole.api.service.FulfillRewardCommandEventBuilder;
import com.extole.api.service.RedeemRewardCommandEventBuilder;
import com.extole.api.service.RewardService;
import com.extole.authorization.service.ClientHandle;
import com.extole.event.reward.command.earn.EarnRewardCommandEventProducer;
import com.extole.event.reward.command.earn.EarnRewardCommandEventProducer.EarnRewardCommandEventBuilder.ContextBuilder;
import com.extole.event.reward.command.fulfill.FulfillRewardCommandEventProducer;
import com.extole.event.reward.command.redeem.RedeemRewardCommandEventProducer;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.journey.JourneyKey;
import com.extole.person.service.profile.journey.JourneyName;
import com.extole.sandbox.Sandbox;

public class RewardServiceImpl implements RewardService {

    private final FulfillRewardCommandEventProducer fulfillRewardCommandEventProducer;
    private final EarnRewardCommandEventProducer earnRewardCommandEventProducer;
    private final RedeemRewardCommandEventProducer redeemRewardCommandEventProducer;
    private final Id<ClientHandle> clientId;
    private final Id<?> rootEventId;
    private final Id<?> causeEventId;
    private final Id<PersonHandle> personId;
    private final Id<Campaign> campaignId;
    private final Id<ClientDomain> clientDomainId;
    private final String programLabel;
    private final Sandbox sandbox;
    private final Id<?> stepId;
    private final Id<?> stepActionId;
    private final String stepName;
    private final JourneyName journeyName;
    private final Optional<JourneyKey> journeyKey;

    public RewardServiceImpl(
        Id<ClientHandle> clientId,
        Id<?> rootEventId,
        Id<?> causeEventId,
        Id<?> campaignId,
        Id<?> clientDomainId,
        String programLabel,
        Id<?> stepId,
        Id<?> stepActionId,
        String stepName,
        Sandbox sandbox,
        Id<PersonHandle> personId,
        JourneyName journeyName,
        Optional<JourneyKey> journeyKey,
        FulfillRewardCommandEventProducer fulfillRewardCommandEventProducer,
        RedeemRewardCommandEventProducer redeemRewardCommandEventProducer,
        EarnRewardCommandEventProducer earnRewardCommandEventProducer) {
        this.earnRewardCommandEventProducer = earnRewardCommandEventProducer;
        this.fulfillRewardCommandEventProducer = fulfillRewardCommandEventProducer;
        this.clientId = clientId;
        this.rootEventId = rootEventId;
        this.causeEventId = causeEventId;
        this.personId = personId;
        this.redeemRewardCommandEventProducer = redeemRewardCommandEventProducer;
        this.campaignId = Id.valueOf(campaignId.getValue());
        this.clientDomainId = Id.valueOf(clientDomainId.getValue());
        this.sandbox = sandbox;
        this.programLabel = programLabel;
        this.stepId = stepId;
        this.stepActionId = stepActionId;
        this.stepName = stepName;
        this.journeyName = journeyName;
        this.journeyKey = journeyKey;
    }

    @Override
    public EarnRewardCommandEventBuilder createEarnRewardCommandEventBuilder() {
        EarnRewardCommandEventProducer.EarnRewardCommandEventBuilder eventBuilder =
            earnRewardCommandEventProducer.createBuilder()
                .withClientId(clientId)
                .withRootEventId(rootEventId)
                .withRewardActionId(rootEventId)
                .withCauseEventId(causeEventId)
                .withDeviceProfileId(personId)
                .withEventTime(Instant.now());

        ContextBuilder contextBuilder = eventBuilder.withContextBuilder()
            .withCampaignId(campaignId)
            .withClientDomainId(clientDomainId)
            .withSandbox(sandbox)
            .withProgramLabel(programLabel)
            .withJourneyName(journeyName);

        journeyKey.ifPresent(value -> contextBuilder.withJourneyKey(value));
        contextBuilder.build();

        eventBuilder.withStepEventContextBuilder()
            .withId(stepActionId)
            .withStepId(stepId)
            .withName(stepName)
            .withEventTime(Instant.now());
        return new EarnRewardCommandEventBuilderImpl(eventBuilder, journeyName);
    }

    @Override
    public FulfillRewardCommandEventBuilder createFulfillRewardCommandEventBuilder(String rewardId) {
        FulfillRewardCommandEventProducer.FulfillRewardCommandEventBuilder builder =
            fulfillRewardCommandEventProducer.createBuilder();

        builder.withRewardId(Id.valueOf(rewardId))
            .withClientId(clientId)
            .withRootEventId(rootEventId)
            .withCauseEventId(causeEventId)
            .withDeviceProfileId(personId)
            .withEventTime(Instant.now());

        return new FulfillRewardCommandEventBuilderImpl(builder);
    }

    @Override
    public RedeemRewardCommandEventBuilder createRedeemRewardCommandEventBuilder(String rewardId) {
        RedeemRewardCommandEventProducer.RedeemRewardCommandEventBuilder builder =
            redeemRewardCommandEventProducer.createBuilder();

        builder.withRewardId(Id.valueOf(rewardId))
            .withClientId(clientId)
            .withRootEventId(rootEventId)
            .withCauseEventId(causeEventId)
            .withDeviceProfileId(personId)
            .withEventTime(Instant.now());

        return new RedeemRewardCommandEventBuilderImpl(builder);
    }

}
