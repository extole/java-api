package com.extole.evaluateable.ecma;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Js2025ExpressionValidator {

    private static final Logger LOG = LoggerFactory.getLogger(Js2025ExpressionValidator.class);
    private static final int EXPRESSION_MAX_LENGTH = 5000;
    private static final Js2025ExpressionValidator INSTANCE = new Js2025ExpressionValidator();

    public static Js2025ExpressionValidator getInstance() {
        return INSTANCE;
    }

    private Js2025ExpressionValidator() {
    }

    public void validate(String expression) throws Js2025ExpressionInvalidLengthException,
        Js2025ExpressionCompileTimeException {
        validateExpressionLength(expression);
        validateCompileTimeErrors(expression);
    }

    private void validateExpressionLength(String expression) throws Js2025ExpressionInvalidLengthException {
        if (StringUtils.isBlank(expression) || expression.length() > EXPRESSION_MAX_LENGTH) {
            throw new Js2025ExpressionInvalidLengthException(expression, EXPRESSION_MAX_LENGTH);
        }
    }

    private void validateCompileTimeErrors(String expression) throws Js2025ExpressionCompileTimeException {
        try (Context context = Context.newBuilder("js").build()) {
            context.getBindings("js").putMember("context", ResilientDummyContext.INSTANCE);
            context.eval("js", wrapInAnonymousFunctionWithReturnValue(expression));
        } catch (PolyglotException e) {
            throw new Js2025ExpressionCompileTimeException(String
                .format("Js2025 %s has compile time errors %s", expression, e.getMessage()), expression, e);
        }
    }

    private String wrapInAnonymousFunctionWithReturnValue(String expression) {
        return "(function () { return " + expression + "; })();";
    }

    public static class ResilientDummyContext implements ProxyObject, ProxyExecutable {
        public static final ProxyObject INSTANCE = new ResilientDummyContext();

        @Override
        public Object getMember(String key) {
            return this;
        }

        @Override
        public Object getMemberKeys() {
            return Collections.emptySet();
        }

        @Override
        public boolean hasMember(String key) {
            return true;
        }

        @Override
        public void putMember(String key, Value value) {
            // ignore
        }

        @Override
        public Object execute(Value... args) {
            return this;
        }
    }
}
