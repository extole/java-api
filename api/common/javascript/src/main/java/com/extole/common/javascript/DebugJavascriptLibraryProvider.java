package com.extole.common.javascript;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.spring.ApplicationNameDeterminer;

@Component
public class DebugJavascriptLibraryProvider implements JavascriptLibraryProvider {
    private final RemoteJavascriptLibraryProvider javascriptLibraryProvider;

    @Autowired
    public DebugJavascriptLibraryProvider(
        @Value("${javascriptFactory.library.cache.expiration.minutes:1440}") int cacheExpirationMinutes,
        @Value("${javascriptFactory.library.cache.size:1000}") int cacheSize,
        ExtoleMetricRegistry metricRegistry,
        ApplicationContext applicationContext) {
        this.javascriptLibraryProvider =
            new RemoteJavascriptLibraryProvider(metricRegistry, cacheExpirationMinutes, cacheSize, false,
                new ApplicationNameDeterminer().fromApplicationContext(applicationContext));
    }

    @PreDestroy
    public void shutdown() {
        javascriptLibraryProvider.shutdown();
    }

    @Override
    public String getLibrary(String uri) throws JavascriptLibraryLoadException {
        return javascriptLibraryProvider.getLibrary(uri);
    }
}
