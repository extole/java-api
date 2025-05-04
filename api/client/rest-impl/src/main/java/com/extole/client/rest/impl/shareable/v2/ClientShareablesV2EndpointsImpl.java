package com.extole.client.rest.impl.shareable.v2;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.shareable.v2.ClientShareableLookupV2RestException;
import com.extole.client.rest.shareable.v2.ClientShareableV2Endpoints;
import com.extole.client.rest.shareable.v2.ClientShareableV2Response;
import com.extole.client.rest.shareable.v2.ClientShareableV2RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.shareable.ClientShareable;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.person.service.shareable.ShareableNotFoundException;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
@Provider
public class ClientShareablesV2EndpointsImpl implements ClientShareableV2Endpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientShareableService clientShareableService;

    @Autowired
    public ClientShareablesV2EndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientShareableService clientShareableService) {
        this.authorizationProvider = authorizationProvider;
        this.clientShareableService = clientShareableService;
    }

    @Override
    public List<ClientShareableV2Response> getAll(String accessToken, String code)
        throws ClientShareableLookupV2RestException, UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        if (Strings.isNullOrEmpty(code)) {
            throw RestExceptionBuilder.newBuilder(ClientShareableLookupV2RestException.class)
                .withErrorCode(ClientShareableLookupV2RestException.CONSTRAINT_REQUIRED)
                .build();
        }

        ArrayList<ClientShareableV2Response> result = new ArrayList<>();

        try {
            ClientShareable shareable = clientShareableService.getByCode(userAuthorization, code);
            result.add(toRestShareable(shareable));
        } catch (ShareableNotFoundException ignored) {
            // ignore if shareable not found
        }

        return result;
    }

    @Override
    public ClientShareableV2Response get(String accessToken, String shareableId)
        throws ClientShareableV2RestException, UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        ClientShareable shareable;
        try {
            shareable = clientShareableService.get(userAuthorization, Id.valueOf(shareableId));
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableV2RestException.class)
                .withErrorCode(ClientShareableV2RestException.NOT_FOUND)
                .addParameter("shareable_id", shareableId)
                .build();
        }

        return toRestShareable(shareable);
    }

    private ClientShareableV2Response toRestShareable(ClientShareable shareable) {
        return new ClientShareableV2Response(shareable.getPersonId().getValue(),
            shareable.getId().getValue(),
            shareable.getKey(),
            shareable.getCode(),
            shareable.getLink().toString(),
            ClientPersonShareableV2EndpointsImpl.toRestShareableShareableContent(shareable.getContent()),
            shareable.getData(),
            shareable.getLabel().orElse(null));
    }

}
