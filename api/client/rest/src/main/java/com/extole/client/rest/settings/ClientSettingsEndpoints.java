package com.extole.client.rest.settings;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;

@Path(ClientSettingsEndpoints.CLIENT_SETTINGS_ENDPOINT_PATH)
public interface ClientSettingsEndpoints {

    String CLIENT_SETTINGS_ENDPOINT_PATH = "/v2/clients";
    String CLIENT_SETTINGS_CURRENT = "/current/settings";
    String CLIENT_SETTINGS_AVAILABLE_TIME_ZONES = "/time-zones";

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(ClientSettingsEndpoints.CLIENT_SETTINGS_CURRENT)
    ClientSettingsResponse update(@UserAccessTokenParam String accessToken, ClientSettingsRequest request)
        throws ClientSettingsRestException, UserAuthorizationRestException, OmissibleRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(ClientSettingsEndpoints.CLIENT_SETTINGS_CURRENT)
    ClientSettingsResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(ClientSettingsEndpoints.CLIENT_SETTINGS_AVAILABLE_TIME_ZONES)
    Collection<String>
        getAvailableTimezones(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
            throws UserAuthorizationRestException;
}
