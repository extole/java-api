package com.extole.client.rest.impl.security.key.oauth;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.BuildClientKeyExceptionMapper;
import com.extole.client.rest.impl.campaign.OAuthClientKeyBuildRestExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.security.key.BaseClientKeyUpdateRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.OAuthClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.builder.OAuthClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyBuildException;
import com.extole.model.service.client.security.key.exception.ExternalElementInUseException;
import com.extole.model.service.client.security.key.exception.TooLongKeyClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.MissingOAuthClientIdOAuthClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.TooLongOAuthClientIdOAuthClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.TooLongScopeOAuthClientKeyException;

@Component
public class OAuthClientKeyUpdateRequestMapper<REQUEST extends OAuthClientKeyUpdateRequest, KEY extends OAuthClientKey>
    extends BaseClientKeyUpdateRequestMapper<REQUEST, KEY> {

    @Autowired
    public OAuthClientKeyUpdateRequestMapper(ClientKeyService clientKeyService, ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    protected KEY completeUpdate(ClientKeyBuilder<?, KEY> clientKeyBuilder, REQUEST updateRequest,
        ComponentReferenceContext componentReferenceContext)
        throws ClientKeyRestException, OAuthSalesforceClientKeyValidationRestException, BuildClientKeyRestException,
        ClientKeyBuildException, BuildClientKeyException, MissingClientKeyAlgorithmException,
        DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException, InvalidClientKeyException,
        ShortClientKeyException, InvalidComponentReferenceException, MoreThanOneComponentReferenceException,
        ExternalElementRestException, OAuthClientKeyValidationRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        OAuthClientKeyBuilder oAuthClientKeyBuilder = (OAuthClientKeyBuilder) clientKeyBuilder;

        updateRequest.getOAuthClientId().ifPresent(oAuthClientId -> {
            try {
                oAuthClientKeyBuilder.withOAuthClientId(oAuthClientId);
            } catch (TooLongOAuthClientIdOAuthClientKeyException e) {
                throw RestExceptionBuilder.newBuilder(OAuthClientKeyValidationRestException.class)
                    .withErrorCode(OAuthClientKeyValidationRestException.OAUTH_CLIENT_ID_TOO_LONG)
                    .addParameter("oauth_client_id", e.getOAuthClientId())
                    .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            }
        });

        updateRequest.getKey().ifPresent(key -> {
            try {
                oAuthClientKeyBuilder.withKey(key.getBytes(StandardCharsets.ISO_8859_1));
            } catch (ShortClientKeyException e) {
                throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                    .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_TOO_SHORT)
                    .addParameter("algorithm", e.getAlgorithm())
                    .addParameter("min_required_length", Integer.valueOf(e.getMinRequiredLength()))
                    .withCause(e)
                    .build();
            } catch (TooLongKeyClientKeyException e) {
                throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                    .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_TOO_LONG)
                    .addParameter("max_allowed_length", Integer.valueOf(e.getMaxLength()))
                    .withCause(e)
                    .build();
            } catch (InvalidClientKeyException e) {
                throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                    .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_INVALID)
                    .withCause(e)
                    .build();
            }
        });

        updateRequest.getAuthorizationUrl().ifPresent(authorizationUrl -> {
            oAuthClientKeyBuilder.withAuthorizationUrl(authorizationUrl);

        });

        updateRequest.getScope().ifPresent(scope -> {
            if (scope.isPresent()) {
                try {
                    oAuthClientKeyBuilder.withScope(scope.get());
                } catch (TooLongScopeOAuthClientKeyException e) {
                    throw RestExceptionBuilder.newBuilder(OAuthClientKeyValidationRestException.class)
                        .withErrorCode(OAuthClientKeyValidationRestException.SCOPE_TOO_LONG)
                        .addParameter("scope", e.getScope())
                        .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                        .withCause(e)
                        .build();
                }
            } else {
                oAuthClientKeyBuilder.clearScope();
            }
        });

        try {
            return (KEY) super.completeUpdate(oAuthClientKeyBuilder, updateRequest, componentReferenceContext);
        } catch (BuildClientKeyException e) {
            throw OAuthClientKeyBuildRestExceptionMapper.getInstance().map(e)
                .orElseThrow(() -> BuildClientKeyExceptionMapper.getInstance().map(e));
        } catch (MissingOAuthClientIdOAuthClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(OAuthClientKeyValidationRestException.class)
                .withErrorCode(OAuthClientKeyValidationRestException.MISSING_OAUTH_CLIENT_ID)
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
        }
    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.OAUTH;
    }

}
