package com.extole.client.rest.impl.security.key;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.FileBasedClientKeyCreateRequest;
import com.extole.client.rest.security.key.FileClientKeyRequest;
import com.extole.client.rest.security.key.SslPkcs12ClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.SslPkcs12ClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.builder.SslPkcs12ClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyInUseException;
import com.extole.model.service.client.security.key.exception.ExternalElementInUseException;
import com.extole.model.service.client.security.key.exception.MissingTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.TooLongKeyClientKeyException;
import com.extole.model.service.client.security.key.exception.UnsupportedTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.MissingOAuthClientIdOAuthClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingAppKeyOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingClientIdOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingPasswordOAuthSfdcPasswordClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingUsernameOAuthSfdcPasswordClientKeyException;

@Component
public class SslPkcs12KeyCreateRequestMapper
    extends BaseClientKeyCreateRequestMapper<SslPkcs12ClientKeyCreateRequest, SslPkcs12ClientKey> {

    public SslPkcs12KeyCreateRequestMapper(ClientKeyService clientKeyService, ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    protected SslPkcs12ClientKey completeCreation(ClientKeyBuilder<?, SslPkcs12ClientKey> clientKeyBuilder,
        FileClientKeyRequest<? extends FileBasedClientKeyCreateRequest> fileClientKeyRequest,
        ComponentReferenceContext componentReferenceContext)
        throws BuildClientKeyRestException, ClientKeyRestException, OAuthSalesforceClientKeyValidationRestException,
        BuildClientKeyException, MissingClientKeyAlgorithmException, DuplicateClientKeyPartnerKeyIdException,
        MissingClientKeyException, InvalidClientKeyException, ShortClientKeyException,
        OAuthLeadPerfectionClientKeyValidationRestException, ClientKeyInUseException, UnsupportedTypeClientKeyException,
        MissingTypeClientKeyException, MissingOAuthClientIdOAuthClientKeyException,
        MissingClientIdOAuthLeadPerfectionClientKeyException, MissingAppKeyOAuthLeadPerfectionClientKeyException,
        InvalidComponentReferenceException, OAuthSfdcPasswordClientKeyValidationRestException,
        MissingPasswordOAuthSfdcPasswordClientKeyException, MissingUsernameOAuthSfdcPasswordClientKeyException,
        MoreThanOneComponentReferenceException, ExternalElementRestException, ExternalElementInUseException,
        ClientKeyValidationRestException, OAuthClientKeyValidationRestException, OAuthClientKeyBuildRestException {
        SslPkcs12ClientKeyBuilder sslPkcs12ClientKeyBuilder = (SslPkcs12ClientKeyBuilder) clientKeyBuilder;

        SslPkcs12ClientKeyCreateRequest createRequest =
            (SslPkcs12ClientKeyCreateRequest) fileClientKeyRequest.getClientKeyCreateRequest();
        createRequest.getPassword().ifPresent(password -> sslPkcs12ClientKeyBuilder.withPassword(password.getBytes(
            StandardCharsets.ISO_8859_1)));
        if (fileClientKeyRequest.getFileInputStreamRequest() != null) {
            try {
                byte[] keyStore = fileClientKeyRequest.getFileInputStreamRequest().getInputStream().readAllBytes();
                clientKeyBuilder.withKey(keyStore);
                clientKeyBuilder.save(() -> componentReferenceContext);
            } catch (TooLongKeyClientKeyException e) {
                throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                    .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_TOO_LONG)
                    .addParameter("max_allowed_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            } catch (ExternalElementInUseException e) {
                throw RestExceptionBuilder.newBuilder(ExternalElementRestException.class)
                    .withErrorCode(ExternalElementRestException.EXTERNAL_ELEMENT_IN_USE)
                    .addParameter("external_element_id", e.getElementId())
                    .addParameter("entity_type", e.getEntityType())
                    .addParameter("associations", e.getAssociations())
                    .withCause(e)
                    .build();
            } catch (IOException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
        }

        return super.completeCreation(sslPkcs12ClientKeyBuilder, createRequest, componentReferenceContext);
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.SSL_PKCS_12;
    }

}
