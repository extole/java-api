package com.extole.api.impl.event;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.impl.event.asset.AssetConsumerEventImpl;
import com.extole.api.impl.event.audience.membership.created.AudienceMembershipCreatedConsumerEventImpl;
import com.extole.api.impl.event.audience.membership.removed.AudienceMembershipRemovedConsumerEventImpl;
import com.extole.api.impl.event.audience.membership.updated.AudienceMembershipUpdatedConsumerEventImpl;
import com.extole.api.impl.event.data_intelligence.DataIntelligenceConsumerEventImpl;
import com.extole.api.impl.event.internal.InternalConsumerEventImpl;
import com.extole.api.impl.event.internal.message.MessageConsumerEventImpl;
import com.extole.api.impl.event.internal.reward_state.RewardStateConsumerEventImpl;
import com.extole.api.impl.event.internal.send_reward.SendRewardConsumerEventImpl;
import com.extole.api.impl.event.share.ShareConsumerEventImpl;
import com.extole.api.impl.event.shareable.AddShareableConsumerEventImpl;
import com.extole.api.impl.event.shareable.ShareableConsumerEventImpl;
import com.extole.api.impl.event.step.StepConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.event.consumer.asset.AssetConsumerEvent;
import com.extole.event.consumer.audience.membership.created.AudienceMembershipCreatedConsumerEvent;
import com.extole.event.consumer.audience.membership.removed.AudienceMembershipRemovedConsumerEvent;
import com.extole.event.consumer.audience.membership.updated.AudienceMembershipUpdatedConsumerEvent;
import com.extole.event.consumer.incentivized.IncentivizedConsumerEvent;
import com.extole.event.consumer.input.InputConsumerEvent;
import com.extole.event.consumer.internal.InternalConsumerEvent;
import com.extole.event.consumer.internal.data_intelligence.DataIntelligenceConsumerEvent;
import com.extole.event.consumer.internal.message.MessageConsumerEvent;
import com.extole.event.consumer.internal.reward_state.RewardStateConsumerEvent;
import com.extole.event.consumer.internal.send_reward.SendRewardConsumerEvent;
import com.extole.event.consumer.share.ShareConsumerEvent;
import com.extole.event.consumer.shareable.AddShareableConsumerEvent;
import com.extole.event.consumer.shareable.ShareableConsumerEvent;
import com.extole.event.consumer.step.StepConsumerEvent;

public class ConsumerEventToApiEventMapper {

    public ConsumerEvent map(com.extole.event.consumer.ConsumerEvent event,
        LazyLoadingSupplier<Person> personSupplier) {
        switch (event.getType()) {
            case INPUT:
                return mapInputConsumerEvent((InputConsumerEvent) event, personSupplier);
            case INCENTIVIZED:
                return mapIncentivizedConsumerEvent((IncentivizedConsumerEvent) event, personSupplier);
            case STEP:
                return mapStepConsumerEvent((StepConsumerEvent) event, personSupplier);
            case REWARD:
                return mapRewardStateConsumerEvent((RewardStateConsumerEvent) event, personSupplier);
            case SEND_REWARD:
                return mapSendRewardConsumerEvent((SendRewardConsumerEvent) event, personSupplier);
            case MESSAGE:
                return mapMessageConsumerEvent((MessageConsumerEvent) event, personSupplier);
            case INTERNAL:
                return mapInternalConsumerEvent((InternalConsumerEvent) event, personSupplier);
            case SHAREABLE:
                return mapShareableConsumerEvent((ShareableConsumerEvent) event, personSupplier);
            case ADD_SHAREABLE:
                return mapAddShareableConsumerEvent((AddShareableConsumerEvent) event, personSupplier);
            case ASSET:
                return mapAssetConsumerEvent((AssetConsumerEvent) event, personSupplier);
            case SHARE:
                return mapShareConsumerEvent((ShareConsumerEvent) event, personSupplier);
            case DATA_INTELLIGENCE:
                return mapDataIntelligenceConsumerEvent((DataIntelligenceConsumerEvent) event, personSupplier);
            case AUDIENCE_MEMBERSHIP_CREATED:
                return mapAudienceMembershipCreatedConsumerEvent((AudienceMembershipCreatedConsumerEvent) event,
                    personSupplier);
            case AUDIENCE_MEMBERSHIP_UPDATED:
                return mapAudienceMembershipUpdatedConsumerEvent((AudienceMembershipUpdatedConsumerEvent) event,
                    personSupplier);
            case AUDIENCE_MEMBERSHIP_REMOVED:
                return mapAudienceMembershipRemovedConsumerEvent((AudienceMembershipRemovedConsumerEvent) event,
                    personSupplier);
            default:
                return ConsumerEventImpl.newInstance(event, personSupplier.get());
        }
    }

    public com.extole.api.event.InputConsumerEvent mapInputConsumerEvent(InputConsumerEvent event,
        LazyLoadingSupplier<Person> personSupplier) {
        return InputConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    public com.extole.api.event.IncentivizedConsumerEvent mapIncentivizedConsumerEvent(IncentivizedConsumerEvent event,
        LazyLoadingSupplier<Person> personSupplier) {
        return IncentivizedConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    public com.extole.api.event.step.StepConsumerEvent mapStepConsumerEvent(StepConsumerEvent event,
        LazyLoadingSupplier<Person> personSupplier) {
        return StepConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    public com.extole.api.event.internal.reward_state.RewardStateConsumerEvent mapRewardStateConsumerEvent(
        RewardStateConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return RewardStateConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private com.extole.api.event.internal.send_reward.SendRewardConsumerEvent mapSendRewardConsumerEvent(
        SendRewardConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return SendRewardConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    public com.extole.api.event.internal.message.MessageConsumerEvent mapMessageConsumerEvent(
        MessageConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return MessageConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    public com.extole.api.event.internal.InternalConsumerEvent mapInternalConsumerEvent(
        InternalConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return InternalConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private com.extole.api.event.shareable.ShareableConsumerEvent
        mapShareableConsumerEvent(ShareableConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return ShareableConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private ConsumerEvent mapAddShareableConsumerEvent(AddShareableConsumerEvent event,
        LazyLoadingSupplier<Person> personSupplier) {
        return AddShareableConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private com.extole.api.event.asset.AssetConsumerEvent mapAssetConsumerEvent(AssetConsumerEvent event,
        LazyLoadingSupplier<Person> personSupplier) {
        return AssetConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private ConsumerEvent mapShareConsumerEvent(ShareConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return ShareConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private ConsumerEvent mapDataIntelligenceConsumerEvent(DataIntelligenceConsumerEvent event,
        LazyLoadingSupplier<Person> personSupplier) {
        return DataIntelligenceConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private com.extole.api.event.audience.membership.created.AudienceMembershipCreatedConsumerEvent
        mapAudienceMembershipCreatedConsumerEvent(
            AudienceMembershipCreatedConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return AudienceMembershipCreatedConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private com.extole.api.event.audience.membership.updated.AudienceMembershipUpdatedConsumerEvent
        mapAudienceMembershipUpdatedConsumerEvent(
            AudienceMembershipUpdatedConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return AudienceMembershipUpdatedConsumerEventImpl.newInstance(event, personSupplier.get());
    }

    private com.extole.api.event.audience.membership.removed.AudienceMembershipRemovedConsumerEvent
        mapAudienceMembershipRemovedConsumerEvent(
            AudienceMembershipRemovedConsumerEvent event, LazyLoadingSupplier<Person> personSupplier) {
        return AudienceMembershipRemovedConsumerEventImpl.newInstance(event, personSupplier.get());
    }

}
