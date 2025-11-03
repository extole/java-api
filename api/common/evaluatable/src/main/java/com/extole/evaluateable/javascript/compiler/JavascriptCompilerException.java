package com.extole.evaluateable.javascript.compiler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JavascriptCompilerException extends Exception {
    private final List<String> output;
    private final Optional<String> path;

    public JavascriptCompilerException(String message, List<String> output, String path) {
        super(appendOutputToMessage(message, output));
        this.output = Collections.unmodifiableList(output);
        this.path = Optional.of(path);
    }

    public JavascriptCompilerException(String message, Throwable cause, String path) {
        super(message, cause);
        this.output = Collections.emptyList();
        this.path = Optional.of(path);
    }

    public JavascriptCompilerException(String message, Throwable cause) {
        super(message, cause);
        this.output = Collections.emptyList();
        this.path = Optional.empty();
    }

    public JavascriptCompilerException(String message, Throwable cause, List<String> output, String path) {
        super(appendOutputToMessage(message, output), cause);
        this.output = Collections.unmodifiableList(output);
        this.path = Optional.of(path);
    }

    public JavascriptCompilerException(String message, Throwable cause, List<String> output) {
        super(appendOutputToMessage(message, output), cause);
        this.output = Collections.unmodifiableList(output);
        this.path = Optional.empty();
    }

    public List<String> getOutput() {
        return output;
    }

    private static String appendOutputToMessage(String message, List<String> output) {
        return output.isEmpty() ? message : (message + ", output=" + output);
    }

    public Optional<String> getPath() {
        return path;
    }
}
