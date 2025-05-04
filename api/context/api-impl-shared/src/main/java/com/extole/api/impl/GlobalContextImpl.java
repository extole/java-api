package com.extole.api.impl;

import com.extole.api.ClientContext;
import com.extole.api.GlobalContext;
import com.extole.api.impl.service.GlobalServicesFactory;
import com.extole.api.service.GlobalServices;
import com.extole.common.lang.LazyLoadingSupplier;

public class GlobalContextImpl implements GlobalContext {

    private final LazyLoadingSupplier<ClientContext> clientContextSupplier;
    private final LazyLoadingSupplier<GlobalServices> globalServicesSupplier;

    public GlobalContextImpl(com.extole.event.consumer.ConsumerEvent causeEvent,
        GlobalServicesFactory globalServicesFactory) {
        this.clientContextSupplier =
            new LazyLoadingSupplier<>(
                () -> new ClientContextImpl(causeEvent.getClientContext().getClientId().getValue(),
                    causeEvent.getClientContext().getClientShortName(),
                    causeEvent.getClientContext().getClientTimeZone().getId()));
        this.globalServicesSupplier =
            new LazyLoadingSupplier<>(
                () -> globalServicesFactory.initializeNew(causeEvent, "global", causeEvent.getId()));
    }

    @Override
    public ClientContext getClientContext() {
        return clientContextSupplier.get();
    }

    @Override
    public GlobalServices getGlobalServices() {
        return globalServicesSupplier.get();
    }

}
