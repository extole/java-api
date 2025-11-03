package com.extole.common.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.metrics.ExtoleMetricRegistry;

@Component
public final class SensitiveDataObfuscationService {
    private final String hashAlgorithm;
    private final SensitiveDataObfuscator sensitiveDataObfuscator;
    private final ExtoleMetricRegistry extoleMetricRegistry;

    private SensitiveDataObfuscationService(
        @Value("${sensitive.data.obfuscation.hash.algorithm:SHA1}") String hashAlgorithm,
        ExtoleMetricRegistry extoleMetricRegistry) {
        this.hashAlgorithm = hashAlgorithm;
        this.sensitiveDataObfuscator = SensitiveDataObfuscator.forAlgorithm(hashAlgorithm);
        this.extoleMetricRegistry = extoleMetricRegistry;
    }

    public String hashRequestBody(String value) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            return sensitiveDataObfuscator.hashRequestBody(value);
        } finally {
            updateHistogram(stopwatch, "Body");
        }
    }

    public String hashRequestUrl(String url) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            return sensitiveDataObfuscator.hashRequestUrl(url);
        } finally {
            updateHistogram(stopwatch, "Url");
        }
    }

    public Map<String, String> mapToSafeMap(Map<String, String> map,
        ObfuscationStrategy strategy) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            return sensitiveDataObfuscator.mapToSafeMap(map, strategy);
        } finally {
            updateHistogram(stopwatch, strategy.name());
        }
    }

    public Map<String, List<String>> multimapToSafeMap(Multimap<String, String> multimap,
        ObfuscationStrategy strategy) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            return sensitiveDataObfuscator.multimapToSafeMap(multimap, strategy);
        } finally {
            updateHistogram(stopwatch, strategy.name());
        }
    }

    public Map<String, List<String>> listMapToSafeMap(Map<String, List<String>> listMap,
        ObfuscationStrategy strategy) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            return sensitiveDataObfuscator.listMapToSafeMap(listMap, strategy);
        } finally {
            updateHistogram(stopwatch, strategy.name());
        }
    }

    private void updateHistogram(Stopwatch stopwatch, String strategyName) {
        extoleMetricRegistry
            .histogram("sensitive.data.obfuscator." + strategyName.toLowerCase() + "." + hashAlgorithm)
            .update(stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

}
