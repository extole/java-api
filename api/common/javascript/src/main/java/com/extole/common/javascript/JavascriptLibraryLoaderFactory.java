package com.extole.common.javascript;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.common.log.execution.ExecutionLogger;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Component
public class JavascriptLibraryLoaderFactory {
    private static final Logger LOG = LoggerFactory.getLogger(JavascriptLibraryLoader.class);
    private static final String FILE_ROOT = "file://" + File.separator;

    private final DefaultJavascriptLibraryProvider defaultJavascriptLibraryProvider;
    private final ExtoleMetricRegistry metricRegistry;

    @Autowired
    public JavascriptLibraryLoaderFactory(DefaultJavascriptLibraryProvider defaultJavascriptLibraryProvider,
        ExtoleMetricRegistry metricRegistry) {
        this.defaultJavascriptLibraryProvider = defaultJavascriptLibraryProvider;
        this.metricRegistry = metricRegistry;
    }

    public JavascriptLibraryLoaderBuilder newLibraryLoader() {
        return newLibraryLoader(defaultJavascriptLibraryProvider);
    }

    public JavascriptLibraryLoaderBuilder newLibraryLoader(JavascriptLibraryProvider javascriptLibraryProvider) {
        return new JavascriptLibraryLoaderBuilderImpl(javascriptLibraryProvider, metricRegistry);
    }

    public interface JavascriptLibraryLoaderBuilder {
        JavascriptLibraryLoaderBuilder addLibrary(URI uri, String library);

        JavascriptLibraryLoaderBuilder addAssetMapping(Pattern match, String replace);

        JavascriptLibraryLoaderBuilder withExecutionLogger(ExecutionLogger executionLogger);

        JavascriptLibraryLoader build();
    }

    private static final class JavascriptLibraryLoaderBuilderImpl implements JavascriptLibraryLoaderBuilder {
        private final JavascriptLibraryLoaderImpl libraryLoader;

        JavascriptLibraryLoaderBuilderImpl(JavascriptLibraryProvider javascriptLibraryProvider,
            ExtoleMetricRegistry metricRegistry) {
            libraryLoader = new JavascriptLibraryLoaderImpl(javascriptLibraryProvider, metricRegistry);
        }

        @Override
        public JavascriptLibraryLoaderBuilder addLibrary(URI uri, String library) {
            libraryLoader.addLibrary(uri.toString(), library);
            return this;
        }

        @Override
        public JavascriptLibraryLoaderBuilder addAssetMapping(Pattern match, String replace) {
            libraryLoader.addAssetMapping(match, replace);
            return this;
        }

        @Override
        public JavascriptLibraryLoaderBuilder withExecutionLogger(ExecutionLogger executionLogger) {
            libraryLoader.setExecutionLogger(executionLogger);
            return this;
        }

        @Override
        public JavascriptLibraryLoader build() {
            return libraryLoader;
        }
    }

    private static final class JavascriptLibraryLoaderImpl implements JavascriptLibraryLoader {
        private static final JavascriptMetrics LIBRARY_LOAD_DURATION =
            new JavascriptMetrics("javascript.library.load.duration.ms");
        private static final String HTTP = "http";

        private final JavascriptLibraryProvider javascriptLibraryProvider;
        private final ExtoleMetricRegistry metricRegistry;
        private final Map<String, String> libraries = Maps.newHashMap();
        private final Map<Pattern, String> assetMappings = Maps.newHashMap();
        private ExecutionLogger executionLogger;

        JavascriptLibraryLoaderImpl(JavascriptLibraryProvider javascriptLibraryProvider,
            ExtoleMetricRegistry metricRegistry) {
            this.javascriptLibraryProvider = javascriptLibraryProvider;
            this.metricRegistry = metricRegistry;
        }

        @Override
        public String getNormalizedUri(String uri) {
            for (Entry<Pattern, String> entry : assetMappings.entrySet()) {
                uri = entry.getKey().matcher(uri).replaceAll(entry.getValue());
            }
            return uri;
        }

        @Override
        public String getNormalizedName(String uri) {
            if (uri.matches("^https?://origin\\..*")) {
                String path = uri.substring(uri.indexOf("type="));
                return path.replaceAll("version=\\d+:", "");
            }
            return uri;
        }

        @Override
        public String get(String uri) throws JavascriptLibraryLoaderException {
            uri = getNormalizedUri(uri);
            if (isRemoteLibrary(uri)) {
                try {
                    return getRemoteLibrary(uri);
                } catch (JavascriptLibraryLoadException e) {
                    throw new JavascriptRemoteLibraryLoaderException("Unable to satisfy dependency: " + uri, e);
                } catch (JavascriptLibraryUriException e) {
                    throw new JavascriptLibraryLoaderException("Could not load remote library by uri: " + uri, e);
                }
            }

            if (!uri.startsWith(FILE_ROOT)) {
                uri = FILE_ROOT + uri;
            }

            String library = libraries.get(uri);
            if (library == null) {
                throw new JavascriptLibraryLoaderException("Javascript library: '" + uri
                    + "' not found. Available libraries: " + libraries.keySet());
            }
            return library;
        }

        private void addLibrary(String uri, String library) {
            libraries.put(uri, library);
        }

        private void addAssetMapping(Pattern match, String replace) {
            assetMappings.put(match, replace);
        }

        private void setExecutionLogger(ExecutionLogger executionLogger) {
            this.executionLogger = executionLogger;
        }

        private boolean isRemoteLibrary(String uri) {
            return uri.startsWith(HTTP);
        }

        private String getRemoteLibrary(String uri)
            throws JavascriptLibraryLoadException, JavascriptLibraryUriException {
            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                return javascriptLibraryProvider.getLibrary(uri);
            } finally {
                stopwatch.stop();
                long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                LOG.trace("Retrieved library in: {} (ms) - {} using provider: {}", Long.valueOf(elapsedTime), uri,
                    javascriptLibraryProvider);
                LIBRARY_LOAD_DURATION.updateHistogram(metricRegistry, elapsedTime);
                if (executionLogger != null) {
                    executionLogger.trace("Retrieved library in (ms): " + elapsedTime + " - " + uri);
                }
            }
        }
    }
}
