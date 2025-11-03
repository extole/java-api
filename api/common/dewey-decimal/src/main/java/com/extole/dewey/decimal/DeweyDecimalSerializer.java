package com.extole.dewey.decimal;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DeweyDecimalSerializer extends StdSerializer<DeweyDecimal> {

    public DeweyDecimalSerializer() {
        super(DeweyDecimal.class, true);
    }

    @Override
    public void serialize(DeweyDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
        serializerProvider.defaultSerializeValue(value.toString(), jsonGenerator);
    }

}
