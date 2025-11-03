package com.extole.common.javascript;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.codahale.metrics.MetricRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.common.javascript.JavascriptRunnableFactory.JavascriptRunnable;
import com.extole.common.metrics.ExtoleMetricRegistry;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JavascriptRunnableFactoryTest.JavascriptRunnableTestConfig.class})
public class JavascriptRunnableFactoryTest {
    @Configuration
    public static class JavascriptRunnableTestConfig {
        @Bean
        public JavascriptRunnableFactory javascriptRunnableFactory() {
            return new JavascriptRunnableFactory(10000, 60000, 30000, 50, new JavascriptScriptEngineFactory(),
                new ExtoleMetricRegistry(new MetricRegistry()));
        }
    }

    @Autowired
    private JavascriptRunnableFactory javascriptRunnableFactory;

    private ExecutorService executor;

    @BeforeEach
    public void setUp() {
        executor = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    public void tearDown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Test
    public void testExecutionTimeout() {
        JavascriptRunnable javascriptRunnable = javascriptRunnableFactory.create(executor);
        javascriptRunnable.addJavascript("while(true) { print('a'); }");
        javascriptRunnable.setTimeoutMillis(200);
        assertThrows(JavascriptExecutionTimeoutException.class, () -> javascriptRunnable.execute());
    }

    @Test
    public void testContextSeparation() throws Exception {
        JavascriptRunnable runnable1 = javascriptRunnableFactory.create(executor);
        JavascriptRunnable runnable2 = javascriptRunnableFactory.create(executor);

        String script = "var x = function() { return { \"value\" : A } }; x();";

        String value1 = "1";
        String value2 = "2";

        runnable1.addVariable("A", value1);
        runnable2.addVariable("A", value2);

        runnable1.addJavascript(script);
        runnable2.addJavascript(script);

        JavascriptResult result1 = runnable1.execute();
        JavascriptResult result2 = runnable2.execute();

        assertThat(result1.getMemberAsString("value")).isEqualTo(value1);
        assertThat(result2.getMemberAsString("value")).isEqualTo(value2);
    }

    @Test
    public void testContextSeparationInParallel() throws Exception {
        AtomicInteger sequence = new AtomicInteger();
        Set<Throwable> errors = Collections.newSetFromMap(new ConcurrentHashMap<>());

        ExecutorService testExecutor = Executors.newFixedThreadPool(10);
        ExecutorService jsExecutor = Executors.newFixedThreadPool(10);
        try {
            for (int i = 0; i < 1000; i++) {
                testExecutor.execute(() -> {
                    String script = "var x = function() { return { \"value\" : A } }; x();";
                    JavascriptRunnable runnable = javascriptRunnableFactory.create(jsExecutor);
                    runnable.addJavascript(script);
                    String variableValue = String.valueOf(sequence.getAndIncrement());
                    runnable.addVariable("A", variableValue);
                    try {
                        JavascriptResult result = runnable.execute();
                        assertThat(result.getMemberAsString("value")).isEqualTo(variableValue);
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                });
            }
        } finally {
            testExecutor.shutdown();
            testExecutor.awaitTermination(1, TimeUnit.MINUTES);
            jsExecutor.shutdownNow();
        }

        assertThat(errors).isEmpty();
    }

    @Test
    public void testExecutionWithScriptObjectThatReturnsObject() throws Exception {
        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascript("function(arg) { return { \"value\" : arg } };");

        JavascriptResult javascriptResult = runnable.execute();
        Object result = javascriptResult.call("1");
        assertThat(((JSObject) result).getMember("value")).isEqualTo("1");
    }

    @Test
    public void testExecutionWithScriptObjectThatReturnsPrimitive() throws Exception {
        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascript("function(arg) { return arg; }");

        JavascriptResult javascriptResult = runnable.execute();
        Object result = javascriptResult.call("1");
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void testExecutionWithScriptObjectThatReturnsVoid() throws Exception {
        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascript("function(arg) { print(arg); };");

        JavascriptResult javascriptResult = runnable.execute();
        Object result = javascriptResult.call("1");
        assertThat(result.toString()).isEqualTo("undefined");
    }

    @Test
    public void testContextSeparationWithScriptObject() throws Exception {
        JavascriptRunnable runnable1 = javascriptRunnableFactory.create(executor);
        JavascriptRunnable runnable2 = javascriptRunnableFactory.create(executor);

        String script = "function(arg) { return x + arg; };";

        runnable1.addJavascript("var x = 'script1_';");
        runnable1.addJavascript(script);

        runnable2.addJavascript(script);
        runnable2.addJavascript("var x = 'script2_';");

        JavascriptResult result1 = runnable1.execute();
        JavascriptResult result2 = runnable2.execute();

        assertThat(result1.<Object>call("1")).isEqualTo("script1_1");
        assertThat(result1.<Object>call("2")).isEqualTo("script1_2");
        assertThat(result2.<Object>call("1")).isEqualTo("script2_1");
        assertThat(result2.<Object>call("2")).isEqualTo("script2_2");
    }

    @Test
    public void testContextSeparationWithScriptObjectInParallel() throws Exception {
        Set<Throwable> errors = Collections.newSetFromMap(new ConcurrentHashMap<>());
        ExecutorService testExecutor = Executors.newFixedThreadPool(10);

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascript("var x = 'script_';");
        runnable.addJavascript("function(arg) { return x + arg; };");
        JavascriptResult javascriptResult = runnable.execute();
        try {
            for (int i = 0; i < 1000000; i++) {
                String scriptNumber = "" + i;
                testExecutor.execute(() -> {
                    try {
                        assertThat(javascriptResult.<Object>call(scriptNumber)).isEqualTo("script_" + scriptNumber);
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                });
            }
        } finally {
            testExecutor.shutdown();
            testExecutor.awaitTermination(1, TimeUnit.MINUTES);
        }
        assertTrue(errors.isEmpty());
    }

}
