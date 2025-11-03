package com.extole.common.event.topic;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.event.KafkaClusterType;
import com.extole.common.event.Topic;

@Component
public class BaseTopicSetDefaulter {
    private final List<TopicConfig> defaultTopicConfigs;

    @Autowired
    public BaseTopicSetDefaulter(@Value("${kafka.topic.retry.intervals:1000,30000,30000}") String retryIntervals,
        @Value("${kafka.topic.batch.sizes:100,10,1}") String batchSizes,
        @Value("${kafka.topic.partition.counts:6,1,1}") String partitionCounts,
        @Value("${kafka.topic.replication.factors:3,3,3}") String replicationFactors) {
        List<Integer> partitionCountsDefault = mapToList(partitionCounts, Integer::valueOf);
        List<Short> replicationFactorsDefault = mapToList(replicationFactors, Short::valueOf);
        List<Duration> retryIntervalsDefault =
            mapToList(retryIntervals, msString -> Duration.ofMillis(Long.valueOf(msString).longValue()));
        List<Long> batchSizesDefault = mapToList(batchSizes, Long::valueOf);
        defaultTopicConfigs = new ArrayList<>();
        int i = 0;
        for (Integer partitionCount : partitionCountsDefault) {
            defaultTopicConfigs.add(new TopicConfigImpl(i,
                new Topic("default", KafkaClusterType.EDGE),
                retryIntervalsDefault.get(i),
                batchSizesDefault.get(i),
                new Topic("onFailureDefault", KafkaClusterType.EDGE),
                partitionCount,
                replicationFactorsDefault.get(i),
                Optional.empty(),
                Optional.empty()));
            i++;
        }
    }

    public TopicSetBuilder create(Topic topic) {
        return new TopicSetBuilderImpl(defaultTopicConfigs, topic);
    }

    private <T> List<T> mapToList(String sourceString, Function<String, ? extends T> mapper) {
        return Arrays.stream(sourceString.split(","))
            .peek(String::trim)
            .map(mapper)
            .collect(Collectors.toList());
    }
}
