package com.extole.client.rest.label;

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

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/label-injectors")
public interface LabelInjectorEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    LabelInjectorResponse create(@UserAccessTokenParam String accessToken, LabelInjectorCreateRequest request,
        @TimeZoneParam ZoneId timeZone) throws LabelInjectorValidationRestException, UserAuthorizationRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{labelInjectorId}")
    LabelInjectorResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("labelInjectorId") String labelInjectorId, LabelInjectorUpdateRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws LabelInjectorRestException, LabelInjectorValidationRestException, UserAuthorizationRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{labelInjectorId}")
    LabelInjectorResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("labelInjectorId") String labelInjectorId, @TimeZoneParam ZoneId timeZone)
        throws LabelInjectorRestException, UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{labelInjectorId}")
    LabelInjectorResponse read(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("labelInjectorId") String labelInjectorId, @TimeZoneParam ZoneId timeZone)
        throws LabelInjectorRestException, UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<LabelInjectorResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;
}
