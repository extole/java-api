package com.extole.reporting.rest.query;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO cleanup QueryEndpoints ENG-10088
@Path("/v2/queries")
public interface QueryEndpoints {
    String CSV_MEDIA_TYPE = "text/csv;qs=.5";

    /**
     * Executes the query with the given name. HTTP query parameters are passed as named parameters to the query.
     *
     * @param accessToken - authorization token
     * @param name - name of the query
     * @return query result
     */
    @GET
    @Path("/{name}{parameter: (.csv)?}")
    @Produces({MediaType.APPLICATION_JSON, CSV_MEDIA_TYPE})
    List<Map<String, Object>> query(@UserAccessTokenParam String accessToken, @PathParam("name") String name)
        throws UserAuthorizationRestException, QueryRestException;
}
