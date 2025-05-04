package com.extole.client.rest.impl.shareable;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.impl.person.PersonShareableRestMapper;
import com.extole.client.rest.person.v4.PersonShareableV4Response;
import com.extole.client.rest.shareable.ClientShareableEndpoints;
import com.extole.client.rest.shareable.ClientShareableRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.service.shareable.ClientShareable;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.person.service.shareable.ShareableNotFoundException;

@Provider
public class ClientShareableEndpointsImpl implements ClientShareableEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientShareableService clientShareableService;
    private final PersonShareableRestMapper personShareableRestMapper;

    @Autowired
    public ClientShareableEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientShareableService clientShareableService,
        PersonShareableRestMapper personShareableRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.clientShareableService = clientShareableService;
        this.personShareableRestMapper = personShareableRestMapper;
    }

    @Override
    public PersonShareableV4Response get(String accessToken, String code)
        throws ClientShareableRestException, UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ClientShareable shareable = clientShareableService.getByCode(userAuthorization, code);
            return personShareableRestMapper.toPersonShareableV4Response(shareable);
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableRestException.class)
                .withErrorCode(ClientShareableRestException.SHAREABLE_NOT_FOUND)
                .addParameter("code", code)
                .withCause(e).build();
        }
    }

}
