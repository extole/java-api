package com.extole.common.javascript;

import java.util.Collections;
import java.util.List;

import org.openjdk.nashorn.api.scripting.NashornException;

import com.extole.common.lang.exception.NotifiableException;

@NotifiableException(notifiableRuntimeCauses = {"org.openjdk.nashorn.api.scripting.NashornException",
    "java.lang.ClassCastException",
    "com.extole.api.impl.NullArgumentRuntimeException"})
public class JavascriptExecutionException extends Exception {
    private final List<String> output;

    public JavascriptExecutionException(String message) {
        super(message);
        this.output = Collections.emptyList();
    }

    public JavascriptExecutionException(String message, Throwable cause) {
        super(appendJsStackTraceToMessage(message, cause), cause);
        this.output = Collections.emptyList();
    }

    public JavascriptExecutionException(String message, Throwable cause, List<String> output) {
        super(appendJsStackTraceToMessage(appendOutputToMessage(message, output), cause), cause);
        this.output = Collections.unmodifiableList(output);
    }

    public List<String> getOutput() {
        return output;
    }

    private static String appendOutputToMessage(String message, List<String> output) {
        return output.isEmpty() ? message : (message + ", output=" + output);
    }

    private static String appendJsStackTraceToMessage(String message, Throwable e) {
        return (e.getCause() != null && e.getCause() instanceof NashornException)
            ? message + ", \njsStackTrace: {\n" + NashornException.getScriptStackString(e.getCause()) + " \n\t}"
            : message;
    }
}
