package com.extole.evaluatable.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.extole.evaluation.model.collection.list.optional.DummyModelWithOptionalBooleanListEvaluatable;
import com.extole.evaluation.model.collection.list.optional.DummyModelWithOptionalEnumListEvaluatable;
import com.extole.evaluation.model.collection.list.optional.DummyModelWithOptionalStringListEvaluatable;
import com.extole.evaluation.model.collection.set.optional.DummyModelWithOptionalBooleanSetEvaluatable;
import com.extole.evaluation.model.collection.set.optional.DummyModelWithOptionalEnumSetEvaluatable;
import com.extole.evaluation.model.collection.set.optional.DummyModelWithOptionalStringSetEvaluatable;
import com.extole.evaluation.model.single.optional.DummyModelWithOptionalBooleanEvaluatable;
import com.extole.evaluation.model.single.optional.DummyModelWithOptionalEnumEvaluatable;
import com.extole.evaluation.model.single.optional.DummyModelWithOptionalStringEvaluatable;

class EvaluatableOptionalSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":\"testValue\"}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@buildtime:'testValue'\"}",
        "{\"evaluatable\":\"handlebars@buildtime:'testValue'\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@runtime:'testValue'\"}",
        "{\"evaluatable\":\"handlebars@runtime:testValue\"}"
    })
    void testOptionalString(String serialized) throws Exception {
        DummyModelWithOptionalStringEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalStringEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":\"TEST_VALUE\"}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"spel@buildtime:'TEST_VALUE'\"}",
        "{\"evaluatable\":\"handlebars@buildtime:'TEST_VALUE'\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"spel@runtime:'TEST_VALUE'\"}",
        "{\"evaluatable\":\"handlebars@runtime:'TEST_VALUE'\"}"
    })
    void testOptionalEnum(String serialized) throws Exception {
        DummyModelWithOptionalEnumEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalEnumEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":true}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:true\"}",
        "{\"evaluatable\":\"handlebars@buildtime:true\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"spel@runtime:true\"}",
        "{\"evaluatable\":\"handlebars@runtime:true\"}"
    })
    void testOptionalBoolean(String serialized) throws Exception {
        DummyModelWithOptionalBooleanEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalBooleanEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":[\"testValue\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'testValue'}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:['testValue']\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'testValue'}\"}",
        "{\"evaluatable\":\"handlebars@runtime:['testValue']\"}",
    })
    void testOptionalListString(String serialized) throws Exception {
        DummyModelWithOptionalStringListEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalStringListEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":[\"testValue\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"handlebars@buildtime:['testValue']\"}",
        "{\"evaluatable\":\"spel@buildtime:{'testValue'}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'testValue'}\"}",
        "{\"evaluatable\":\"handlebars@runtime:['testValue']\"}",
    })
    void testOptionalSetString(String serialized) throws Exception {
        DummyModelWithOptionalStringSetEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalStringSetEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":[\"TEST_VALUE\",\"TEST_VALUE2\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:['TEST_VALUE', 'TEST_VALUE2']\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}",
        "{\"evaluatable\":\"handlebars@runtime:['TEST_VALUE', 'TEST_VALUE2']\"}",
    })
    void testOptionalListOfEnum(String serialized) throws Exception {
        DummyModelWithOptionalEnumListEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithOptionalEnumListEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":[\"TEST_VALUE\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['TEST_VALUE'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['TEST_VALUE'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'TEST_VALUE'}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:['TEST_VALUE']\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['TEST_VALUE'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['TEST_VALUE'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'TEST_VALUE'}\"}",
        "{\"evaluatable\":\"handlebars@runtime:['TEST_VALUE', 'TEST_VALUE2']\"}",
    })
    void testOptionalSetOfEnum(String serialized) throws Exception {
        DummyModelWithOptionalEnumSetEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithOptionalEnumSetEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":[true,false]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{true, false}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:[true, false]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{true, false}\"}",
        "{\"evaluatable\":\"handlebars@runtime:[true, false]\"}",
    })
    void testOptionalListOfBoolean(String serialized) throws Exception {
        DummyModelWithOptionalBooleanListEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithOptionalBooleanListEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":[true]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [true];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [true];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{true}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:[true]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [true];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [true];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{true}\"}",
        "{\"evaluatable\":\"handlebars@runtime:[true]\"}",
    })
    void testOptionalSetOfBoolean(String serialized) throws Exception {
        DummyModelWithOptionalBooleanSetEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithOptionalBooleanSetEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:null\"}",
        "{\"evaluatable\":\"handlebars@buildtime:#{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalStringAsNull(String serialized) throws Exception {
        DummyModelWithOptionalStringEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalStringEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:null\"}",
        "{\"evaluatable\":\"handlebars@buildtime:#{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalEnumAsNull(String serialized) throws Exception {
        DummyModelWithOptionalEnumEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalEnumEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:null\"}",
        "{\"evaluatable\":\"handlebars@buildtime:#{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalBooleanAsNull(String serialized) throws Exception {
        DummyModelWithOptionalBooleanEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalBooleanEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: null\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalListStringAsNull(String serialized) throws Exception {
        DummyModelWithOptionalStringListEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalStringListEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: null\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalSetStringAsNull(String serialized) throws Exception {
        DummyModelWithOptionalStringSetEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalStringSetEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: null\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalListOfEnumAsNull(String serialized) throws Exception {
        DummyModelWithOptionalEnumListEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithOptionalEnumListEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: null\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalSetOfEnumAsNull(String serialized) throws Exception {
        DummyModelWithOptionalEnumSetEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithOptionalEnumSetEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:null\"}",
        "{\"evaluatable\":\"handlebars@buildtime:#{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalListOfBooleanAsNull(String serialized) throws Exception {
        DummyModelWithOptionalBooleanListEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithOptionalBooleanListEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        "{\"evaluatable\":null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:null\"}",
        "{\"evaluatable\":\"handlebars@buildtime:#{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime:#{null}\"}"
    })
    void testOptionalSetOfBooleanAsNull(String serialized) throws Exception {
        DummyModelWithOptionalBooleanSetEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithOptionalBooleanSetEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

}
