package com.extole.client.rest.impl.security.key;

import org.springframework.stereotype.Component;

import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.SslPkcs12ClientKeyUpdateRequest;
import com.extole.model.entity.client.security.key.SslPkcs12ClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;

@Component
public class SslPkcs12ClientKeyUpdateRequestMapper
    extends BaseClientKeyUpdateRequestMapper<SslPkcs12ClientKeyUpdateRequest, SslPkcs12ClientKey> {

    public SslPkcs12ClientKeyUpdateRequestMapper(ClientKeyService clientKeyService, ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.SSL_PKCS_12;
    }

}
