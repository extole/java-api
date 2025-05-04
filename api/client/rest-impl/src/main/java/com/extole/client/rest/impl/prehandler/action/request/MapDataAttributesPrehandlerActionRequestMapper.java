package com.extole.client.rest.impl.prehandler.action.request;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.client.rest.prehandler.action.exception.MapDataAttributesPrehandlerActionRestException;
import com.extole.client.rest.prehandler.action.request.MapDataAttributesPrehandlerActionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.prehandler.action.DataAttributeMapping;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.action.data.DuplicatedAttributeInDataAttributeMappingsException;
import com.extole.model.service.prehandler.action.data.DuplicatedSourceAttributeInDataAttributeMappingsException;
import com.extole.model.service.prehandler.action.data.InvalidDataValuePrehandlerActionException;
import com.extole.model.service.prehandler.action.data.MapDataAttributesPrehandlerActionBuilder;
import com.extole.model.service.prehandler.action.data.MissingAttributeFromDataAttributeMappingException;
import com.extole.model.service.prehandler.action.data.MissingDataAttributeMappingsException;
import com.extole.model.service.prehandler.action.data.RedundantDataAttributeMappingException;

@Component
public class MapDataAttributesPrehandlerActionRequestMapper
    implements PrehandlerActionRequestMapper<MapDataAttributesPrehandlerActionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, MapDataAttributesPrehandlerActionRequest action)
        throws PrehandlerActionValidationRestException {
        try {
            MapDataAttributesPrehandlerActionBuilder builder = prehandlerBuilder
                .addAction(com.extole.model.entity.prehandler.PrehandlerActionType.MAP_DATA_ATTRIBUTES);
            builder.withMappings(
                action.getDataAttributeMappings().stream()
                    .filter(mapping -> Objects.nonNull(mapping))
                    .map(mapping -> new DataAttributeMapping(mapping.getAttribute(), mapping.getSourceAttribute(),
                        mapping.getDefaultValue().orElse(null)))
                    .collect(Collectors.toList()));
            builder.done();
        } catch (MissingAttributeFromDataAttributeMappingException e) {
            throw RestExceptionBuilder.newBuilder(MapDataAttributesPrehandlerActionRestException.class)
                .withErrorCode(MapDataAttributesPrehandlerActionRestException.MISSING_ATTRIBUTE)
                .withCause(e)
                .build();
        } catch (RedundantDataAttributeMappingException e) {
            throw RestExceptionBuilder.newBuilder(MapDataAttributesPrehandlerActionRestException.class)
                .withErrorCode(MapDataAttributesPrehandlerActionRestException.REDUNDANT_ATTRIBUTES)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (DuplicatedAttributeInDataAttributeMappingsException e) {
            throw RestExceptionBuilder.newBuilder(MapDataAttributesPrehandlerActionRestException.class)
                .withErrorCode(MapDataAttributesPrehandlerActionRestException.DUPLICATED_ATTRIBUTES)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (DuplicatedSourceAttributeInDataAttributeMappingsException e) {
            throw RestExceptionBuilder.newBuilder(MapDataAttributesPrehandlerActionRestException.class)
                .withErrorCode(MapDataAttributesPrehandlerActionRestException.DUPLICATED_SOURCE_ATTRIBUTES)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (MissingDataAttributeMappingsException e) {
            throw RestExceptionBuilder.newBuilder(MapDataAttributesPrehandlerActionRestException.class)
                .withErrorCode(MapDataAttributesPrehandlerActionRestException.MISSING_DATA_ATTRIBUTE_MAPPINGS)
                .withCause(e)
                .build();
        } catch (InvalidDataValuePrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(MapDataAttributesPrehandlerActionRestException.class)
                .withErrorCode(MapDataAttributesPrehandlerActionRestException.DEFAULT_VALUE_INVALID)
                .addParameter("name", e.getName())
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.MAP_DATA_ATTRIBUTES;
    }
}
