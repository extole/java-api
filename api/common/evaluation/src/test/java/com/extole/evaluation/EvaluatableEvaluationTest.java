package com.extole.evaluation;

import static com.extole.evaluation.model.collection.set.DummyModelWithEnumSetEvaluatable.DummyEnum.TEST_VALUE;
import static com.extole.evaluation.model.collection.set.DummyModelWithEnumSetEvaluatable.DummyEnum.TEST_VALUE2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import com.extole.evaluation.model.collection.list.DummyModelWithBooleanListEvaluatable;
import com.extole.evaluation.model.collection.list.DummyModelWithEnumListEvaluatable;
import com.extole.evaluation.model.collection.list.DummyModelWithEnumListEvaluatable.DummyEnum;
import com.extole.evaluation.model.collection.list.DummyModelWithStringListEvaluatable;
import com.extole.evaluation.model.collection.set.DummyModelWithBooleanSetEvaluatable;
import com.extole.evaluation.model.collection.set.DummyModelWithEnumSetEvaluatable;
import com.extole.evaluation.model.collection.set.DummyModelWithStringSetEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithBooleanEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithEnumEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithIdEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithMapOfEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithStringEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithVoidEvaluatable;
import com.extole.id.Id;

class EvaluatableEvaluationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    private final EvaluationService evaluationService =
        new EvaluationServiceImpl(new ExtoleMetricRegistry(new MetricRegistry()));

    @Test
    void testEvaluationEvaluatableWhichUseJavaType() {
        Evaluatable<DummyContext, String> runtimeEvaluatable =
            SpelRuntimeEvaluatable.of("T(javal.ang.String).valueOf(123)", SimpleType.constructUnsafe(String.class));
        Evaluatable<DummyContext, String> buildtimeEvaluatable =
            SpelBuildtimeEvaluatable.of("T(javal.ang.String).valueOf(234)", SimpleType.constructUnsafe(String.class));

        EvaluationException e1 = assertThrows(EvaluationException.class,
            () -> evaluate(runtimeEvaluatable));
        assertThat(e1.getMessage()).isEqualTo("Failed to evaluate expression T(javal.ang.String).valueOf(123)");

        EvaluationException e2 = assertThrows(EvaluationException.class,
            () -> evaluate(buildtimeEvaluatable));
        assertThat(e2.getMessage()).isEqualTo("Failed to evaluate expression T(javal.ang.String).valueOf(234)");
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"testValue\"}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@buildtime:'testValue'\"}",
        "{\"evaluatable\":\"handlebars@buildtime:testValue\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@runtime:'testValue'\"}",
        "{\"evaluatable\":\"handlebars@runtime:testValue\"}",
        "{\"evaluatable\":\"javascript@installtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@installtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@installtime:'testValue'\"}",
        "{\"evaluatable\":\"handlebars@installtime:testValue\"}"
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
    void testEvaluationEvaluatableString(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo("testValue");
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"TEST_VALUE\"}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"spel@buildtime:'TEST_VALUE'\"}",
        "{\"evaluatable\":\"handlebars@buildtime:TEST_VALUE\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"spel@runtime:'TEST_VALUE'\"}",
        "{\"evaluatable\":\"handlebars@runtime:TEST_VALUE\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, DummyModelWithEnumEvaluatable.DummyEnum> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(DummyModelWithEnumEvaluatable.DummyEnum.TEST_VALUE);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":true}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:true\"}",
        "{\"evaluatable\":\"handlebars@buildtime:true\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"spel@runtime:true\"}",
        "{\"evaluatable\":\"handlebars@runtime:true\"}"
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, Boolean> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[\"testValue\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'testValue'}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:[\\\"testValue\\\"]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'testValue'}\"}",
        "{\"evaluatable\":\"handlebars@runtime:[\\\"testValue\\\"]\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListString(Object... args) throws Exception {
        Evaluatable<DummyContext, List<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithStringListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Lists.newArrayList("testValue"));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[\"testValue\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'testValue'}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:[\\\"testValue\\\"]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'testValue'}\"}",
        "{\"evaluatable\":\"handlebars@runtime:[\\\"testValue\\\"]\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetString(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithStringSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Sets.newHashSet("testValue"));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[\"TEST_VALUE\",\"TEST_VALUE2\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:[\\\"TEST_VALUE\\\", \\\"TEST_VALUE2\\\"]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}",
        "{\"evaluatable\":\"handlebars@runtime:[\\\"TEST_VALUE\\\", \\\"TEST_VALUE2\\\"]\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListOfEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, List<DummyEnum>> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(ImmutableList.of(DummyEnum.TEST_VALUE, DummyEnum.TEST_VALUE2));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[\"TEST_VALUE\",\"TEST_VALUE2\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:[\\\"TEST_VALUE\\\", \\\"TEST_VALUE2\\\"]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}",
        "{\"evaluatable\":\"handlebars@runtime:[\\\"TEST_VALUE\\\", \\\"TEST_VALUE2\\\"]\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetOfEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<DummyModelWithEnumSetEvaluatable.DummyEnum>> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(ImmutableSet.of(TEST_VALUE, TEST_VALUE2));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[true,false]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{true, false}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:[true, false]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{true, false}\"}",
        "{\"evaluatable\":\"handlebars@runtime:[true, false]\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListOfBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, List<Boolean>> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Lists.newArrayList(Boolean.TRUE, Boolean.FALSE));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[true,false]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{true, false}\"}",
        "{\"evaluatable\":\"handlebars@buildtime:[true, false]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{true, false}\"}",
        "{\"evaluatable\":\"handlebars@runtime:[true, false]\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetOfBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<Boolean>> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Sets.newHashSet(Boolean.TRUE, Boolean.FALSE));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"testValue\"}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@buildtime:'testValue'\"}",
        "{\"evaluatable\":\"handlebars@buildtime:testValue\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@runtime:'testValue'\"}",
        "{\"evaluatable\":\"handlebars@runtime:testValue\"}"
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableId(Object... args) throws Exception {
        Evaluatable<DummyContext, Id<?>> evaluatable =
            deserialize((String) args[0], DummyModelWithIdEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable).getValue()).isEqualTo("testValue");
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"properties\" : {} }}",
        "{\"evaluatables\":{\"properties\" : \"javascript@buildtime:function(){ return {};}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"js2025@buildtime:function(){ return {};}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"spel@buildtime:{:}\" }}",
        "{\"evaluatables\":{\"properties\" : \"handlebars@buildtime:{}\" }}",
        "{\"evaluatables\":{\"properties\" : \"javascript@runtime:function(){ return new Object();}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"js2025@runtime:function(){ return new Object();}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"spel@runtime:{:}\" }}",
        "{\"evaluatables\":{\"properties\" : \"handlebars@runtime:{}\" }}"
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEmptyObject(Object... args) throws Exception {
        Map<String, Evaluatable<DummyContext, Object>> evaluatables =
            deserialize((String) args[0], DummyModelWithMapOfEvaluatable.class).getEvaluatables();
        assertThat(evaluatables.size()).isEqualTo(1);
        assertThat(evaluatables.get("properties").getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatables.get("properties"))).isEqualTo(Collections.emptyMap());
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: #{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime: #{null}\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableStringAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: #{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime: #{null}\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableEnumAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, DummyModelWithEnumEvaluatable.DummyEnum> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: #{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime: #{null}\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableBooleanAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Boolean> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: #{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime: #{null}\"}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableIdAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Id<?>> evaluatable =
            deserialize((String) args[0], DummyModelWithIdEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"properties\" : null }}",
        "{\"evaluatables\":{\"properties\" : \"javascript@buildtime:function(){ return null;}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"js2025@buildtime:function(){ return null;}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"spel@buildtime:null\" }}",
        "{\"evaluatables\":{\"properties\" : \"handlebars@buildtime:#{null}\" }}",
        "{\"evaluatables\":{\"properties\" : \"javascript@runtime:function(){ return null;}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"js2025@runtime:function(){ return null;}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"spel@runtime:null\" }}",
        "{\"evaluatables\":{\"properties\" : \"handlebars@runtime:#{null}\" }}",
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
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationEmptyObjectAsNull(Object... args) throws Exception {
        Map<String, Evaluatable<DummyContext, Object>> evaluatables =
            deserialize((String) args[0], DummyModelWithMapOfEvaluatable.class).getEvaluatables();
        assertThat(evaluatables.size()).isEqualTo(1);
        assertThat(evaluatables.get("properties").getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatables.get("properties")));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"handlebars@buildtime: #{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}",
        "{\"evaluatable\":\"handlebars@runtime: #{null}\"}",
        "{\"evaluatable\": [null]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [null];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [null];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{null}\"}",
        "{\"evaluatable\":\"handlebars@buildtime: [null]\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [null];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [null];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{null}\"}",
        "{\"evaluatable\":\"handlebars@runtime: [null]\"}",
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
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            HandlebarsBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class,
            HandlebarsRuntimeEvaluatable.class
        })

    void testEvaluationEvaluatableReturningNullsAndNullElements(Object... args) throws Exception {
        Evaluatable<DummyContext, List<String>> stringListEvaluatable =
            deserialize((String) args[0], DummyModelWithStringListEvaluatable.class).getEvaluatable();
        assertThat(stringListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(stringListEvaluatable));

        Evaluatable<DummyContext, Set<String>> stringSetEvalutable =
            deserialize((String) args[0], DummyModelWithStringSetEvaluatable.class).getEvaluatable();
        assertThat(stringSetEvalutable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(stringSetEvalutable));

        Evaluatable<DummyContext, List<DummyEnum>> enumListEvaluatable =
            deserialize((String) args[0], DummyModelWithEnumListEvaluatable.class).getEvaluatable();
        assertThat(enumListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(enumListEvaluatable));

        Evaluatable<DummyContext, Set<DummyModelWithEnumSetEvaluatable.DummyEnum>> enumSetEvaluatable =
            deserialize((String) args[0], DummyModelWithEnumSetEvaluatable.class).getEvaluatable();
        assertThat(enumSetEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(enumSetEvaluatable));

        Evaluatable<DummyContext, List<Boolean>> booleanListEvaluatable =
            deserialize((String) args[0], DummyModelWithBooleanListEvaluatable.class).getEvaluatable();
        assertThat(booleanListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(booleanListEvaluatable));

        Evaluatable<DummyContext, Set<Boolean>> booleanSetEvaluatable =
            deserialize((String) args[0], DummyModelWithBooleanSetEvaluatable.class).getEvaluatable();
        assertThat(booleanSetEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(booleanSetEvaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime: function(){ return context.dummyText;} ()\"}",
        "{\"evaluatable\":\"js2025@buildtime: function(){ return context.getDummyText();} ()\"}",
        "{\"evaluatable\":\"javascript@buildtime: context.dummyText\"}",
        "{\"evaluatable\":\"js2025@buildtime: context.getDummyText()\"}",
        "{\"evaluatable\":\"spel@buildtime: context.dummyText\"}",
        "{\"evaluatable\":\"handlebars@buildtime:{{dummyText}}\"}",
        "{\"evaluatable\":\"javascript@runtime: function(){ return context.dummyText;} ()\"}",
        "{\"evaluatable\":\"js2025@runtime: function(){ return context.getDummyText();} ()\"}",
        "{\"evaluatable\":\"javascript@runtime: context.dummyText\"}",
        "{\"evaluatable\":\"js2025@runtime: context.getDummyText()\"}",
        "{\"evaluatable\":\"spel@runtime: context.dummyText\"}",
        "{\"evaluatable\":\"handlebars@runtime:{{dummyText}}\"}",
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            HandlebarsBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class,
            HandlebarsRuntimeEvaluatable.class
        })
    void testEvaluationWithAccessingContext(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo("dummy text");
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"handlebars@runtime:{{EMPTY}}\"}",
        "{\"evaluatable\":\"spel@runtime:''\"}",
        "{\"evaluatable\":\"javascript@runtime:''\"}",
        "{\"evaluatable\":\"js2025@runtime:''\"}",
        "{\"evaluatable\":\"handlebars@buildtime:{{EMPTY}}\"}"
    },
        classes = {
            HandlebarsRuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            HandlebarsBuildtimeEvaluatable.class
        })
    void testEvaluationToEmptyString(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo("");
    }

    @Test
    void testEvaluationToHandlebarsRuntimeEmpty() throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize("{\"evaluatable\":\"javascript@buildtime:'handlebars@runtime:{{EMPTY}}'\"}",
                DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(JavascriptBuildtimeEvaluatable.class);
        assertThat(evaluate(evaluatable)).isEqualTo("handlebars@runtime:{{EMPTY}}");
    }

    @Test
    void testEvaluationToSpelRuntimeEmptyString() throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize("{\"evaluatable\":\"handlebars@buildtime:spel@runtime:''\"}",
                DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        assertThat(evaluate(evaluatable)).isEqualTo("spel@runtime:''");
    }

    @Test
    void testEvaluationToHandlebarsBuildtimeEmpty() throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize("{\"evaluatable\":\"javascript@runtime:'handlebars@buildtime:{{EMPTY}}'\"}",
                DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(JavascriptRuntimeEvaluatable.class);
        assertThat(evaluate(evaluatable)).isEqualTo("handlebars@buildtime:{{EMPTY}}");
    }

    @Test
    void testEvaluationToEmptyStringWithSingleQuotes() throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize("{\"evaluatable\":\"handlebars@buildtime:'{{EMPTY}}'\"}", DummyModelWithStringEvaluatable.class)
                .getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(HandlebarsBuildtimeEvaluatable.class);
        assertThat(evaluate(evaluatable)).isEqualTo("''");
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@runtime:handlebars@buildtime:'{{EMPTY}}'\"}"
    },
        classes = {
            JavascriptRuntimeEvaluatable.class
        })
    void testEvaluationFailsWhenSubexpressionIsHandlebarsEmptyWithSingleQuotes(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(EvaluationException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime: dummyText;\"}",
        "{\"evaluatable\":\"spel@buildtime: dummyText\"}",
        "{\"evaluatable\":\"javascript@runtime: dummyText;\"}",
        "{\"evaluatable\":\"spel@runtime: dummyText\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationFailsWhenAccessingPropertiesWithoutContextPrefix(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(EvaluationException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime: context.dummyText;\"}",
        "{\"evaluatable\":\"js2025@buildtime: context.dummyText;\"}",
        "{\"evaluatable\":\"spel@buildtime: context.dummyText\"}",
        "{\"evaluatable\":\"handlebars@buildtime:{{dummyText}}\"}",
        "{\"evaluatable\":\"javascript@runtime: context.dummyText;\"}",
        "{\"evaluatable\":\"js2025@runtime: context.dummyText;\"}",
        "{\"evaluatable\":\"spel@runtime: context.dummyText\"}",
        "{\"evaluatable\":\"handlebars@runtime:{{dummyText}}\"}",
        "{\"evaluatable\":\"javascript@installtime: context.dummyText;\"}",
        "{\"evaluatable\":\"js2025@installtime: context.dummyText;\"}",
        "{\"evaluatable\":\"spel@installtime: context.dummyText\"}",
        "{\"evaluatable\":\"handlebars@installtime:{{dummyText}}\"}",
    },
        classes = {
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
    void testEvaluationReturnsNullWhenReturnTypeIsVoid(Object... args) throws Exception {
        Evaluatable<DummyContext, Void> evaluatable =
            deserialize((String) args[0], DummyModelWithVoidEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertNull(evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){ return 0.0;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return 0.0;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:0.0\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return 0.0;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return 0.0;}()\"}",
        "{\"evaluatable\":\"spel@runtime:0.0\"}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [0.0];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [0.0];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{0.0}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [0.0];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [0.0];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{0.0}\"}",
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
    void testEvaluationEvaluatableWrongType(Object... args) throws Exception {
        Evaluatable<DummyContext, Boolean> booleanEvaluatable =
            deserialize((String) args[0], DummyModelWithBooleanEvaluatable.class).getEvaluatable();
        assertThat(booleanEvaluatable.getClass()).isEqualTo(args[1]);
        EvaluationException evaluationException =
            assertThrows(EvaluationException.class, () -> evaluate(booleanEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, DummyModelWithEnumEvaluatable.DummyEnum> enumEvaluatable =
            deserialize((String) args[0], DummyModelWithEnumEvaluatable.class).getEvaluatable();
        assertThat(enumEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(enumEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, String> stringEvaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(stringEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(stringEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, List<String>> stringListEvaluatable =
            deserialize((String) args[0], DummyModelWithStringListEvaluatable.class).getEvaluatable();
        assertThat(stringListEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(stringListEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, List<DummyEnum>> enumListEvaluatable =
            deserialize((String) args[0], DummyModelWithEnumListEvaluatable.class).getEvaluatable();
        assertThat(enumListEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(enumListEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, List<Boolean>> booleanListEvaluatable =
            deserialize((String) args[0], DummyModelWithBooleanListEvaluatable.class).getEvaluatable();
        assertThat(booleanListEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(booleanListEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, Id<?>> idEvaluatable =
            deserialize((String) args[0], DummyModelWithIdEvaluatable.class).getEvaluatable();
        assertThat(idEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(idEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());
    }

    private <T> T evaluate(Evaluatable<DummyContext, T> evaluateable) throws EvaluationException {
        return evaluationService.evaluate(evaluateable, DummyContext.DUMMY_CONTEXT_SUPPLIER);
    }

    private <T> T deserialize(String serialized, Class<T> valueType) throws Exception {
        return objectMapper.readValue(serialized, valueType);
    }

}
