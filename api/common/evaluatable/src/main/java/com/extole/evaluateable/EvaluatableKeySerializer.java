package com.extole.evaluateable;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

final class EvaluatableKeySerializer<CONTEXT, RESULT> extends StdSerializer<Evaluatable<CONTEXT, RESULT>> {

    EvaluatableKeySerializer() {
        super(EvaluatableKeySerializer.class, false);
    }

    @Override
    public void serialize(Evaluatable<CONTEXT, RESULT> value, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider) throws IOException {
        EvaluatableSerializer<CONTEXT, RESULT> serializer = new EvaluatableSerializer<>(true);
        serializer.serialize(value, jsonGenerator, serializerProvider);
    }

}
