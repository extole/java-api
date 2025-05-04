package com.extole.client.rest.impl.security.key;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
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

import com.extole.client.rest.security.key.FileBasedClientKeyCreateRequest;
import com.extole.client.rest.security.key.FileClientKeyRequest;
import com.extole.common.rest.request.FileAttributes;
import com.extole.common.rest.request.FileInputStreamRequest;

@Provider
@Consumes(MediaType.MULTIPART_FORM_DATA)
public final class FileClientKeyRequestReader<REQUEST extends FileBasedClientKeyCreateRequest>
    implements MessageBodyReader<FileClientKeyRequest<REQUEST>> {

    @Inject
    private javax.inject.Provider<ContainerRequest> requestProvider;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == FileClientKeyRequest.class;
    }

    @Override
    public FileClientKeyRequest<REQUEST> readFrom(Class<FileClientKeyRequest<REQUEST>> type, Type genericType,
        Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
        InputStream entityStream) {

        ContainerRequest request = requestProvider.get();
        FormDataMultiPart entity = request.readEntity(FormDataMultiPart.class);
        if (entity == null) {
            throw new BadRequestException(LocalizationMessages.ENTITY_IS_EMPTY());
        }

        FormDataBodyPart metadata = entity.getField("metadata");
        FileBasedClientKeyCreateRequest metadataRequest = null;
        if (metadata != null) {
            metadata.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            try {
                metadataRequest = metadata.getValueAs(FileBasedClientKeyCreateRequest.class);
            } catch (ProcessingException e) {
                throw new BadRequestException(LocalizationMessages.ERROR_READING_ENTITY(e.getLocalizedMessage()), e);
            }
        }

        FormDataBodyPart part = entity.getField("file");
        FileInputStreamRequest fileInputStreamRequest = null;
        if (part != null) {
            InputStream stream = part.getEntityAs(BodyPartEntity.class).getInputStream();
            FormDataContentDisposition contentDisposition = part.getFormDataContentDisposition();
            FileAttributes fileAttributes = new FileAttributes(contentDisposition.getFileName(),
                contentDisposition.getSize());
            fileInputStreamRequest = new FileInputStreamRequest(stream, fileAttributes);
        }

        return new FileClientKeyRequest<REQUEST>((REQUEST) metadataRequest, fileInputStreamRequest);
    }
}
