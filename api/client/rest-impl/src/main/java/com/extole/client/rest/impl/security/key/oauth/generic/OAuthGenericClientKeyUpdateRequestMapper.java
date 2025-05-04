package com.extole.client.rest.impl.security.key.oauth.generic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.security.key.oauth.OAuthClientKeyUpdateRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.generic.OAuthGenericClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.OAuthGenericClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.builder.OAuthGenericClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyBuildException;

@Component
public class OAuthGenericClientKeyUpdateRequestMapper
    extends OAuthClientKeyUpdateRequestMapper<OAuthGenericClientKeyUpdateRequest, OAuthGenericClientKey> {

    @Autowired
    public OAuthGenericClientKeyUpdateRequestMapper(ClientKeyService clientKeyService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    protected OAuthGenericClientKey completeUpdate(ClientKeyBuilder<?, OAuthGenericClientKey> clientKeyBuilder,
        OAuthGenericClientKeyUpdateRequest updateRequest, ComponentReferenceContext componentReferenceContext)
        throws ClientKeyRestException, OAuthSalesforceClientKeyValidationRestException, BuildClientKeyRestException,
        ClientKeyBuildException, BuildClientKeyException, MissingClientKeyAlgorithmException,
        DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException, InvalidClientKeyException,
        ShortClientKeyException, InvalidComponentReferenceException, MoreThanOneComponentReferenceException,
        ExternalElementRestException, OAuthClientKeyValidationRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        OAuthGenericClientKeyBuilder oAuthGenericClientKeyBuilder = (OAuthGenericClientKeyBuilder) clientKeyBuilder;

        updateRequest.getRequest().ifPresent(request -> oAuthGenericClientKeyBuilder.withRequest(request));
        updateRequest.getResponseHandler()
            .ifPresent(handler -> oAuthGenericClientKeyBuilder.withResponseHandler(handler));

        return super.completeUpdate(oAuthGenericClientKeyBuilder, updateRequest, componentReferenceContext);

    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.OAUTH_GENERIC;
    }

}
