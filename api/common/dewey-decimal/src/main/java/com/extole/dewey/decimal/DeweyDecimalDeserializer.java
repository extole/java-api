package com.extole.dewey.decimal;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.apache.commons.lang3.StringUtils;

import com.extole.common.lang.deserializer.CacheableStdDeserializer;

final class DeweyDecimalDeserializer extends CacheableStdDeserializer<DeweyDecimal> implements ContextualDeserializer {

    DeweyDecimalDeserializer() {
        super((JavaType) null);
    }

    @Override
    public DeweyDecimal deserialize(JsonParser parser, DeserializationContext deserializationContext)
        throws IOException {
        if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            return tryParseDeweyDecimal(parser);
        }

        throw new DeweyDecimalMismatchedInputException(parser,
            "Token with type=" + parser.getCurrentToken() + " can't be deserialized as a Dewey decimal");
    }

    private DeweyDecimal tryParseDeweyDecimal(JsonParser parser)
        throws IOException {
        String value = parser.getValueAsString();
        if (StringUtils.isBlank(value)) {
            throw new DeweyDecimalInvalidFormatException(parser, "Blank string not allowed", value, DeweyDecimal.class);
        }

        try {
            return DeweyDecimal.valueOf(value);
        } catch (NumberFormatException e) {
            throw new DeweyDecimalInvalidFormatException(parser,
                "String has an invalid format for a Dewey decimal=" + value,
                value, DeweyDecimal.class);
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext,
        BeanProperty beanProperty) throws JsonMappingException {
        JavaType contextualType = deserializationContext.getContextualType();

        if (!DeweyDecimal.class.isAssignableFrom(contextualType.getRawClass())) {
            throw InvalidDefinitionException.from(deserializationContext,
                "This deserializer supports only " + DeweyDecimal.class.getSimpleName());
        }

        return new DeweyDecimalDeserializer();
    }

}
