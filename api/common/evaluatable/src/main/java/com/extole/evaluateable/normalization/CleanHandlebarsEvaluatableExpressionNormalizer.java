package com.extole.evaluateable.normalization;

import java.util.regex.Pattern;

import com.extole.evaluateable.ExpressionEvaluatableType;

public class CleanHandlebarsEvaluatableExpressionNormalizer implements EvaluatableExpressionNormalizer {

    public static final CleanHandlebarsEvaluatableExpressionNormalizer INSTANCE =
        new CleanHandlebarsEvaluatableExpressionNormalizer();
    private static final Pattern HANDLEBARS_NORMALIZATION_PATTERN = Pattern.compile(
        "\\{\\{\\s*(.*?)\\s*}}|" +
            "\\{\\[\\s*(.*?)\\s*]}|" +
            "\\{\\{\\{\\s*(.*?)\\s*}}}");
    private static final int DOUBLE_CURLY_GROUP = 1;
    private static final int CURLY_SQUARE_GROUP = 2;
    private static final int TRIPLE_CURLY_GROUP = 3;

    @Override
    public String normalize(String expression, ExpressionEvaluatableType type) {
        if (type.getLanguage().equals(com.extole.evaluateable.Evaluatable.HANDLEBARS)) {
            return normalizeHandlebars(expression);
        }
        return expression;
    }

    public static String normalizeHandlebars(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return HANDLEBARS_NORMALIZATION_PATTERN.matcher(input).replaceAll(matchResult -> {
            String group1 = matchResult.group(DOUBLE_CURLY_GROUP);
            String group2 = matchResult.group(CURLY_SQUARE_GROUP);
            String group3 = matchResult.group(TRIPLE_CURLY_GROUP);
            if (group1 != null) {
                return "{{" + group1 + "}}";
            }
            if (group2 != null) {
                return "{[" + group2 + "]}";
            }
            return "{{{" + group3 + "}}}";
        });
    }
}
