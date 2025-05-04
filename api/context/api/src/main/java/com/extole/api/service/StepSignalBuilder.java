package com.extole.api.service;

public interface StepSignalBuilder {

    StepSignalBuilder withName(String name);

    StepSignalBuilder addData(String key, Object value);

    void send();

}
