package com.extole.common.javascript;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.script.ScriptEngine;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JavascriptScriptEngineFactory {
    private static final Logger LOG = LoggerFactory.getLogger(JavascriptScriptEngineFactory.class);

    private final NashornScriptEngineFactory scriptEngineFactory = new NashornScriptEngineFactory();

    public ScriptEngineBuilder create(String name) {
        return new ScriptEngineBuilder(name);
    }

    public class ScriptEngineBuilder {
        private final String name;

        private Optional<Boolean> globalPerEngine = Optional.empty();
        private Optional<Boolean> strict = Optional.empty();
        private Optional<Boolean> lazyCompilation = Optional.empty();
        private Optional<Boolean> optimisticTypes = Optional.of(Boolean.FALSE);
        private Optional<Integer> classCacheSize = Optional.empty();

        public ScriptEngineBuilder(String name) {
            this.name = name;
        }

        public ScriptEngineBuilder withLazyCompilation(boolean lazyCompilation) {
            this.lazyCompilation = Optional.of(lazyCompilation);
            return this;
        }

        public ScriptEngineBuilder withOptimisticTypes(boolean optimisticTypes) {
            this.optimisticTypes = Optional.of(optimisticTypes);
            return this;
        }

        public ScriptEngineBuilder withClassCacheSize(int classCacheSize) {
            this.classCacheSize = Optional.of(classCacheSize);
            return this;
        }

        public ScriptEngineBuilder withStrict(boolean strict) {
            this.strict = Optional.of(strict);
            return this;
        }

        public ScriptEngineBuilder withGlobalPerEngine(boolean globalPerEngine) {
            this.globalPerEngine = Optional.of(globalPerEngine);
            return this;
        }

        public ScriptEngine build() {
            List<String> arguments = new ArrayList<>();
            arguments.add("--language=es6");
            globalPerEngine.filter(value -> value).ifPresent(value -> arguments.add("--global-per-engine"));
            strict.filter(value -> value).ifPresent(value -> arguments.add("-strict"));
            lazyCompilation.ifPresent(value -> arguments.add("--lazy-compilation=" + value));
            optimisticTypes.ifPresent(value -> arguments.add("--optimistic-types=" + value));
            classCacheSize.ifPresent(value -> arguments.add("--class-cache-size=" + value));

            String[] argumentsArray = arguments.toArray(new String[arguments.size()]);

            LOG.info("Creating nashorn script engine {} with arguments: {}", name, arguments);

            return scriptEngineFactory.getScriptEngine(argumentsArray);
        }
    }
}
