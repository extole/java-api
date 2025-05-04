package com.extole.client.rest.impl.security.key;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.BuildClientKeyExceptionMapper;
import com.extole.client.rest.impl.campaign.OAuthClientKeyBuildRestExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyCreateRequest;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.FileBasedClientKeyCreateRequest;
import com.extole.client.rest.security.key.FileClientKeyRequest;
import com.extole.client.rest.security.key.StringBasedClientKeyCreateRequest;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ComponentReferenceContext;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.exception.ClientKeyInUseException;
import com.extole.model.service.client.security.key.exception.ExternalElementInUseException;
import com.extole.model.service.client.security.key.exception.InvalidClientKeyTagException;
import com.extole.model.service.client.security.key.exception.InvalidPartnerKeyIdClientKeyException;
import com.extole.model.service.client.security.key.exception.MissingTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.TooLongKeyClientKeyException;
import com.extole.model.service.client.security.key.exception.UnsupportedTypeClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.MissingOAuthClientIdOAuthClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingAppKeyOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.lead.perfection.MissingClientIdOAuthLeadPerfectionClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingPasswordOAuthSfdcPasswordClientKeyException;
import com.extole.model.service.client.security.key.exception.oauth.salesforce.password.MissingUsernameOAuthSfdcPasswordClientKeyException;

