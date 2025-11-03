package com.extole.evaluateable;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.extole.evaluateable.normalization.EvaluatableExpressionNormalizer;
import com.extole.evaluateable.validation.EvaluatableValidator;

final class EvaluatableKeyDeserializer extends KeyDeserializer {

    private static final EvaluatableDeserializer DESERIALIZER = new EvaluatableDeserializer(
        TypeFactory.defaultInstance().constructType(String.class),
        Evaluatable.class, false, EvaluatableExpressionNormalizer.DEFAULT_NOOP, EvaluatableValidator.DEFAULT_NOOP);

    @Override
    public Object deserializeKey(String key, DeserializationContext deserializationContext) throws IOException {
        return DESERIALIZER.deserializeKey(key, deserializationContext);
    }

}
