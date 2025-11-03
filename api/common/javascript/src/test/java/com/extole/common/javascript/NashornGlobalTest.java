package com.extole.common.javascript;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class NashornGlobalTest {
    private static final ScriptEngine SCRIPT_ENGINE = new JavascriptScriptEngineFactory().create("NashornGlobalTest")
        .withGlobalPerEngine(true)
        .withStrict(true)
        .withLazyCompilation(false)
        .withClassCacheSize(1000)
        .build();

    @Test
    public void testParallelExecutionReads() throws Exception {
        System.out.println(Instant.now() + " start");

        ThreadPoolExecutor executor =
            new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        executor.prestartAllCoreThreads();

        List<Pair<Integer, Future<Integer>>> results = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            final int sequence = i;
            Future<Integer> future = executor.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return (Integer) executeReadWithValue(sequence);
                }
            });
            results.add(Pair.of(sequence, future));
        }
        executor.shutdown();

        System.out.println(Instant.now() + " end");

        executor.awaitTermination(5, TimeUnit.MINUTES);

        System.out.println(Instant.now() + " completed");

        for (Pair<Integer, Future<Integer>> pair : results) {
            assertThat(pair.getRight().get()).isEqualTo(pair.getLeft());
        }
    }

    @Test
    public void testParallelExecutions() throws Exception {
        System.out.println(Instant.now() + " start");
        ThreadPoolExecutor executor =
            new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        executor.prestartAllCoreThreads();

        List<Pair<Integer, Future<Integer>>> results = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            final int sequence = i;
            Future<Integer> future = executor.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return (Integer) executeWithValue(sequence);
                }
            });
            results.add(Pair.of(sequence, future));
        }
        executor.shutdown();

        System.out.println(Instant.now() + " end");

        executor.awaitTermination(5, TimeUnit.MINUTES);

        System.out.println(Instant.now() + " completed");

        for (Pair<Integer, Future<Integer>> pair : results) {
            assertThat(pair.getRight().get()).isEqualTo(pair.getLeft());
        }
    }

    @Test
    public void testVariablesVisibility() throws Exception {
        Bindings bindings = SCRIPT_ENGINE.createBindings();
        bindings.put("context", new TestContext(1));
        ScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        String conditionScript = "(function() { return context.getValue(); })();";
        SCRIPT_ENGINE.eval(conditionScript, scriptContext);

        Bindings bindings2 = SCRIPT_ENGINE.createBindings();
        ScriptContext scriptContext2 = new SimpleScriptContext();
        scriptContext2.setBindings(bindings2, ScriptContext.ENGINE_SCOPE);

        try {
            SCRIPT_ENGINE.eval(conditionScript, scriptContext2);
            fail("Expecting script exception");
        } catch (ScriptException e) {
            Throwable cause = e.getCause();
            assertTrue(cause.getMessage().contains("context.getValue is not a function"));
        }
    }

    private Object executeReadWithValue(Integer value) throws ScriptException {
        TestContext context = new TestContext(value);

        Bindings bindings = SCRIPT_ENGINE.createBindings();
        bindings.put("context", context);
        ScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        String conditionScript = "(function() { return context.getValue(); })();";
        return SCRIPT_ENGINE.eval(conditionScript, scriptContext);
    }

    private Object executeWithValue(Integer value) throws ScriptException {
        TestContext context = new TestContext(value);
        TestContextBuilder contextBuilder = new TestContextBuilder();

        Bindings bindings = SCRIPT_ENGINE.createBindings();
        bindings.put("context", context);
        bindings.put("contextBuilder", contextBuilder);
        ScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        String conditionScript = "(function() { contextBuilder.setValue(context.getValue()); })();";
        SCRIPT_ENGINE.eval(conditionScript, scriptContext);
        return contextBuilder.getValue();
    }

    public static class TestContext {
        private final Integer value;

        public TestContext(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static class TestContextBuilder {
        private Integer value;

        public TestContextBuilder() {
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

}
