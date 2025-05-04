package com.extole.client.rest.shareable.v2;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
@Path("/v2/shareables")
public interface ClientShareableV2Endpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientShareableV2Response> getAll(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("code") String code) throws ClientShareableLookupV2RestException, UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{shareable_id}")
    ClientShareableV2Response get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("shareable_id") String shareableId)
        throws ClientShareableV2RestException, UserAuthorizationRestException;
}
