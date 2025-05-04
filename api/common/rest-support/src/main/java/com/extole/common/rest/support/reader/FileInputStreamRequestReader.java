package com.extole.common.rest.support.reader;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;
import org.glassfish.jersey.server.ContainerRequest;

import com.extole.common.rest.request.FileAttributes;
import com.extole.common.rest.request.FileInputStreamRequest;

@Provider
@Consumes(MediaType.MULTIPART_FORM_DATA)
public final class FileInputStreamRequestReader implements MessageBodyReader<FileInputStreamRequest> {

    @Inject
    private javax.inject.Provider<ContainerRequest> requestProvider;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == FileInputStreamRequest.class;
    }

    @Override
    public FileInputStreamRequest readFrom(Class<FileInputStreamRequest> type, Type genericType,
        Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
        InputStream entityStream) {

        ContainerRequest request = requestProvider.get();

        FormDataMultiPart entity = request.readEntity(FormDataMultiPart.class);
        if (entity == null) {
            throw new BadRequestException(LocalizationMessages.ENTITY_IS_EMPTY());
        }

        FormDataBodyPart part = entity.getField("file");

        if (part == null) {
            return null;
        }

        InputStream stream = part.getEntityAs(BodyPartEntity.class).getInputStream();

        FormDataContentDisposition contentDisposition = part.getFormDataContentDisposition();

        FileAttributes fileAttributes =
            new FileAttributes(contentDisposition.getFileName(), contentDisposition.getSize());

        return new FileInputStreamRequest(stream, fileAttributes);
    }
}
