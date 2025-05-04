package com.extole.client.rest.timeline;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.timeline.exception.ClientTimelineEntryNotFoundRestException;
import com.extole.client.rest.timeline.exception.ClientTimelineEntryNotModifiableRestException;
import com.extole.client.rest.timeline.exception.ClientTimelineEntryRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/timeline-entries")
public interface ClientTimelineEntryEndpoints {

    /**
     * Internal reserved name that cannot be created/updated via API. Returns the creation date of the client.
     */
    String CLIENT_CREATED_ENTRY_NAME = "client_created";
    /**
     * Standard name that denotes client launch. Returns the creation date of the client if not specified.
     */
    String CLIENT_LAUNCHED_ENTRY_NAME = "client_launched";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ClientTimelineEntryResponse create(@UserAccessTokenParam String accessToken,
        ClientTimelineEntryRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientTimelineEntryRestException,
        ClientTimelineEntryNotModifiableRestException;

    @PUT
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ClientTimelineEntryResponse update(@UserAccessTokenParam String accessToken, @PathParam("name") String name,
        UpdateClientTimelineEntryRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientTimelineEntryNotFoundRestException,
        ClientTimelineEntryRestException, ClientTimelineEntryNotModifiableRestException;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientTimelineEntryResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("name") String name, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientTimelineEntryNotFoundRestException;

    @DELETE
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientTimelineEntryResponse delete(@UserAccessTokenParam String accessToken, @PathParam("name") String name,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, ClientTimelineEntryNotFoundRestException,
        ClientTimelineEntryNotModifiableRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientTimelineEntryResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @QueryParam("tags") Set<String> tags, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;
}
