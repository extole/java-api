package com.extole.evaluation;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;

import com.extole.evaluateable.handlebars.HandlebarsEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsProvider;
import com.extole.evaluation.handlebars.ShortVariableSyntaxValueResolver;

final class CompiledHandlebarsEvaluatableExecutorFactory {
    private static final String HANDLEBARS_NULL_LITERAL = "#{null}";
    private static final Handlebars HANDLEBARS = HandlebarsProvider.getInstance();

    private CompiledHandlebarsEvaluatableExecutorFactory() {

    }

    public static <CONTEXT, RESULT> EvaluatableExecutor<CONTEXT, RESULT>
        create(HandlebarsEvaluatable<CONTEXT, RESULT> evaluatable) throws EvaluationException {
        try {
            Template template = HANDLEBARS.compileInline(evaluatable.getExpression());
            return new CompiledHandlebarsEvaluatableExecutor<>(evaluatable, template);
        } catch (Exception e) {
            throw new EvaluationException("Failed to compile expression " + evaluatable.getExpression(),
                evaluatable, e);
        }
    }

    private static final class CompiledHandlebarsEvaluatableExecutor<CONTEXT, RESULT>
        implements EvaluatableExecutor<CONTEXT, RESULT> {
        private final HandlebarsEvaluatable<CONTEXT, RESULT> evaluatable;
        private final Template compiled;

        private CompiledHandlebarsEvaluatableExecutor(HandlebarsEvaluatable<CONTEXT, RESULT> evaluatable,
            Template compiled) {
            this.evaluatable = evaluatable;
            this.compiled = compiled;
        }

        @Override
        public RESULT evaluate(CONTEXT context) throws EvaluationException {
            try {
                Context handlebarsContext = Context.newBuilder(context)
                    .resolver(MapValueResolver.INSTANCE,
                        JavaBeanValueResolver.INSTANCE,
                        ShortVariableSyntaxValueResolver.INSTANCE)
                    .build();
                String evaluated = compiled.apply(handlebarsContext);

                if (evaluated.trim().equals(HANDLEBARS_NULL_LITERAL)) {
                    return null;
                }
                return (RESULT) evaluated;
            } catch (Exception e) {
                throw new EvaluationException("Failed to evaluate expression " + evaluatable.getExpression(),
                    evaluatable, e);
            }
        }
    }

}
