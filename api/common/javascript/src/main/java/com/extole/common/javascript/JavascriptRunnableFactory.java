package com.extole.common.javascript;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.log.execution.ExecutionLogger;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Component
public final class JavascriptRunnableFactory {
    private static final Logger LOG = LoggerFactory.getLogger(JavascriptRunnableFactory.class);

    private final ExtoleMetricRegistry metricRegistry;
    private final ScriptEngine defaultScriptEngine;
    private final int defaultTimeoutMillis;
    private final int warnExecutionThresholdMillis;
    private final int warnExecutionDelayThresholdMillis;

    @Autowired
    public JavascriptRunnableFactory(
        @Value("${javascriptFactory.execution.timeout.ms:180000}") int timeoutMillis,
        @Value("${javascriptFactory.warning.execution.threshold.ms:60000}") int warnExecutionThresholdMillis,
        @Value("${javascriptFactory.warning.execution.delay.threshold.ms:30000}") int warnExecutionDelayThresholdMillis,
        @Value("${javascriptFactory.nashorn.classCacheSize:1000}") int nashornClassCacheSize,
        JavascriptScriptEngineFactory javascriptScriptEngineFactory,
        ExtoleMetricRegistry metricRegistry) {
        this.defaultTimeoutMillis = timeoutMillis;
        this.warnExecutionThresholdMillis = warnExecutionThresholdMillis;
        this.warnExecutionDelayThresholdMillis = warnExecutionDelayThresholdMillis;
        this.metricRegistry = metricRegistry;
        this.defaultScriptEngine = javascriptScriptEngineFactory.create("defaultScriptEngine")
            .withLazyCompilation(false)
            .withClassCacheSize(nashornClassCacheSize)
            .build();
    }

    public JavascriptRunnable create(ExecutorService executor) {
        return create(executor, defaultScriptEngine);
    }

    public JavascriptRunnable create(ExecutorService executor, ScriptEngine scriptEngine) {
        JavascriptRunnable runnable = new JavascriptRunnable(executor, scriptEngine);
        runnable.setTimeoutMillis(defaultTimeoutMillis);
        return runnable;
    }

    public JavascriptRunnable create(ExecutorService executor, ScriptEngine scriptEngine,
        CompiledScript compiledScript) {
        JavascriptRunnable runnable = new JavascriptRunnable(executor, scriptEngine, compiledScript);
        runnable.setTimeoutMillis(defaultTimeoutMillis);
        return runnable;
    }

    private static void copyVariablesToBindings(Map<String, Object> variables, Bindings bindings) {
        variables.entrySet().stream().forEach(entry -> bindings.put(entry.getKey(), entry.getValue()));
    }

    public final class JavascriptRunnable {
        private static final int SCRIPT_SNIPPET_LENGTH = 1024;
        private static final String DURATION_METRICS_SUFFIX = ".duration.ms";
        private static final String DELAY_METRICS_SUFFIX = ".delay.ms";

        private final AtomicReference<String> executionThreadName = new AtomicReference<>();
        private final AtomicReference<Long> executionStartTime = new AtomicReference<>();
        private final Map<String, Object> variables = new HashMap<>();
        private final ExecutorService executor;
        private final ScriptEngine scriptEngine;
        private final CompiledScript compiledScript;
        private final StringWriter outputWriter = new StringWriter();
        private ExecutionLogger executionLogger;
        private long timeoutMillis;
        private String script = "";
        private Optional<JavascriptMetrics> durationMetric = Optional.empty();
        private Optional<JavascriptMetrics> delayMetric = Optional.empty();

        private JavascriptRunnable(ExecutorService executor, ScriptEngine scriptEngine) {
            this(executor, scriptEngine, null);
        }

        private JavascriptRunnable(ExecutorService executor, ScriptEngine scriptEngine, CompiledScript compiledScript) {
            this.executor = executor;
            this.scriptEngine = scriptEngine;
            this.compiledScript = compiledScript;
        }

        private List<String> getOutput() {
            return Arrays.asList(StringUtils.split(outputWriter.toString(), System.getProperty("line.separator")));
        }

        public void addJavascript(String script) {
            this.script += script;
        }

