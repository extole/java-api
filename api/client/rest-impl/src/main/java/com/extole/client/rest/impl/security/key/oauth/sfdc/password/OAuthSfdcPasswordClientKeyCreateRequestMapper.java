package com.extole.client.rest.impl.security.key.oauth.sfdc.password;

import java.nio.charset.StandardCharsets;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.security.key.oauth.OAuthClientKeyCreateRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.OAuthSfdcPasswordClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.builder.OAuthSfdcPasswordClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyInUseException;
import com.extole.model.service.client.security.key.exception.MissingTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.UnsupportedTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingAppKeyOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingClientIdOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingPasswordOAuthSfdcPasswordClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingUsernameOAuthSfdcPasswordClientKeyException;

@Component
public class OAuthSfdcPasswordClientKeyCreateRequestMapper extends
    OAuthClientKeyCreateRequestMapper<OAuthSfdcPasswordClientKeyCreateRequest, OAuthSfdcPasswordClientKey> {

    @Autowired
    public OAuthSfdcPasswordClientKeyCreateRequestMapper(ClientKeyService clientKeyService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    protected OAuthSfdcPasswordClientKey completeCreation(
        ClientKeyBuilder<?, OAuthSfdcPasswordClientKey> clientKeyBuilder,
        OAuthSfdcPasswordClientKeyCreateRequest createRequest,
        ComponentReferenceContext componentReferenceContext)
        throws OAuthClientKeyValidationRestException, OAuthSalesforceClientKeyValidationRestException,
        BuildClientKeyRestException, BuildClientKeyException, MissingClientKeyAlgorithmException,
        DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException, InvalidClientKeyException,
        ShortClientKeyException, OAuthLeadPerfectionClientKeyValidationRestException, ClientKeyInUseException,
        UnsupportedTypeClientKeyException, MissingTypeClientKeyException,
        MissingClientIdOAuthLeadPerfectionClientKeyException, MissingAppKeyOAuthLeadPerfectionClientKeyException,
        InvalidComponentReferenceException, OAuthSfdcPasswordClientKeyValidationRestException,
        MoreThanOneComponentReferenceException, ExternalElementRestException, OAuthClientKeyBuildRestException {
        OAuthSfdcPasswordClientKeyBuilder oAuthSfdcPasswordClientKeyBuilder =
            (OAuthSfdcPasswordClientKeyBuilder) clientKeyBuilder;

        if (Strings.isNullOrEmpty(createRequest.getUsername())) {
            throw RestExceptionBuilder.newBuilder(OAuthSfdcPasswordClientKeyValidationRestException.class)
                .withErrorCode(OAuthSfdcPasswordClientKeyValidationRestException.MISSING_USERNAME)
                .build();
        } else {
            oAuthSfdcPasswordClientKeyBuilder.withUsername(createRequest.getUsername());
        }

        if (Strings.isNullOrEmpty(createRequest.getPassword())) {
            throw RestExceptionBuilder.newBuilder(OAuthSfdcPasswordClientKeyValidationRestException.class)
                .withErrorCode(OAuthSfdcPasswordClientKeyValidationRestException.MISSING_PASSWORD)
                .build();
        } else {
            oAuthSfdcPasswordClientKeyBuilder.withPassword(
                createRequest.getPassword().getBytes(StandardCharsets.ISO_8859_1));
        }

        try {
            return super.completeCreation(oAuthSfdcPasswordClientKeyBuilder, createRequest, componentReferenceContext);
        } catch (MissingUsernameOAuthSfdcPasswordClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(OAuthSfdcPasswordClientKeyValidationRestException.class)
                .withErrorCode(OAuthSfdcPasswordClientKeyValidationRestException.MISSING_USERNAME)
                .build();
        } catch (MissingPasswordOAuthSfdcPasswordClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(OAuthSfdcPasswordClientKeyValidationRestException.class)
                .withErrorCode(OAuthSfdcPasswordClientKeyValidationRestException.MISSING_PASSWORD)
                .build();
        }
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.OAUTH_SFDC_PASSWORD;
    }

}
