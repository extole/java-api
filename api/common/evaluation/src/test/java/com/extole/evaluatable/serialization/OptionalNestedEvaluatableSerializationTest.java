package com.extole.evaluatable.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.ecma.Js2025BuildtimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsBuildtimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptBuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.evaluateable.spel.SpelBuildtimeEvaluatable;
import com.extole.evaluation.junit.MultipleValueSource;
import com.extole.evaluation.model.DummyContext;
import com.extole.evaluation.model.DummyModelWithOptionalNestedEvaluatable;

@SuppressWarnings("checkstyle:lineLength")
public class OptionalNestedEvaluatableSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    @Test
    public void testEvaluationNestedNull() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":null," +
            "\"build_in_evaluatable\":null," +
            "\"runtime_in_evaluatable\":null," +
            "\"evaluatable_in_build\":null," +
            "\"build_in_build\":null," +
            "\"runtime_in_build\":null," +
            "\"evaluatable_in_runtime\":null," +
            "\"build_in_runtime\":null," +
            "\"runtime_in_runtime\":null," +
            "\"optional_evaluatable\":null" +
            "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);

        assertFalse(getProvidedValueAsOptional(deserialized.getEvaluatableEvaluatable()).isPresent());
        assertFalse(getProvidedValueAsOptional(deserialized.getBuildInEvaluatable()).isPresent());
        assertFalse(getProvidedValueAsOptional(deserialized.getRuntimeInEvaluatable()).isPresent());
        assertFalse(getProvidedValueAsOptional(deserialized.getEvaluatableInBuild()).isPresent());
        assertFalse(getProvidedValueAsOptional(deserialized.getBuildInBuild()).isPresent());
        assertFalse(getProvidedValueAsOptional(deserialized.getRuntimeInBuild()).isPresent());
        assertFalse(getProvidedValueAsOptional(deserialized.getEvaluatableInRuntime()).isPresent());
        assertFalse(getProvidedValueAsOptional(deserialized.getBuildInRuntime()).isPresent());
        assertFalse(getProvidedValueAsOptional(deserialized.getRuntimeInRuntime()).isPresent());
        assertFalse(deserialized.getOptionalEvaluatable().isPresent());
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedProvided() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":[\"testValue\"]," +
            "\"build_in_evaluatable\":[\"testValue\"]," +
            "\"runtime_in_evaluatable\":[\"testValue\"]," +
            "\"evaluatable_in_build\":[\"testValue\"]," +
            "\"build_in_build\":[\"testValue\"]," +
            "\"runtime_in_build\":[\"testValue\"]," +
            "\"evaluatable_in_runtime\":[\"testValue\"]," +
            "\"build_in_runtime\":[\"testValue\"]," +
            "\"runtime_in_runtime\":[\"testValue\"]," +
            "\"optional_evaluatable\":[\"testValue\"]" +
            "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedJavascript() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":\"javascript@buildtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"build_in_evaluatable\":\"javascript@runtime:function(){ return \\\"javascript@buildtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"runtime_in_evaluatable\":\"javascript@buildtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"evaluatable_in_build\":\"javascript@buildtime:function(){ return ['testValue'];}()\"," +
            "\"build_in_build\":\"javascript@buildtime:function(){ return \\\"javascript@buildtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"runtime_in_build\":\"javascript@buildtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"evaluatable_in_runtime\":\"javascript@runtime:function(){ return ['testValue'];}()\"," +
            "\"build_in_runtime\":\"javascript@runtime:function(){ return \\\"javascript@buildtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\","
            +
            "\"optional_evaluatable\":\"javascript@buildtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\""
            +
            "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedSpel() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"spel@buildtime:'spel@runtime:{''testValue''}'\"," +
                "\"build_in_evaluatable\":\"spel@runtime:'spel@buildtime:{''testValue''}'\"," +
                "\"runtime_in_evaluatable\":\"spel@buildtime:'spel@runtime:{''testValue''}'\"," +
                "\"evaluatable_in_build\":\"spel@buildtime:{'testValue'}\"," +
                "\"build_in_build\":\"spel@buildtime:'spel@buildtime:{''testValue''}'\"," +
                "\"runtime_in_build\":\"spel@buildtime:'spel@runtime:{''testValue''}'\"," +
                "\"evaluatable_in_runtime\":\"spel@runtime:{'testValue'}\"," +
                "\"build_in_runtime\":\"spel@runtime:'spel@buildtime:{''testValue''}'\"," +
                "\"runtime_in_runtime\":\"spel@runtime:'spel@runtime:{''testValue''}'\"," +
                "\"optional_evaluatable\":\"spel@buildtime:'spel@runtime:{''testValue''}'\"" +
                "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedHandlebars() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"handlebars@buildtime:handlebars@runtime:['testValue']\"," +
                "\"build_in_evaluatable\":\"handlebars@runtime:handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_evaluatable\":\"handlebars@buildtime:handlebars@runtime:['testValue']\"," +
                "\"evaluatable_in_build\":\"handlebars@buildtime:['testValue']\"," +
                "\"build_in_build\":\"handlebars@buildtime:handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_build\":\"handlebars@buildtime:handlebars@runtime:['testValue']\"," +
                "\"evaluatable_in_runtime\":\"handlebars@runtime:['testValue']\"," +
                "\"build_in_runtime\":\"handlebars@runtime:handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:handlebars@runtime:['testValue']\"," +
                "\"optional_evaluatable\":\"handlebars@buildtime:handlebars@runtime:['testValue']\"" +
                "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedProvidedAsNull() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":null," +
            "\"build_in_evaluatable\":null," +
            "\"runtime_in_evaluatable\":null," +
            "\"evaluatable_in_build\":null," +
            "\"build_in_build\":null," +
            "\"runtime_in_build\":null," +
            "\"evaluatable_in_runtime\":null," +
            "\"build_in_runtime\":null," +
            "\"runtime_in_runtime\":null," +
            "\"optional_evaluatable\":null" +
            "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedProvidedAsProvidedWithNullElements() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":[null]," +
            "\"build_in_evaluatable\":[null]," +
            "\"runtime_in_evaluatable\":[null]," +
            "\"evaluatable_in_build\":[null]," +
            "\"build_in_build\":[null]," +
            "\"runtime_in_build\":[null]," +
            "\"evaluatable_in_runtime\":[null]," +
            "\"build_in_runtime\":[null]," +
            "\"runtime_in_runtime\":[null]," +
            "\"optional_evaluatable\":[null]" +
            "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedJavascriptAsNull() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":\"javascript@buildtime:function(){ return null;}()\"," +
            "\"build_in_evaluatable\":\"javascript@runtime:function(){ return null;}()\"," +
            "\"runtime_in_evaluatable\":\"javascript@buildtime:function(){ return null;}()\"," +
            "\"evaluatable_in_build\":\"javascript@buildtime:function(){ return null;}()\"," +
            "\"build_in_build\":\"javascript@buildtime:function(){ return null;}()\"," +
            "\"runtime_in_build\":\"javascript@buildtime:function(){ return null;}()\"," +
            "\"evaluatable_in_runtime\":\"javascript@runtime:function(){ return null;}()\"," +
            "\"build_in_runtime\":\"javascript@runtime:function(){ return null;}()\"," +
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return null;}()\"," +
            "\"optional_evaluatable\":\"javascript@runtime:function(){ return null;}()\"" +
            "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedJavascriptWithNullElements() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":\"javascript@buildtime:function(){ return [null];}()\"," +
            "\"build_in_evaluatable\":\"javascript@runtime:function(){ return [null];}()\"," +
            "\"runtime_in_evaluatable\":\"javascript@buildtime:function(){ return [null];}()\"," +
            "\"evaluatable_in_build\":\"javascript@buildtime:function(){ return [null];}()\"," +
            "\"build_in_build\":\"javascript@buildtime:function(){ return [null];}()\"," +
            "\"runtime_in_build\":\"javascript@buildtime:function(){ return [null];}()\"," +
            "\"evaluatable_in_runtime\":\"javascript@runtime:function(){ return [null];}()\"," +
            "\"build_in_runtime\":\"javascript@runtime:function(){ return [null];}()\"," +
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return [null];}()\"," +
            "\"optional_evaluatable\":\"javascript@runtime:function(){ return [null];}()\"" +
            "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedSpelAsNull() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"spel@buildtime:null\"," +
                "\"build_in_evaluatable\":\"spel@runtime:null\"," +
                "\"runtime_in_evaluatable\":\"spel@buildtime:null\"," +
                "\"evaluatable_in_build\":\"spel@buildtime:null\"," +
                "\"build_in_build\":\"spel@buildtime:null\"," +
                "\"runtime_in_build\":\"spel@buildtime:null\"," +
                "\"evaluatable_in_runtime\":\"spel@runtime:null\"," +
                "\"build_in_runtime\":\"spel@runtime:null\"," +
                "\"runtime_in_runtime\":\"spel@runtime:null\"," +
                "\"optional_evaluatable\":\"spel@buildtime:null\"" +
                "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedHandlebarsAsNull() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"handlebars@buildtime:#{null}\"," +
                "\"build_in_evaluatable\":\"handlebars@runtime:#{null}\"," +
                "\"runtime_in_evaluatable\":\"handlebars@buildtime:#{null}\"," +
                "\"evaluatable_in_build\":\"handlebars@buildtime:#{null}\"," +
                "\"build_in_build\":\"handlebars@buildtime:#{null}\"," +
                "\"runtime_in_build\":\"handlebars@buildtime:#{null}\"," +
                "\"evaluatable_in_runtime\":\"handlebars@runtime:#{null}\"," +
                "\"build_in_runtime\":\"handlebars@runtime:#{null}\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:#{null}\"," +
                "\"optional_evaluatable\":\"handlebars@buildtime:#{null}\"" +
                "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedSpelWithNullElements() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"spel@buildtime:{null}\"," +
                "\"build_in_evaluatable\":\"spel@runtime:{null}\"," +
                "\"runtime_in_evaluatable\":\"spel@buildtime:{null}\"," +
                "\"evaluatable_in_build\":\"spel@buildtime:{null}\"," +
                "\"build_in_build\":\"spel@buildtime:{null}\"," +
                "\"runtime_in_build\":\"spel@buildtime:{null}\"," +
                "\"evaluatable_in_runtime\":\"spel@runtime:{null}\"," +
                "\"build_in_runtime\":\"spel@runtime:{null}\"," +
                "\"runtime_in_runtime\":\"spel@runtime:{null}\"," +
                "\"optional_evaluatable\":\"spel@buildtime:{null}\"" +
                "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedHandlebarsWithNullElements() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"handlebars@buildtime:[null]\"," +
                "\"build_in_evaluatable\":\"handlebars@runtime:[null]\"," +
                "\"runtime_in_evaluatable\":\"handlebars@buildtime:[null]\"," +
                "\"evaluatable_in_build\":\"handlebars@buildtime:[null]\"," +
                "\"build_in_build\":\"handlebars@buildtime:[null]\"," +
                "\"runtime_in_build\":\"handlebars@buildtime:[null]\"," +
                "\"evaluatable_in_runtime\":\"handlebars@runtime:[null]\"," +
                "\"build_in_runtime\":\"handlebars@runtime:[null]\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:[null]\"," +
                "\"optional_evaluatable\":\"handlebars@buildtime:[null]\"" +
                "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedProvidedJavascript() throws Exception {
        String serialized = "{" +
            "\"evaluatable_evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"," +
            "\"build_in_evaluatable\":\"javascript@buildtime:function(){ return ['testValue'];}()\"," +
            "\"runtime_in_evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"," +
            "\"evaluatable_in_build\":\"javascript@runtime:function(){ return ['testValue'];}()\"," +
            "\"build_in_build\":\"javascript@buildtime:function(){ return ['testValue'];}()\"," +
            "\"runtime_in_build\":\"javascript@runtime:function(){ return ['testValue'];}()\"," +
            "\"evaluatable_in_runtime\":\"javascript@runtime:function(){ return ['testValue'];}()\"," +
            "\"build_in_runtime\":\"javascript@buildtime:function(){ return ['testValue'];}()\"," +
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return ['testValue'];}()\"," +
            "\"optional_evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"" +
            "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedProvidedSpel() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"spel@runtime:{'testValue'}\"," +
                "\"build_in_evaluatable\":\"spel@buildtime:{'testValue'}\"," +
                "\"runtime_in_evaluatable\":\"spel@runtime:{'testValue'}\"," +
                "\"evaluatable_in_build\":\"spel@runtime:{'testValue'}\"," +
                "\"build_in_build\":\"spel@buildtime:{'testValue'}\"," +
                "\"runtime_in_build\":\"spel@runtime:{'testValue'}\"," +
                "\"evaluatable_in_runtime\":\"spel@runtime:{'testValue'}\"," +
                "\"build_in_runtime\":\"spel@buildtime:{'testValue'}\"," +
                "\"runtime_in_runtime\":\"spel@runtime:{'testValue'}\"," +
                "\"optional_evaluatable\":\"spel@runtime:{'testValue'}\"" +
                "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @Test
    public void testEvaluationNestedProvidedHandlebars() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"handlebars@runtime:['testValue']\"," +
                "\"build_in_evaluatable\":\"handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_evaluatable\":\"handlebars@runtime:['testValue']\"," +
                "\"evaluatable_in_build\":\"handlebars@runtime:['testValue']\"," +
                "\"build_in_build\":\"handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_build\":\"handlebars@runtime:['testValue']\"," +
                "\"evaluatable_in_runtime\":\"handlebars@runtime:['testValue']\"," +
                "\"build_in_runtime\":\"handlebars@buildtime:['testValue']\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:['testValue']\"," +
                "\"optional_evaluatable\":\"handlebars@runtime:['testValue']\"" +
                "}";
        DummyModelWithOptionalNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithOptionalNestedEvaluatable.class);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"runtime_in_build\":[\"testValue\"]}",
        "{\"runtime_in_build\":\"javascript@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"runtime_in_build\":\"js2025@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"runtime_in_build\":\"spel@buildtime:{'testValue'}\"}",
        "{\"runtime_in_build\":\"handlebars@buildtime:['testValue']\"}",
        "{\"runtime_in_build\":\"javascript@runtime:function(){ return ['testValue'];}()\"}",
        "{\"runtime_in_build\":\"spel@runtime:{'testValue'}\"}",
        "{\"runtime_in_build\":\"handlebars@runtime:['testValue']\"}",
        "{\"runtime_in_build\":\"javascript@buildtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\"}",
        "{\"runtime_in_build\":\"js2025@buildtime:function(){ return \\\"js2025@runtime:function(){ return ['testValue'];}()\\\";}()\"}",
        "{\"runtime_in_build\":\"javascript@buildtime:\\\"javascript@runtime:function(){return ['testValue'];}()\\\"\"}",
        "{\"runtime_in_build\":\"js2025@buildtime:\\\"js2025@runtime:function(){return ['testValue'];}()\\\"\"}",
        "{\"runtime_in_build\":\"spel@buildtime:'spel@runtime:{''testValue''}'\"}",
        "{\"runtime_in_build\":\"handlebars@buildtime:handlebars@runtime:['testValue']\"}",
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            HandlebarsBuildtimeEvaluatable.class,
            Provided.class,
            Provided.class,
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            HandlebarsBuildtimeEvaluatable.class,
        })
    void testRuntimeInBuildtimeAllCombinations(Object... args) throws Exception {
        com.extole.evaluation.model.single.optional.DummyModelWithOptionalNestedEvaluatable deserialized =
            deserialize((String) args[0],
                com.extole.evaluation.model.single.optional.DummyModelWithOptionalNestedEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, Optional<RuntimeEvaluatable<DummyContext, List<String>>>> evaluatable =
            deserialized.getRuntimeInBuild();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(args[0]);
    }

    private <T> T deserialize(String serialized, Class<T> valueType) throws Exception {
        return objectMapper.readValue(serialized, valueType);
    }

    private static Optional getProvidedValueAsOptional(Evaluatable evaluatable) {
        return (Optional) ((Provided) evaluatable).getValue();
    }

}
