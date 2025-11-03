package com.extole.client.rest.client.domain.pattern;

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

import com.extole.api.client.domain.pattern.ClientDomainPattern;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.client.domain.pattern.built.BuiltClientDomainPatternQueryParams;
import com.extole.client.rest.client.domain.pattern.built.BuiltClientDomainPatternResponse;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.id.Id;

@Path("/v1/client-domain-patterns")
@Tag(name = "/v1/client-domain-patterns")
public interface ClientDomainPatternEndpoints {

    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List client domain patterns")
    List<ClientDomainPatternResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam ClientDomainPatternQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Get client domain pattern")
    ClientDomainPatternResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("id") Id<ClientDomainPattern> id,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientDomainPatternRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/built")
    @Operation(summary = "List built client domain patterns")
    List<BuiltClientDomainPatternResponse> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam BuiltClientDomainPatternQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{id}/built")
    @Operation(summary = "Get built client domain pattern")
    BuiltClientDomainPatternResponse getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("id") Id<ClientDomainPattern> id,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientDomainPatternRestException;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Create client domain pattern")
    ClientDomainPatternResponse create(
        @UserAccessTokenParam String accessToken,
        @RequestBody(required = true) ClientDomainPatternCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        BuildClientDomainPatternRestException;

    @PUT
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Update client domain pattern")
    ClientDomainPatternResponse update(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("id") Id<ClientDomainPattern> id,
        @RequestBody(required = true) ClientDomainPatternUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientDomainPatternRestException, OmissibleRestException,
        CampaignComponentValidationRestException, BuildClientDomainPatternRestException;

    @DELETE
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Archive client domain pattern")
    ClientDomainPatternResponse archive(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("id") Id<ClientDomainPattern> clientDomainPatternId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientDomainPatternRestException;

    @POST
    @Path("/{id}/delete")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Delete client domain pattern")
    ClientDomainPatternResponse delete(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("id") Id<ClientDomainPattern> clientDomainPatternId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientDomainPatternRestException;

    @POST
    @Path("/{id}/unarchive")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Unarchive client domain pattern")
    ClientDomainPatternResponse unarchive(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("id") Id<ClientDomainPattern> clientDomainPatternId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientDomainPatternRestException;

}
