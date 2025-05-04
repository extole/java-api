package com.extole.client.rest.shareable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.person.v4.PersonShareableV4Response;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v3/shareables")
public interface ClientShareableEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{code : .+}")
    PersonShareableV4Response get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("code") String code) throws ClientShareableRestException, UserAuthorizationRestException;
}
