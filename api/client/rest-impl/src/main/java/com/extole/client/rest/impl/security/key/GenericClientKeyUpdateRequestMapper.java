package com.extole.client.rest.impl.security.key;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.GenericClientKeyUpdateRequest;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.builder.GenericClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyBuildException;
import com.extole.model.service.client.security.key.exception.ExternalElementInUseException;
import com.extole.model.service.client.security.key.exception.TooLongKeyClientKeyException;

@Component
public class GenericClientKeyUpdateRequestMapper
    extends BaseClientKeyUpdateRequestMapper<GenericClientKeyUpdateRequest, ClientKey> {

    public GenericClientKeyUpdateRequestMapper(ClientKeyService clientKeyService, ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        super(clientKeyService, componentService, componentReferenceRequestMapper);
    }

    @Override
    protected ClientKey completeUpdate(ClientKeyBuilder<?, ClientKey> clientKeyBuilder,
        GenericClientKeyUpdateRequest updateRequest, ComponentReferenceContext componentReferenceContext)
        throws BuildClientKeyRestException, MissingClientKeyAlgorithmException, ShortClientKeyException,
        MissingClientKeyException, MoreThanOneComponentReferenceException,
        OAuthSalesforceClientKeyValidationRestException, InvalidClientKeyException, BuildClientKeyException,
        ClientKeyRestException, InvalidComponentReferenceException, ClientKeyBuildException,
        DuplicateClientKeyPartnerKeyIdException, ExternalElementInUseException, ExternalElementRestException,
        ClientKeyValidationRestException, OAuthClientKeyValidationRestException, OAuthClientKeyBuildRestException {
        GenericClientKeyBuilder genericClientKeyBuilder = (GenericClientKeyBuilder) clientKeyBuilder;

        updateRequest.getKey().ifPresent(key -> {
            try {
                genericClientKeyBuilder.withKey(key.getBytes(StandardCharsets.ISO_8859_1));
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

        return super.completeUpdate(genericClientKeyBuilder, updateRequest, componentReferenceContext);

    }

    @Override
    public ClientKeyAlgorithm getAlgorithm() {
        return ClientKeyAlgorithm.GENERIC;
    }

}
