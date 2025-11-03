package com.extole.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.util.Map;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.ecma.Js2025BuildtimeEvaluatable;
import com.extole.evaluateable.ecma.Js2025InstalltimeEvaluatable;
import com.extole.evaluateable.ecma.Js2025RuntimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsBuildtimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsInstalltimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsRuntimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptBuildtimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptInstalltimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptRuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.evaluateable.spel.SpelBuildtimeEvaluatable;
import com.extole.evaluateable.spel.SpelInstalltimeEvaluatable;
import com.extole.evaluateable.spel.SpelRuntimeEvaluatable;
import com.extole.evaluation.junit.MultipleValueSource;
import com.extole.evaluation.model.DummyContext;
import com.extole.evaluation.model.single.DummyModelWithBooleanEvaluatableMapKey;
import com.extole.evaluation.model.single.DummyModelWithEvaluatableMapKey;

class EvaluatableMapKeyEvaluationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    private final EvaluationService evaluationService =
        new EvaluationServiceImpl(new ExtoleMetricRegistry(new MetricRegistry()));

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"testValue\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@buildtime:function(){ return \\\"testValue\\\";}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@buildtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@buildtime:'testValue'\": \"bla\"}}",
        "{\"evaluatables\":{\"handlebars@buildtime:testValue\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@runtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@runtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@runtime:'testValue'\": \"bla\"}}",
        "{\"evaluatables\":{\"handlebars@runtime:testValue\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@installtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@installtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@installtime:'testValue'\": \"bla\"}}",
        "{\"evaluatables\":{\"handlebars@installtime:testValue\": \"bla\"}}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            HandlebarsBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class,
            HandlebarsRuntimeEvaluatable.class,
            JavascriptInstalltimeEvaluatable.class,
            Js2025InstalltimeEvaluatable.class,
            SpelInstalltimeEvaluatable.class,
            HandlebarsInstalltimeEvaluatable.class
        })
    void testEvaluationOfEvaluatableThatReturnsString(Object... args) throws Exception {
        Map<Evaluatable<DummyContext, String>, Object> map =
            deserialize((String) args[0], DummyModelWithEvaluatableMapKey.class).getEvaluatables();

        assertThat(map).hasSize(1);
        assertThat(map.keySet().iterator().next())
            .satisfies(evaluatable -> {
                assertThat(evaluatable.getClass()).isEqualTo(args[1]);
                assertThat(evaluate(evaluatable)).isEqualTo("testValue");
            });
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"testValue\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@buildtime:function(){ return \\\"testValue\\\";}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@buildtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@buildtime:'testValue'\": \"bla\"}}",
        "{\"evaluatables\":{\"handlebars@buildtime:testValue\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@runtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@runtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@runtime:'testValue'\": \"bla\"}}",
        "{\"evaluatables\":{\"handlebars@runtime:testValue\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@installtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@installtime:function(){ return 'testValue';}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@installtime:'testValue'\": \"bla\"}}",
        "{\"evaluatables\":{\"handlebars@installtime:testValue\": \"bla\"}}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            HandlebarsBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class,
            HandlebarsRuntimeEvaluatable.class,
            JavascriptInstalltimeEvaluatable.class,
            Js2025InstalltimeEvaluatable.class,
            SpelInstalltimeEvaluatable.class,
            HandlebarsInstalltimeEvaluatable.class
        })
    void testEvaluationOfEvaluatableThatReturnsStringWhenExpectedTypeIsNotString(Object... args) throws Exception {
        Map<Evaluatable<DummyContext, Boolean>, Object> map =
            deserialize((String) args[0], DummyModelWithBooleanEvaluatableMapKey.class).getEvaluatables();

        assertThat(map).hasSize(1);
        assertThat(map.keySet().iterator().next())
            .satisfies(evaluatable -> {
                assertThat(evaluatable.getClass()).isEqualTo(args[1]);
                Object evaluatedValue = evaluate(evaluatable);
                assertThat(evaluatedValue).isEqualTo("testValue");
            });
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"javascript@buildtime:function(){ return true;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@buildtime:function(){ return true;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@buildtime:true\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@runtime:function(){ return true;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@runtime:function(){ return true;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@runtime:true\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@installtime:function(){ return true;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@installtime:function(){ return true;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@installtime:true\": \"bla\"}}",
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class,
            JavascriptInstalltimeEvaluatable.class,
            Js2025InstalltimeEvaluatable.class,
            SpelInstalltimeEvaluatable.class,
        })
    void testEvaluationOfEvaluatableThatReturnsNonStringObjectThrowsException(Object... args) throws Exception {
        Map<Evaluatable<DummyContext, Boolean>, Object> map =
            deserialize((String) args[0], DummyModelWithBooleanEvaluatableMapKey.class).getEvaluatables();

        assertThat(map).hasSize(1);
        Evaluatable<DummyContext, Boolean> key = map.keySet().iterator().next();
        assertThat(key.getClass()).isEqualTo(args[1]);

        assertThrows(Exception.class, () -> evaluate(key));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"javascript@buildtime:function(){ return 0.0;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@buildtime:function(){ return 0.0;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@buildtime:0.0\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@runtime:function(){ return 0.0;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@runtime:function(){ return 0.0;}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@runtime:0.0\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@buildtime:function(){ return [0.0];}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@buildtime:function(){ return [0.0];}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@buildtime:{0.0}\": \"bla\"}}",
        "{\"evaluatables\":{\"javascript@runtime:function(){ return [0.0];}()\": \"bla\"}}",
        "{\"evaluatables\":{\"js2025@runtime:function(){ return [0.0];}()\": \"bla\"}}",
        "{\"evaluatables\":{\"spel@runtime:{0.0}\": \"bla\"}}",
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationOfEvaluatableThatReturnsObjectWithWrongTypeThrowsException(Object... args) throws Exception {
        Map<Evaluatable<DummyContext, String>, Object> map =
            deserialize((String) args[0], DummyModelWithEvaluatableMapKey.class).getEvaluatables();

        assertThat(map).hasSize(1);

        Evaluatable<DummyContext, String> key = map.keySet().iterator().next();
        assertThat(key.getClass()).isEqualTo(args[1]);

        EvaluationException exception = assertThrows(EvaluationException.class, () -> evaluate(key));
        assertThat(MismatchedInputException.class).isEqualTo(exception.getCause().getClass());
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"handlebars@buildtime:true\": \"bla\"}}",
        "{\"evaluatables\":{\"handlebars@runtime:true\": \"bla\"}}",
        "{\"evaluatables\":{\"handlebars@installtime:true\": \"bla\"}}"
    },
        classes = {
            HandlebarsBuildtimeEvaluatable.class,
            HandlebarsRuntimeEvaluatable.class,
            HandlebarsInstalltimeEvaluatable.class
        })
    void testEvaluationOfHandlebarsEvaluatableThatReturnsNonStringObject(Object... args) throws Exception {
        Map<Evaluatable<DummyContext, Boolean>, Object> map =
            deserialize((String) args[0], DummyModelWithBooleanEvaluatableMapKey.class).getEvaluatables();

        assertThat(map).hasSize(1);
        Evaluatable<DummyContext, Boolean> key = map.keySet().iterator().next();
        assertThat(key.getClass()).isEqualTo(args[1]);

        Object evaluated = evaluate(key);
        assertThat(evaluated).isEqualTo("true");
    }

    @Test
    void testSerializeEvaluatableThatReturnsString() throws Exception {
        DummyModelWithEvaluatableMapKey dummyValue =
            new DummyModelWithEvaluatableMapKey(
                ImmutableMap.of(
                    Provided.of("testKey"), "testValue"));

        String serializedEvaluatable = objectMapper.writeValueAsString(dummyValue);
        assertThat(serializedEvaluatable).isEqualTo("{\"evaluatables\":{\"testKey\":\"testValue\"}}");
    }

    @Test
    void testEvaluatableGetsStringifiedDuringSerializationIfNotOfStringType() throws Exception {
        DummyModelWithBooleanEvaluatableMapKey dummyValue =
            new DummyModelWithBooleanEvaluatableMapKey(
                ImmutableMap.of(
                    Provided.booleanTrue(), "test"));

        String serializedEvaluatable = objectMapper.writeValueAsString(dummyValue);
        assertThat(serializedEvaluatable).isEqualTo("{\"evaluatables\":{\"true\":\"test\"}}");
    }

    private <T> T evaluate(Evaluatable<DummyContext, T> evaluateable) throws EvaluationException {
        return evaluationService.evaluate(evaluateable, DummyContext.DUMMY_CONTEXT_SUPPLIER);
    }

    private <T> T deserialize(String serialized, Class<T> valueType) throws Exception {
        return objectMapper.readValue(serialized, valueType);
    }

}
