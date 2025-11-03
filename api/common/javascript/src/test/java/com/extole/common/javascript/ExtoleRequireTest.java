package com.extole.common.javascript;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.codahale.metrics.MetricRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.common.javascript.JavascriptRunnableFactory.JavascriptRunnable;
import com.extole.common.metrics.ExtoleMetricRegistry;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ExtoleRequireTest.JavascriptRunnableTestConfig.class})
public class ExtoleRequireTest {
    private static final String EXTOLE_REQUIRE = "extole-require.js";

    @Configuration
    public static class JavascriptRunnableTestConfig {
        @Bean
        public JavascriptRunnableFactory javascriptRunnableFactory() {
            return new JavascriptRunnableFactory(10000, 60000, 30000, 50, new JavascriptScriptEngineFactory(),
                new ExtoleMetricRegistry(new MetricRegistry()));
        }

        @Bean
        public JavascriptLibraryLoaderFactory javascriptLibraryLoaderFactory() {
            return new JavascriptLibraryLoaderFactory(null, new ExtoleMetricRegistry(new MetricRegistry()));
        }
    }

    @Autowired
    private JavascriptRunnableFactory javascriptRunnableFactory;

    @Autowired
    private JavascriptLibraryLoaderFactory javascriptLibraryLoaderFactory;

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
    public void testAnonymousModuleDefinition() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader()
            .addLibrary(new URI("file:///dependency.js"),
                "extole.define([], function () {" +
                    "    return 'dependency_value';" +
                    "});")
            .build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.define(['dependency.js'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        JavascriptResult result = runnable.execute();

        assertTrue(result.getMember("value").equals("dependency_value"));
    }

    @Test
    public void testNamedModuleDefinition() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader().build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.define('dependency', [], function () {" +
                "    return 'dependency_value';" +
                "});");
        runnable.addJavascript(
            "extole.define(['dependency'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        JavascriptResult result = runnable.execute();

        assertTrue(result.getMember("value").equals("dependency_value"));
    }

    @Test
    public void testNamedModuleDefinitionVisibility() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader().build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.define('dependency', [], function () {" +
                "    return 'dependency_value';" +
                "});");
        runnable.addJavascript(
            "extole.define(['dependency'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        JavascriptResult result = runnable.execute();

        assertTrue(result.getMember("value").equals("dependency_value"));

        JavascriptLibraryLoader libraryLoader2 = javascriptLibraryLoaderFactory.newLibraryLoader().build();
        JavascriptRunnable runnable2 = javascriptRunnableFactory.create(executor);
        runnable2.addJavascriptResource(EXTOLE_REQUIRE);
        runnable2.addJavascript(
            "extole.define(['dependency'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable2.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader2);
        assertThrows(JavascriptExecutionException.class, () -> runnable2.execute());
    }

    @Test
    public void testGetNormalizedUri() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader()
            .addAssetMapping(Pattern.compile("creative-root://"), "file://")
            .addAssetMapping(Pattern.compile("^" + File.separator), "file://" + File.separator)
            .addLibrary(new URI("file:///dependency1.js"),
                "extole.define([], function () {" +
                    "   return 'dependency_value_1';" +
                    "});")
            .addLibrary(new URI("file:///dependency2.js"),
                "extole.define([], function () {" +
                    "    return 'dependency_value_2';" +
                    "});")
            .addLibrary(new URI("file:///dependency3.js"),
                "extole.define([], function () {" +
                    "    return 'dependency_value_3';" +
                    "});")
            .build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.require(['creative-root:///dependency1.js', '/dependency2.js', 'dependency3.js'], " +
                "    function (dependency1, dependency2, dependency3) {" +
                "        return {value1: dependency1, value2: dependency2, value3: dependency3};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        JavascriptResult result = runnable.execute();

        assertTrue(result.getMember("value1").equals("dependency_value_1"));
        assertTrue(result.getMember("value2").equals("dependency_value_2"));
        assertTrue(result.getMember("value3").equals("dependency_value_3"));
    }

    @Test
    public void testRequire() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader()
            .addLibrary(new URI("file:///dependency.js"),
                "extole.define([], function () {" +
                    "    return 'dependency_value';" +
                    "});")
            .build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.require(['dependency.js'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        JavascriptResult result = runnable.execute();

        assertTrue(result.getMember("value").equals("dependency_value"));
    }

    @Test
    public void testRequireVisibility() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader()
            .addLibrary(new URI("file:///dependency.js"),
                "extole.define([], function () {" +
                    "    return 'dependency_value';" +
                    "});")
            .build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.require(['dependency.js'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        JavascriptResult result = runnable.execute();

        assertTrue(result.getMember("value").equals("dependency_value"));

        JavascriptLibraryLoader libraryLoader2 = javascriptLibraryLoaderFactory.newLibraryLoader().build();
        JavascriptRunnable runnable2 = javascriptRunnableFactory.create(executor);
        runnable2.addJavascriptResource(EXTOLE_REQUIRE);
        runnable2.addJavascript(
            "extole.require(['dependency.js'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable2.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader2);
        assertThrows(JavascriptExecutionException.class, () -> runnable2.execute());
    }

    @Test
    public void testCreativeRootRequire() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader()
            .addAssetMapping(Pattern.compile("creative-root://"), "file://")
            .addLibrary(new URI("file:///dependency.js"),
                "extole.define([], function () {" +
                    "    return 'dependency_value';" +
                    "});")
            .build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.require(['creative-root:///dependency.js'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        JavascriptResult result = runnable.execute();

        assertTrue(result.getMember("value").equals("dependency_value"));
    }

    @Test
    public void testCircularDependencyDetection() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader()
            .addLibrary(new URI("file:///dependency_2.js"),
                "extole.define(['dependency_1.js'], function (dependency1) {" +
                    "    return 'dependency_1_value';" +
                    "});")
            .addLibrary(new URI("file:///dependency_1.js"),
                "extole.define(['dependency_2.js'], function (dependency2) {" +
                    "    return 'dependency_2_value';" +
                    "});")
            .build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.define(['dependency_1.js'], function (dependency1) {" +
                "    return {value: dependency1};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        assertThrows(JavascriptExecutionException.class, () -> runnable.execute());
    }

    @Test
    public void testInvalidDependency() throws Exception {
        JavascriptLibraryLoader libraryLoader = javascriptLibraryLoaderFactory.newLibraryLoader()
            .addLibrary(new URI("file:///dependency.js"),
                "extole.define([], function () {" +
                    "    return 'dependency_value';" +
                    "});")
            .build();

        JavascriptRunnable runnable = javascriptRunnableFactory.create(executor);
        runnable.addJavascriptResource(EXTOLE_REQUIRE);
        runnable.addJavascript(
            "extole.require(['dependency_invalid.js'], function (dependency) {" +
                "    return {value: dependency};" +
                "});");
        runnable.addVariable(JavascriptLibraryLoader.VARIABLE_NAME, libraryLoader);
        assertThrows(JavascriptExecutionException.class, () -> runnable.execute());
    }

}
