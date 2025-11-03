package com.extole.evaluation;

import static com.extole.evaluation.model.collection.set.DummyModelWithEnumSetEvaluatable.DummyEnum.TEST_VALUE;
import static com.extole.evaluation.model.collection.set.DummyModelWithEnumSetEvaluatable.DummyEnum.TEST_VALUE2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.util.List;
import java.util.Set;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.extole.evaluateable.spel.SpelBuildtimeEvaluatable;
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
import com.extole.evaluation.model.single.DummyModelWithStringEvaluatable;
import com.extole.evaluation.model.single.DummyModelWithVoidEvaluatable;

class MultilineEvaluatableEvaluationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    private final EvaluationService evaluationService =
        new EvaluationServiceImpl(new ExtoleMetricRegistry(new MetricRegistry()));

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return 'testValue';\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return 'testValue';\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\n'testValue'\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return 'testValue';\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return 'testValue';\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\n'testValue'\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableString(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo("testValue");
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return 'TEST_VALUE';\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return 'TEST_VALUE';\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\n'TEST_VALUE'\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return 'TEST_VALUE';\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return 'TEST_VALUE';\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\n'TEST_VALUE'\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, DummyModelWithEnumEvaluatable.DummyEnum> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(DummyModelWithEnumEvaluatable.DummyEnum.TEST_VALUE);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return true;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return true;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\ntrue\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return true;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return true;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\ntrue\\n \"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, Boolean> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return ['testValue'];\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return ['testValue'];\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\n{\\n'testValue'\\n}\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return ['testValue'];\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return ['testValue'];\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\n{\\n'testValue'\\n}\\n\"}",
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListString(Object... args) throws Exception {
        Evaluatable<DummyContext, List<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithStringListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Lists.newArrayList("testValue"));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return ['testValue'];\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return ['testValue'];\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\n{\\n'testValue'\\n}\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return ['testValue'];\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return ['testValue'];\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\n{\\n'testValue'\\n}\\n\"}",
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetString(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithStringSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Sets.newHashSet("testValue"));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return ['TEST_VALUE', 'TEST_VALUE2'];\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return ['TEST_VALUE', 'TEST_VALUE2'];\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\n{\\n'TEST_VALUE',\\n 'TEST_VALUE2'\\n}\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return ['TEST_VALUE', 'TEST_VALUE2'];\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return ['TEST_VALUE', 'TEST_VALUE2'];\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\n{\\n'TEST_VALUE',\\n 'TEST_VALUE2'\\n}\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListOfEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, List<DummyEnum>> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(ImmutableList.of(DummyEnum.TEST_VALUE, DummyEnum.TEST_VALUE2));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return ['TEST_VALUE', 'TEST_VALUE2'];\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return ['TEST_VALUE', 'TEST_VALUE2'];\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\n{\\n'TEST_VALUE',\\n 'TEST_VALUE2'\\n}\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return ['TEST_VALUE', 'TEST_VALUE2'];\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return ['TEST_VALUE', 'TEST_VALUE2'];\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\n{\\n'TEST_VALUE',\\n 'TEST_VALUE2'\\n}\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetOfEnum(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<DummyModelWithEnumSetEvaluatable.DummyEnum>> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(ImmutableSet.of(TEST_VALUE, TEST_VALUE2));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return [true, false];\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return [true, false];\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\n{\\ntrue,\\nfalse\\n}\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return [true, false];\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return [true, false];\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\n{\\ntrue,\\nfalse\\n}\\n\"}",
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListOfBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, List<Boolean>> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Lists.newArrayList(Boolean.TRUE, Boolean.FALSE));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return [true, false];\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return [true, false];\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime:\\n{\\ntrue,\\nfalse\\n}\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return [true, false];\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return [true, false];\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\n{\\ntrue,\\nfalse\\n}\\n\"}",
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetOfBoolean(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<Boolean>> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo(Sets.newHashSet(Boolean.TRUE, Boolean.FALSE));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableStringAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableEnumAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, DummyModelWithEnumEvaluatable.DummyEnum> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableBooleanAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Boolean> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListStringAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, List<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithStringListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetStringAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<String>> evaluatable =
            deserialize((String) args[0], DummyModelWithStringSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListOfEnumAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, List<DummyEnum>> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetOfEnumAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<DummyModelWithEnumSetEvaluatable.DummyEnum>> evaluatable =
            deserialize((String) args[0], DummyModelWithEnumSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableListOfBooleanAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, List<Boolean>> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanListEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@buildtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@buildtime: \\nnull\\n\"}",
        "{\"evaluatable\":\"javascript@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"js2025@runtime:function(){\\n return null;\\n}()\"}",
        "{\"evaluatable\":\"spel@runtime:\\nnull\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationEvaluatableSetOfBooleanAsNull(Object... args) throws Exception {
        Evaluatable<DummyContext, Set<Boolean>> evaluatable =
            deserialize((String) args[0], DummyModelWithBooleanSetEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThrows(NullEvaluationResultException.class, () -> evaluate(evaluatable));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime: function(){\\n return context\\n.dummyText;\\n} ()\\n\"}",
        "{\"evaluatable\":\"js2025@buildtime: function(){\\n return context\\n.getDummyText();\\n} ()\\n\"}",
        "{\"evaluatable\":\"javascript@runtime: function(){\\n return context\\n.dummyText;\\n} ()\\n\"}",
        "{\"evaluatable\":\"js2025@runtime: function(){\\n return context\\n.getDummyText();\\n} ()\\n\"}",
        "{\"evaluatable\":\"javascript@buildtime: context\\n.dummyText\\n\"}",
        "{\"evaluatable\":\"js2025@buildtime: context\\n.getDummyText()\\n\"}",
        "{\"evaluatable\":\"javascript@runtime: context\\n.dummyText\\n\"}",
        "{\"evaluatable\":\"js2025@runtime: context\\n.getDummyText()\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class
        })
    void testJavascriptEvaluationWithLongVersion(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo("dummy text");
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime: context\\n.dummyText\\n\"}",
        "{\"evaluatable\":\"js2025@buildtime: context\\n.getDummyText()\\n\"}",
        "{\"evaluatable\":\"spel@buildtime: \\ncontext\\n.dummyText\\n\"}",
        "{\"evaluatable\":\"javascript@runtime: context\\n.dummyText\\n\"}",
        "{\"evaluatable\":\"js2025@runtime: context\\n.getDummyText()\\n\"}",
        "{\"evaluatable\":\"spel@runtime: \\ncontext\\n.dummyText\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationWithAccessingContext(Object... args) throws Exception {
        Evaluatable<DummyContext, String> evaluatable =
            deserialize((String) args[0], DummyModelWithStringEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertThat(evaluate(evaluatable)).isEqualTo("dummy text");
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MultipleValueSource(strings = {
        "{\"evaluatable\":\"javascript@buildtime: \\ndummyText;\\n\"}",
        "{\"evaluatable\":\"js2025@buildtime: \\ndummyText;\\n\"}",
        "{\"evaluatable\":\"spel@buildtime: \\ndummyText\\n\"}",
        "{\"evaluatable\":\"javascript@runtime: \\ndummyText;\\n\"}",
        "{\"evaluatable\":\"js2025@runtime: \\ndummyText;\\n\"}",
        "{\"evaluatable\":\"spel@runtime: \\ndummyText\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
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
        "{\"evaluatable\":\"javascript@buildtime: \\ncontext\\n.dummyText;\\n\"}",
        "{\"evaluatable\":\"js2025@buildtime: \\ncontext\\n.dummyText;\\n\"}",
        "{\"evaluatable\":\"spel@buildtime: \\ncontext\\n.dummyText\\n\"}",
        "{\"evaluatable\":\"javascript@runtime: \\ncontext\\n.dummyText;\\n\"}",
        "{\"evaluatable\":\"js2025@runtime: \\ncontext\\n.dummyText;\\n\"}",
        "{\"evaluatable\":\"spel@runtime: \\ncontext\\n.dummyText\\n\"}"
    },
        classes = {
            JavascriptBuildtimeEvaluatable.class,
            Js2025BuildtimeEvaluatable.class,
            SpelBuildtimeEvaluatable.class,
            JavascriptRuntimeEvaluatable.class,
            Js2025RuntimeEvaluatable.class,
            SpelRuntimeEvaluatable.class
        })
    void testEvaluationReturnsNullWhenReturnTypeIsVoid(Object... args) throws Exception {
        Evaluatable<DummyContext, Void> evaluatable =
            deserialize((String) args[0], DummyModelWithVoidEvaluatable.class).getEvaluatable();
        assertThat(evaluatable.getClass()).isEqualTo(args[1]);
        assertNull(evaluate(evaluatable));
    }

    private <T> T evaluate(Evaluatable<DummyContext, T> evaluateable) throws EvaluationException {
        return evaluationService.evaluate(evaluateable, DummyContext.DUMMY_CONTEXT_SUPPLIER);
    }

    private <T> T deserialize(String serialized, Class<T> valueType) throws Exception {
        return objectMapper.readValue(serialized, valueType);
    }

}
