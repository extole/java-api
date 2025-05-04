package com.extole.client.rest.impl.security.key.oauth.listrak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.security.key.oauth.OAuthClientKeyUpdateRequestMapper;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.oauth.listrak.OAuthListrakClientKeyUpdateRequest;
import com.extole.model.entity.client.security.key.OAuthListrakClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;

@Component
public class OAuthListrakClientKeyUpdateRequestMapper
    extends OAuthClientKeyUpdateRequestMapper<OAuthListrakClientKeyUpdateRequest, OAuthListrakClientKey> {

    @Autowired
    public OAuthListrakClientKeyUpdateRequestMapper(ClientKeyService clientKeyService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.OAUTH_LISTRAK;
    }

}
