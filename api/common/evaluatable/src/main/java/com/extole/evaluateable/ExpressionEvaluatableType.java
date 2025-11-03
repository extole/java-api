package com.extole.evaluateable;

import static com.extole.evaluateable.Evaluatable.BUILDTIME;
import static com.extole.evaluateable.Evaluatable.EXPRESSION_DELIMITER;
import static com.extole.evaluateable.Evaluatable.HANDLEBARS;
import static com.extole.evaluateable.Evaluatable.INSTALLTIME;
import static com.extole.evaluateable.Evaluatable.JAVASCRIPT;
import static com.extole.evaluateable.Evaluatable.JS2025;
import static com.extole.evaluateable.Evaluatable.PHASE_DELIMITER;
import static com.extole.evaluateable.Evaluatable.RUNTIME;
import static com.extole.evaluateable.Evaluatable.SPEL;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum ExpressionEvaluatableType {
    BUILD_TIME_SPEL(BUILDTIME, SPEL),
    RUN_TIME_SPEL(RUNTIME, SPEL),
    INSTALL_TIME_SPEL(INSTALLTIME, SPEL),
    BUILD_TIME_JS(BUILDTIME, JAVASCRIPT),
    RUNTIME_TIME_JS(RUNTIME, JAVASCRIPT),
    INSTALL_TIME_JS(INSTALLTIME, JAVASCRIPT),
    BUILD_TIME_HANDLEBARS(BUILDTIME, HANDLEBARS),
    RUNTIME_TIME_HANDLEBARS(RUNTIME, HANDLEBARS),
    INSTALL_TIME_HANDLEBARS(INSTALLTIME, HANDLEBARS),
    BUILD_TIME_JS2025(BUILDTIME, JS2025),
    RUNTIME_TIME_JS2025(RUNTIME, JS2025),
    INSTALL_TIME_JS2025(INSTALLTIME, JS2025);

    private final String phase;
    private final String language;
    private final String prefixHeader;

    ExpressionEvaluatableType(String phase, String language) {
        this.phase = phase;
        this.language = language;
        this.prefixHeader = language + PHASE_DELIMITER + phase + EXPRESSION_DELIMITER;
    }

    public String getPhase() {
        return phase;
    }

    public String getLanguage() {
        return language;
    }

    public String getPrefixHeader() {
        return prefixHeader;
    }

    private static final Map<String, ExpressionEvaluatableType> BY_PREFIX;
    static {
        Map<String, ExpressionEvaluatableType> internalCache = new HashMap<>();
        for (ExpressionEvaluatableType type : values()) {
            internalCache.put(type.prefixHeader, type);
        }
        BY_PREFIX = Collections.unmodifiableMap(internalCache);
    }

    public static Optional<ExpressionEvaluatableType> detect(String value) {
        int end = value.indexOf(EXPRESSION_DELIMITER);
        if (end < 0) {
            return Optional.empty();
        }
        String header = value.substring(0, end + EXPRESSION_DELIMITER.length());
        return Optional.ofNullable(BY_PREFIX.get(header));
    }
}
