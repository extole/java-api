package com.extole.client.rest.impl.security.key;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.security.key.built.BuiltClientKeyResponseMapperRepository;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyArchiveRestException;
import com.extole.client.rest.security.key.ClientKeyEndpoints;
import com.extole.client.rest.security.key.ClientKeyFilterRequest;
import com.extole.client.rest.security.key.ClientKeyResponse;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.ClientKeyUpdateRequest;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.FileBasedClientKeyCreateRequest;
import com.extole.client.rest.security.key.FileClientKeyRequest;
import com.extole.client.rest.security.key.GenericClientKeyResponse;
import com.extole.client.rest.security.key.StringBasedClientKeyCreateRequest;
import com.extole.client.rest.security.key.built.BuiltClientKeyResponse;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.security.key.DecryptedClientKey;
import com.extole.model.entity.client.security.key.built.BuiltClientKey;
import com.extole.model.service.client.security.key.ClientKeyNotFoundException;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.built.BuiltClientKeyService;
import com.extole.model.service.client.security.key.exception.ClientKeyInUseException;
import com.extole.model.service.client.security.key.exception.ExternalElementInUseException;
import com.extole.model.service.client.security.key.pgpextole.PgpExtoleClientKey;

@Provider
public class ClientKeyEndpointsImpl implements ClientKeyEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientKeyService clientKeyService;
    private final BuiltClientKeyService builtclientKeyService;
    private final ClientKeyCreateRequestMapperRepository clientKeyCreateRequestMapperRepository;
    private final ClientKeyUpdateRequestMapperRepository clientKeyUpdateRequestMapperRepository;
    private final ClientKeyResponseMapperRepository clientKeyResponseMapperRepository;
    private final BuiltClientKeyResponseMapperRepository builtClientKeyResponseMapperRepository;

    @Autowired
    public ClientKeyEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientKeyService clientKeyService,
        BuiltClientKeyService builtclientKeyService,
        ClientKeyCreateRequestMapperRepository clientKeyCreateRequestMapperRepository,
        ClientKeyUpdateRequestMapperRepository clientKeyUpdateRequestMapperRepository,
        ClientKeyResponseMapperRepository clientKeyResponseMapperRepository,
        BuiltClientKeyResponseMapperRepository builtClientKeyResponseMapperRepository) {
        this.authorizationProvider = authorizationProvider;
        this.clientKeyService = clientKeyService;
        this.builtclientKeyService = builtclientKeyService;
        this.clientKeyCreateRequestMapperRepository = clientKeyCreateRequestMapperRepository;
        this.clientKeyUpdateRequestMapperRepository = clientKeyUpdateRequestMapperRepository;
        this.clientKeyResponseMapperRepository = clientKeyResponseMapperRepository;
        this.builtClientKeyResponseMapperRepository = builtClientKeyResponseMapperRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ClientKeyResponse> List<T> listClientKeys(String accessToken,
        ClientKeyFilterRequest clientKeyFilterRequest, ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return (List<T>) clientKeyService.list(authorization).stream()
                .filter(listTags -> clientKeyFilterRequest.getTags().isEmpty()
                    || listTags.getTags().containsAll(clientKeyFilterRequest.getTags()))
                .map(clientKey -> clientKeyResponseMapperRepository.getClientKeyResponseMapper(clientKey.getAlgorithm())
                    .toResponse(clientKey, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends BuiltClientKeyResponse> List<T> listBuiltClientKeys(String accessToken,
        ClientKeyFilterRequest clientKeyFilterRequest, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<BuiltClientKey> clientKeys = builtclientKeyService.createQueryBuilder(authorization)
                .list()
                .stream()
                .filter(listTags -> clientKeyFilterRequest.getTags().isEmpty() ||
                    listTags.getTags().containsAll(clientKeyFilterRequest.getTags()))
                .collect(Collectors.toList());

            return (List<T>) clientKeys.stream()
                .map(clientKey -> builtClientKeyResponseMapperRepository
                    .getClientKeyResponseMapper(clientKey.getAlgorithm())
                    .toResponse(clientKey, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public ClientKeyResponse create(String accessToken, StringBasedClientKeyCreateRequest createRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, BuildClientKeyRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, ClientKeyValidationRestException, OAuthClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (createRequest.getAlgorithm() == null) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_MISSING_ALGORITHM)
                .build();
        }

        ClientKeyCreateRequestMapper createRequestMapper =
            clientKeyCreateRequestMapperRepository.getClientKeyCreateRequestMapper(createRequest.getAlgorithm());

        ClientKey clientKey = createRequestMapper.create(authorization, createRequest);
        return clientKeyResponseMapperRepository.getClientKeyResponseMapper(clientKey.getAlgorithm())
            .toResponse(clientKey, timeZone);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <REQUEST extends FileBasedClientKeyCreateRequest> ClientKeyResponse create(
        String accessToken,
        FileClientKeyRequest<REQUEST> fileClientKeyRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildClientKeyRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, OAuthClientKeyValidationRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        FileBasedClientKeyCreateRequest createRequest = fileClientKeyRequest.getClientKeyCreateRequest();
        if (createRequest.getAlgorithm() == null) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_MISSING_ALGORITHM)
                .build();
        }

        ClientKeyCreateRequestMapper createRequestMapper =
            clientKeyCreateRequestMapperRepository.getClientKeyCreateRequestMapper(createRequest.getAlgorithm());

        ClientKey clientKey = createRequestMapper.create(authorization, fileClientKeyRequest);
        return clientKeyResponseMapperRepository.getClientKeyResponseMapper(clientKey.getAlgorithm())
            .toResponse(clientKey, timeZone);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public ClientKeyResponse update(String accessToken, String keyId, ClientKeyUpdateRequest updateRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientKeyRestException, BuildClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthClientKeyValidationRestException,
        CampaignComponentValidationRestException, ExternalElementRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (updateRequest.getAlgorithm() == null) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_MISSING_ALGORITHM)
                .build();
        }

        ClientKeyUpdateRequestMapper updateRequestMapper =
            clientKeyUpdateRequestMapperRepository.getClientKeyUpdateRequestMapper(updateRequest.getAlgorithm());

        ClientKey clientKey = updateRequestMapper.update(authorization, Id.valueOf(keyId), updateRequest);
        return clientKeyResponseMapperRepository.getClientKeyResponseMapper(clientKey.getAlgorithm())
            .toResponse(clientKey, timeZone);
    }

    @Override
    public ClientKeyResponse getDecrypted(String accessToken, String keyId, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            DecryptedClientKey decryptedClientKey = clientKeyService.getDecrypted(authorization, Id.valueOf(keyId));
            return mapToClientKeyResponse(decryptedClientKey, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyRestException.class)
                .withErrorCode(ClientKeyRestException.CLIENT_KEY_NOT_FOUND)
                .addParameter("key_id", keyId)
                .withCause(e)
                .build();
        }
    }

    private ClientKeyResponse mapToClientKeyResponse(DecryptedClientKey decryptedClientKey, ZoneId zone) {
        return new ClientKeyResponse(decryptedClientKey.getId().getValue(),
            decryptedClientKey.getName(),
            ClientKeyAlgorithm.valueOf(decryptedClientKey.getAlgorithm().name()),
            new String(decryptedClientKey.getDecryptedKey(), StandardCharsets.ISO_8859_1),
            ClientKeyType.valueOf(decryptedClientKey.getType().name()),
            decryptedClientKey.getDescription(),
            decryptedClientKey.getPartnerKeyId(),
            decryptedClientKey.getTags(),
            ZonedDateTime.ofInstant(decryptedClientKey.getCreatedAt(), zone),
            ZonedDateTime.ofInstant(decryptedClientKey.getUpdatedAt(), zone),
            decryptedClientKey.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            decryptedClientKey.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public ClientKeyResponse archive(String accessToken, String keyId, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException, ClientKeyArchiveRestException,
        ExternalElementRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientKey archivedClientKey = clientKeyService.archive(authorization, Id.valueOf(keyId));
            return clientKeyResponseMapperRepository.getClientKeyResponseMapper(archivedClientKey.getAlgorithm())
                .toResponse(archivedClientKey, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyRestException.class)
                .withErrorCode(ClientKeyRestException.CLIENT_KEY_NOT_FOUND)
                .addParameter("key_id", keyId)
                .withCause(e)
                .build();
        } catch (ClientKeyInUseException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyArchiveRestException.class)
                .withErrorCode(ClientKeyArchiveRestException.CLIENT_KEY_ASSOCIATED_WITH_ENTITY)
                .addParameter("key_id", e.getClientKeyId())
                .addParameter("entity_type", e.getEntityType())
                .addParameter("entity_ids", e.getAssociatedEntityIds())
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

    @SuppressWarnings({"unchecked"})
    @Override
    public ClientKeyResponse delete(String accessToken, String keyId, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException, ClientKeyArchiveRestException,
        ExternalElementRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientKey archivedClientKey = clientKeyService.delete(authorization, Id.valueOf(keyId));
            return clientKeyResponseMapperRepository.getClientKeyResponseMapper(archivedClientKey.getAlgorithm())
                .toResponse(archivedClientKey, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyRestException.class)
                .withErrorCode(ClientKeyRestException.CLIENT_KEY_NOT_FOUND)
                .addParameter("key_id", keyId)
                .withCause(e)
                .build();
        } catch (ClientKeyInUseException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyArchiveRestException.class)
                .withErrorCode(ClientKeyArchiveRestException.CLIENT_KEY_ASSOCIATED_WITH_ENTITY)
                .addParameter("key_id", e.getClientKeyId())
                .addParameter("entity_type", e.getEntityType())
                .addParameter("entity_ids", e.getAssociatedEntityIds())
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

    @SuppressWarnings({"unchecked"})
    @Override
    public ClientKeyResponse unArchive(String accessToken, String keyId, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException, ClientKeyArchiveRestException,
        ExternalElementRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientKey archivedClientKey = clientKeyService.unArchive(authorization, Id.valueOf(keyId));
            return clientKeyResponseMapperRepository.getClientKeyResponseMapper(archivedClientKey.getAlgorithm())
                .toResponse(archivedClientKey, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyRestException.class)
                .withErrorCode(ClientKeyRestException.CLIENT_KEY_NOT_FOUND)
                .addParameter("key_id", keyId)
                .withCause(e)
                .build();
        } catch (ClientKeyInUseException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyArchiveRestException.class)
                .withErrorCode(ClientKeyArchiveRestException.CLIENT_KEY_ASSOCIATED_WITH_ENTITY)
                .addParameter("key_id", e.getClientKeyId())
                .addParameter("entity_type", e.getEntityType())
                .addParameter("entity_ids", e.getAssociatedEntityIds())
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

    @SuppressWarnings({"unchecked"})
    @Override
    public GenericClientKeyResponse getPgpExtoleClientKey(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return (GenericClientKeyResponse) clientKeyService.list(authorization, ClientKey.Type.PGP_EXTOLE)
                .stream()
                .map(clientKey -> clientKeyResponseMapperRepository.getClientKeyResponseMapper(clientKey.getAlgorithm())
                    .toResponse(clientKey, timeZone))
                .findFirst()
                .orElseThrow(() -> new ClientKeyNotFoundException("Can't find PGP Extole client key"));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyRestException.class)
                .withErrorCode(ClientKeyRestException.PGP_EXTOLE_CLIENT_KEY_NOT_FOUND)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public GenericClientKeyResponse createPgpExtoleClientKey(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PgpExtoleClientKey clientKey = clientKeyService.createPgpExtoleClientKey(authorization);
            return (GenericClientKeyResponse) clientKeyResponseMapperRepository
                .getClientKeyResponseMapper(clientKey.getAlgorithm())
                .toResponse(clientKey, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (DuplicateClientKeyPartnerKeyIdException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyValidationRestException.class)
                .withErrorCode(ClientKeyValidationRestException.CLIENT_KEY_DUPLICATE_PARTNER_KEY_ID)
                .addParameter("partner_key_id", e.getPartnerKeyId())
                .withCause(e)
                .build();
        }
    }
}