public abstract class BaseClientKeyCreateRequestMapper<REQUEST extends ClientKeyCreateRequest, KEY extends ClientKey>
    implements ClientKeyCreateRequestMapper<REQUEST, KEY> {

    private final ClientKeyService clientKeyService;
    private final ComponentService componentService;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    public BaseClientKeyCreateRequestMapper(ClientKeyService clientKeyService, ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.clientKeyService = clientKeyService;
        this.componentService = componentService;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public KEY create(Authorization authorization, REQUEST createRequest)
        throws BuildClientKeyRestException, UserAuthorizationRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, ClientKeyValidationRestException, OAuthClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        return buildKey(authorization, createRequest,
            (builder) -> {
                StringBasedClientKeyCreateRequest stringBasedClientKeyCreateRequest =
                    (StringBasedClientKeyCreateRequest) createRequest;
                if (StringUtils.isNotBlank(stringBasedClientKeyCreateRequest.getKey())) {
                    builder.withKey(stringBasedClientKeyCreateRequest.getKey().getBytes(StandardCharsets.ISO_8859_1));
                }
                return this.completeCreation(builder, createRequest,
                    componentService.buildDefaultComponentReferenceContext(authorization));
            });

    }

    @Override
    public KEY create(Authorization authorization,
        FileClientKeyRequest<? extends FileBasedClientKeyCreateRequest> createRequest)
        throws BuildClientKeyRestException, UserAuthorizationRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, OAuthClientKeyValidationRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        return buildKey(authorization, createRequest.getClientKeyCreateRequest(),
            (builder -> this.completeCreation(builder, createRequest,
                componentService.buildDefaultComponentReferenceContext(authorization))));
    }

    private KEY buildKey(Authorization authorization, ClientKeyCreateRequest createRequest,
        CreationCompleter<ClientKeyBuilder<?, KEY>, KEY> requestCompleter)
        throws UserAuthorizationRestException, BuildClientKeyRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, ClientKeyValidationRestException, OAuthClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        ClientKeyBuilder<?, KEY> clientKeyBuilder;
        try {
            clientKeyBuilder = clientKeyService.createClientKey(authorization,
                ClientKey.Algorithm.valueOf(createRequest.getAlgorithm().name()));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }

        try {
            if (createRequest.getType() != null) {
                clientKeyBuilder.withType(ClientKey.Type.valueOf(createRequest.getType().name()));

                if (createRequest.getType() == ClientKeyType.PGP_EXTOLE) {
                    validateIsSuperUser(authorization);
                }
            }
            clientKeyBuilder.withName(createRequest.getName());
            clientKeyBuilder.withDescription(createRequest.getDescription());
            if (createRequest.getPartnerKeyId().isPresent()
                && StringUtils.isNotBlank(createRequest.getPartnerKeyId().get())) {
                clientKeyBuilder.withPartnerKeyId(createRequest.getPartnerKeyId().get());
            }

            createRequest.getTags().ifPresent(tags -> clientKeyBuilder.withTags(tags));

            createRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(clientKeyBuilder, componentIds);
            });

            createRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(clientKeyBuilder, componentReferences);
            });

            return requestCompleter.apply(clientKeyBuilder);
        } catch (MissingClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_MISSING)
                .withCause(e)
                .build();
        } catch (MissingClientKeyAlgorithmException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_MISSING_ALGORITHM)
                .withCause(e)
                .build();
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
        } catch (DuplicateClientKeyPartnerKeyIdException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_DUPLICATE_PARTNER_KEY_ID)
                .addParameter("partner_key_id", createRequest.getPartnerKeyId())
                .withCause(e)
                .build();
        } catch (InvalidPartnerKeyIdClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_INVALID_PARTNER_KEY_ID)
                .addParameter("partner_key_id", e.getPartnerKeyId())
                .withCause(e)
                .build();
        } catch (MissingTypeClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_MISSING_TYPE)
                .withCause(e)
                .build();
        } catch (UnsupportedTypeClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_UNSUPPORTED_TYPE)
                .addParameter("type", e.getType().name())
                .addParameter("algorithm", e.getAlgorithm().name())
                .withCause(e)
                .build();
        } catch (InvalidClientKeyTagException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_INVALID_TAG)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (MissingClientIdOAuthLeadPerfectionClientKeyException
            | ClientKeyInUseException
            | MissingOAuthClientIdOAuthClientKeyException | MissingAppKeyOAuthLeadPerfectionClientKeyException
            | MissingUsernameOAuthSfdcPasswordClientKeyException
            | MissingPasswordOAuthSfdcPasswordClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildClientKeyException e) {
            throw OAuthClientKeyBuildRestExceptionMapper.getInstance().map(e)
                .orElseThrow(() -> BuildClientKeyExceptionMapper.getInstance().map(e));
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
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

    protected KEY completeCreation(ClientKeyBuilder<?, KEY> clientKeyBuilder, REQUEST createRequest,
        ComponentReferenceContext componentReferenceContext)
        throws BuildClientKeyRestException, OAuthClientKeyValidationRestException,
        OAuthSalesforceClientKeyValidationRestException, BuildClientKeyException, MissingClientKeyAlgorithmException,
        DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException, InvalidClientKeyException,
        ShortClientKeyException, OAuthLeadPerfectionClientKeyValidationRestException, ClientKeyInUseException,
        UnsupportedTypeClientKeyException, MissingTypeClientKeyException, MissingOAuthClientIdOAuthClientKeyException,
        MissingClientIdOAuthLeadPerfectionClientKeyException, MissingAppKeyOAuthLeadPerfectionClientKeyException,
        InvalidComponentReferenceException, OAuthSfdcPasswordClientKeyValidationRestException,
        MissingPasswordOAuthSfdcPasswordClientKeyException, MissingUsernameOAuthSfdcPasswordClientKeyException,
        MoreThanOneComponentReferenceException, ExternalElementInUseException, ExternalElementRestException,
        OAuthClientKeyBuildRestException {
        return clientKeyBuilder.save(() -> componentReferenceContext);
    }

    protected KEY completeCreation(ClientKeyBuilder<?, KEY> clientKeyBuilder,
        FileClientKeyRequest<? extends FileBasedClientKeyCreateRequest> createRequest,
        ComponentReferenceContext componentReferenceContext)
        throws BuildClientKeyRestException, ClientKeyRestException, OAuthSalesforceClientKeyValidationRestException,
        BuildClientKeyException, MissingClientKeyAlgorithmException, DuplicateClientKeyPartnerKeyIdException,
        MissingClientKeyException, InvalidClientKeyException, ShortClientKeyException,
        OAuthLeadPerfectionClientKeyValidationRestException, ClientKeyInUseException, UnsupportedTypeClientKeyException,
        MissingTypeClientKeyException, MissingOAuthClientIdOAuthClientKeyException,
        MissingClientIdOAuthLeadPerfectionClientKeyException, MissingAppKeyOAuthLeadPerfectionClientKeyException,
        InvalidComponentReferenceException, OAuthSfdcPasswordClientKeyValidationRestException,
        MissingPasswordOAuthSfdcPasswordClientKeyException, MissingUsernameOAuthSfdcPasswordClientKeyException,
        MoreThanOneComponentReferenceException, ExternalElementInUseException, ExternalElementRestException,
        ClientKeyValidationRestException, OAuthClientKeyValidationRestException, OAuthClientKeyBuildRestException {
        return clientKeyBuilder.save(() -> componentReferenceContext);
    }

    private void validateIsSuperUser(Authorization authorization) throws AuthorizationException {
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            throw new AuthorizationException("Access denied");
        }
    }

    interface CreationCompleter<BUILDER extends ClientKeyBuilder, KEY> {

        KEY apply(BUILDER builder) throws BuildClientKeyRestException, OAuthClientKeyValidationRestException,
            OAuthSalesforceClientKeyValidationRestException, BuildClientKeyException,
            MissingClientKeyAlgorithmException, DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException,
            InvalidClientKeyException, ShortClientKeyException, OAuthLeadPerfectionClientKeyValidationRestException,
            ClientKeyInUseException, UnsupportedTypeClientKeyException, MissingTypeClientKeyException,
            MissingOAuthClientIdOAuthClientKeyException, MissingClientIdOAuthLeadPerfectionClientKeyException,
            MissingAppKeyOAuthLeadPerfectionClientKeyException, TooLongKeyClientKeyException,
            InvalidComponentReferenceException, OAuthSfdcPasswordClientKeyValidationRestException,
            MissingPasswordOAuthSfdcPasswordClientKeyException, MissingUsernameOAuthSfdcPasswordClientKeyException,
            MoreThanOneComponentReferenceException, ExternalElementInUseException, ExternalElementRestException,
            ClientKeyRestException, ClientKeyValidationRestException, OAuthClientKeyBuildRestException;
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }
}
