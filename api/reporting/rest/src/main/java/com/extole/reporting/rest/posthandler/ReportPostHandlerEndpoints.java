package com.extole.reporting.rest.posthandler;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/report-post-handlers")
public interface ReportPostHandlerEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ReportPostHandlerResponse create(@UserAccessTokenParam String accessToken,
        ReportPostHandlerRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportPostHandlerValidationRestException,
        ReportPostHandlerActionValidationRestException, ReportPostHandlerConditionValidationRestException;

    @GET
    @Path("/{handlerId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportPostHandlerResponse get(@UserAccessTokenParam String accessToken, @PathParam("handlerId") String handlerId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, ReportPostHandlerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ReportPostHandlerResponse> list(@UserAccessTokenParam String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @PUT
    @Path("/{handlerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ReportPostHandlerResponse update(@UserAccessTokenParam String accessToken, @PathParam("handlerId") String handlerId,
        ReportPostHandlerRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportPostHandlerValidationRestException, ReportPostHandlerRestException,
        ReportPostHandlerActionValidationRestException, ReportPostHandlerConditionValidationRestException;

    @DELETE
    @Path("/{handlerId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportPostHandlerResponse delete(@UserAccessTokenParam String accessToken, @PathParam("handlerId") String handlerId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, ReportPostHandlerRestException;
}
