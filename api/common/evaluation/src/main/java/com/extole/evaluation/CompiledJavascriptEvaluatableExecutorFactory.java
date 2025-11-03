package com.extole.evaluation;

import java.io.StringWriter;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.commons.lang3.StringUtils;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.javascript.JavascriptExpression;
import com.extole.common.javascript.JavascriptScriptEngineFactory;
import com.extole.evaluateable.javascript.JavascriptBuildtimeEvaluatable;
import com.extole.evaluateable.javascript.JavascriptEvaluatable;

final class CompiledJavascriptEvaluatableExecutorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CompiledJavascriptEvaluatableExecutorFactory.class);
    private static final int CLASS_CACHE_SIZE = 1000;
    private static final ScriptEngine NASHORN_SCRIPT_ENGINE = new JavascriptScriptEngineFactory()
        .create(JavascriptBuildtimeEvaluatable.class.getSimpleName())
        .withGlobalPerEngine(true)
        .withStrict(true)
        .withLazyCompilation(false)
        .withClassCacheSize(CLASS_CACHE_SIZE)
        .build();

    private CompiledJavascriptEvaluatableExecutorFactory() {

    }

    public static <CONTEXT, RESULT> EvaluatableExecutor<CONTEXT, RESULT>
        create(JavascriptEvaluatable<CONTEXT, RESULT> evaluatable) throws EvaluationException {
        try {
            Class<?> expectedResultTypeClass = evaluatable.getExpectedResultType().getRawClass();
            CompiledScript script = createScript(evaluatable.getExpression(), expectedResultTypeClass);
            return new CompiledJavascriptEvaluatableExecutor<>(evaluatable, script);
        } catch (Exception e) {
            throw new EvaluationException("Failed to compile expression " + evaluatable.getExpression(), evaluatable,
                e);
        }
    }

    private static final class CompiledJavascriptEvaluatableExecutor<CONTEXT, RESULT>
        implements EvaluatableExecutor<CONTEXT, RESULT> {
        private final JavascriptEvaluatable<CONTEXT, RESULT> evaluatable;
        private final CompiledScript compiled;

        private CompiledJavascriptEvaluatableExecutor(JavascriptEvaluatable<CONTEXT, RESULT> evaluatable,
            CompiledScript compiled) {
            this.evaluatable = evaluatable;
            this.compiled = compiled;
        }

        @Override
        public RESULT evaluate(CONTEXT context) throws EvaluationException {
            try {
                Bindings bindings = NASHORN_SCRIPT_ENGINE.createBindings();
                bindings.put("context", context);

                ScriptContext scriptContext = new SimpleScriptContext();
                scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

                if (LOG.isDebugEnabled()) {
                    StringWriter writer = new StringWriter();
                    scriptContext.setWriter(writer);
                    RESULT result = (RESULT) unwrapScriptObjectMirror(compiled.eval(scriptContext));
                    String printMessages = writer.toString();
                    if (StringUtils.isNotBlank(printMessages)) {
                        LOG.debug("Print messages:\n{}\nfor javascript evaluatable:\n{}",
                            printMessages, evaluatable.toString());
                    }
                    return result;
                }

                return (RESULT) unwrapScriptObjectMirror(compiled.eval(scriptContext));
            } catch (Exception e) {
                throw new EvaluationException("Failed to evaluate expression " + evaluatable.getExpression(),
                    evaluatable, e);
            }
        }
    }

    private static CompiledScript createScript(String javascriptExpression, Class<?> expectedResultTypeClass)
        throws ScriptException {
        String expression;
        if (isVoidType(expectedResultTypeClass)) {
            expression = new JavascriptExpression(javascriptExpression)
                .wrapInAnonymousFunction()
                .getExpression();
        } else {
            expression = new JavascriptExpression(javascriptExpression)
                .wrapInAnonymousFunctionWithReturnValue()
                .getExpression();
        }
        return ((Compilable) NASHORN_SCRIPT_ENGINE).compile(expression);
    }

    private static Object unwrapScriptObjectMirror(Object object) {
        if (object instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror) object;
            if (mirror.isFunction()) {
                return mirror.toString();
            }
            if (mirror.isArray()) {
                return mirror.values()
                    .stream()
                    .map(element -> unwrapScriptObjectMirror(element))
                    .collect(Collectors.toList());
            }
            return mirror.entrySet()
                .stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .collect(Collectors.toMap(entry -> entry.getKey(),
                    entry -> unwrapScriptObjectMirror(entry.getValue())));
        }
        return object;
    }

    private static boolean isVoidType(Class<?> type) {
        return type == Void.class;
    }
}
