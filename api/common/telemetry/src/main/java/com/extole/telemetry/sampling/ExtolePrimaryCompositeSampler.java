package com.extole.telemetry.sampling;

import java.util.List;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

public class ExtolePrimaryCompositeSampler implements Sampler {

    private final List<Sampler> samplers;

    public ExtolePrimaryCompositeSampler(List<Sampler> samplers) {
        this.samplers = samplers;
    }

    @Override
    public SamplingResult shouldSample(Context parentContext, String traceId, String name, SpanKind spanKind,
        Attributes attributes, List<LinkData> parentLinks) {
        for (Sampler sampler : samplers) {
            SamplingResult result =
                sampler.shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks);
            if (!SamplingResult.drop().equals(result)) {
                return result;
            }
        }

        return SamplingResult.drop();
    }

    @Override
    public String getDescription() {
        return "Extole custom OpenTelemetry sampler that allows chaining multiple samplers together.";
    }
}
