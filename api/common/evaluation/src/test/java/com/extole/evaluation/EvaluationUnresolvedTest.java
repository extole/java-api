package com.extole.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.util.Map;
import java.util.Optional;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.params.ParameterizedTest;

import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.ecma.Js2025BuildtimeEvaluatable;
import com.extole.evaluateable.ecma.Js2025RuntimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsBuildtimeEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsRuntimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptBuildtimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptRuntimeEvaluatable;
import com.extole.evaluateable.spel.SpelBuildtimeEvaluatable;
import com.extole.evaluateable.spel.SpelRuntimeEvaluatable;
import com.extole.evaluation.junit.MultipleValueSource;

class EvaluationUnresolvedTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    private final EvaluationService evaluationService =
        new EvaluationServiceImpl(new ExtoleMetricRegistry(new MetricRegistry()));

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime: function(){ return context.missing;} ()\"}",
        "{\"evaluatable\":\"js2025@buildtime: function(){ return context.missing;} ()\"}",
        "{\"evaluatable\":\"javascript@buildtime: context.missing\"}",
        "{\"evaluatable\":\"js2025@buildtime: context.missing\"}",
        "{\"evaluatable\":\"spel@buildtime: context.get('missing')\"}",
        "{\"evaluatable\":\"javascript@runtime: function(){ return context.missing;} ()\"}",
        "{\"evaluatable\":\"js2025@runtime: function(){ return context.missing;} ()\"}",
        "{\"evaluatable\":\"javascript@runtime: context.missing\"}",
        "{\"evaluatable\":\"js2025@runtime: context.missing\"}",
        "{\"evaluatable\":\"spel@runtime: context.get('missing')\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationWithAccessingContext(Object... args) throws Exception {
        Evaluatable<Map<Object, Object>, Optional<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.empty());
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"handlebars@buildtime:{{missing}}\"}",
        "{\"evaluatable\":\"handlebars@runtime:{{missing}}\"}",
    },
        classes = {
            HandlebarsBuildtimeEvaluatable.class,
            HandlebarsRuntimeEvaluatable.class
        })
    void testMissingVariable(Object... args) throws Exception {
        Evaluatable<Map<Object, Object>, Optional<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.of(""));
    }

    private <T> T evaluate(Evaluatable<Map<Object, Object>, T> evaluateable) throws EvaluationException {
        return evaluationService.evaluate(evaluateable, new LazyLoadingSupplier<>(() -> Map.of()));
    }

    private <T> T deserialize(String serialized, Class<T> valueType) throws Exception {
        return objectMapper.readValue(serialized, valueType);
    }

    private static final class DummyModelWithOptionalStringEvaluatable {

        private static final String JSON_EVALUATABLE = "evaluatable";
        private final Evaluatable<Map<Object, Object>, Optional<String>> evaluatable;

        @JsonCreator
        private DummyModelWithOptionalStringEvaluatable(
            @JsonProperty(JSON_EVALUATABLE) Evaluatable<Map<Object, Object>, Optional<String>> evaluatable) {
            this.evaluatable = evaluatable;
        }

        @JsonProperty(JSON_EVALUATABLE)
        public Evaluatable<Map<Object, Object>, Optional<String>> getEvaluatable() {
            return evaluatable;
        }

    }

}
