package com.extole.client.rest.component.type;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/component-types")
@Tag(name = "/v1/component-types")
public interface ComponentTypeEndpoints {

    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List component types")
    List<ComponentTypeResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam ComponentTypeQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{name}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Get component type")
    ComponentTypeResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("name") String name, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentTypeRestException;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Create component type")
    ComponentTypeResponse create(
        @UserAccessTokenParam String accessToken,
        @RequestBody(required = true) ComponentTypeCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentTypeValidationRestException;

    @PUT
    @Path("/{name}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Update component type")
    ComponentTypeResponse update(
        @UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("name") String name,
        @RequestBody(required = true) ComponentTypeUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentTypeRestException, ComponentTypeValidationRestException;

    @DELETE
    @Path("/{name}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Archive component type")
    ComponentTypeResponse archive(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("name") String name, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentTypeRestException, ComponentTypeArchiveRestException;

    @GET
    @Path("/default")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List default component types")
    List<ComponentTypeResponse> listDefault(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam ComponentTypeQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

}
