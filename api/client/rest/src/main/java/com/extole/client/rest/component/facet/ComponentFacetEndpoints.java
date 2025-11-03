package com.extole.client.rest.component.facet;

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

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/component-facets")
@Tag(name = "/v1/component-facets")
public interface ComponentFacetEndpoints {

    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List component facets")
    List<ComponentFacetResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam ComponentFacetQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{name}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Get component facet")
    ComponentFacetResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("name") String name, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentFacetRestException;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Create component facet")
    ComponentFacetResponse create(
        @UserAccessTokenParam String accessToken,
        @RequestBody(required = true) ComponentFacetCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentFacetRestException, CampaignComponentValidationRestException,
        ComponentFacetValidationRestException;

    @PUT
    @Path("/{name}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Update component facet")
    ComponentFacetResponse update(
        @UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("name") String name,
        @RequestBody(required = true) ComponentFacetUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentFacetRestException, CampaignComponentValidationRestException,
        ComponentFacetValidationRestException;

    @DELETE
    @Path("/{name}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Archive component facet")
    ComponentFacetResponse archive(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("name") String name, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentFacetRestException, ComponentFacetValidationRestException;

    @GET
    @Path("/default")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List default component facets")
    List<ComponentFacetResponse> listDefault(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

}
