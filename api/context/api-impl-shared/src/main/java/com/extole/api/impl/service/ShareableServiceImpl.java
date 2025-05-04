package com.extole.api.impl.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.extole.api.impl.person.ShareableImpl;
import com.extole.api.person.Shareable;
import com.extole.api.service.ShareableService;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;

public class ShareableServiceImpl implements ShareableService {

    private final Id<ClientHandle> clientId;
    private final com.extole.person.service.shareable.ShareableService shareableService;

    public ShareableServiceImpl(Id<ClientHandle> clientId,
        com.extole.person.service.shareable.ShareableService shareableService) {
        this.clientId = clientId;
        this.shareableService = shareableService;
    }

    @Override
    public Shareable getByCode(String code) {
        return shareableService.findByCode(clientId, Optional.ofNullable(code).orElse(StringUtils.EMPTY))
            .map(ShareableImpl::new).orElse(null);
    }
}
