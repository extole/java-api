package com.extole.api.impl.service;

import java.util.List;
import java.util.Locale;

import com.google.common.base.Strings;

import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.entity.blocks.ListType;
import com.extole.model.service.blocks.BlockService;
import com.extole.model.service.blocks.EvaluationResultType;
import com.extole.model.shared.blocklist.BlockEvaluationCache;

public class BlockServiceImpl implements com.extole.api.service.BlockService {

    private final Id<ClientHandle> clientId;
    private final BlockEvaluationCache blockEvaluationCacache;
    private final BlockService blockService;

    public BlockServiceImpl(Id<ClientHandle> clientId,
        BlockEvaluationCache blockEvaluationCacache,
        BlockService blockService) {
        this.clientId = clientId;
        this.blockEvaluationCacache = blockEvaluationCacache;
        this.blockService = blockService;
    }

    @Override
    public boolean isEmailBlocked(String email) {
        return blockEvaluationCacache.evaluateValue(clientId, email, ListType.NORMALIZED_EMAIL)
            .equals(EvaluationResultType.BLACKLISTED);
    }

    @Override
    public boolean isEmailDomainBlocked(String email) {
        boolean blacklisted = false;
        if (!Strings.isNullOrEmpty(email)) {
            String[] parts = email.split("@");
            if (parts.length > 1) {
                String domain = parts[parts.length - 1].toLowerCase(Locale.ENGLISH);
                blacklisted = blockEvaluationCacache.evaluateValue(clientId, domain, ListType.EMAIL_DOMAIN)
                    .equals(EvaluationResultType.BLACKLISTED);
            }
        }
        return blacklisted;
    }

    @Override
    public boolean isIpGloballyBlocked(String ip) {
        return blockService.isIpGloballyBlocked(ip);
    }

    @Override
    public boolean isIpBlocked(String ip, String[] subnets) {
        return blockService.isIpBlocked(ip, List.of(subnets));
    }
}
