package com.extole.client.rest.person.step.v2;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.StepQuality;
import com.extole.client.rest.person.v2.PartnerEventIdV2Request;
import com.extole.client.rest.person.v2.PersonStepV2Response;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

/**
 * @deprecated use {@link com.extole.client.rest.person.RuntimePersonEndpoints#getSteps}
 *             instead. This class will be removed once all clients have been updated.
 */
@Deprecated // TODO remove in ENG-13035
@Path("/v2/persons/{person_id}/steps")
public interface PersonStepV2Endpoints {

    @Hidden
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonStepV2Response> getSteps(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @Nullable @QueryParam("campagn_id") String campaignId,
        @Nullable @QueryParam("program_label") String programLabel,
        @Nullable @QueryParam("stepName") String stepName,
        @Nullable @QueryParam("quality") StepQuality quality,
        @Nullable @BeanParam PartnerEventIdV2Request partnerEventId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{step_id}")
    PersonStepV2Response getStep(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("step_id") String stepId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonStepV2RestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{step_id}")
    void updateStep(
        @UserAccessTokenParam String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("step_id") String stepId,
        @RequestBody(description = "PersonStepUpdateRequest object",
            required = true) PersonStepV2UpdateRequest stepUpdateRequest)
        throws UserAuthorizationRestException, PersonRestException, PersonStepV2RestException,
        PersonStepValidationV2RestException;

}
