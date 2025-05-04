package com.extole.client.rest.impl.person.provider.legacy;

import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.consumer.event.service.processor.EventProcessorCandidateProvider;
import com.extole.consumer.event.service.processor.EventProcessorPrehandler;

public interface LegacyClientEventPersonProviderFactory {

    EventProcessorCandidateProvider newPersonProvider(ClientAuthorization authorization,
        boolean dynamicPersonKeysEnabled);

    EventProcessorPrehandler newPersonPrehandler(ClientAuthorization authorization, boolean dynamicPersonKeysEnabled);

}
