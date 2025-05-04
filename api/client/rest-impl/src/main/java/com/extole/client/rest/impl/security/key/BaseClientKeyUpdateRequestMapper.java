package com.extole.client.rest.impl.security.key;

import java.util.List;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.impl.campaign.BuildClientKeyExceptionMapper;
import com.extole.client.rest.impl.campaign.OAuthClientKeyBuildRestExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyUpdateRequest;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
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
import com.extole.model.service.client.security.key.ClientKeyNotFoundException;
import com.extole.model.service.client.security.key.ClientKeyService;
import com.extole.model.service.client.security.key.DuplicateClientKeyPartnerKeyIdException;
import com.extole.model.service.client.security.key.InvalidClientKeyException;
import com.extole.model.service.client.security.key.MissingClientKeyAlgorithmException;
import com.extole.model.service.client.security.key.MissingClientKeyException;
import com.extole.model.service.client.security.key.ShortClientKeyException;
import com.extole.model.service.client.security.key.builder.ClientKeyBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.built.MissingClientKeyNameException;
import com.extole.model.service.client.security.key.exception.ClientKeyBuildException;
import com.extole.model.service.client.security.key.exception.ExternalElementInUseException;

public abstract class BaseClientKeyUpdateRequestMapper<REQUEST extends ClientKeyUpdateRequest, KEY extends ClientKey>
    implements ClientKeyUpdateRequestMapper<REQUEST, KEY> {

    private final ClientKeyService clientKeyService;
    private final ComponentService componentService;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    public BaseClientKeyUpdateRequestMapper(ClientKeyService clientKeyService,
        ComponentService componentService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.clientKeyService = clientKeyService;
        this.componentService = componentService;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public KEY update(Authorization authorization, Id<ClientKey> clientKeyId, REQUEST updateRequest)
        throws BuildClientKeyRestException, UserAuthorizationRestException, OAuthClientKeyValidationRestException,
        OAuthSalesforceClientKeyValidationRestException, ClientKeyRestException,
        CampaignComponentValidationRestException, ExternalElementRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        ClientKeyBuilder<?, KEY> clientKeyBuilder;
        try {
            ClientKey clientKey = clientKeyService.getClientKey(authorization, clientKeyId);
            if (!clientKey.getAlgorithm().equals(ClientKey.Algorithm.valueOf(updateRequest.getAlgorithm().name()))) {
                throw RestExceptionBuilder.newBuilder(ClientKeyRestException.class)
                    .withErrorCode(ClientKeyRestException.CLIENT_KEY_NOT_FOUND)
                    .addParameter("key_id", clientKeyId)
                    .build();
            }
            clientKeyBuilder = clientKeyService.updateClientKey(authorization, clientKeyId);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientKeyRestException.class)
                .withErrorCode(ClientKeyRestException.CLIENT_KEY_NOT_FOUND)
                .addParameter("key_id", clientKeyId)
                .withCause(e)
                .build();
        }

        try {
            updateRequest.getName().ifPresent(name -> {
                clientKeyBuilder.withName(name);
            });
            updateRequest.getDescription().ifPresent(description -> {
                clientKeyBuilder.withDescription(description);
            });
            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(clientKeyBuilder, componentIds);
            });
            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(clientKeyBuilder, componentReferences);
            });
            updateRequest.getPartnerKeyId().ifPresent(partnerKeyId -> {
                clientKeyBuilder.withPartnerKeyId(partnerKeyId);
            });

            return completeUpdate(clientKeyBuilder, updateRequest,
                componentService.buildDefaultComponentReferenceContext(authorization));
        } catch (ClientKeyBuildException | MissingClientKeyException | MissingClientKeyAlgorithmException
            | ShortClientKeyException | InvalidClientKeyException | DuplicateClientKeyPartnerKeyIdException
            | MissingClientKeyNameException e) {
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

    protected KEY completeUpdate(ClientKeyBuilder<?, KEY> clientKeyBuilder, REQUEST createRequest,
        ComponentReferenceContext componentReferenceContext)
        throws BuildClientKeyRestException, ClientKeyRestException, OAuthSalesforceClientKeyValidationRestException,
        ClientKeyBuildException, BuildClientKeyException, MissingClientKeyAlgorithmException,
        DuplicateClientKeyPartnerKeyIdException, MissingClientKeyException, InvalidClientKeyException,
        ShortClientKeyException, InvalidComponentReferenceException, MoreThanOneComponentReferenceException,
        ExternalElementInUseException, ExternalElementRestException, ClientKeyValidationRestException,
        OAuthClientKeyValidationRestException, OAuthClientKeyBuildRestException {
        return clientKeyBuilder.save(() -> componentReferenceContext);
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
