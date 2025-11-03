package com.extole.common.event;

import java.io.Serializable;

import com.extole.common.lang.ToString;

public class Topic implements Serializable {
    private final String name;
    private final KafkaClusterType clusterType;

    public Topic(String name, KafkaClusterType clusterType) {
        this.name = name;
        this.clusterType = clusterType;
    }

    public String getName() {
        return name;
    }

    public KafkaClusterType getClusterType() {
        return clusterType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
