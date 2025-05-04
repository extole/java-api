package com.extole.consumer.rest.impl.barcode;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.extole.consumer.rest.barcode.BarcodeResponse;

@Produces("image/png")
@Provider
public final class BarcodeBodyWriter implements MessageBodyWriter<BarcodeResponse> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == BarcodeResponse.class;
    }

    @Override
    public long getSize(BarcodeResponse barcode, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType) {
        return barcode.getBytes().length;
    }

    @Override
    public void writeTo(BarcodeResponse barcode, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        entityStream.write(barcode.getBytes());
    }
}
