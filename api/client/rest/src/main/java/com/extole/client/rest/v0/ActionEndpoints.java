package com.extole.client.rest.v0;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v0/actions")
public interface ActionEndpoints {

    String FILTER_PATH = "/filter.json";
    String ACTION_DETAIL_PATH = "/detail";

    @GET
    @Path(FILTER_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    ResultList<ActionResponse> listActions(@QueryParam("campaign_id") String campaignId,
        @QueryParam("start_date") Long startDate,
        @QueryParam("end_date") Long endDate,
        @QueryParam("actionChannel") String actionChannel,
        @QueryParam("action_type") List<ActionType> actionTypes,
        @QueryParam("quality_score") List<QualityScore> qualityScores,
        @QueryParam("review_status") List<ReviewStatus> reviewStatuses,
        @QueryParam("search") String search,
        @QueryParam("person_id") String personId,
        @QueryParam("offset") Integer offset,
        @QueryParam("limit") Integer limit,
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path(ACTION_DETAIL_PATH + "/{action_id}.json")
    @Produces(MediaType.APPLICATION_JSON)
    DataResponse getActionDetail(@PathParam("action_id") String actionId,
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws ActionRestException, UserAuthorizationRestException;

    @Hidden
    @DELETE
    @Path("/{action_id}")
    void deleteActionById(@PathParam("action_id") String actionId,
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken)
        throws ActionRestException, UserAuthorizationRestException;
}
