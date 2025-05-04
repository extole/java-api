package com.extole.client.rest.impl.security.key.oauth.lead.perfection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.security.key.oauth.OAuthClientKeyUpdateRequestMapper;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyUpdateRequest;
import com.extole.model.entity.client.security.key.OAuthLeadPerfectionClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;

@Component
public class OAuthLeadPerfectionClientKeyUpdateRequestMapper
    extends OAuthClientKeyUpdateRequestMapper<OAuthLeadPerfectionClientKeyUpdateRequest, OAuthLeadPerfectionClientKey> {

    @Autowired
    public OAuthLeadPerfectionClientKeyUpdateRequestMapper(ClientKeyService clientKeyService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.OAUTH_LEAD_PERFECTION;
    }

}
