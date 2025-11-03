package com.extole.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.util.Collections;
import java.util.List;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.ecma.Js2025BuildtimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsBuildtimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsRuntimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptBuildtimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptRuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.evaluateable.spel.SpelBuildtimeEvaluatable;
import com.extole.evaluateable.spel.SpelRuntimeEvaluatable;
import com.extole.evaluation.junit.MultipleValueSource;
import com.extole.evaluation.model.DummyContext;
import com.extole.evaluation.model.DummyModelWithNestedEvaluatable;

@SuppressWarnings("checkstyle:lineLength")
public class NestedEvaluatableEvaluationTest {

    private static final List<String> TEST_VALUE_ARRAY = Lists.newArrayList("testValue");

    private final EvaluationService evaluationService =
        new EvaluationServiceImpl(new ExtoleMetricRegistry(new MetricRegistry()));
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

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
            "\"runtime_in_runtime\":[\"testValue\"]" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInRuntime().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInRuntime().getClass());

        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getEvaluatableEvaluatable()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getEvaluatableEvaluatable()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getBuildInEvaluatable()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getRuntimeInEvaluatable()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getEvaluatableInBuild()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getBuildInBuild()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getRuntimeInBuild()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getEvaluatableInRuntime()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getBuildInRuntime()).getClass());

        List<String> result = Collections.singletonList("testValue");
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableEvaluatable())));
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableEvaluatable())));
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getBuildInEvaluatable())));
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getRuntimeInEvaluatable())));
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableInBuild())));
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getBuildInBuild())));
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getRuntimeInBuild())));
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableInRuntime())));
        assertThat(result).isEqualTo(evaluate(evaluate(deserialized.getBuildInRuntime())));

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
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\";}()\""
            +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithNestedEvaluatable.class);

        assertJavascriptEvaluatableOfEvaluatable(deserialized);
        assertJavascriptBuildEvaluatableOfEvaluatable(deserialized);
        assertJavascriptRuntimeEvaluatableOfEvaluatable(deserialized);

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
                "\"runtime_in_runtime\":\"spel@runtime:'spel@runtime:{''testValue''}'\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertSpelEvaluatableOfEvaluatable(deserialized);
        assertSpelBuildEvaluatableOfEvaluatable(deserialized);
        assertSpelRuntimeEvaluatableOfEvaluatable(deserialized);
    }

    @Test
    public void testEvaluationNestedHandlebars() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"handlebars@buildtime:handlebars@runtime:[\\\"testValue\\\"]\"," +
                "\"build_in_evaluatable\":\"handlebars@runtime:handlebars@buildtime:[\\\"testValue\\\"]\"," +
                "\"runtime_in_evaluatable\":\"handlebars@buildtime:handlebars@runtime:[\\\"testValue\\\"]\"," +
                "\"evaluatable_in_build\":\"handlebars@buildtime:handlebars@runtime:[\\\"testValue\\\"]\"," +
                "\"build_in_build\":\"handlebars@buildtime:handlebars@buildtime:[\\\"testValue\\\"]\"," +
                "\"runtime_in_build\":\"handlebars@buildtime:handlebars@runtime:[\\\"testValue\\\"]\"," +
                "\"evaluatable_in_runtime\":\"handlebars@runtime:handlebars@runtime:[\\\"testValue\\\"]\"," +
                "\"build_in_runtime\":\"handlebars@runtime:handlebars@buildtime:[\\\"testValue\\\"]\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:handlebars@runtime:[\\\"testValue\\\"]\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertHandlebarsEvaluatableOfEvaluatable(deserialized);
        assertHandlebarsBuildEvaluatableOfEvaluatable(deserialized);
        assertHandlebarsRuntimeEvaluatableOfEvaluatable(deserialized);
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
            "\"runtime_in_runtime\":null" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInRuntime().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInRuntime().getClass());

        assertAllInnerEvaluatablesThrowEvaluationException(deserialized);

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
            "\"runtime_in_runtime\":[null]" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInRuntime().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInRuntime().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInRuntime().getClass());

        assertAllInnerEvaluatablesThrowEvaluationException(deserialized);

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
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return null;}()\"" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithNestedEvaluatable.class);
        assertAllInnerEvaluatablesThrowEvaluationException(deserialized);
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
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return [null];}()\"" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithNestedEvaluatable.class);
        assertAllInnerEvaluatablesThrowEvaluationException(deserialized);
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
                "\"runtime_in_runtime\":\"spel@runtime:null\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);

        assertAllInnerEvaluatablesThrowEvaluationException(deserialized);

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
                "\"runtime_in_runtime\":\"spel@runtime:{null}\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);

        assertAllInnerEvaluatablesThrowEvaluationException(deserialized);

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
                "\"build_in_runtime\":\"handlebars@runtime:#{null}'\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:#{null}\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);

        assertAllInnerEvaluatablesThrowEvaluationException(deserialized);

    }

    @Test
    public void testEvaluationNestedHandlebarsWithNullElements() throws Exception {
        String serialized =
            "{\"evaluatable_evaluatable\":\"handlebars@buildtime:handlebars@buildtime:[null]\"," +
                "\"build_in_evaluatable\":\"handlebars@runtime:handlebars@buildtime:[null]\"," +
                "\"runtime_in_evaluatable\":\"handlebars@buildtime:handlebars@buildtime:[null]\"," +
                "\"evaluatable_in_build\":\"handlebars@buildtime:handlebars@buildtime:[null]\"," +
                "\"build_in_build\":\"handlebars@buildtime:handlebars@buildtime:[null]\"," +
                "\"runtime_in_build\":\"handlebars@buildtime:handlebars@buildtime:[null]\"," +
                "\"evaluatable_in_runtime\":\"handlebars@runtime:handlebars@buildtime:[null]\"," +
                "\"build_in_runtime\":\"handlebars@runtime:handlebars@buildtime:[null]'\"," +
                "\"runtime_in_runtime\":\"handlebars@runtime:handlebars@buildtime:[null]\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedEvaluatable.class);

        assertAllInnerEvaluatablesThrowEvaluationException(deserialized);

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
            "\"runtime_in_runtime\":\"javascript@runtime:function(){ return ['testValue'];}()\"" +
            "}";
        DummyModelWithNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithNestedEvaluatable.class);

        assertThat(JavascriptRuntimeEvaluatable.class).isEqualTo(deserialized.getEvaluatableEvaluatable().getClass());
        assertThat(JavascriptBuildtimeEvaluatable.class).isEqualTo(deserialized.getBuildInEvaluatable().getClass());
        assertThat(JavascriptRuntimeEvaluatable.class).isEqualTo(deserialized.getRuntimeInEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(JavascriptBuildtimeEvaluatable.class).isEqualTo(deserialized.getBuildInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInBuild().getClass());
        assertThat(JavascriptRuntimeEvaluatable.class).isEqualTo(deserialized.getEvaluatableInRuntime().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInRuntime().getClass());
        assertThat(JavascriptRuntimeEvaluatable.class).isEqualTo(deserialized.getRuntimeInRuntime().getClass());

        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getEvaluatableEvaluatable()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getBuildInEvaluatable()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getRuntimeInEvaluatable()).getClass());
        assertThat(JavascriptRuntimeEvaluatable.class)
            .isEqualTo(evaluate(deserialized.getEvaluatableInBuild()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getBuildInBuild()).getClass());
        assertThat(JavascriptRuntimeEvaluatable.class).isEqualTo(evaluate(deserialized.getRuntimeInBuild()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getEvaluatableInRuntime()).getClass());
        assertThat(JavascriptBuildtimeEvaluatable.class)
            .isEqualTo(evaluate(deserialized.getBuildInRuntime()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getRuntimeInRuntime()).getClass());

        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableEvaluatable())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getBuildInEvaluatable())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getRuntimeInEvaluatable())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableInBuild())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getBuildInBuild())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getRuntimeInBuild())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableInRuntime())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getBuildInRuntime())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getRuntimeInRuntime())));

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
                "\"runtime_in_runtime\":\"spel@runtime:{'testValue'}\"" +
                "}";
        DummyModelWithNestedEvaluatable deserialized =
            deserialize(serialized, DummyModelWithNestedEvaluatable.class);

        assertThat(SpelRuntimeEvaluatable.class).isEqualTo(deserialized.getEvaluatableEvaluatable().getClass());
        assertThat(SpelBuildtimeEvaluatable.class).isEqualTo(deserialized.getBuildInEvaluatable().getClass());
        assertThat(SpelRuntimeEvaluatable.class).isEqualTo(deserialized.getRuntimeInEvaluatable().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getEvaluatableInBuild().getClass());
        assertThat(SpelBuildtimeEvaluatable.class).isEqualTo(deserialized.getBuildInBuild().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getRuntimeInBuild().getClass());
        assertThat(SpelRuntimeEvaluatable.class).isEqualTo(deserialized.getEvaluatableInRuntime().getClass());
        assertThat(Provided.class).isEqualTo(deserialized.getBuildInRuntime().getClass());
        assertThat(SpelRuntimeEvaluatable.class).isEqualTo(deserialized.getRuntimeInRuntime().getClass());

        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getEvaluatableEvaluatable()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getBuildInEvaluatable()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getRuntimeInEvaluatable()).getClass());
        assertThat(SpelRuntimeEvaluatable.class).isEqualTo(evaluate(deserialized.getEvaluatableInBuild()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getBuildInBuild()).getClass());
        assertThat(SpelRuntimeEvaluatable.class).isEqualTo(evaluate(deserialized.getRuntimeInBuild()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getEvaluatableInRuntime()).getClass());
        assertThat(SpelBuildtimeEvaluatable.class).isEqualTo(evaluate(deserialized.getBuildInRuntime()).getClass());
        assertThat(Provided.class).isEqualTo(evaluate(deserialized.getRuntimeInRuntime()).getClass());

        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableEvaluatable())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getBuildInEvaluatable())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getRuntimeInEvaluatable())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableInBuild())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getBuildInBuild())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getRuntimeInBuild())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getEvaluatableInRuntime())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getBuildInRuntime())));
        assertThat(TEST_VALUE_ARRAY).isEqualTo(evaluate(evaluate(deserialized.getRuntimeInRuntime())));

    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"runtime_in_build\":[\"testValue\"]}",
        "{\"runtime_in_build\":\"javascript@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"runtime_in_build\":\"js2025@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"runtime_in_build\":\"spel@buildtime:{'testValue'}\"}",
        "{\"runtime_in_build\":\"javascript@runtime:function(){ return ['testValue'];}()\"}",
        "{\"runtime_in_build\":\"spel@runtime:{'testValue'}\"}",
        "{\"runtime_in_build\":\"javascript@buildtime:function(){ return \\\"javascript@runtime:function(){ return ['testValue'];}()\\\""
            + ";}()\"}",
        "{\"runtime_in_build\":\"js2025@buildtime:function(){ return \\\"js2025@runtime:function(){ return ['testValue'];}()\\\""
            + ";}()\"}",
        "{\"runtime_in_build\":\"javascript@buildtime:\\\"javascript@runtime:function(){ return ['testValue'];}()\\\"\"}",
        "{\"runtime_in_build\":\"js2025@buildtime:\\\"js2025@runtime:function(){ return ['testValue'];}()\\\"\"}",
        "{\"runtime_in_build\":\"spel@buildtime:'spel@runtime:{''testValue''}'\"}",
        "{\"runtime_in_build\":\"handlebars@buildtime:handlebars@runtime:[\\\"testValue\\\"]\"}",
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
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
        BuildtimeEvaluatable<DummyContext, RuntimeEvaluatable<DummyContext, List<String>>> evaluatable =
            deserialize((String) args[0], DummyModelWithNestedEvaluatable.class).getRuntimeInBuild();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluate(evaluatable))).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertAllInnerEvaluatablesThrowEvaluationException(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        Evaluatable<DummyContext, List<String>> evaluatedEvaluatableEvaluatable =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedEvaluatableEvaluatable));
        Evaluatable<DummyContext, List<String>> evaluatedBuildInEvaluatable =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedBuildInEvaluatable));
        Evaluatable<DummyContext, List<String>> evaluatedRuntimeInEvaluatable =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedRuntimeInEvaluatable));
        Evaluatable<DummyContext, List<String>> evaluatedEvaluatableInBuild =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedEvaluatableInBuild));
        Evaluatable<DummyContext, List<String>> evaluatedBuildInBuild =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedBuildInBuild));
        Evaluatable<DummyContext, List<String>> evaluatedRuntimeInBuild =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedRuntimeInBuild));
        Evaluatable<DummyContext, List<String>> evaluatedEvaluatableInRuntime =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedEvaluatableInRuntime));
        Evaluatable<DummyContext, List<String>> evaluatedBuildInRuntime =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedBuildInRuntime));
        Evaluatable<DummyContext, List<String>> evaluatedRuntimeInRuntime =
            evaluate(deserialized.getEvaluatableEvaluatable());
        assertThrows(EvaluationException.class, () -> evaluate(evaluatedRuntimeInRuntime));
    }

    private void assertJavascriptEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableEvaluatable().getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableEvaluatable());
        assertThat(any.getClass()).isEqualTo(JavascriptRuntimeEvaluatable.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInEvaluatable().getClass()).isEqualTo(JavascriptRuntimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInEvaluatable());
        assertThat(build.getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInEvaluatable().getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInEvaluatable());
        assertThat(runtime.getClass()).isEqualTo(JavascriptRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertJavascriptBuildEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableInBuild().getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableInBuild());
        assertThat(any.getClass()).isEqualTo(Provided.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInBuild().getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInBuild());
        assertThat(build.getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInBuild().getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInBuild());
        assertThat(runtime.getClass()).isEqualTo(JavascriptRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertJavascriptRuntimeEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableInRuntime().getClass()).isEqualTo(JavascriptRuntimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableInRuntime());
        assertThat(any.getClass()).isEqualTo(Provided.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInRuntime().getClass()).isEqualTo(JavascriptRuntimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInRuntime());
        assertThat(build.getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInBuild().getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInBuild());
        assertThat(runtime.getClass()).isEqualTo(JavascriptRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertSpelEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableEvaluatable().getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableEvaluatable());
        assertThat(any.getClass()).isEqualTo(SpelRuntimeEvaluatable.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInEvaluatable().getClass()).isEqualTo(SpelRuntimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInEvaluatable());
        assertThat(build.getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInEvaluatable().getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInEvaluatable());
        assertThat(runtime.getClass()).isEqualTo(SpelRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertSpelBuildEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableInBuild().getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableInBuild());
        assertThat(any.getClass()).isEqualTo(Provided.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInBuild().getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInBuild());
        assertThat(build.getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInBuild().getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInBuild());
        assertThat(runtime.getClass()).isEqualTo(SpelRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertSpelRuntimeEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableInRuntime().getClass()).isEqualTo(SpelRuntimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableInRuntime());
        assertThat(any.getClass()).isEqualTo(Provided.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInRuntime().getClass()).isEqualTo(SpelRuntimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInRuntime());
        assertThat(build.getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInBuild().getClass()).isEqualTo(SpelBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInBuild());
        assertThat(runtime.getClass()).isEqualTo(SpelRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertHandlebarsEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableEvaluatable().getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableEvaluatable());
        assertThat(any.getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInEvaluatable().getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInEvaluatable());
        assertThat(build.getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInEvaluatable().getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInEvaluatable());
        assertThat(runtime.getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertHandlebarsBuildEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableInBuild().getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableInBuild());
        assertThat(any.getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInBuild().getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInBuild());
        assertThat(build.getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInBuild().getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInBuild());
        assertThat(runtime.getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private void assertHandlebarsRuntimeEvaluatableOfEvaluatable(DummyModelWithNestedEvaluatable deserialized)
        throws EvaluationException {
        assertThat(deserialized.getEvaluatableInRuntime().getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        Evaluatable<DummyContext, List<String>> any = evaluate(deserialized.getEvaluatableInRuntime());
        assertThat(any.getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        assertThat(evaluate(any)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getBuildInRuntime().getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        BuildtimeEvaluatable<DummyContext, List<String>> build = evaluate(deserialized.getBuildInRuntime());
        assertThat(build.getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        assertThat(evaluate(build)).isEqualTo(TEST_VALUE_ARRAY);

        assertThat(deserialized.getRuntimeInBuild().getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        RuntimeEvaluatable<DummyContext, List<String>> runtime = evaluate(deserialized.getRuntimeInBuild());
        assertThat(runtime.getClass()).isEqualTo(HandlebarsRuntimeEvaluatable.class);
        assertThat(evaluate(runtime)).isEqualTo(TEST_VALUE_ARRAY);
    }

    private <T> T evaluate(Evaluatable<DummyContext, T> evaluateable) throws EvaluationException {
        return evaluationService.evaluate(evaluateable, DummyContext.DUMMY_CONTEXT_SUPPLIER);
    }

    private <T> T deserialize(String serialized, Class<T> valueType) throws Exception {
        return objectMapper.readValue(serialized, valueType);
    }

}
