package com.extole.evaluation;

import static com.extole.evaluation.model.collection.set.optional.DummyModelWithOptionalEnumSetEvaluatable.DummyEnum.TEST_VALUE;
import static com.extole.evaluation.model.collection.set.optional.DummyModelWithOptionalEnumSetEvaluatable.DummyEnum.TEST_VALUE2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.params.ParameterizedTest;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.ecma.Js2025BuildtimeEvaluatable;
import com.extole.evaluateable.ecma.Js2025RuntimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptBuildtimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptRuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.evaluateable.spel.SpelBuildtimeEvaluatable;
import com.extole.evaluateable.spel.SpelRuntimeEvaluatable;
import com.extole.evaluation.junit.MultipleValueSource;
import com.extole.evaluation.model.DummyContext;
import com.extole.evaluation.model.collection.list.optional.DummyModelWithOptionalBooleanListEvaluatable;
import com.extole.evaluation.model.collection.list.optional.DummyModelWithOptionalEnumListEvaluatable;
import com.extole.evaluation.model.collection.list.optional.DummyModelWithOptionalEnumListEvaluatable.DummyEnum;
import com.extole.evaluation.model.collection.list.optional.DummyModelWithOptionalStringListEvaluatable;
import com.extole.evaluation.model.collection.set.optional.DummyModelWithOptionalBooleanSetEvaluatable;
import com.extole.evaluation.model.collection.set.optional.DummyModelWithOptionalEnumSetEvaluatable;
import com.extole.evaluation.model.collection.set.optional.DummyModelWithOptionalStringSetEvaluatable;
import com.extole.evaluation.model.single.optional.DummyModelWithMapOfOptionalEvaluatable;
import com.extole.evaluation.model.single.optional.DummyModelWithOptionalBooleanEvaluatable;
import com.extole.evaluation.model.single.optional.DummyModelWithOptionalEnumEvaluatable;
import com.extole.evaluation.model.single.optional.DummyModelWithOptionalStringEvaluatable;

