package com.extole.evaluatable.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;

import com.extole.evaluateable.provided.Provided;
import com.extole.evaluation.model.DummyModelWithNestedEvaluatable;

@SuppressWarnings("checkstyle:lineLength")
public class NestedEvaluatableSerializationTest {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    @Test
    public void testNestedEvaluatable() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":[\"testValue\"]," +
            "\"build_in_evaluatable\":[\"testValue\"]," +
            "\"runtime_in_evaluatable\":[\"testValue\"]," +
            "\"evaluatable_in_build\":[\"testValue\"]," +
            "\"build_in_build\":[\"testValue\"]," +
            "\"runtime_in_build\":[\"testValue\"]," +
            "\"evaluatable_in_runtime\":[\"testValue\"]," +
            "\"build_in_runtime\":[\"testValue\"]," +
            "\"runtime_in_runtime\":[\"testValue\"]" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);

    }

    @Test
    public void testNestedSpelEvaluatable() throws Exception {
        String serialized =
            "{" +
                "\"evaluatable_evaluatable\":[\"testValue\"]," +
                "\"build_in_evaluatable\":\"spel@runtime:'spel@buildtime:{''testValue''}'\"," +
                "\"runtime_in_evaluatable\":\"spel@buildtime:'spel@runtime:{''testValue''}'\"," +
                "\"evaluatable_in_build\":\"spel@buildtime:'[''testValue'']'\"," +
                "\"build_in_build\":\"spel@buildtime:'spel@buildtime:{''testValue''}'\"," +
                "\"runtime_in_build\":\"spel@runtime:'spel@runtime:{''testValue''}'\"," +
                "\"evaluatable_in_runtime\":\"spel@runtime:'[''testValue'']'\"," +
                "\"build_in_runtime\":\"spel@runtime:'spel@buildtime:{''testValue''}'\"," +
                "\"runtime_in_runtime\":\"spel@runtime:'spel@runtime:{''testValue''}'\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);

    }

    @Test
    public void testNestedHandlebarsEvaluatable() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":[\"testValue\"]," +
                "\"build_in_evaluatable\":\"handlebars@runtime:handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_evaluatable\":\"handlebars@buildtime:handlebars@runtime:['testValue']\"," +
                "\"evaluatable_in_build\":\"handlebars@buildtime:['testValue']\"," +
                "\"build_in_build\":\"handlebars@buildtime:handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_build\":\"handlebars@buildtime:handlebars@runtime:['testValue']\"," +
                "\"evaluatable_in_runtime\":\"handlebars@runtime:['testValue']\"," +
                "\"build_in_runtime\":\"handlebars@runtime:handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:handlebars@runtime:['testValue']\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);

    }

    @Test
    public void testNestedJavascriptEvaluatable() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":[\"testValue\"]," +
            "\"build_in_evaluatable\":\"javascript@runtime:function(){ return \\\"javascript@buildtimetime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"runtime_in_evaluatable\":\"javascript@buildtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"evaluatable_in_build\":\"javascript@buildtime:function(){ return ['testValue'];}()\"," +
            "\"build_in_build\":\"javascript@buildtime:function(){ return \\\"javascript@buildtimetime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"runtime_in_build\":\"javascript@runtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"evaluatable_in_runtime\":\"javascript@runtime:function(){ return ['testValue'];}()\"," +
            "\"build_in_runtime\":\"javascript@runtime:function(){ return \\\"javascript@buildtimetime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\""
            +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);

    }

    @Test
    public void testNestedEvaluatableAsNull() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":null," +
            "\"build_in_evaluatable\":null," +
            "\"runtime_in_evaluatable\":null," +
            "\"evaluatable_in_build\":null," +
            "\"build_in_build\":null," +
            "\"runtime_in_build\":null," +
            "\"evaluatable_in_runtime\":null," +
            "\"build_in_runtime\":null," +
            "\"runtime_in_runtime\":null" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);

    }

    @Test
    public void testNestedSpelEvaluatableAsNull() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":null," +
                "\"build_in_evaluatable\":\"spel@runtime:null\"," +
                "\"runtime_in_evaluatable\":\"spel@buildtime:null\"," +
                "\"evaluatable_in_build\":\"spel@buildtime:null\"," +
                "\"build_in_build\":\"spel@buildtime:null\"," +
                "\"runtime_in_build\":\"spel@runtime:null\"," +
                "\"evaluatable_in_runtime\":\"spel@runtime:null\"," +
                "\"build_in_runtime\":\"spel@runtime:null\"," +
                "\"runtime_in_runtime\":\"spel@runtime:null\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);

    }

    @Test
    public void testNestedHandlebarsEvaluatableAsNull() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":null," +
                "\"build_in_evaluatable\":\"handlebars@runtime:#{null}\"," +
                "\"runtime_in_evaluatable\":\"handlebars@buildtime:#{null}\"," +
                "\"evaluatable_in_build\":\"handlebars@buildtime:#{null}\"," +
                "\"build_in_build\":\"handlebars@buildtime:#{null}\"," +
                "\"runtime_in_build\":\"handlebars@runtime:#{null}\"," +
                "\"evaluatable_in_runtime\":\"handlebars@runtime:#{null}\"," +
                "\"build_in_runtime\":\"handlebars@runtime:#{null}\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:#{null}\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);

    }

    @Test
    public void testNestedJavascriptEvaluatableAsNull() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":[\"testValue\"]," +
            "\"build_in_evaluatable\":\"javascript@runtime:function(){ return null;}()\"," +
            "\"runtime_in_evaluatable\":\"javascript@buildtime:function(){ return null;}()\"," +
            "\"evaluatable_in_build\":\"javascript@buildtime:function(){ return null;}()\"," +
            "\"build_in_build\":\"javascript@buildtime:function(){ return null;}()\"," +
            "\"runtime_in_build\":\"javascript@runtime:function(){ return null;}()\"," +
            "\"evaluatable_in_runtime\":\"javascript@runtime:function(){ return null;}()\"," +
            "\"build_in_runtime\":\"javascript@runtime:function(){ return null;}()\"," +
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return null;}()\"" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);

    }

}
