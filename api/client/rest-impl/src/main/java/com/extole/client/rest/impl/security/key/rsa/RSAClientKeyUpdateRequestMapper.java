package com.extole.client.rest.impl.security.key.rsa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.security.key.BaseClientKeyUpdateRequestMapper;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.GenericClientKeyUpdateRequest;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;

@Component
public class RSAClientKeyUpdateRequestMapper
    extends BaseClientKeyUpdateRequestMapper<GenericClientKeyUpdateRequest, ClientKey> {

    @Autowired
    public RSAClientKeyUpdateRequestMapper(ClientKeyService clientKeyService, ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.RSA;
    }

}