        public void addJavascriptResource(String resourceName) throws JavascriptResourceException {
            try {
                addJavascript(Resources.toString(Resources.getResource(resourceName), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new JavascriptResourceException("Unable to load resource: " + resourceName, e);
            }
        }

        public void addVariable(String name, Object value) {
            variables.put(name, value);
        }

        public void setTimeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
        }

        public void setMetricsPrefix(String metricsPrefix) {
            durationMetric = Optional.of(new JavascriptMetrics(metricsPrefix + DURATION_METRICS_SUFFIX));
            delayMetric = Optional.of(new JavascriptMetrics(metricsPrefix + DELAY_METRICS_SUFFIX));
        }

        public void setExecutionLogger(ExecutionLogger executionLogger) {
            this.executionLogger = executionLogger;
        }

        public JavascriptResult execute() throws JavascriptExecutionException {
            long startTime = System.currentTimeMillis();
            String scriptSnippet = getScriptSnippet();
            Future<Object> future = null;
            try {
                if (compiledScript != null) {
                    future = executor.submit(new CompiledJavascriptCallable());
                } else {
                    future = executor.submit(new JavascriptCallable());
                }
                Object result;
                if (timeoutMillis > 0) {
                    result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
                } else {
                    result = future.get();
                }
                return JavascriptResult.newInstance(scriptSnippet, result);
            } catch (ExecutionException e) {
                Throwable error = e.getCause() != null ? e.getCause() : e;
                throw new JavascriptExecutionException(
                    "Error executing script=" + scriptSnippet + ". Variables=" + variables, error,
                    buildErrorOutput(error));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new JavascriptExecutionException(
                    "Error executing script=" + scriptSnippet + ". Variables=" + variables, e, buildErrorOutput(e));
            } catch (TimeoutException e) {
                throw new JavascriptExecutionTimeoutException("Exceeded time limit of " + timeoutMillis
                    + " (ms) while executing script=" + scriptSnippet + ". Variables=" + variables, e,
                    buildErrorOutput(e));
            } catch (RejectedExecutionException e) {
                throw new JavascriptExecutionException(
                    "Error executing script=" + scriptSnippet + ". Variables=" + variables, e, buildErrorOutput(e));
            } finally {
                if (future != null) {
                    future.cancel(true);
                }
                long endTime = System.currentTimeMillis();
                long executionDurationMillis = endTime - startTime;
                long delayDurationMillis =
                    (executionStartTime.get() != null ? executionStartTime.get().longValue() : endTime) - startTime;

                durationMetric.ifPresent(metric -> metric.updateHistogram(metricRegistry, executionDurationMillis));
                delayMetric.ifPresent(metric -> metric.updateHistogram(metricRegistry, delayDurationMillis));

                if (executionDurationMillis > warnExecutionThresholdMillis) {
                    LOG.warn("Execution duration of {} ms exceeds the warning threshold ({} ms) for script: {}",
                        Long.valueOf(executionDurationMillis), Integer.valueOf(warnExecutionThresholdMillis),
                        scriptSnippet);
                }
                if (delayDurationMillis > warnExecutionDelayThresholdMillis) {
                    LOG.warn("Execution delay duration of {} ms exceeds the warning threshold ({} ms) for script: {}",
                        Long.valueOf(delayDurationMillis), Integer.valueOf(warnExecutionDelayThresholdMillis),
                        scriptSnippet);
                }
                if (executionLogger != null) {
                    for (String line : getOutput()) {
                        executionLogger.trace("Javascript output: " + line);
                    }
                    executionLogger.trace("Javascript execution duration (ms): " + executionDurationMillis);
                    executionLogger.trace("Javascript execution delay duration (ms): " + delayDurationMillis);
                    executionLogger.trace("Javascript execution thread: " + executionThreadName.get());
                }
            }
        }

        private String getScriptSnippet() {
            if (compiledScript != null) {
                return "<CompiledScript>";
            }

            int start = 0;
            String elipses = "";
            if (script.length() > SCRIPT_SNIPPET_LENGTH) {
                start = script.length() - SCRIPT_SNIPPET_LENGTH;
                elipses = "...";
            }
            return "<" + elipses + script.substring(start, script.length()) + ">";
        }

        private List<String> buildErrorOutput(Throwable error) {
            List<String> output = new ArrayList<>(getOutput());
            output.add("Error: " + error.toString());
            if (error.getCause() != null) {
                output.add("Error cause: " + error.getCause().toString());
            }
            return output;
        }

        private final class JavascriptCallable implements Callable<Object> {
            @Override
            public Object call() throws Exception {
                executionStartTime.set(Long.valueOf(System.currentTimeMillis()));
                executionThreadName.set(Thread.currentThread().getName());

                Bindings bindings = scriptEngine.createBindings();
                copyVariablesToBindings(variables, bindings);

                ScriptContext context = new SimpleScriptContext();
                context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
                context.setWriter(outputWriter);

                return scriptEngine.eval(script, context);
            }
        }

        private final class CompiledJavascriptCallable implements Callable<Object> {
            @Override
            public Object call() throws Exception {
                executionStartTime.set(Long.valueOf(System.currentTimeMillis()));
                executionThreadName.set(Thread.currentThread().getName());

                Bindings bindings = scriptEngine.createBindings();
                copyVariablesToBindings(variables, bindings);

                ScriptContext context = new SimpleScriptContext();
                context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
                context.setWriter(outputWriter);

                return compiledScript.eval(context);
            }
        }
    }
}
