package com.extole.consumer.rest.barcode;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/v4/barcode")
public interface BarcodeEndpoints {

    @GET
    @Produces("image/png")
    BarcodeResponse generate(@QueryParam("type") BarcodeType type,
        @QueryParam("content") String content,
        @Nullable @QueryParam("width") Integer width,
        @Nullable @QueryParam("height") Integer height,
        @Nullable @QueryParam("margin_width") Integer marginWidth)
        throws BarcodeRestException;

    @GET
    @Path("/legacy")
    @Produces("image/png")
    BarcodeResponse generate(@QueryParam("codetype") BarcodeType type,
        @QueryParam("text") String content,
        @Nullable @QueryParam("size") Integer size)
        throws BarcodeRestException;
}
