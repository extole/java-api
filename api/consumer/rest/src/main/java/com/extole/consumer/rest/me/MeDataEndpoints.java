package com.extole.consumer.rest.me;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v4/me/parameters")
public interface MeDataEndpoints {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> getPersonProfileParameters(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    UpdateProfileResponse editPersonProfileParameters(@AccessTokenParam(readCookie = false) String accessToken,
        MeDataBulkUpdateRequest request) throws MeDataRestException, AuthorizationRestException;

    @PUT
    @Path("/{parameter_name}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    UpdateProfileResponse putPersonProfileParameter(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("parameter_name") String parameterName, MeDataUpdateRequest request)
        throws MeDataRestException, AuthorizationRestException;
}
