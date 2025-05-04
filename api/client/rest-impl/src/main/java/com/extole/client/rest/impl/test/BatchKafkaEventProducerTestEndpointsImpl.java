package com.extole.client.rest.impl.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.client.rest.test.BatchKafkaEventProducerTestEndpoints;
import com.extole.client.rest.test.ProcessEventStatus;
import com.extole.common.event.AsyncKafkaEventProducer;
import com.extole.common.event.PartitionKey;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.request.FileInputStreamRequest;
import com.extole.event.consumer.ConsumerEvent;
import com.extole.event.consumer.ConsumerEventMetadata;

@Provider
public class BatchKafkaEventProducerTestEndpointsImpl implements BatchKafkaEventProducerTestEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(BatchKafkaEventProducerTestEndpointsImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();

    private final AsyncKafkaEventProducer asyncKafkaEventProducer;

    @Autowired
    public BatchKafkaEventProducerTestEndpointsImpl(AsyncKafkaEventProducer asyncKafkaEventProducer) {
        this.asyncKafkaEventProducer = asyncKafkaEventProducer;
    }

    @Override
    public List<ProcessEventStatus> produce(String accessToken, FileInputStreamRequest request) {
        return processRequest(request);
    }

    private List<ProcessEventStatus> processRequest(FileInputStreamRequest request) {
        ImmutableList.Builder<ProcessEventStatus> statusMessages = ImmutableList.builder();

        try (InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String jsonEvent;
            while ((jsonEvent = bufferedReader.readLine()) != null) {
                ProcessEventStatus processStatus = processJsonEvent(jsonEvent);
                statusMessages.add(processStatus);
            }
        } catch (IOException ioException) {
            LOG.error("T3-1010 Failed to read line from input stream", ioException);
        }

        return statusMessages.build();
    }

    private ProcessEventStatus processJsonEvent(String jsonEvent) {
        if (StringUtils.isEmpty(jsonEvent)) {
            return ProcessEventStatus.IGNORED_EMPTY_LINE;
        }

        try {
            ConsumerEventMetadata event = OBJECT_MAPPER.readValue(jsonEvent, ConsumerEventMetadata.class);
            asyncKafkaEventProducer.sendEvent(jsonEvent, ConsumerEvent.TOPIC,
                new PartitionKey(event.getPartitionKey()),
                event.getClientId(),
                event.getRequestTime());
            return ProcessEventStatus.SUCCESSFULLY_SENT;
        } catch (JsonProcessingException e) {
            LOG.error("T3-1010 Failed to deserialize json event to consumer event metadata={}", jsonEvent, e);
            return ProcessEventStatus.INVALID_JSON;
        } catch (EventTooLargeException e) {
            LOG.error("T3-1010 Failed to produce too large consumer event={}", jsonEvent, e);
            return ProcessEventStatus.EVENT_TOO_LARGE;
        } catch (Exception e) {
            LOG.error("T3-1010 Failed to produce consumer event={}", jsonEvent, e);
            return ProcessEventStatus.FAILED;
        }
    }

}
