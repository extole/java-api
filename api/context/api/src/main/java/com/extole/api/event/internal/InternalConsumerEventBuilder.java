package com.extole.api.event.internal;

public interface InternalConsumerEventBuilder {

    InternalConsumerEventBuilder withName(String name);

    InternalConsumerEventBuilder addData(String name, Object value);

    InternalConsumerEventBuilder withCampaignId(String campaignId);

    InternalConsumerEventBuilder addLabel(String label);

    InternalConsumerEvent send();

}
