package com.extole.evaluatable.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.extole.evaluation.model.collection.list.DummyModelWithBooleanListEvaluatable;
import com.extole.evaluation.model.collection.list.DummyModelWithEnumListEvaluatable;
import com.extole.evaluation.model.collection.list.DummyModelWithStringListEvaluatable;
import com.extole.evaluation.model.collection.set.DummyModelWithBooleanSetEvaluatable;
import com.extole.evaluation.model.collection.set.DummyModelWithEnumSetEvaluatable;
import com.extole.evaluation.model.collection.set.DummyModelWithStringSetEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithBooleanEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithEnumEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithStringEvaluatable;

class EvaluatableSerializationTest {

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
    void testString(String serialized) throws Exception {
        DummyModelWithStringEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithStringEvaluatable.class);
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
    void testEnum(String serialized) throws Exception {
        DummyModelWithEnumEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithEnumEvaluatable.class);
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
    void testBoolean(String serialized) throws Exception {
        DummyModelWithBooleanEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithBooleanEvaluatable.class);
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
    void testListString(String serialized) throws Exception {
        DummyModelWithStringListEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithStringListEvaluatable.class);
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
    void testSetString(String serialized) throws Exception {
        DummyModelWithStringSetEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithStringSetEvaluatable.class);
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
    void testListOfEnum(String serialized) throws Exception {
        DummyModelWithEnumListEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithEnumListEvaluatable.class);
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
    void testSetOfEnum(String serialized) throws Exception {
        DummyModelWithEnumSetEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithEnumSetEvaluatable.class);
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
    void testListOfBoolean(String serialized) throws Exception {
        DummyModelWithBooleanListEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithBooleanListEvaluatable.class);
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
    void testSetOfBoolean(String serialized) throws Exception {
        DummyModelWithBooleanSetEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithBooleanSetEvaluatable.class);
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
    void testStringAsNull(String serialized) throws Exception {
        DummyModelWithStringEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithStringEvaluatable.class);
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
    void testEnumAsNull(String serialized) throws Exception {
        DummyModelWithEnumEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithEnumEvaluatable.class);
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
    void testBooleanAsNull(String serialized) throws Exception {
        DummyModelWithBooleanEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithBooleanEvaluatable.class);
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
    void testListStringAsNull(String serialized) throws Exception {
        DummyModelWithStringListEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithStringListEvaluatable.class);
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
    void testSetStringAsNull(String serialized) throws Exception {
        DummyModelWithStringSetEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithStringSetEvaluatable.class);
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
    void testListOfEnumAsNull(String serialized) throws Exception {
        DummyModelWithEnumListEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithEnumListEvaluatable.class);
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
    void testSetOfEnumAsNull(String serialized) throws Exception {
        DummyModelWithEnumSetEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithEnumSetEvaluatable.class);
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
    void testListOfBooleanAsNull(String serialized) throws Exception {
        DummyModelWithBooleanListEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithBooleanListEvaluatable.class);
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
    void testSetOfBooleanAsNull(String serialized) throws Exception {
        DummyModelWithBooleanSetEvaluatable deserialized = objectMapper
            .readValue(serialized, DummyModelWithBooleanSetEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

}
