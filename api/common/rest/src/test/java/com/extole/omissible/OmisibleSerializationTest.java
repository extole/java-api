package com.extole.omissible;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import com.extole.common.rest.omissible.OmissibleInvalidNullException;
import com.extole.common.rest.omissible.OmissibleModule;
import com.extole.omissible.types.ConcretePojo;
import com.extole.omissible.types.PojoWithFlags;

class OmisibleSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new OmissibleModule())
        .registerModule(new Jdk8Module());

    @Test
    void testDeserializePojoWithInheritanceAndNestedOmissible() throws Exception {
        ConcretePojo pojo =
            deserialize("{\"simple_pojo\":{\"a\":[\"x\",\"y\"],\"c\":[\"z\"]},\"type\":\"CONCRETE\"}",
                ConcretePojo.class);

        assertThat(pojo.getSimplePojo().isOmitted()).isFalse();
        assertThat(pojo.getSimplePojo().getValue()).isPresent();
        assertThat(pojo.getSimplePojo().getValue().get().getA().getValue()).isEqualTo(Lists.newArrayList("x", "y"));
        assertThat(pojo.getSimplePojo().getValue().get().getC().getValue()).isEqualTo(Lists.newArrayList("z"));
        assertThat(pojo.getSimplePojo().getValue().get().getB().isOmitted()).isTrue();
    }

    /**
     * @see com/fasterxml/jackson/databind/deser/std/StdDeserializer.java:530 - why empty string can be null for Boolean
     */
    @Test
    void testDeserializeOmissibleWithIndirectNull() throws Exception {
        OmissibleInvalidNullException exception = assertThrows(OmissibleInvalidNullException.class,
            () -> deserialize("{\"flag\":\"\"}", PojoWithFlags.class));
        assertThat(exception.getPropertyName().getSimpleName()).isEqualTo("flag");

        PojoWithFlags pojoWithFlags = deserialize("{\"optional_flag\":\"\"}", PojoWithFlags.class);
        assertThat(pojoWithFlags.getFlag().isOmitted()).isTrue();
        assertThat(pojoWithFlags.getOptionalFlag().isOmitted()).isFalse();
        assertThat(pojoWithFlags.getOptionalFlag().getValue()).isEmpty();
    }

    private <T> T deserialize(String serialized, Class<T> valueType) throws Exception {
        return objectMapper.readValue(serialized, valueType);
    }

}
