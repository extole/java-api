package com.extole.client.rest.impl.campaign.component.setting.variable.batch;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.extole.client.rest.campaign.component.setting.BatchComponentVariableValues;
import com.extole.common.rest.ExtoleMediaType;
import com.extole.file.parser.content.FileContentParseFormat;
import com.extole.file.parser.content.FileContentParser;
import com.extole.file.parser.content.FileContentWriteStrategy;

@Provider
@Priority(Priorities.ENTITY_CODER)
@Produces(ExtoleMediaType.TEXT_CSV)
public class CsvBatchComponentVariableValuesMessageBodyWriter
    implements MessageBodyWriter<List<BatchComponentVariableValues>> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Arrays.stream(annotations)
            .anyMatch(
                annotation -> annotation.annotationType().equals(BatchComponentVariableValuesResponseBinding.class));
    }

    @Override
    public long getSize(List<BatchComponentVariableValues> response, Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List<BatchComponentVariableValues> response, Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        FileContentWriteStrategy fileFormatParseStrategy =
            FileContentParser.getWriteStrategy(FileContentParseFormat.CSV);
        entityStream.write(fileFormatParseStrategy.writeFileContent(response).getContentAsBytes());
    }

}
