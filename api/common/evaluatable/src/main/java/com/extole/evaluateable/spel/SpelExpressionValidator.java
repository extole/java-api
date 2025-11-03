package com.extole.evaluateable.spel;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.QualifiedIdentifier;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public final class SpelExpressionValidator {
    private static final int EXPRESSION_MAX_LENGTH = 5000;
    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser(
        new SpelParserConfiguration(null, null, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE));
    private static final SpelExpressionValidator INSTANCE = new SpelExpressionValidator();

    public static SpelExpressionValidator getInstance() {
        return INSTANCE;
    }

    private SpelExpressionValidator() {
    }

    // TODO on part 2 of ENG-19753 do not catch validation exceptions but add them to the method signature
    public void validate(String expression) throws SpelExpressionCompileTimeException {
        validateCompileTimeErrors(expression);
    }

    private void validateExpressionLength(String expression) throws SpelExpressionInvalidLengthException {
        if (StringUtils.isBlank(expression) || expression.length() > EXPRESSION_MAX_LENGTH) {
            throw new SpelExpressionInvalidLengthException(expression, EXPRESSION_MAX_LENGTH);
        }
    }

    private void validateCompileTimeErrors(String expression) throws SpelExpressionCompileTimeException {
        SpelExpression spelExpression = internalParseExpression(expression);
        Set<String> nodesTypeDescriptors = getNodesTypeValue(spelExpression.getAST());

        if (!nodesTypeDescriptors.isEmpty()) {
            String errorMessage = String.format("Spel expression %s contains bad statements: %s",
                expression, nodesTypeDescriptors);
            throw new SpelExpressionCompileTimeException(errorMessage, expression);
        }
    }

    private SpelExpression internalParseExpression(String expression) throws SpelExpressionCompileTimeException {
        try {
            return SPEL_EXPRESSION_PARSER.parseRaw(expression);
        } catch (Exception e) {
            throw new SpelExpressionCompileTimeException(String.format("Spel %s has compile time errors", expression),
                expression, e);
        }
    }

    private Set<String> getNodesTypeValue(SpelNode rootSpelNode) {
        HashSet<String> fullTypeDefinitions = new HashSet<>();

        if (rootSpelNode instanceof QualifiedIdentifier) {
            QualifiedIdentifier qualifiedIdentifier = (QualifiedIdentifier) rootSpelNode;
            fullTypeDefinitions.add(qualifiedIdentifier.toStringAST());
            return fullTypeDefinitions;
        }

        for (int i = 0; i < rootSpelNode.getChildCount(); i++) {
            SpelNode currentNode = rootSpelNode.getChild(i);
            fullTypeDefinitions.addAll(getNodesTypeValue(currentNode));
        }

        return fullTypeDefinitions;
    }

}
