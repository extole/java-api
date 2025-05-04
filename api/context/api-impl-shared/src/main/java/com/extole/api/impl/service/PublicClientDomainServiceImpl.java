package com.extole.api.impl.service;

import java.util.stream.Collectors;

import com.extole.api.PublicClientDomain;
import com.extole.api.impl.PublicClientDomainImpl;
import com.extole.api.service.PublicClientDomainService;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.shared.program.ProgramDomainCache;

public class PublicClientDomainServiceImpl implements PublicClientDomainService {
    private final PublicClientDomain[] clientDomains;

    public PublicClientDomainServiceImpl(Id<ClientHandle> clientId, ProgramDomainCache programDomainCache) {
        this.clientDomains = programDomainCache.getByClientId(clientId).stream()
            .map(programDomain -> new PublicClientDomainImpl(programDomain.getProgramDomain().toString(),
                programDomain.getId().getValue()))
            .collect(Collectors.toList()).toArray(new PublicClientDomain[0]);
    }

    @Override
    public PublicClientDomain[] getPublicClientDomains() {
        return clientDomains;
    }
}
