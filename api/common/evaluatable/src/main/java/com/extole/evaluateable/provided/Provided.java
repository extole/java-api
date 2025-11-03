package com.extole.evaluateable.provided;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.InstalltimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public final class Provided<CONTEXT, RESULT> implements Evaluatable<CONTEXT, RESULT>,
    BuildtimeEvaluatable<CONTEXT, RESULT>,
    RuntimeEvaluatable<CONTEXT, RESULT>,
    InstalltimeEvaluatable<CONTEXT, RESULT> {

    private static final Provided<?, ?> NULL = Provided.of(null);
    private static final Provided<?, ?> VOIDED = Provided.of(null);
    private static final Provided<?, ?> EMPTY = Provided.of(Optional.empty());
    private static final Provided<?, Set<?>> EMPTY_SET = Provided.of(Collections.emptySet());
    private static final Provided<?, List<?>> EMPTY_LIST = Provided.of(Collections.emptyList());
    private static final Provided<?, Boolean> TRUE = Provided.of(Boolean.TRUE);
    private static final Provided<?, Boolean> FALSE = Provided.of(Boolean.FALSE);

    public static <CONTEXT, RESULT> Provided<CONTEXT, RESULT> nullified() {
        return (Provided<CONTEXT, RESULT>) NULL;
    }

    public static <CONTEXT> Provided<CONTEXT, Void> voided() {
        return (Provided<CONTEXT, Void>) VOIDED;
    }

    public static <CONTEXT, RESULT> Provided<CONTEXT, Optional<RESULT>> optionalEmpty() {
        return (Provided<CONTEXT, Optional<RESULT>>) EMPTY;
    }

    public static <CONTEXT, RESULT> Provided<CONTEXT, Optional<RESULT>> optionalOf(RESULT value) {
        return Provided.of(Optional.ofNullable(value));
    }

    public static <CONTEXT, RESULT extends Boolean> Provided<CONTEXT, RESULT> booleanTrue() {
        return (Provided<CONTEXT, RESULT>) TRUE;
    }

    public static <CONTEXT, RESULT extends Boolean> Provided<CONTEXT, RESULT> booleanFalse() {
        return (Provided<CONTEXT, RESULT>) FALSE;
    }

    public static <CONTEXT, RESULT extends Set<?>> Provided<CONTEXT, RESULT> emptySet() {
        return (Provided<CONTEXT, RESULT>) EMPTY_SET;
    }

    public static <CONTEXT, RESULT extends List<?>> Provided<CONTEXT, RESULT> emptyList() {
        return (Provided<CONTEXT, RESULT>) EMPTY_LIST;
    }

    public static <CONTEXT, ELEMENT> Provided<CONTEXT, Set<ELEMENT>> setOf(ELEMENT... values) {
        return Provided.of(Collections.unmodifiableSet(Sets.newHashSet(values)));
    }

    public static <CONTEXT, ELEMENT> Provided<CONTEXT, List<ELEMENT>> listOf(ELEMENT... values) {
        return Provided.of(Collections.unmodifiableList(Lists.newArrayList(values)));
    }

    public static <CONTEXT> Provided<CONTEXT, BigDecimal> bigDecimalOf(long value) {
        return Provided.of(BigDecimal.valueOf(value));
    }

    public static <CONTEXT> Provided<CONTEXT, BigDecimal> bigDecimalOf(double value) {
        return Provided.of(BigDecimal.valueOf(value));
    }

    public static <CONTEXT> Provided<CONTEXT, Integer> ofInteger(int value) {
        return Provided.of(Integer.valueOf(value));
    }

    public static <OUTER_CONTEXT, INNER_CONTEXT, RESULT>
        Provided<OUTER_CONTEXT, RuntimeEvaluatable<INNER_CONTEXT, RESULT>> nestedOf(RESULT value) {
        RuntimeEvaluatable<INNER_CONTEXT, RESULT> inner = of(value);
        Provided<OUTER_CONTEXT, RuntimeEvaluatable<INNER_CONTEXT, RESULT>> outer = Provided.of(inner);
        return outer;
    }

    public static <OUTER_CONTEXT, INNER_CONTEXT, RESULT>
        Provided<OUTER_CONTEXT, RuntimeEvaluatable<INNER_CONTEXT, Optional<RESULT>>> nestedOptionalOf(RESULT value) {
        RuntimeEvaluatable<INNER_CONTEXT, Optional<RESULT>> inner = of(Optional.ofNullable(value));
        Provided<OUTER_CONTEXT, RuntimeEvaluatable<INNER_CONTEXT, Optional<RESULT>>> outer = Provided.of(inner);
        return outer;
    }

    public static <CONTEXT, RESULT> Provided<CONTEXT, RESULT> of(RESULT value) {
        return new Provided<>(value);
    }

    public static <CONTEXT> Provided<CONTEXT, Long> longOf(long value) {
        return Provided.of(Long.valueOf(value));
    }

    private final RESULT value;

    private Provided(RESULT value) {
        this.value = value;
    }

    public RESULT getValue() {
        return value;
    }

    @Override
    public JavaType getExpectedResultType() {
        return null;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.value)
            .build()
            .intValue();
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || otherObject.getClass() != Provided.class) {
            return false;
        }

        Provided<?, ?> otherEvaluatable = (Provided<?, ?>) otherObject;

        return new EqualsBuilder()
            .append(this.value, otherEvaluatable.value)
            .build()
            .booleanValue();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
