package com.extole.evaluateable;

import java.io.IOException;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.extole.evaluateable.normalization.CleanHandlebarsEvaluatableExpressionNormalizer;
import com.extole.evaluateable.validation.DefaultEvaluatableValidator;

public class ValidEvaluatableModule extends SimpleModule {

    public ValidEvaluatableModule() {
        this.setMixInAnnotation(Evaluatable.class, ValidatedEvaluatable.class);
    }

    @JsonDeserialize(using = ValidatedEvaluatableDeserializer.class,
        keyUsing = ValidatedEvaluatableKeyDeserializer.class)
    @JsonSerialize(using = EvaluatableSerializer.class, keyUsing = EvaluatableKeySerializer.class)
    private interface ValidatedEvaluatable {

    }

    private static final class ValidatedEvaluatableDeserializer extends EvaluatableDeserializer {

        @Override
        public EvaluatableDeserializer createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
            return new EvaluatableDeserializer(ctxt, property, CleanHandlebarsEvaluatableExpressionNormalizer.INSTANCE,
                DefaultEvaluatableValidator.INSTANCE);
        }
    }

    private static final class ValidatedEvaluatableKeyDeserializer extends KeyDeserializer {

        private static final EvaluatableDeserializer DESERIALIZER = new EvaluatableDeserializer(
            TypeFactory.defaultInstance().constructType(String.class),
            Evaluatable.class, false, CleanHandlebarsEvaluatableExpressionNormalizer.INSTANCE,
            DefaultEvaluatableValidator.INSTANCE);

        @Override
        public Object deserializeKey(String key, DeserializationContext deserializationContext) throws IOException {
            return DESERIALIZER.deserializeKey(key, deserializationContext);
        }
    }

}
