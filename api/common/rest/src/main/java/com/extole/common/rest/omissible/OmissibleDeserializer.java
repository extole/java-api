package com.extole.common.rest.omissible;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import com.extole.common.lang.deserializer.CacheableStdDeserializer;
import com.extole.evaluateable.Evaluatable;

final class OmissibleDeserializer extends CacheableStdDeserializer<Omissible<?>> implements ContextualDeserializer {

    private JavaType boundType;
    private BeanProperty beanProperty;

    OmissibleDeserializer() {
        super((JavaType) null);
    }

    OmissibleDeserializer(JavaType boundType, BeanProperty beanProperty) {
        this();
        this.boundType = boundType;
        this.beanProperty = beanProperty;
    }

    @Override
    public Omissible<?> deserialize(JsonParser parser, DeserializationContext deserializationContext)
        throws IOException {
        Object deserialized = parser.getCodec().readValue(parser, boundType);
        if (deserialized != null) {
            return Omissible.of(deserialized, false);
        }
        if (mayBeNull(boundType)) {
            return Omissible.nullified();
        }
        throw new OmissibleInvalidNullException(deserializationContext,
            "The attribute of required types was specified (not omitted) but deserialized into null." +
                " The type : " + boundType.toString() + " is not considered optional",
            beanProperty.getFullName());
    }

    @Override
    public Omissible<?> getNullValue(DeserializationContext deserializationContext) throws JsonMappingException {
        if (mayBeNull(boundType)) {
            Object nullValue = getDefaultDeserializer(deserializationContext).getNullValue(deserializationContext);
            return Omissible.of(nullValue, false);
        }

        throw new OmissibleInvalidNullException(deserializationContext,
            "The attribute of required types may be omitted but not nullified." +
                " The type : " + boundType.toString() + " is not considered optional",
            beanProperty == null ? PropertyName.NO_NAME : beanProperty.getFullName());
    }

    @Override
    public Omissible<?> getAbsentValue(DeserializationContext deserializationContext) throws JsonMappingException {
        Object nullValue = getDefaultDeserializer(deserializationContext).getNullValue(deserializationContext);
        return Omissible.of(nullValue, true);
    }

    @Override
    public Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
        Object emptyValue = getDefaultDeserializer(deserializationContext).getEmptyValue(deserializationContext);
        return Omissible.of(emptyValue, false);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext,
        BeanProperty beanProperty) throws JsonMappingException {
        JavaType contextualType = deserializationContext.getContextualType();

        if (!Omissible.class.isAssignableFrom(contextualType.getRawClass())) {
            throw InvalidDefinitionException.from(deserializationContext,
                "This deserializer supports only " + Omissible.class.getSimpleName());
        }

        if (contextualType.getBindings().size() != 1) {
            throw InvalidDefinitionException.from(deserializationContext,
                "Raw Omissible is not supported. Type parameter must be specified.");
        }

        return new OmissibleDeserializer(contextualType.getBindings().getBoundType(0), beanProperty);
    }

    private JsonDeserializer<?> getDefaultDeserializer(DeserializationContext deserializationContext)
        throws JsonMappingException {
        return deserializationContext.findRootValueDeserializer(boundType);
    }

    private boolean mayBeNull(JavaType javaType) {
        return isOptionalType(javaType) || isEvaluatableOptionalType(javaType);
    }

    private boolean isEvaluatableOptionalType(JavaType javaType) {
        return javaType.isTypeOrSubTypeOf(Evaluatable.class) && mayBeNull(javaType.getBindings().getBoundType(1));
    }

    private boolean isOptionalType(JavaType javaType) {
        return javaType.getRawClass() == Optional.class;
    }
}
