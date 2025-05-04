package com.extole.consumer.rest.me.shareable;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v6/me/shareables")
public interface MeShareableEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<MeShareableResponse> getShareables(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @QueryParam("key") String key, @Nullable @QueryParam("label") String label)
        throws AuthorizationRestException;

    @GET
    @Path("/{code : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    MeShareableResponse getShareable(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("code") String code)
        throws AuthorizationRestException, ShareableRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    MeShareableResponse create(@AccessTokenParam(readCookie = false) String accessToken,
        CreateMeShareableRequest request)
        throws AuthorizationRestException, CreateMeShareableRestException;

    @POST
    @Path("/get-or-create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    MeShareableResponse getOrCreate(@AccessTokenParam(readCookie = false) String accessToken,
        CreateMeShareableRequest request)
        throws AuthorizationRestException, CreateMeShareableRestException;

    @POST
    @Path("/get-create-or-update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    MeShareableResponse getCreateOrUpdate(@AccessTokenParam(readCookie = false) String accessToken,
        CreateMeShareableRequest request) throws AuthorizationRestException, CreateMeShareableRestException;

}
