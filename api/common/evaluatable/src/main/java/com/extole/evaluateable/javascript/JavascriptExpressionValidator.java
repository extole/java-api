package com.extole.evaluateable.javascript;

import static java.util.stream.Collectors.joining;

import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.evaluateable.javascript.compiler.JavascriptCompiler;
import com.extole.evaluateable.javascript.compiler.JavascriptCompilerException;

public final class JavascriptExpressionValidator {
    private static final Logger LOG = LoggerFactory.getLogger(JavascriptExpressionValidator.class);
    private static final int EXPRESSION_MAX_LENGTH = 5000;
    private static final JavascriptExpressionValidator INSTANCE = new JavascriptExpressionValidator();

    public static JavascriptExpressionValidator getInstance() {
        return INSTANCE;
    }

    private JavascriptExpressionValidator() {
    }

    // TODO on part 2 of ENG-19753 do not catch validation exceptions but add them to the method signature
    public void validate(String expression) {
        try {
            validateExpressionLength(expression);
            validateCompileTimeErrors(expression);
        } catch (Exception e) {
            LOG.warn("Validation failed for expression: " + expression, e);
        }
    }

    private void validateExpressionLength(String expression) throws JavascriptExpressionInvalidLengthException {
        if (StringUtils.isBlank(expression) || expression.length() > EXPRESSION_MAX_LENGTH) {
            throw new JavascriptExpressionInvalidLengthException(expression, EXPRESSION_MAX_LENGTH);
        }
    }

    private void validateCompileTimeErrors(String expression) throws JavascriptExpressionCompileTimeException {
        try {
            JavascriptCompiler.create()
                .addJavascript(Paths.get("javascript"), wrapInAnonymousFunctionWithReturnValue(expression))
                .compile();
        } catch (JavascriptCompilerException e) {
            throw new JavascriptExpressionCompileTimeException(String
                .format("Javascript %s has compile time errors %s",
                    expression, e.getOutput().stream().collect(joining(",", "[", "]"))),
                expression, e);
        }
    }

    private String wrapInAnonymousFunctionWithReturnValue(String expression) {
        return "(function () { return " + expression + "; })();";
    }

}
