package com.extole.consumer.rest.validation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v6/address")
public interface AddressEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/validate")
    AddressValidationResponse validate(@AccessTokenParam(readCookie = false) String accessToken,
        AddressValidationRequest request) throws AuthorizationRestException, AddressRestException;

}
