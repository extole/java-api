package com.extole.consumer.rest.web.zones;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/core")
public interface CorePreviewEndpoints {
    @GET
    @Path("/preview.html")
    @Produces("text/html")
    Response preview();

    @GET
    @Path("/preview/nested-creatives.js")
    @Produces("text/javascript")
    Response previewNestedCreativeJs(@QueryParam("campaign_id") String campaignId,
        @QueryParam("zone_name") String zoneName, @QueryParam("journey_name") String journeyName);
}
