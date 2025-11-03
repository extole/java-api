package com.extole.evaluateable.ecma;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.InstalltimeEvaluatable;

public final class Js2025InstalltimeEvaluatable<CONTEXT, RESULT>
    implements InstalltimeEvaluatable<CONTEXT, RESULT>, Js2025Evaluatable<CONTEXT, RESULT> {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();

    private final String expression;
    private final JavaType expectedResultType;

    private Js2025InstalltimeEvaluatable(String expression, JavaType expectedResultType) {
        this.expression = expression;
        this.expectedResultType = expectedResultType;
    }

    public static <CONTEXT, RESULT> Js2025InstalltimeEvaluatable<CONTEXT, RESULT> of(String expression,
        TypeReference<RESULT> expectedResultTypeReference) {
        return new Js2025InstalltimeEvaluatable<>(expression,
            OBJECT_MAPPER.constructType(expectedResultTypeReference));
    }

    public static <CONTEXT, RESULT> Js2025InstalltimeEvaluatable<CONTEXT, RESULT> of(String expression,
        JavaType expectedResultType) {
        return new Js2025InstalltimeEvaluatable<>(expression, expectedResultType);
    }

    @Override
    public JavaType getExpectedResultType() {
        return expectedResultType;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.expression)
            .append(this.expectedResultType)
            .build()
            .intValue();
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || otherObject.getClass() != Js2025InstalltimeEvaluatable.class) {
            return false;
        }

        Js2025InstalltimeEvaluatable<?, ?> otherEvaluatable =
            (Js2025InstalltimeEvaluatable<?, ?>) otherObject;

        return new EqualsBuilder()
            .append(this.expression, otherEvaluatable.expression)
            .append(this.expectedResultType, otherEvaluatable.expectedResultType)
            .build()
            .booleanValue();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