@SuppressWarnings("checkstyle:lineLength")
class EvaluatableOptionalEvaluationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    private final EvaluationService evaluationService =
        new EvaluationServiceImpl(new ExtoleMetricRegistry(new MetricRegistry()));

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"testValue\"}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@buildtime:'testValue'\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return 'testValue';}()\"}",
        "{\"evaluatable\":\"spel@runtime:'testValue'\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalString(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.of("testValue"));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"TEST_VALUE\"}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"spel@buildtime:'TEST_VALUE'\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return 'TEST_VALUE';}()\"}",
        "{\"evaluatable\":\"spel@runtime:'TEST_VALUE'\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<DummyModelWithOptionalEnumEvaluatable.DummyEnum>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalEnumEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable))
            .isEqualTo(Optional.of(DummyModelWithOptionalEnumEvaluatable.DummyEnum.TEST_VALUE));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":true}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"spel@buildtime:true\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return true;}()\"}",
        "{\"evaluatable\":\"spel@runtime:true\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<Boolean>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.of(Boolean.TRUE));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[\"testValue\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'testValue'}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'testValue'}\"}",
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalListString(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<List<String>>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.of(Lists.newArrayList("testValue")));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[\"testValue\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'testValue'}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['testValue'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'testValue'}\"}",
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalSetString(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<Set<String>>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.of(Sets.newHashSet("testValue")));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[\"TEST_VALUE\",\"TEST_VALUE2\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalListOfEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<List<DummyEnum>>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalEnumListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable))
            .isEqualTo(Optional.of(ImmutableList.of(DummyEnum.TEST_VALUE, DummyEnum.TEST_VALUE2)));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[\"TEST_VALUE\",\"TEST_VALUE2\"]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return ['TEST_VALUE', 'TEST_VALUE2'];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{'TEST_VALUE', 'TEST_VALUE2'}\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalSetOfEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<Set<DummyModelWithOptionalEnumSetEvaluatable.DummyEnum>>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalEnumSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.of(ImmutableSet.of(TEST_VALUE, TEST_VALUE2)));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[true,false]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{true, false}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{true, false}\"}",
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalListOfBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<List<Boolean>>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.of(Lists.newArrayList(Boolean.TRUE, Boolean.FALSE)));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":[true,false]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{true, false}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [true, false];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{true, false}\"}",
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalSetOfBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<Set<Boolean>>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.of(Sets.newHashSet(Boolean.TRUE, Boolean.FALSE)));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"properties\" : {} }}",
        "{\"evaluatables\":{\"properties\" : \"javascript@buildtime:function(){ return {};}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"js2025@buildtime:function(){ return {};}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"spel@buildtime:{:}\" }}",
        "{\"evaluatables\":{\"properties\" : \"javascript@runtime:function(){ return new Object();}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"js2025@runtime:function(){ return new Object();}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"spel@runtime:{:}\" }}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEmptyObject(Object... args) throws Exception {
        Map<String, Evaluatable<DummyContext, Optional<Object>>> evaluatables =
            deserialize((String) args[0], DummyModelWithMapOfOptionalEvaluatable.class).getEvaluatables();
        assertThat(evaluatables.size()).isEqualTo(1);
        assertThat(evaluatables.get("properties").getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatables.get("properties"))).isEqualTo(Optional.of(Collections.emptyMap()));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalStringAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.empty());
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalEnumAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<DummyModelWithOptionalEnumEvaluatable.DummyEnum>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalEnumEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.empty());
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalBooleanAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<Boolean>> evaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Optional.empty());
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatables\":{\"properties\" : null }}",
        "{\"evaluatables\":{\"properties\" : \"javascript@buildtime:function(){ return null;}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"js2025@buildtime:function(){ return null;}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"spel@buildtime:null\" }}",
        "{\"evaluatables\":{\"properties\" : \"javascript@runtime:function(){ return null;}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"js2025@runtime:function(){ return null;}()\" }}",
        "{\"evaluatables\":{\"properties\" : \"spel@runtime:null\" }}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEmptyObjectAsNull(Object... args) throws Exception {
        Map<String, Evaluatable<DummyContext, Optional<Object>>> evaluatables =
            deserialize((String) args[0], DummyModelWithMapOfOptionalEvaluatable.class).getEvaluatables();
        assertThat(evaluatables.size()).isEqualTo(1);
        assertThat(evaluatables.get("properties").getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatables.get("properties"))).isEqualTo(Optional.empty());
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": null}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@buildtime: null\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return null;}()\"}",
        "{\"evaluatable\":\"spel@runtime:null\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableOptionalNullCollection(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<List<String>>> stringListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringListEvaluatable.class).getEvaluatable();
        assertThat(stringListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(stringListEvaluatable)).isEqualTo(Optional.empty());

        Evaluatable<DummyContext, Optional<Set<String>>> stringSetEvalutable =
            deserialize((String) args[0], DummyModelWithOptionalStringSetEvaluatable.class).getEvaluatable();
        assertThat(stringSetEvalutable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(stringSetEvalutable)).isEqualTo(Optional.empty());

        Evaluatable<DummyContext, Optional<List<DummyEnum>>> enumListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalEnumListEvaluatable.class).getEvaluatable();
        assertThat(enumListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(enumListEvaluatable)).isEqualTo(Optional.empty());

        Evaluatable<DummyContext,
            Optional<Set<DummyModelWithOptionalEnumSetEvaluatable.DummyEnum>>> enumSetEvaluatable =
                deserialize((String) args[0], DummyModelWithOptionalEnumSetEvaluatable.class).getEvaluatable();
        assertThat(enumSetEvaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(enumSetEvaluatable)).isEqualTo(Optional.empty());

        Evaluatable<DummyContext, Optional<List<Boolean>>> booleanListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanListEvaluatable.class).getEvaluatable();
        assertThat(booleanListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(booleanListEvaluatable)).isEqualTo(Optional.empty());

        Evaluatable<DummyContext, Optional<Set<Boolean>>> booleanSetEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanSetEvaluatable.class).getEvaluatable();
        assertThat(booleanSetEvaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(booleanSetEvaluatable)).isEqualTo(Optional.empty());
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\": [null]}",
        "{\"evaluatable\":\"javascript@buildtime:function(){ return [null];}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){ return [null];}()\"}",
        "{\"evaluatable\":\"spel@buildtime:{null}\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){ return [null];}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){ return [null];}()\"}",
        "{\"evaluatable\":\"spel@runtime:{null}\"}"
    },
        classes = {
            Provided.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableReturningNullsAndNullElements(Object... args) throws Exception {

        Evaluatable<DummyContext, Optional<List<String>>> stringListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringListEvaluatable.class).getEvaluatable();
        assertThat(stringListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(stringListEvaluatable));

        Evaluatable<DummyContext, Optional<Set<String>>> stringSetEvalutable =
            deserialize((String) args[0], DummyModelWithOptionalStringSetEvaluatable.class).getEvaluatable();
        assertThat(stringSetEvalutable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(stringSetEvalutable));

        Evaluatable<DummyContext, Optional<List<DummyEnum>>> enumListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalEnumListEvaluatable.class).getEvaluatable();
        assertThat(enumListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(enumListEvaluatable));

        Evaluatable<DummyContext,
            Optional<Set<DummyModelWithOptionalEnumSetEvaluatable.DummyEnum>>> enumSetEvaluatable =
                deserialize((String) args[0], DummyModelWithOptionalEnumSetEvaluatable.class).getEvaluatable();
        assertThat(enumSetEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(enumSetEvaluatable));

        Evaluatable<DummyContext, Optional<List<Boolean>>> booleanListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanListEvaluatable.class).getEvaluatable();
        assertThat(booleanListEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(booleanListEvaluatable));

        Evaluatable<DummyContext, Optional<Set<Boolean>>> booleanSetEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanSetEvaluatable.class).getEvaluatable();
        assertThat(booleanSetEvaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(booleanSetEvaluatable));
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
    void testEvaluationEvaluatableOptionalWrongType(Object... args) throws Exception {
        Evaluatable<DummyContext, Optional<Boolean>> booleanEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanEvaluatable.class).getEvaluatable();
        assertThat(booleanEvaluatable.getClass()).isEqualTo(args[1]);
        EvaluationException evaluationException =
            assertThrows(EvaluationException.class, () -> evaluate(booleanEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, Optional<DummyModelWithOptionalEnumEvaluatable.DummyEnum>> enumEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalEnumEvaluatable.class).getEvaluatable();
        assertThat(booleanEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(enumEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, Optional<String>> stringEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringEvaluatable.class).getEvaluatable();
        assertThat(booleanEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(stringEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, Optional<List<String>>> stringListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalStringListEvaluatable.class).getEvaluatable();
        assertThat(booleanEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(stringListEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, Optional<List<DummyEnum>>> enumListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalEnumListEvaluatable.class).getEvaluatable();
        assertThat(booleanEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(enumListEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());

        Evaluatable<DummyContext, Optional<List<Boolean>>> booleanListEvaluatable =
            deserialize((String) args[0], DummyModelWithOptionalBooleanListEvaluatable.class).getEvaluatable();
        assertThat(booleanEvaluatable.getClass()).isEqualTo(args[1]);
        evaluationException = assertThrows(EvaluationException.class, () -> evaluate(booleanListEvaluatable));
        assertThat(MismatchedInputException.class).isEqualTo(evaluationException.getCause().getClass());
    }

    private <T> T evaluate(Evaluatable<DummyContext, T> evaluateable) throws EvaluationException {
        return evaluationService.evaluate(evaluateable, DummyContext.DUMMY_CONTEXT_SUPPLIER);
    }

    private <T> T deserialize(String serialized, Class<T> valueType) throws Exception {
        return objectMapper.readValue(serialized, valueType);
    }

}
