package com.extole.client.rest.impl.security.key.oauth.lead.perfection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.security.key.oauth.OAuthClientKeyCreateRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.OAuthLeadPerfectionClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.builder.OAuthLeadPerfectionClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyInUseException;
import com.extole.model.service.client.security.key.exception.MissingTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.UnsupportedTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingAppKeyOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingClientIdOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingPasswordOAuthSfdcPasswordClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingUsernameOAuthSfdcPasswordClientKeyException;

@Component
public class OAuthLeadPerfectionClientKeyCreateRequestMapper
    extends OAuthClientKeyCreateRequestMapper<OAuthLeadPerfectionClientKeyCreateRequest, OAuthLeadPerfectionClientKey> {

    @Autowired
    public OAuthLeadPerfectionClientKeyCreateRequestMapper(ClientKeyService clientKeyService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    protected OAuthLeadPerfectionClientKey completeCreation(
        ClientKeyBuilder<?, OAuthLeadPerfectionClientKey> clientKeyBuilder,
        OAuthLeadPerfectionClientKeyCreateRequest createRequest, ComponentReferenceContext componentReferenceContext)
        throws OAuthClientKeyValidationRestException, OAuthSalesforceClientKeyValidationRestException,
        BuildClientKeyRestException, BuildClientKeyException, MissingClientKeyAlgorithmException,
        DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException, InvalidClientKeyException,
        ShortClientKeyException, OAuthLeadPerfectionClientKeyValidationRestException, ClientKeyInUseException,
        UnsupportedTypeClientKeyException, MissingTypeClientKeyException,
        MissingClientIdOAuthLeadPerfectionClientKeyException, MissingAppKeyOAuthLeadPerfectionClientKeyException,
        InvalidComponentReferenceException, OAuthSfdcPasswordClientKeyValidationRestException,
        MissingPasswordOAuthSfdcPasswordClientKeyException, MissingUsernameOAuthSfdcPasswordClientKeyException,
        MoreThanOneComponentReferenceException, ExternalElementRestException, OAuthClientKeyBuildRestException {
        OAuthLeadPerfectionClientKeyBuilder oAuthLeadPerfectionClientKeyBuilder =
            (OAuthLeadPerfectionClientKeyBuilder) clientKeyBuilder;

        if (!createRequest.getLeadPerfectionClientId().isOmitted()
            && StringUtils.isNotBlank(createRequest.getLeadPerfectionClientId().getValue())) {
            oAuthLeadPerfectionClientKeyBuilder
                .withLeadPerfectionClientId(createRequest.getLeadPerfectionClientId().getValue());
        } else {
            throw RestExceptionBuilder.newBuilder(OAuthLeadPerfectionClientKeyValidationRestException.class)
                .withErrorCode(OAuthLeadPerfectionClientKeyValidationRestException.MISSING_LEAD_PERFECTION_CLIENT_ID)
                .build();
        }

        if (!createRequest.getAppKey().isOmitted() && StringUtils.isNotBlank(createRequest.getAppKey().getValue())) {
            oAuthLeadPerfectionClientKeyBuilder.withAppKey(createRequest.getAppKey().getValue());
        } else {
            throw RestExceptionBuilder.newBuilder(OAuthLeadPerfectionClientKeyValidationRestException.class)
                .withErrorCode(OAuthLeadPerfectionClientKeyValidationRestException.MISSING_APP_KEY)
                .build();
        }
        return super.completeCreation(oAuthLeadPerfectionClientKeyBuilder, createRequest, componentReferenceContext);
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.OAUTH_LEAD_PERFECTION;
    }

}
