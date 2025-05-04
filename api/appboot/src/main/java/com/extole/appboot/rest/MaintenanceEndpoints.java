package com.extole.appboot.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import com.extole.appboot.health.AppbootHealthCheckpoint;
import com.extole.common.rest.exception.RestExceptionBuilder;

@Provider
@Path("/")
public class MaintenanceEndpoints {

    private static final int REQUIRED_PORT = 8088;
    private final AppbootHealthCheckpoint appbootHealthCheckpoint;
    private final HttpServletRequest servletRequest;

    @Autowired
    public MaintenanceEndpoints(AppbootHealthCheckpoint appbootHealthCheckpoint,
        @Context HttpServletRequest servletRequest) {
        this.appbootHealthCheckpoint = appbootHealthCheckpoint;
        this.servletRequest = servletRequest;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MaintenanceResponse getState() throws AppbootRestException {
        validatePort();
        return new MaintenanceResponse(appbootHealthCheckpoint.getMaintenanceState());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MaintenanceResponse setState(@RequestBody MaintenanceRequest maintenanceRequest)
        throws AppbootRestException {
        validatePort();
        appbootHealthCheckpoint.setMaintenanceState(maintenanceRequest.getState());
        return new MaintenanceResponse(appbootHealthCheckpoint.getMaintenanceState());
    }

    private void validatePort() throws AppbootRestException {
        if (servletRequest.getServerPort() != REQUIRED_PORT) {
            throw RestExceptionBuilder.newBuilder(AppbootRestException.class)
                .withErrorCode(AppbootRestException.ACCESS_DENIED)
                .addParameter("port", Integer.valueOf(servletRequest.getServerPort())).build();
        }
    }
}
