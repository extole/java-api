package com.extole.consumer.rest.me.shareable.v5;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v5/me/shareables")
public interface MeShareableV5Endpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<MeShareableV5Response> getShareables(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @QueryParam("key") String key, @Nullable @QueryParam("label") String label)
        throws AuthorizationRestException;

    @GET
    @Path("/{code : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    MeShareableV5Response getShareable(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("code") String code)
        throws AuthorizationRestException, ShareableV5RestException;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    CreateMeShareableV5Response create(@AccessTokenParam(readCookie = false) String accessToken,
        CreateMeShareableV5Request request) throws AuthorizationRestException;

    @PUT
    @Path("/{code : .+}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    UpdateMeShareableV5Response update(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("code") String code,
        UpdateMeShareableV5Request request) throws AuthorizationRestException, ShareableV5RestException;

    @GET
    @Path("/status/{polling_id}")
    @Produces(MediaType.APPLICATION_JSON)
    ShareableV5PollingResponse getStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("polling_id") String pollingId) throws AuthorizationRestException;

}
