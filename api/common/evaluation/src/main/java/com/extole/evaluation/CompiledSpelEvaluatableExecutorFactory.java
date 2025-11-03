package com.extole.evaluation;

import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.DataBindingMethodResolver;
import org.springframework.expression.spel.support.DataBindingPropertyAccessor;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import com.extole.evaluateable.spel.SpelEvaluatable;

final class CompiledSpelEvaluatableExecutorFactory {
    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER;

    static {
        var config = new SpelParserConfiguration(null, null, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
        SPEL_EXPRESSION_PARSER = new SpelExpressionParser(config);
    }

    private CompiledSpelEvaluatableExecutorFactory() {

    }

    public static <CONTEXT, RESULT> EvaluatableExecutor<CONTEXT, RESULT>
        create(SpelEvaluatable<CONTEXT, RESULT> evaluatable) throws EvaluationException {
        try {
            SpelExpression spelExpression = SPEL_EXPRESSION_PARSER.parseRaw(evaluatable.getExpression());
            return new CompiledSpelEvaluatableExecutor<>(evaluatable, spelExpression);
        } catch (Exception e) {
            throw new EvaluationException("Failed to compile expression " + evaluatable.getExpression(),
                evaluatable, e);
        }
    }

    private static final class CompiledSpelEvaluatableExecutor<CONTEXT, RESULT>
        implements EvaluatableExecutor<CONTEXT, RESULT> {
        private final SpelEvaluatable<CONTEXT, RESULT> evaluatable;
        private final SpelExpression compiled;

        private CompiledSpelEvaluatableExecutor(SpelEvaluatable<CONTEXT, RESULT> evaluatable,
            SpelExpression compiled) {
            this.evaluatable = evaluatable;
            this.compiled = compiled;
        }

        @SuppressWarnings("unchecked")
        @Override
        public RESULT evaluate(CONTEXT context) throws EvaluationException {
            try {
                return (RESULT) compiled
                    .getValue(SimpleEvaluationContext
                        .forPropertyAccessors(DataBindingPropertyAccessor.forReadOnlyAccess())
                        .withMethodResolvers(DataBindingMethodResolver.forInstanceMethodInvocation())
                        .withRootObject(new SpelExpressionEvaluationContextDecorator<>(context))
                        .build());
            } catch (Exception e) {
                throw new EvaluationException("Failed to evaluate expression " + evaluatable.getExpression(),
                    evaluatable, e);
            }
        }
    }

    private static final class SpelExpressionEvaluationContextDecorator<CONTEXT> {

        private final CONTEXT context;

        private SpelExpressionEvaluationContextDecorator(CONTEXT context) {
            this.context = context;
        }

        @SuppressWarnings("unused")
        public CONTEXT getContext() {
            return context;
        }

    }
}
