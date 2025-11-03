package com.extole.evaluateable.handlebars;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import org.apache.commons.lang3.StringUtils;

public final class HandlebarsExpressionValidator {
    private static final int EXPRESSION_MAX_LENGTH = 5000;
    private static final HandlebarsExpressionValidator INSTANCE = new HandlebarsExpressionValidator();
    private static final Handlebars HANDLEBARS = new Handlebars();

    public static HandlebarsExpressionValidator getInstance() {
        return INSTANCE;
    }

    private HandlebarsExpressionValidator() {
    }

    public void validate(String expression) throws HandlebarsExpressionCompileTimeException {
        validateCompileTimeErrors(expression);
    }

    private void validateExpressionLength(String expression) throws HandlebarsExpressionInvalidLengthException {
        if (StringUtils.isBlank(expression) || expression.length() > EXPRESSION_MAX_LENGTH) {
            throw new HandlebarsExpressionInvalidLengthException(expression, EXPRESSION_MAX_LENGTH);
        }
    }

    private void validateCompileTimeErrors(String expression) throws HandlebarsExpressionCompileTimeException {
        try {
            HandlebarsProvider.getInstance().compileInline(expression);
        } catch (IOException | HandlebarsException e) {
            throw new HandlebarsExpressionCompileTimeException(
                String.format("Handlebars %s has compile time errors", expression),
                expression, e);
        }
    }

}
