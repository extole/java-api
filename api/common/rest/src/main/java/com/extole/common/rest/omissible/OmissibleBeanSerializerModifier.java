package com.extole.common.rest.omissible;

import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

class OmissibleBeanSerializerModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDescription,
        List<BeanPropertyWriter> beanProperties) {

        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter beanPropertyWriter = beanProperties.get(i);
            if (beanPropertyWriter.getType().isTypeOrSubTypeOf(Omissible.class)) {
                beanProperties.set(i, new OmissibleBeanPropertyWriter(beanPropertyWriter));
            }
        }

        return beanProperties;
    }
}
