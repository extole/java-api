package com.extole.consumer.rest.shareable.v4;

import java.util.List;

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

@Deprecated // TODO remove ENG-10127
@Path("/v4/shareable")
public interface ShareableV4Endpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<GetShareableV4Response> getShareables(@AccessTokenParam(readCookie = false) String accessToken,
        @QueryParam("code") String code)
        throws AuthorizationRestException;

    @GET
    @Path("/{shareable_id}")
    @Produces(MediaType.APPLICATION_JSON)
    GetShareableV4Response get(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("shareable_id") String shareableId)
        throws AuthorizationRestException, GetShareableV4RestException;

    @Deprecated // TODO remove ENG-10127
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    CreateShareableV4Response create(@AccessTokenParam(readCookie = false) String accessToken,
        CreateShareableV4Request request)
        throws CreateShareableV4RestException, AuthorizationRestException;

    @Deprecated // TODO remove ENG-10127
    @GET
    @Path("/status/{polling_id}")
    @Produces(MediaType.APPLICATION_JSON)
    ShareableCreateV4PollingResponse getStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("polling_id") String pollingId) throws AuthorizationRestException;

}
