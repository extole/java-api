package com.extole.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.javascript.JavascriptExpression;
import com.extole.evaluateable.ecma.Js2025Evaluatable;

final class CompiledJs2025EvaluatableExecutorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CompiledJs2025EvaluatableExecutorFactory.class);
    private static final String JS = "js";
    private static final Engine ENGINE = Engine.create(JS);

    private CompiledJs2025EvaluatableExecutorFactory() {

    }

    public static <CONTEXT, RESULT> EvaluatableExecutor<CONTEXT, RESULT>
        create(Js2025Evaluatable<CONTEXT, RESULT> evaluatable) throws EvaluationException {
        try {
            String expression;
            Class<?> type = evaluatable.getExpectedResultType().getRawClass();
            if (isVoidType(type)) {
                expression = new JavascriptExpression(evaluatable.getExpression())
                    .wrapInAnonymousFunction()
                    .getExpression();
            } else {
                expression = new JavascriptExpression(evaluatable.getExpression())
                    .wrapInAnonymousFunctionWithReturnValue()
                    .getExpression();
            }
            Source compiled = Source.newBuilder(JS, expression, UUID.randomUUID().toString())
                .cached(true)
                .buildLiteral();
            return new CompiledJs2025EvaluatableExecutor<>(evaluatable, compiled);
        } catch (Exception e) {
            throw new EvaluationException("Failed to compile expression " + evaluatable.getExpression(), evaluatable,
                e);
        }
    }

    private static final class CompiledJs2025EvaluatableExecutor<CONTEXT, RESULT>
        implements EvaluatableExecutor<CONTEXT, RESULT> {
        private final Js2025Evaluatable<CONTEXT, RESULT> evaluatable;
        private final Source compiled;

        private CompiledJs2025EvaluatableExecutor(Js2025Evaluatable<CONTEXT, RESULT> evaluatable,
            Source compiled) {
            this.evaluatable = evaluatable;
            this.compiled = compiled;
        }

        @Override
        public RESULT evaluate(CONTEXT context) throws EvaluationException {
            try {
                try (Context evaluator = Context.newBuilder(JS)
                    .engine(ENGINE)
                    .allowAllAccess(true)
                    .build()) {
                    evaluator.getBindings(JS).putMember("context", context);
                    return (RESULT) unwrap(evaluator.eval(compiled));
                }
            } catch (Exception e) {
                throw new EvaluationException("Failed to evaluate expression " + evaluatable.getExpression(),
                    evaluatable, e);
            }
        }
    }

    private static boolean isVoidType(Class<?> type) {
        return type == Void.class;
    }

    public static Object unwrap(Value value) {
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isNumber()) {
            return value.asDouble();
        }
        if (value.isString()) {
            return value.asString();
        }
        if (value.hasArrayElements()) {
            List<Object> list = new ArrayList<>();
            for (long i = 0; i < value.getArraySize(); i++) {
                list.add(unwrap(value.getArrayElement(i)));
            }
            return list;
        }
        if (value.hasMembers()) {
            Map<String, Object> map = new HashMap<>();
            for (String key : value.getMemberKeys()) {
                map.put(key, unwrap(value.getMember(key)));
            }
            return map;
        }
        return value;
    }
}
