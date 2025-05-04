package com.extole.api.impl.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.extole.api.service.StepSignalBuilder;
import com.extole.event.pending.operation.signal.StepSignalPendingOperationEventProducer;
import com.extole.event.pending.operation.signal.step.QualityResults;
import com.extole.event.pending.operation.signal.step.StepSignal;

public class StepSignalBuilderImpl implements StepSignalBuilder {

    private final Map<String, Object> data;
    private String name;
    private final StepSignalPendingOperationEventProducer.StepSignalEventBuilder eventBuilder;

    public StepSignalBuilderImpl(StepSignalPendingOperationEventProducer.StepSignalEventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
        this.data = new HashMap<>();
    }

    @Override
    public StepSignalBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public StepSignalBuilder addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    public void send() {
        QualityResults emptyQualityResult = new QualityResults(QualityResults.QualityScore.HIGH, List.of());
        this.eventBuilder.withSignal(new StepSignal(name, false,
            Optional.empty(), data, Set.of(), emptyQualityResult))
            .send();
    }
}
