package com.extole.common.rest.omissible;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

class OmissibleBeanPropertyWriter extends BeanPropertyWriter {

    OmissibleBeanPropertyWriter(BeanPropertyWriter base) {
        super(base);
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws Exception {
        Omissible<?> omissible = (Omissible<?>) get(bean);
        if (omissible != null && omissible.isOmitted()) {
            return;
        }
        super.serializeAsField(bean, jsonGenerator, serializerProvider);
    }
}
