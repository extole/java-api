package com.extole.consumer.rest.me.shareable.v4;

import java.util.List;

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
import com.extole.consumer.rest.shareable.v4.CreateShareableV4RestException;
import com.extole.consumer.rest.shareable.v4.ShareableCreateV4PollingResponse;

@Deprecated // TODO remove ENG-10127
@Path("/v4/me/shareables")
public interface MeShareableV4Endpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<MeShareableV4Response> getMeShareables(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @GET
    @Path("/{shareable_id}")
    @Produces(MediaType.APPLICATION_JSON)
    MeShareableV4Response getMeShareable(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("shareable_id") String shareableId,
        @QueryParam("consumer_event.source") String promotionSource)
        throws AuthorizationRestException, MeShareableV4RestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    CreateMeShareableV4Response create(@AccessTokenParam(readCookie = false) String accessToken,
        CreateMeShareableV4Request request) throws CreateShareableV4RestException, AuthorizationRestException;

    @PUT
    @Path("/{shareable_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    CreateMeShareableV4Response edit(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("shareable_id") String shareableId, EditMeShareableV4Request request)
        throws CreateShareableV4RestException, AuthorizationRestException, MeShareableV4RestException;

    @GET
    @Path("/status/{polling_id}")
    @Produces(MediaType.APPLICATION_JSON)
    ShareableCreateV4PollingResponse getStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("polling_id") String pollingId) throws AuthorizationRestException;

}
