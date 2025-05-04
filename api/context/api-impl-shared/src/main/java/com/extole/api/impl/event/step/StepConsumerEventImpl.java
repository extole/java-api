package com.extole.api.impl.event.step;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import com.extole.api.event.ReferralContext;
import com.extole.api.event.step.PartnerEventId;
import com.extole.api.event.step.SelectedCampaignContext;
import com.extole.api.event.step.StepConsumerEvent;
import com.extole.api.impl.event.ConsumerEventImpl;
import com.extole.api.impl.event.ReferralContextImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;
import com.extole.event.consumer.step.campaign.StepMatchingCampaignContext;

public final class StepConsumerEventImpl extends ConsumerEventImpl implements StepConsumerEvent {

    private final String name;
    private final Set<String> aliases;
    private final boolean firstSiteVisit;
    private final SelectedCampaignContext selectedCampaignContext;
    private final Optional<ReferralContext> referralContext;
    private Optional<BigDecimal> value;
    private Optional<PartnerEventId> partnerEventId;

    private StepConsumerEventImpl(com.extole.event.consumer.step.StepConsumerEvent event, Person person) {
        super(event, person);
        this.name = event.getName();
        this.aliases =
            ImmutableSet.copyOf(event.getAliasPojos().stream().map(pojo -> pojo.getName()).collect(Collectors.toSet()));
        this.firstSiteVisit = event.isFirstSiteVisit();
        this.selectedCampaignContext = event.getSelectedCampaignContext()
            .map(context -> {
                Optional<StepMatchingCampaignContext> selectedStepMatchingCampaignContext =
                    event.getStepMatchingCampaignContexts().stream()
                        .filter(matchingContext -> matchingContext.isSelected())
                        .findFirst();

                if (selectedStepMatchingCampaignContext.isEmpty()) {
                    throw new StepConsumerEventRuntimeException(
                        "Missing selected StepMatchingCampaignContext in step event with id=" + event.getId()
                            + ", for client=" + event.getClientContext().getClientId());
                }

                SelectedCampaignContextImpl campaignContext =
                    new SelectedCampaignContextImpl(context, selectedStepMatchingCampaignContext.get());
                return campaignContext;
            }).orElse(null);

        this.referralContext = createReferralContext(event);
        this.value = event.getValue();
        this.partnerEventId =
            event.getPartnerEventId().map(value -> new PartnerEventIdImpl(value.getName(), value.getValue()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getAliases() {
        return aliases.toArray(new String[] {});
    }

    @Override
    public boolean isFirstSiteVisit() {
        return firstSiteVisit;
    }

    @Override
    public SelectedCampaignContext getSelectedCampaignContext() {
        return selectedCampaignContext;
    }

    @Nullable
    @Override
    public ReferralContext getReferralContext() {
        return referralContext.orElse(null);
    }

    @Nullable
    @Override
    public BigDecimal getValue() {
        return value.orElse(null);
    }

    @Nullable
    @Override
    public PartnerEventId getPartnerEventId() {
        return partnerEventId.orElse(null);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static StepConsumerEvent newInstance(com.extole.event.consumer.step.StepConsumerEvent event, Person person) {
        return new StepConsumerEventImpl(event, person);
    }

    private static Optional<ReferralContext>
        createReferralContext(com.extole.event.consumer.step.StepConsumerEvent event) {
        return event.getReferralContext().map(context -> new ReferralContextImpl(context));
    }

    private static final class StepConsumerEventRuntimeException extends RuntimeException {

        private StepConsumerEventRuntimeException(String message) {
            super(message);
        }

    }

}
